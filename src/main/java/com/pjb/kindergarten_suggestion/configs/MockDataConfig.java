package com.pjb.kindergarten_suggestion.configs;

import com.github.javafaker.Faker;
import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.common.enums.RequestStatus;
import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.entities.Address;
import com.pjb.kindergarten_suggestion.entities.ChildAge;
import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;
import com.pjb.kindergarten_suggestion.entities.EducationMethod;
import com.pjb.kindergarten_suggestion.entities.Facility;
import com.pjb.kindergarten_suggestion.entities.ImageUrl;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolEnrollment;
import com.pjb.kindergarten_suggestion.entities.SchoolRating;
import com.pjb.kindergarten_suggestion.entities.SchoolType;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.entities.Utilities;
import com.pjb.kindergarten_suggestion.repositories.AddressRepository;
import com.pjb.kindergarten_suggestion.repositories.ChildAgeRepository;
import com.pjb.kindergarten_suggestion.repositories.CounsellingRequestRepository;
import com.pjb.kindergarten_suggestion.repositories.EducationMethodRepository;
import com.pjb.kindergarten_suggestion.repositories.FacilityRepository;
import com.pjb.kindergarten_suggestion.repositories.SchoolEnrollmentRepository;
import com.pjb.kindergarten_suggestion.repositories.SchoolRatingRepository;
import com.pjb.kindergarten_suggestion.repositories.SchoolRepository;
import com.pjb.kindergarten_suggestion.repositories.SchoolTypeRepository;
import com.pjb.kindergarten_suggestion.repositories.UserRepository;
import com.pjb.kindergarten_suggestion.repositories.UtilitiesRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class MockDataConfig {

  private static final int MAX_USER = 100;
  private static final int MAX_SCHOOL = 1000;
  private static final int MAX_COUNSELLING_REQUESTS = 3000;
  private static final int MAX_SCHOOL_ENROLLMENTS = 5000;
  private static final int MAX_WARDS = 10584;

  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final SchoolRepository schoolRepository;
  private final ChildAgeRepository childAgeRepository;
  private final EducationMethodRepository educationMethodRepository;
  private final CounsellingRequestRepository counsellingRequestRepository;
  private final SchoolEnrollmentRepository schoolEnrollmentRepository;
  private final SchoolRatingRepository schoolRatingRepository;
  private final SchoolTypeRepository schoolTypeRepository;
  private final FacilityRepository facilityRepository;
  private final UtilitiesRepository utilitiesRepository;
  private final PasswordEncoder passwordEncoder;

  private final Faker faker = new Faker();
  private final JdbcTemplate jdbcTemplate;
  private final DataSource dataSource;

  @Bean
  CommandLineRunner mockData() {
    return args -> {
      if (userRepository.count() < MAX_USER) {
        System.out.println("\u001B[33mGenerating mock data...\u001B[0m");
        addTriggers();
        runDataSql();
        makeMockData();
        System.out.println("\u001B[32mMock data generated successfully.\u001B[0m");
      } else {
        System.out.println("\u001B[32mMock data already exists.\u001B[0m");
      }
    };
  }

  public void runDataSql() throws IOException {
    String sql =
        Files.readString(
            Paths.get("src/main/resources/data.sql"), java.nio.charset.StandardCharsets.UTF_8);
    String[] statements = sql.split(";");

    for (String statement : statements) {
      String trimmedStatement = statement.trim();
      if (!trimmedStatement.isEmpty()) {
        jdbcTemplate.execute(trimmedStatement);
      }
    }
  }

  private void addTriggers() {
    String createTriggerAfterInsert =
        """
        CREATE TRIGGER after_insert_school_rating
        AFTER INSERT ON school_rating FOR EACH ROW
        BEGIN
        DECLARE new_rating BIGINT;
        SET new_rating = NEW.rating1 + NEW.rating2 + NEW.rating3 + NEW.rating4 + NEW.rating5;
        UPDATE school SET total_rating = total_rating + new_rating,
        total_rating_count = total_rating_count + 1
        WHERE id = NEW.school;
        END;
        """;

    String createTriggerAfterUpdate =
        """
        CREATE TRIGGER after_update_school_rating
        AFTER UPDATE ON school_rating FOR EACH ROW
        BEGIN
        DECLARE old_rating BIGINT;
        DECLARE new_rating BIGINT;
        SET old_rating = OLD.rating1 + OLD.rating2 + OLD.rating3 + OLD.rating4 + OLD.rating5;
        SET new_rating = NEW.rating1 + NEW.rating2 + NEW.rating3 + NEW.rating4 + NEW.rating5;
        UPDATE school SET total_rating = total_rating - old_rating + new_rating
        WHERE id = NEW.school;
        END;
        """;

    String createTriggerAfterDelete =
        """
        CREATE TRIGGER after_delete_school_rating
        AFTER DELETE ON school_rating FOR EACH ROW
        BEGIN
        DECLARE old_rating BIGINT;
        SET old_rating = OLD.rating1 + OLD.rating2 + OLD.rating3 + OLD.rating4 + OLD.rating5;
        UPDATE school SET total_rating = total_rating - old_rating,
        total_rating_count = total_rating_count - 1
        WHERE id = OLD.school;
        END;
        """;

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createTriggerAfterInsert);
      stmt.execute(createTriggerAfterUpdate);
      stmt.execute(createTriggerAfterDelete);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private int between(int x, int y) {
    return faker.random().nextInt(y - x + 1) + x;
  }

  private long between(long x, long y) {
    return faker.random().nextLong(y - x + 1) + x;
  }

  private void makeMockData() {
    List<User> users = generateUsers(MAX_USER);
    List<School> schools = genSchools(MAX_SCHOOL);
    List<User> adminUsers =
        users.stream().filter(user -> user.getRole().equals(Role.ADMIN)).toList();
    List<User> schoolUsers =
        users.stream().filter(user -> user.getRole().equals(Role.SCHOOL_OWNER)).toList();
    List<User> parentUsers =
        users.stream().filter(user -> user.getRole().equals(Role.PARENT)).toList();
    List<School> acceptedSchools =
        schools.stream()
            .filter(
                school ->
                    (school.getStatus().equals(SchoolStatus.APPROVED)
                        || school.getStatus().equals(SchoolStatus.PUBLISHED)
                        || school.getStatus().equals(SchoolStatus.UNPUBLISHED)
                        || school.getStatus().equals(SchoolStatus.DELETED)))
            .toList();
    List<CounsellingRequest> counsellingRequests =
        genCounsellingRequests(MAX_COUNSELLING_REQUESTS, parentUsers, acceptedSchools);
    List<SchoolEnrollment> schoolEnrollments =
        genSchoolEnrollments(
            Math.min(MAX_SCHOOL_ENROLLMENTS, parentUsers.size() * acceptedSchools.size()),
            parentUsers,
            acceptedSchools);
    List<SchoolEnrollment> schoolEnrollmentsForRating =
        schoolEnrollments.stream()
            .filter(
                enrollment ->
                    enrollment.getStatus().equals(EnrollStatus.ENROLL)
                        || enrollment.getStatus().equals(EnrollStatus.UNENROLL))
            .toList();
    List<SchoolRating> schoolRatings = genSchoolRatings(schoolEnrollmentsForRating);
    schools.forEach(
        s -> {
          s.setCreator(schoolUsers.get(between(0, schoolUsers.size() - 1)));
          if (!s.getStatus().equals(SchoolStatus.SUBMITTED)
              && !s.getStatus().equals(SchoolStatus.SAVED)) {
            s.setAcceptor(adminUsers.get(between(0, adminUsers.size() - 1)));
          }
        });
    System.out.println("Saving users...");
    userRepository.saveAll(users);
    System.out.println("Saving schools...");
    schoolRepository.saveAll(schools);
    System.out.println("Saving counselling requests...");
    counsellingRequestRepository.saveAll(counsellingRequests);
    System.out.println("Saving school enrollments...");
    schoolEnrollmentRepository.saveAll(schoolEnrollments);
    System.out.println("Saving school ratings...");
    schoolRatingRepository.saveAll(schoolRatings);
  }

  private List<User> generateUsers(int n) {
    System.out.println("Generating " + n + " users...");
    Set<String> usernames = new HashSet<>();
    Set<String> phones = new HashSet<>();
    Set<String> emails = new HashSet<>();
    List<User> users = new ArrayList<>(n);
    String password = passwordEncoder.encode("1234");
    while (users.size() < n) {
      String username = faker.name().username();
      String phone = faker.regexify("\\+\\d{10}");
      String email = faker.internet().emailAddress();
      if (usernames.add(username) && phones.add(phone) && emails.add(email)) {
        User user =
            User.builder()
                .fullname(faker.name().name())
                .username(username)
                .email(email)
                .password(password)
                .birthDate(
                    faker
                        .date()
                        .birthday()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .role(faker.options().option(Role.class))
                .phone(phone)
                .address(genAddress())
                .isActive(faker.bool().bool())
                .isDeleted(false)
                .createdAt(
                    faker
                        .date()
                        .past(365 * 2, java.util.concurrent.TimeUnit.DAYS)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .build();
        users.add(user);
      }
    }
    return users;
  }

  private Address genAddress() {
    Address newAddress = addressRepository.getAddressByWardsId(between(1, MAX_WARDS));
    newAddress.setDetail(faker.address().fullAddress());
    return newAddress;
  }

  private List<School> genSchools(int n) {
    System.out.println("Generating " + n + " schools...");
    HashMap<String, Integer> schoolCodes = new HashMap<>();
    List<School> schools = new ArrayList<>(n);
    List<Facility> facilityList = facilityRepository.findAll();
    List<Utilities> utilityList = utilitiesRepository.findAll();
    List<ChildAge> childAgeList = childAgeRepository.findAll();
    List<SchoolType> schoolTypeList = schoolTypeRepository.findAll();
    List<EducationMethod> educationMethodList = educationMethodRepository.findAll();
    HashMap<Integer, SchoolStatus> statusMap = new HashMap<>();
    for (int i = 0; i < 80; i++) {
      statusMap.put(i, SchoolStatus.PUBLISHED);
    }
    for (int i = 80; i <= 100; i++) {
      statusMap.put(i, faker.options().option(SchoolStatus.class));
    }
    for (int i = 0; i < n; i++) {
      Set<Facility> facilities = new HashSet<>();
      Set<Utilities> utilities = new HashSet<>();
      for (int j = 0; j < 7; j++) {
        facilities.add(facilityList.get(between(0, facilityList.size() - 1)));
        utilities.add(utilityList.get(between(0, utilityList.size() - 1)));
      }
      List<ImageUrl> images = genImages(between(0, 6));
      LocalDateTime createdAt =
          faker
              .date()
              .past(365 * 5, java.util.concurrent.TimeUnit.DAYS)
              .toInstant()
              .atZone(ZoneId.systemDefault())
              .toLocalDateTime();
      Long feeFrom = between(1_000_000L, 10_000_000L);
      Long feeTo = between(feeFrom+1_000_000L, 20_000_000L);
      School school =
          School.builder()
              .name(faker.company().name())
              .phone(faker.regexify("\\+\\d{10}"))
              .email(faker.internet().emailAddress())
              .childAge(childAgeList.get(between(0, childAgeList.size() - 1)))
              .educationMethod(educationMethodList.get(between(0, educationMethodList.size() - 1)))
              .schoolType(schoolTypeList.get(between(0, schoolTypeList.size() - 1)))
              .feeFrom(feeFrom)
              .feeTo(feeTo)
              .description(faker.lorem().paragraph(between(20,50)))
              .status(statusMap.get(between(0, 100)))
              .facilities(facilities)
              .utilities(utilities)
              .images(images)
              .address(genAddress())
              .createdAt(createdAt)
              .updatedAt(createdAt)
              .build();
      if (school.getStatus().equals(SchoolStatus.PUBLISHED)
          || school.getStatus().equals(SchoolStatus.UNPUBLISHED)
          || school.getStatus().equals(SchoolStatus.DELETED)) {
        school.setPublishedAt(createdAt.plusNanos(between(dayToNano(10), dayToNano(365))));
      }
      String schoolCode = generateSchoolCode(school.getName());
      schoolCodes.put(schoolCode, schoolCodes.getOrDefault(schoolCode, 0) + 1);
      schoolCode += schoolCodes.get(schoolCode);
      school.setSchoolCode(schoolCode);
      schools.add(school);
    }
    return schools;
  }

  String generateSchoolCode(String schoolName) {
    String[] words = schoolName.split("\\W+");
    StringBuilder code = new StringBuilder("SCH-");
    for (String word : words) {
      if (word.isBlank()) continue;
      code.append(word.charAt(0));
    }
    return code.toString().toUpperCase();
  }

  String baseUrl = "/assets/img/school/school-img-";

  List<ImageUrl> genImages(int n) {
    Set<String> images = new HashSet<>();
    while (images.size() < n) {
      int x = between(1, 35);
      images.add(baseUrl + x + ".png");
    }
    return images.stream().map(url -> ImageUrl.builder().url(url).build()).toList();
  }

  List<CounsellingRequest> genCounsellingRequests(int n, List<User> parents, List<School> schools) {
    System.out.println("Generating " + n + " counselling requests...");
    List<CounsellingRequest> counsellingRequests = new ArrayList<>(n);
    LocalDateTime createdAt = LocalDateTime.now().minusYears(10);
    for (int i = 0; i < n; i++) {
      User parent = parents.get(between(0, parents.size() - 1));
      School school = schools.get(between(0, schools.size() - 1));
      createdAt = createdAt.plusNanos(between(0, dayToNano(2)));
      CounsellingRequest counsellingRequest =
          CounsellingRequest.builder()
              .fullname(faker.name().name())
              .email(parent.getEmail())
              .phone(parent.getPhone())
              .inquiry(faker.lorem().paragraph())
              .parent(parent)
              .school(school)
              .status(faker.options().option(RequestStatus.class))
              .createdAt(createdAt)
              .build();
      if (counsellingRequest.getStatus().equals(RequestStatus.CLOSED)) {
        counsellingRequest.setUpdatedAt(createdAt.plusNanos(between(10_000_000_000L, dayToNano(30))));
      }
      counsellingRequests.add(counsellingRequest);
    }
    return counsellingRequests;
  }

  private List<SchoolEnrollment> genSchoolEnrollments(
      int n, List<User> parents, List<School> schools) {
    System.out.println("Generating " + n + " school enrollments...");
    List<SchoolEnrollment> schoolEnrollments = new ArrayList<>(n);
    Set<Pair<User, School>> uniquePairs = new HashSet<>();
    while (schoolEnrollments.size() < n) {
      User parent = parents.get(between(0, parents.size() - 1));
      School school = schools.get(between(0, schools.size() - 1));
      Pair<User, School> pair = Pair.of(parent, school);
      if (uniquePairs.add(pair)) {
        SchoolEnrollment schoolEnrollment =
            SchoolEnrollment.builder()
                .parent(parent)
                .school(school)
                .status(faker.options().option(EnrollStatus.class))
                .createdAt(
                    faker
                        .date()
                        .past(365 * 2, java.util.concurrent.TimeUnit.DAYS)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .build();
        if (schoolEnrollment.getStatus().equals(EnrollStatus.ENROLL)
            || schoolEnrollment.getStatus().equals(EnrollStatus.UNENROLL)) {
          schoolEnrollment.setEnrolledDate(
              schoolEnrollment.getCreatedAt().toLocalDate().plusDays(between(0, 30)));
        }
        schoolEnrollments.add(schoolEnrollment);
      }
    }
    return schoolEnrollments;
  }

  private List<SchoolRating> genSchoolRatings(List<SchoolEnrollment> schoolEnrollments) {
    System.out.println("Generating " + schoolEnrollments.size() + " school ratings...");
    List<SchoolRating> schoolRatings = new ArrayList<>(schoolEnrollments.size());
    for (SchoolEnrollment schoolEnrollment : schoolEnrollments) {
      LocalDateTime createdAt =
          schoolEnrollment
              .getEnrolledDate()
              .atStartOfDay()
              .plusNanos(between(dayToNano(10), dayToNano(100)));
      SchoolRating schoolRating =
          SchoolRating.builder()
              .parent(schoolEnrollment.getParent())
              .school(schoolEnrollment.getSchool())
              .rating1((byte) between(1, 5))
              .rating2((byte) between(1, 5))
              .rating3((byte) between(1, 5))
              .rating4((byte) between(1, 5))
              .rating5((byte) between(1, 5))
              .feedback(faker.lorem().paragraph(between(3, 10))) 
              .createdAt(createdAt)
              .build();
      schoolRatings.add(schoolRating);
    }
    return schoolRatings;
  }

  private Long dayToNano(long days) {
    return days * 86_400_000_000_000L;
  }
}
