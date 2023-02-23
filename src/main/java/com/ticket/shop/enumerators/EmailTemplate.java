package com.ticket.shop.enumerators;

/**
 * Enumerator for email template
 */
public enum EmailTemplate {
    CHANGE_PASSWORD("password-changed-email"),
    RESET_PASSWORD("reset-password-email"),
    CONFIRM_EMAIL_ADDRESS("confirm-email");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
