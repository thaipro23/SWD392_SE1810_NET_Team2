package com.pjb.kindergarten_suggestion.common.utils;

import java.util.List;

public class AppRoutes {

        public static final String DEFAULT = "/";
        public static final String EXAMPLE = "/example";
        public static final String PARENT = "/parent";
        public static final String HOME_PAGE = "/homepage";
        public static final String FAQ_PAGE = "/faq";

        // Auth
        public static final String LOGIN = "/auth/login";
        public static final String LOGOUT = "/auth/logout";
        public static final String REGISTER = "/auth/register";
        public static final String ACTIVATE_ACCOUNT = "/auth/activate-account";
        public static final String RESEND_ACTIVATION = "/auth/resend-activation";
        public static final String FORGOT_PASSWORD = "/auth/forgot-password";
        public static final String RESET_PASSWORD = "/auth/reset-password";
        public static final String PROFILE = "/auth/profile";
        public static final String CHANGE_PASSWORD = "/auth/profile/change-password";

        // Admin routes
        public static final String ADMIN_ROUTES = "/admin/**";
        // Admin user routes
        public static final String ADMIN_USER_MANAGEMENT = "/admin/users";
        public static final String ADMIN_USER_CREATE = "/admin/users/create";
        public static final String ADMIN_USER_DETAIL = "/admin/users/detail/{id}";
        public static final String ADMIN_USER_EDIT = "/admin/users/detail/{id}/edit";
        // Admin parent routes
        public static final String ADMIN_PARENT_MANAGEMENT = "/admin/parents";
        public static final String ADMIN_PARENT_DETAIL = "/admin/parents/detail/{id}";
        public static final String ADMIN_PARENT_ENROLL_ID = "/admin/parents/enroll/{id}";
        public static final String ADMIN_PARENT_UNENROLL_ID = "/admin/parents/unenroll/{id}";
        public static final String ADMIN_PARENT_UNCANCEL_ID = "/admin/parents/uncancel/{id}";
        // Admin school routes
        public static final String ADMIN_SCHOOL_MANAGEMENT = "/admin/schools";
        public static final String ADMIN_SCHOOL_ADD = "/admin/schools/add";
        public static final String ADMIN_SCHOOL_DETAIL = "/admin/schools/detail/{id}";
        public static final String ADMIN_SCHOOL_EDIT = "/admin/schools/detail/{id}/edit";
        public static final String ADMIN_SCHOOL_RATING = "/admin/schools/detail/{id}/rating";
        public static final String ADMIN_SCHOOL_FILTER = "/admin/schools/detail/{id}/filter";
        // Admin school request routes
        public static final String ADMIN_SCHOOL_REQUEST = "/admin/schools/request";
        public static final String ADMIN_SCHOOL_REQUEST_SEARCH = "/admin/schools/request/search";
        public static final String ADMIN_SCHOOL_REQUEST_DETAIL = "/admin/schools/request/detail/{id}";
        public static final String ADMIN_SCHOOL_REQUEST_EDIT = "/admin/schools/request/detail/{id}/edit";
        public static final String ADMIN_SCHOOL_REQUEST_REMINDER = "/admin/schools/request/reminder";
        public static final String ADMIN_SCHOOL_REQUEST_REMINDER_SEARCH = "/admin/schools/request/reminder/search";
        // Admin faq routes
        public static final String ADMIN_FAQ = "/admin/faq";
        public static final String ADMIN_FAQ_CREATE = "/admin/faq/create";
        public static final String ADMIN_FAQ_DETAIL = "/admin/faq/detail/{id}";
        public static final String ADMIN_FAQ_DELETE = "/admin/faq/delete/{id}";
        public static final String ADMIN_FAQ_UPDATE = "/admin/faq/detail/{id}/update";
        // school owner routes
        public static final String SCHOOL_OWNER_ROUTES = "/school-owner/**";
        // school owner parent routes
        public static final String SCHOOL_OWNER_PARENT_MANAGEMENT = "/school-owner/parents";
        public static final String SCHOOL_OWNER_PARENT_DETAIL = "/school-owner/parents/detail/{id}";
        public static final String SCHOOL_OWNER_PARENT_ENROLL_ID = "/school-owner/parents/enroll/{id}";
        public static final String SCHOOL_OWNER_PARENT_UNENROLL_ID = "/school-owner/parents/unenroll/{id}";
        public static final String SCHOOL_OWNER_PARENT_UNCANCEL_ID = "/school-owner/parents/uncancel/{id}";
        // school owner school routes
        public static final String SCHOOL_OWNER_SCHOOL = "/school-owner/schools";
        public static final String SCHOOL_OWNER_SCHOOL_ADD = "/school-owner/schools/add";
        public static final String SCHOOL_OWNER_SCHOOL_DETAIL = "/school-owner/schools/detail/{id}";
        public static final String SCHOOL_OWNER_SCHOOL_EDIT = "/school-owner/schools/detail/{id}/edit";
        public static final String SCHOOL_OWNER_SCHOOL_RATING = "/school-owner/schools/detail/{id}/rating";
        public static final String SCHOOL_OWNER_SCHOOL_FILTER = "/school-owner/schools/detail/{id}/filter";

        // school owner school request routes
        public static final String SCHOOL_OWNER_SCHOOL_REQUEST = "/school-owner/schools/request";
        public static final String SCHOOL_OWNER_SCHOOL_REQUEST_SEARCH = "/school-owner/schools/request/search";
        public static final String SCHOOL_OWNER_SCHOOL_REQUEST_DETAIL = "/school-owner/schools/request/detail/{id}";
        public static final String SCHOOL_OWNER_SCHOOL_REQUEST_EDIT = "/school-owner/schools/request/detail/{id}/edit";
        public static final String SCHOOL_OWNER_SCHOOL_REQUEST_REMINDER = "/school-owner/schools/request/reminder";
        public static final String SCHOOL_OWNER_SCHOOL_REQUEST_REMINDER_SEARCH = "/school-owner/schools/request/reminder/search";

        // parent view my schools routes
        public static final String PARENT_SCHOOL_DETAIL = "/parent/school-detail/{id}";
        public static final String PARENT_MY_SCHOOLS = "/parent/my_schools";
        public static final String PARENT_MY_SCHOOLS_RATING = "/parent/my_schools/rating/";
        public static final String PARENT_MY_SCHOOLS_RATING_DETAIL = "/parent/my_schools/rating/detail/{id}";

        public static final List<String> privateRoutes = List.of(
                        CHANGE_PASSWORD,
                        LOGOUT,
                        PARENT,
                        PROFILE,
                        EXAMPLE);

        public static final List<String> adminRoutes = List.of(ADMIN_ROUTES);

        public static final List<String> schoolOwnerRoutes = List.of(SCHOOL_OWNER_ROUTES);

        public static final List<String> parentRoutes = List.of();

        public static final List<String> adminAndSchoolOwnerRoutes = List.of();
}
