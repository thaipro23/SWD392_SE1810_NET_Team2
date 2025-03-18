package com.pjb.kindergarten_suggestion.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pjb.kindergarten_suggestion.common.enums.RequestStatus;
import com.pjb.kindergarten_suggestion.common.exception.RequestAcceptedException;
import com.pjb.kindergarten_suggestion.common.exception.RequestCancelledException;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.CounsellingRequestWithRatingDTO;
import com.pjb.kindergarten_suggestion.dto.SchoolSearchRequest;
import com.pjb.kindergarten_suggestion.dto.SchoolSearchResponse;
import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.AuthService;
import com.pjb.kindergarten_suggestion.services.CounsellingRequestService;
import com.pjb.kindergarten_suggestion.services.SchoolEnrollmentService;
import com.pjb.kindergarten_suggestion.services.SchoolService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CounselingRequestsController {

    public final CounsellingRequestService counsellingRequestService;
    public final AuthService authService;
    public final SchoolService schoolService;
    private final SchoolEnrollmentService schoolEnrollmentService;

    @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST)
    public String getCounselingRequest(Model model, @RequestParam("page") Optional<String> pageOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        model.addAttribute("pageTitle", "Counseling Requests");
        Optional<User> currentUser = authService.getCurrentUser();

        if (!currentUser.isPresent()) {
            System.out.println("User not found");
            return "redirect:" + AppRoutes.LOGIN;
        }

        User user = currentUser.get();
        int page = 1;
        int pageSize = 10;

        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
            if (pageSizeOptional.isPresent()) {
                pageSize = Integer.parseInt(pageSizeOptional.get());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Pageable pageable = PageRequest.of(page -1 , pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CounsellingRequest> list = counsellingRequestService.getAllRequestBySchoolOwner(user.getId(), pageable);
        List<CounsellingRequest> listRequest = list.getContent();

        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", list.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        return "pages/school/list-counseling-requests";
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST_DETAIL)
    public String getCounselingRequestDetail(@PathVariable("id") long id, Model model) {
        model.addAttribute("pageTitle", "Counseling Request Details");
        CounsellingRequest request = counsellingRequestService.getRequestById(id);
        model.addAttribute("request", request);
        return "pages/school/counseling-request-details";
    }

    @GetMapping(AppRoutes.ADMIN_SCHOOL_REQUEST_DETAIL)
    public String getCounselingRequestDetailForAdmin(@PathVariable("id") long id, Model model) {
        model.addAttribute("pageTitle", "Counseling Request Details");
        CounsellingRequest request = counsellingRequestService.getRequestById(id);
        model.addAttribute("request", request);
        return "pages/admin/school/counseling-request-details";
    }

    @GetMapping(AppRoutes.ADMIN_SCHOOL_REQUEST)
    public String getAllSchoolRequest(Model model,
            @RequestParam("page") Optional<String> pageOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        model.addAttribute("pageTitle", "Counseling Requests");

        int page = 1;
        int pageSize = 10;

        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
            if (pageSizeOptional.isPresent()) {
                pageSize = Integer.parseInt(pageSizeOptional.get());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Pageable pageable = PageRequest.of(page - 1, pageSize);
        Pageable pageable = PageRequest.of(page -1 , pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CounsellingRequest> list = counsellingRequestService.getAllCounselingRequest(pageable);
        List<CounsellingRequest> listRequest = list.getContent();

        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", list.getTotalPages());
        model.addAttribute("pageSize", pageSize);

        return "pages/admin/school/list-counseling-requests";
    }

    @GetMapping(AppRoutes.ADMIN_SCHOOL_REQUEST_SEARCH)
    public String searchRequest(@RequestParam("query") String query, Model model,
            @RequestParam("page") Optional<String> pageOptional) {
        model.addAttribute("pageTitle", "Counseling Requests");
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
        }

        if (query == null || query.trim().isEmpty()) {
            return "redirect:" + AppRoutes.ADMIN_SCHOOL_REQUEST;
        }

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<CounsellingRequest> searchResults = counsellingRequestService.searchByKeyWord(query, pageable);
        List<CounsellingRequest> listRequest = searchResults.getContent();
        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("count", listRequest.size());
        return "pages/admin/school/search-counseling-requests";
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST_SEARCH)
    public String searchRequestForSchool(@RequestParam("query") String query, Model model,
            @RequestParam("page") Optional<String> pageOptional) {
        model.addAttribute("pageTitle", "Counseling Requests");
        Optional<User> currentUser = authService.getCurrentUser();

        if (!currentUser.isPresent()) {
            System.out.println("User not found");
            return "redirect:" + AppRoutes.LOGIN;
        }

        if (query == null || query.trim().isEmpty()) {
            return "redirect:" + AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST;
        }

        User user = currentUser.get();
        int page = 1;

        if (pageOptional.isPresent()) {
            try {
                page = Integer.parseInt(pageOptional.get());
            } catch (NumberFormatException e) {
                System.out.println("Invalid page number, using default page 1");
                page = 1;
            }
        }

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<CounsellingRequest> searchResults = counsellingRequestService.searchByKeyWordInSchool(query, pageable,
                user.getId());
        List<CounsellingRequest> listRequest = searchResults.getContent();
        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("count", listRequest.size());
        return "pages/school/search-counseling-requests";
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST_REMINDER)
    public String getRequestReminderForSchool(Model model, @RequestParam("page") Optional<String> pageOptional) {
        model.addAttribute("pageTitle", "Request Reminder");
        Optional<User> currentUser = authService.getCurrentUser();

        if (!currentUser.isPresent()) {
            System.out.println("User not found");
            return "redirect:" + AppRoutes.LOGIN;
        }

        User user = currentUser.get();
        int page = 1;

        if (pageOptional.isPresent()) {
            try {
                page = Integer.parseInt(pageOptional.get());
            } catch (NumberFormatException e) {
                System.out.println("Invalid page number, using default page 1");
                page = 1;
            }
        }

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<CounsellingRequest> list = counsellingRequestService.getAllRequestReminderBySchoolOwner(user.getId(),
                pageable);
        List<CounsellingRequest> listRequest = list.getContent();

        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", list.getTotalPages());
        return "pages/school/list-request-reminder";
    }

    @PostMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST_EDIT)
    public String editRequest(Model model, @PathVariable("id") long id) {
        CounsellingRequest counsellingRequest = counsellingRequestService.getRequestById(id);
        counsellingRequest.setStatus(RequestStatus.CLOSED);
        counsellingRequest.setUpdatedAt(LocalDateTime.now());
        counsellingRequestService.EditRequest(counsellingRequest);
        return "redirect:" + AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST_DETAIL.replace("{id}", String.valueOf(id));
    }

    @PostMapping(AppRoutes.ADMIN_SCHOOL_REQUEST_EDIT)
    public String editRequestForAdmin(Model model, @PathVariable("id") long id) {
        CounsellingRequest counsellingRequest = counsellingRequestService.getRequestById(id);
        counsellingRequest.setStatus(RequestStatus.CLOSED);
        counsellingRequest.setUpdatedAt(LocalDateTime.now());
        counsellingRequestService.EditRequest(counsellingRequest);
        return "redirect:" + AppRoutes.ADMIN_SCHOOL_REQUEST_DETAIL.replace("{id}", String.valueOf(id));
    }

    @GetMapping(AppRoutes.ADMIN_SCHOOL_REQUEST_REMINDER)
    public String getRequestReminder(Model model, @RequestParam("page") Optional<String> pageOptional) {
        model.addAttribute("pageTitle", "Request Reminder");
        int page = 1;

        if (pageOptional.isPresent()) {
            try {
                page = Integer.parseInt(pageOptional.get());
            } catch (NumberFormatException e) {
                System.out.println("Invalid page number, using default page 1");
                page = 1;
            }
        }

        int pageSize=10;
        Pageable pageable = PageRequest.of(page -1 , pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        RequestStatus status = RequestStatus.valueOf("OPEN");
        Page<CounsellingRequest> list = counsellingRequestService.getAllRequestReminder(pageable, status);
        List<CounsellingRequest> listRequest = list.getContent();

        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", list.getTotalPages());
        return "pages/admin/school/list-request-reminder";
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST_REMINDER_SEARCH)
    public String searchRequestReminderForSchool(@RequestParam("query") String query, Model model,
            @RequestParam("page") Optional<String> pageOptional) {
        model.addAttribute("pageTitle", "Request Reminder");
        Optional<User> currentUser = authService.getCurrentUser();

        if (!currentUser.isPresent()) {
            System.out.println("User not found");
            return "redirect:" + AppRoutes.LOGIN;
        }

        if (query == null || query.trim().isEmpty()) {
            return "redirect:" + AppRoutes.SCHOOL_OWNER_SCHOOL_REQUEST_REMINDER;
        }

        User user = currentUser.get();
        int page = 1;

        if (pageOptional.isPresent()) {
            try {
                page = Integer.parseInt(pageOptional.get());
            } catch (NumberFormatException e) {
                System.out.println("Invalid page number, using default page 1");
                page = 1;
            }
        }

        int pageSize=10;
        Pageable pageable = PageRequest.of(page -1 , pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CounsellingRequest> searchResults = counsellingRequestService.searchRequestReminderByKeyWordInSchool(query,
                pageable, user.getId());
        List<CounsellingRequest> listRequest = searchResults.getContent();
        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("count", listRequest.size());
        return "pages/school/search-requests-reminder";
    }

    @GetMapping(AppRoutes.ADMIN_SCHOOL_REQUEST_REMINDER_SEARCH)
    public String searchRequestReminder(@RequestParam("query") String query, Model model,
            @RequestParam("page") Optional<String> pageOptional) {
        model.addAttribute("pageTitle", "Request Reminder");
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
        }

        if (query == null || query.trim().isEmpty()) {
            return "redirect:" + AppRoutes.ADMIN_SCHOOL_REQUEST_REMINDER;
        }

        int pageSize=10;
        Pageable pageable = PageRequest.of(page -1 , pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CounsellingRequest> searchResults = counsellingRequestService.searchRequestReminderByKeyWord(query,
                pageable);
        List<CounsellingRequest> listRequest = searchResults.getContent();
        model.addAttribute("listRequest", listRequest);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("count", listRequest.size());
        return "pages/admin/school/search-requests-reminder";
    }

    @GetMapping("/parent/request")
    public ResponseEntity<CounsellingRequest> getUserInfo() {
        Optional<User> currentUser = authService.getCurrentUser();
        if (!currentUser.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User user = currentUser.get();
        CounsellingRequest request = CounsellingRequest.builder()
                .fullname(user.getFullname())
                .email(user.getEmail())
                .parent(user)
                .phone(user.getPhone())
                .build();
        return ResponseEntity.ok(request);
    }

    @PostMapping("/parent/request")
    @ResponseBody
    public String getCounselingRequest(Model model, @ModelAttribute("request") CounsellingRequest request,
            @RequestParam("id") long id) {
        try {
            Optional<User> currentUser = authService.getCurrentUser();

            if (!currentUser.isPresent()) {
                return "error";
            }

            User user = currentUser.get();
            School school = schoolService.getSchoolById(id);

            CounsellingRequest counsellingRequest = new CounsellingRequest();
            counsellingRequest.setFullname(request.getFullname());
            counsellingRequest.setEmail(request.getEmail());
            counsellingRequest.setPhone(request.getPhone());
            counsellingRequest.setInquiry(request.getInquiry());
            counsellingRequest.setStatus(RequestStatus.OPEN);
            counsellingRequest.setParent(user);
            counsellingRequest.setSchool(school);
            counsellingRequest.setCreatedAt(LocalDateTime.now());

            counsellingRequestService.EditRequest(counsellingRequest);

            return "success";

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "error";
        }

    }

    @PostMapping("/parent/pending")
    @ResponseBody
    public String parentPendingSchool(Model model, HttpSession session,
            @RequestParam("schoolId") long schoolId, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> currentUser = authService.getCurrentUser();
            if (!currentUser.isPresent()) {
                return "error";
            }
            User user = currentUser.get();
            School school = schoolService.getSchoolById(schoolId);
            schoolEnrollmentService.parentWannaEnrollToSchool(user, school);
            updateSearchRequestSession(session);
            return "success";
        } catch (Exception e) {
            updateSearchRequestSession(session);
            System.out.println("Error: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/parent/cancel")
    @ResponseBody
    public String parentCancelPendingSchool(Model model, HttpSession session,
            @RequestParam("schoolId") long schoolId, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> currentUser = authService.getCurrentUser();
            if (!currentUser.isPresent()) {
                return "error";
            }
            User user = currentUser.get();
            School school = schoolService.getSchoolById(schoolId);
            schoolEnrollmentService.parentWannaCancelEnrollToSchool(user, school);
            updateSearchRequestSession(session);
            return "success";
        } catch (RequestAcceptedException ra) {
            updateSearchRequestSession(session);
            return "cancel-accepted-enroll";
        } catch (RequestCancelledException rc) {
            updateSearchRequestSession(session);
            return "cancel-cancelled-enroll";
        } catch (Exception e) {
            updateSearchRequestSession(session);
            System.out.println("Error: " + e.getMessage());
            return "error";
        }
    }

    private void updateSearchRequestSession(HttpSession session) {
        SchoolSearchRequest searchRequest = (SchoolSearchRequest) session.getAttribute("searchRequest");
        SchoolSearchResponse searchResults = schoolService.searchSchools(searchRequest);
        session.setAttribute("searchResults", searchResults);
    }

    @GetMapping("/parent/myRequest")
    public String getMyCounselingRequest(Model model, @RequestParam("page") Optional<String> pageOptional) {
        model.addAttribute("pageTitle", "My Request");
        Optional<User> currentUser = authService.getCurrentUser();

        if (!currentUser.isPresent()) {
            System.out.println("User not found");
            return "redirect:" + AppRoutes.LOGIN;
        }

        User user = currentUser.get();
        int page = 1;

        if (pageOptional.isPresent()) {
            try {
                page = Integer.parseInt(pageOptional.get());
            } catch (NumberFormatException e) {
                System.out.println("Invalid page number, using default page 1");
                page = 1;
            }
        }

        Pageable pageable = PageRequest.of(page - 1, 10);

        Page<CounsellingRequestWithRatingDTO> perentRequest = counsellingRequestService
                .getCounsellingRequestWithRatingByParentId(user.getId(), pageable);
        List<CounsellingRequestWithRatingDTO> myRequest = perentRequest.getContent();
        int countOpenRequest = counsellingRequestService.countByParentAndStatus(user, RequestStatus.OPEN);
        if (myRequest.size() > 0) {
            model.addAttribute("count", countOpenRequest);
            model.addAttribute("myRequest", myRequest);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", perentRequest.getTotalPages());
        } else {
            model.addAttribute("message", "There’s no requests available.");
        }

        return "pages/parent/parent-requets";
    }

    @GetMapping("/searchDemo")
    public String searchDemo(Model model) {
        Optional<User> currentUser = authService.getCurrentUser();

        if (!currentUser.isPresent()) {
            System.out.println("User not found");
            return "redirect:" + AppRoutes.LOGIN;
        }

        User user = currentUser.get();
        CounsellingRequest request = new CounsellingRequest();
        request.setFullname(user.getFullname());
        request.setEmail(user.getEmail());
        request.setParent(user);
        request.setPhone(user.getPhone());
        model.addAttribute("request", request);
        return "pages/timkiem";
    }

}
