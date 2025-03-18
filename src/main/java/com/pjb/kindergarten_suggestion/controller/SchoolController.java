package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.SchoolCreateDTO;
import com.pjb.kindergarten_suggestion.entities.ChildAge;
import com.pjb.kindergarten_suggestion.entities.District;
import com.pjb.kindergarten_suggestion.entities.EducationMethod;
import com.pjb.kindergarten_suggestion.entities.Facility;
import com.pjb.kindergarten_suggestion.entities.Province;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolType;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.entities.Utilities;
import com.pjb.kindergarten_suggestion.entities.Wards;
import com.pjb.kindergarten_suggestion.services.AuthService;
import com.pjb.kindergarten_suggestion.services.ChildAgeService;
import com.pjb.kindergarten_suggestion.services.DistrictService;
import com.pjb.kindergarten_suggestion.services.EducationMethodService;
import com.pjb.kindergarten_suggestion.services.FacilityService;
import com.pjb.kindergarten_suggestion.services.ProvinceService;
import com.pjb.kindergarten_suggestion.services.SchoolService;
import com.pjb.kindergarten_suggestion.services.SchoolTypeService;
import com.pjb.kindergarten_suggestion.services.UtilitiesService;
import com.pjb.kindergarten_suggestion.services.WardsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SchoolController {

  private final AuthService authService;
  private final ProvinceService provinceService;
  private final DistrictService districtService;
  private final WardsService wardService;
  private final SchoolService schoolService;
  private final SchoolTypeService schoolTypeService;
  private final ChildAgeService childAgeService;
  private final EducationMethodService educationMethodService;
  private final FacilityService facilityService;
  private final UtilitiesService utilitiesService;

  @Value("${file.upload-dir}")
  private String uploadDir;

  @GetMapping("/schools/add/get-school-code")
  public ResponseEntity<String> countUsernamePrefix(@RequestParam("name") String name) {
    return ResponseEntity.ok(schoolService.generateSchoolCode(name));
  }

  @GetMapping(AppRoutes.ADMIN_SCHOOL_ADD)
  public String addNewSchoolPage(Model model) {
    return setupSchoolPage(
        "Add new School", new SchoolCreateDTO(), model, AppRoutes.ADMIN_SCHOOL_ADD);
  }

  private String setupSchoolPage(
      String pageTitle, SchoolCreateDTO school, Model model, String formAction) {
    List<Province> provinces = provinceService.getAllProvinces();
    List<SchoolType> schoolTypes = schoolTypeService.getAll();
    List<ChildAge> childAges = childAgeService.getAll();
    List<EducationMethod> educationMethods = educationMethodService.getAll();
    List<Facility> facilities = facilityService.getAll();
    List<Utilities> utilities = utilitiesService.getAll();
    if (school.getAddress() != null && school.getAddress().getProvince() != null) {
      List<District> districts =
          districtService.getDistrictsByProvinceId(school.getAddress().getProvince().getId());
      model.addAttribute("districts", districts);
      if (school.getAddress().getDistrict() != null) {
        List<Wards> wards =
            wardService.getWardsByDistrictId(school.getAddress().getDistrict().getId());
        model.addAttribute("wards", wards);
      }
    }
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("formAction", formAction);
    model.addAttribute("facilities", facilities);
    model.addAttribute("utilities", utilities);
    model.addAttribute("educationMethods", educationMethods);
    model.addAttribute("childAges", childAges);
    model.addAttribute("schoolTypes", schoolTypes);
    model.addAttribute("school", school);
    model.addAttribute("provinces", provinces);
    return "pages/school/add-new-school";
  }

  @PostMapping(AppRoutes.ADMIN_SCHOOL_ADD)
  public String addNewSchoolAdmin(
      @Valid @ModelAttribute("school") SchoolCreateDTO schoolDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes,
      @RequestParam("imageList") MultipartFile[] files) {
    handleSaveImg(schoolDTO, files);
    if (!validationSchool(schoolDTO, bindingResult)) {
      return setupSchoolPage("Add new School", schoolDTO, model, AppRoutes.ADMIN_SCHOOL_ADD);
    }
    // save school with admin role
    User currentUser = authService.getCurrentUser().get();
    schoolDTO.setSchoolCode(schoolService.generateSchoolCode(schoolDTO.getName()));
    School newSchool = new School(schoolDTO);
    newSchool.setStatus(SchoolStatus.APPROVED);
    newSchool.setCreator(currentUser);
    schoolService.saveSchool(newSchool);
    redirectAttributes.addFlashAttribute("successMessage", "School has been added successfully");
    return "redirect:"
        + AppRoutes.ADMIN_SCHOOL_DETAIL.replace("{id}", newSchool.getId().toString());
  }

  private void handleSaveImg(SchoolCreateDTO schoolDTO, MultipartFile[] files) {
    long maxId = schoolService.getMaximumSchoolId() + 1;
    int lastIndexImg = findLastIndexImg(schoolDTO.getImages());
    removeOldImages(schoolDTO.getImages());
    List<String> listUrl;
    try {
      listUrl = uploadImages(files, maxId, lastIndexImg);
      if (schoolDTO.getImages() == null) {
        schoolDTO.setImages(new ArrayList<>());
      }
      schoolDTO.getImages().addAll(listUrl);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean validationSchool(SchoolCreateDTO schoolDTO, BindingResult bindingResult) {
    if (schoolDTO.getFeeTo() < schoolDTO.getFeeFrom()) {
      bindingResult.rejectValue(
          "feeTo", "feeTo.invalid", "Fee to must be greater than or equal to fee from");
    }
    if (schoolDTO.getAddress().getDetail().isBlank()) {
      bindingResult.rejectValue(
          "Address.detail", "address.detail.invalid", "Address detail is required");
    }
    return !bindingResult.hasErrors();
  }

  private int findLastIndexImg(List<String> images) {
    if (images == null) {
      return 0;
    }
    // 1001_school_8.jpg
    int lastIndex = 0;
    for (String img : images) {
      if (img.startsWith("/assets")) continue;
      String index = img.substring(img.lastIndexOf("_") + 1);
      int i = Integer.parseInt(index.substring(0, index.indexOf(".")));
      if (i > lastIndex) {
        lastIndex = i;
      }
    }
    return lastIndex;
  }

  private void removeOldImages(List<String> images) {
    if (images == null) {
      return;
    }
    // images: /uploads/schools/1001/1001_school_3.jpg
    Set<String> imagesSet = new HashSet<>();
    for (String img : images) {
      imagesSet.add(img.substring(img.lastIndexOf("/") + 1));
    }
    String schoolDirectory = System.getProperty("user.dir") + uploadDir + "/schools";
    try {
      Files.walk(Paths.get(schoolDirectory))
          .filter(Files::isRegularFile)
          .forEach(
              path -> {
                String relativePath = path.getFileName().toString();
                if (!imagesSet.contains(relativePath)) {
                  try {
                    Files.deleteIfExists(path);
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }
              });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_ADD)
  public String addNewSchoolPageOwner(Model model) {
    return setupSchoolPage(
        "Add new School", new SchoolCreateDTO(), model, AppRoutes.SCHOOL_OWNER_SCHOOL_ADD);
  }

  @PostMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_ADD)
  public String addNewSchoolOwner(
      @Valid @ModelAttribute("school") SchoolCreateDTO schoolDTO,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes,
      @RequestParam("imageList") MultipartFile[] files,
      @RequestParam("action") String action)
      throws IOException {
    handleSaveImg(schoolDTO, files);
    if (!validationSchool(schoolDTO, bindingResult)) {
      return setupSchoolPage("Add new School", schoolDTO, model, AppRoutes.SCHOOL_OWNER_SCHOOL_ADD);
    }
    // save school with school owner role
    User currentUser = authService.getCurrentUser().get();
    schoolDTO.setSchoolCode(schoolService.generateSchoolCode(schoolDTO.getName()));
    School newSchool = new School(schoolDTO);
    newSchool.setStatus(action.equals("saved") ? SchoolStatus.SAVED : SchoolStatus.SUBMITTED);
    newSchool.setCreator(currentUser);
    schoolService.saveSchool(newSchool);
    redirectAttributes.addFlashAttribute("successMessage", "School has been added successfully");
    return "redirect:"
        + AppRoutes.SCHOOL_OWNER_SCHOOL_DETAIL.replace("{id}", newSchool.getId().toString());
  }

  @GetMapping(AppRoutes.ADMIN_SCHOOL_EDIT)
  public String editSchoolPage(@PathVariable("id") Long id, Model model) {
    School school = schoolService.getSchoolById(id);
    SchoolCreateDTO schoolDTO = new SchoolCreateDTO(school);
    return setupSchoolPage(
        "Edit School",
        schoolDTO,
        model,
        AppRoutes.ADMIN_SCHOOL_EDIT.replace("{id}", id.toString()));
  }

  @PostMapping(AppRoutes.ADMIN_SCHOOL_EDIT)
  public String editSchool(
      @Valid @ModelAttribute("school") SchoolCreateDTO schoolDTO,
      BindingResult bindingResult,
      @PathVariable("id") Long id,
      Model model,
      RedirectAttributes redirectAttributes,
      @RequestParam("imageList") MultipartFile[] files) {
    handleSaveImg(schoolDTO, files);
    if (!validationSchool(schoolDTO, bindingResult)) {
      return setupSchoolPage(
          "Edit School",
          schoolDTO,
          model,
          AppRoutes.ADMIN_SCHOOL_EDIT.replace("{id}", id.toString()));
    }
    School currentSchool = schoolService.getSchoolById(id);
    schoolDTO.setSchoolCode(schoolService.generateSchoolCode(schoolDTO.getName()));
    currentSchool.update(schoolDTO);
    currentSchool.setStatus(SchoolStatus.APPROVED);
    schoolService.saveSchool(currentSchool);
    redirectAttributes.addFlashAttribute("successMessage", "School has been updated successfully");
    return "redirect:" + AppRoutes.ADMIN_SCHOOL_DETAIL.replace("{id}", id.toString());
  }

  @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_EDIT)
  public String editSchoolPageOwner(@PathVariable("id") Long id, Model model) {
    School school = schoolService.getSchoolById(id);
    if (!checkSchoolOwnerRole(school)) {
      return "error/403";
    }
    SchoolCreateDTO schoolDTO = new SchoolCreateDTO(school);
    return setupSchoolPage(
        "Edit School",
        schoolDTO,
        model,
        AppRoutes.SCHOOL_OWNER_SCHOOL_EDIT.replace("{id}", id.toString()));
  }

  @PostMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_EDIT)
  public String editSchoolOwner(
      @ModelAttribute("school") SchoolCreateDTO schoolDTO,
      BindingResult bindingResult,
      @PathVariable("id") Long id,
      Model model,
      RedirectAttributes redirectAttributes,
      @RequestParam("imageList") MultipartFile[] files,
      @RequestParam("action") String action) {
    School currentSchool = schoolService.getSchoolById(id);
    if (!checkSchoolOwnerRole(currentSchool)) {
      return "error/403";
    }
    handleSaveImg(schoolDTO, files);
    if (!validationSchool(schoolDTO, bindingResult)) {
      return setupSchoolPage(
          "Edit School",
          schoolDTO,
          model,
          AppRoutes.SCHOOL_OWNER_SCHOOL_EDIT.replace("{id}", id.toString()));
    }
    schoolDTO.setSchoolCode(schoolService.generateSchoolCode(schoolDTO.getName()));
    currentSchool.update(schoolDTO);
    currentSchool.setStatus(action.equals("saved") ? SchoolStatus.SAVED : SchoolStatus.SUBMITTED);
    schoolService.saveSchool(currentSchool);
    redirectAttributes.addFlashAttribute("successMessage", "School has been updated successfully");
    return "redirect:" + AppRoutes.SCHOOL_OWNER_SCHOOL_DETAIL.replace("{id}", id.toString());
  }

  @GetMapping(AppRoutes.ADMIN_SCHOOL_DETAIL)
  public String detailSchoolPage(@PathVariable("id") Long id, Model model) {
    model.addAttribute("pageTitle", "School Detail");
    School school = schoolService.getSchoolById(id);
    List<Facility> facilities = facilityService.getAll();
    List<Utilities> utilities = utilitiesService.getAll();
    model.addAttribute("school", school);
    model.addAttribute("facilities", facilities);
    model.addAttribute("utilities", utilities);
    return "pages/school/detail-school";
  }

  @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_DETAIL)
  public String detailSchoolPageOwner(@PathVariable("id") Long id, Model model) {
    School currentSchool = schoolService.getSchoolById(id);
    if (!checkSchoolOwnerRole(currentSchool)) {
      return "error/403";
    }
    return detailSchoolPage(id, model);
  }

  private List<String> uploadImages(MultipartFile[] files, long schoolId, int startIndex)
      throws IOException {
    String UPLOAD_DIRECTORY = System.getProperty("user.dir") + uploadDir + "/schools";
    String SRC_URL = "/uploads/schools/" + schoolId;
    List<String> fileNames = new ArrayList<>(files.length);

    String schoolDirectory = UPLOAD_DIRECTORY + "/" + schoolId;
    Path schoolPath = Paths.get(schoolDirectory);
    if (!Files.exists(schoolPath)) {
      Files.createDirectories(schoolPath);
    }

    int fileIndex = startIndex + 1;
    for (MultipartFile file : files) {
      if (!file.isEmpty()) {
        String fileName = schoolId + "_school_" + fileIndex + ".jpg";
        Path fileNameAndPath = schoolPath.resolve(fileName);
        Files.write(fileNameAndPath, file.getBytes());
        fileIndex++;
        fileNames.add(SRC_URL + "/" + fileName);
      }
    }
    return fileNames;
  }

  private boolean checkSchoolOwnerRole(School school) {
    Optional<User> user = authService.getCurrentUser();
    if (user.isEmpty()) {
      return false;
    }
    if(user.get().getRole().equals(Role.ADMIN)){
      return true;
    }
    return school.getCreator().getId().equals(user.get().getId());
  }

  private boolean checkSchoolOwnerRole(Long SchoolId) {
    return checkSchoolOwnerRole(schoolService.getSchoolById(SchoolId));
  }

  @GetMapping(AppRoutes.ADMIN_SCHOOL_DETAIL + "/approve")
  public ResponseEntity<String> ApproveSchool(@PathVariable Long id) {
    SchoolStatus status = getSchoolStatus(id);
    if(status == SchoolStatus.SUBMITTED){
      schoolService.ApproveSchool(id);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().body("School can not approve.Please reload page and try again");
  }

  @GetMapping(AppRoutes.ADMIN_SCHOOL_DETAIL + "/reject")
  public ResponseEntity<String> RejectSchool(@PathVariable Long id) {
    SchoolStatus status = getSchoolStatus(id);
    if(status == SchoolStatus.SUBMITTED){
      schoolService.RejectSchool(id);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().body("School can not reject.Please reload page and try again");
  }
  public SchoolStatus getSchoolStatus(Long id) {
    Optional<School> schoolOpt = Optional.ofNullable(schoolService.getSchoolById(id));
    if (schoolOpt.isPresent()) {
      return schoolOpt.get().getStatus();
    }
    throw new EntityNotFoundException("School not found with id " + id);
  }

  @GetMapping(AppRoutes.ADMIN_SCHOOL_DETAIL + "/publish")
  public ResponseEntity<String> AdminPublishSchool(@PathVariable Long id) {
    SchoolStatus status = getSchoolStatus(id);
    if (status == SchoolStatus.PUBLISHED) {
      return ResponseEntity.badRequest().body("School already published");
    } else if (status == SchoolStatus.APPROVED||status == SchoolStatus.UNPUBLISHED) {
      schoolService.PublishSchool(id);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("School can not published");
    }

  }

  @GetMapping(AppRoutes.ADMIN_SCHOOL_DETAIL + "/unpublish")
  public ResponseEntity<String> AdminUnpublishSchool(@PathVariable Long id) {
    SchoolStatus status = getSchoolStatus(id);
    if (status == SchoolStatus.UNPUBLISHED) {
      return ResponseEntity.badRequest().body("School already published");
    } else if (status == SchoolStatus.APPROVED||status == SchoolStatus.PUBLISHED) {
      schoolService.UnPublishSchool(id);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("School can not Unpublished");
    }

  }

  @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_DETAIL + "/submit")
  public ResponseEntity<String> schoolOwnerSubmitSchool(@PathVariable Long id) {
    if(!checkSchoolOwnerRole(id)){
      return ResponseEntity.badRequest().body("You are not owner of this school");
    }
    schoolService.submitSchool(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_DETAIL + "/publish")
  public ResponseEntity<String> PublishSchool(@PathVariable Long id) {
    if(!checkSchoolOwnerRole(id)){
      return ResponseEntity.badRequest().body("You are not owner of this school");
    }
    SchoolStatus status = getSchoolStatus(id);
    if (status == SchoolStatus.PUBLISHED) {
      return ResponseEntity.badRequest().body("School already published");
    } else if (status == SchoolStatus.APPROVED||status == SchoolStatus.UNPUBLISHED) {
      schoolService.PublishSchool(id);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("School can not published");
    }

  }

  @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_DETAIL + "/unpublish")
  public ResponseEntity<String> UnpublishSchool(@PathVariable Long id) {
    if(!checkSchoolOwnerRole(id)){
      return ResponseEntity.badRequest().body("You are not owner of this school");
    }
    SchoolStatus status = getSchoolStatus(id);
    if (status == SchoolStatus.UNPUBLISHED) {
      return ResponseEntity.badRequest().body("School already published");
    } else if (status == SchoolStatus.APPROVED||status == SchoolStatus.PUBLISHED) {
      schoolService.UnPublishSchool(id);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("School not Unpublished");
    }

  }

  @DeleteMapping(AppRoutes.ADMIN_SCHOOL_DETAIL + "/delete")
  public ResponseEntity<String> deleteSchoolAdmin(@PathVariable Long id) {
    SchoolStatus status = getSchoolStatus(id);
    if (status == SchoolStatus.DELETED) {
      return ResponseEntity.badRequest().body("School already deleted");
    }
    schoolService.deleteSchool(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_DETAIL + "/delete")
  public ResponseEntity<String> deleteSchoolOwner(@PathVariable Long id) {
    if(!checkSchoolOwnerRole(id)){
      return ResponseEntity.badRequest().body("You are not owner of this school");
    }
    SchoolStatus status = getSchoolStatus(id);
    if (status == SchoolStatus.DELETED) {
      return ResponseEntity.badRequest().body("School already deleted");
    }
    schoolService.deleteSchool(id);
    return ResponseEntity.ok().build();
  }
}
