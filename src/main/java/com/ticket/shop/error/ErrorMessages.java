package com.ticket.shop.error;

/**
 * Error Messages
 */
public class ErrorMessages {

    /**
     * Private constructor to avoid unnecessary instantiation
     */
    private ErrorMessages() {
    }

    public static final String USER_NOT_FOUND = "Can't find any user with the given id";
    public static final String COMPANY_NOT_FOUND = "Can't find any company with the given id";
    public static final String COUNTRY_NOT_FOUND = "Can't find any country with the given id";
    public static final String ADDRESS_NOT_FOUND = "Can't find any address with the given id";

    public static final String EMAIL_ALREADY_EXISTS = "The given email already exists";
    public static final String NAME_ALREADY_EXISTS = "The given name already exists";
    public static final String WEBSITE_ALREADY_EXISTS = "The given website already exists";

    public static final String WRONG_CREDENTIALS = "The email doesn't exist or the password is wrong";
    public static final String DATABASE_COMMUNICATION_ERROR = "Database communication error";
    public static final String OPERATION_FAILED = "Failed to process the requested operation";
    public static final String ACCESS_DENIED = "Access is denied";
    public static final String ROLE_INVALID = "The given role is invalid";
}
