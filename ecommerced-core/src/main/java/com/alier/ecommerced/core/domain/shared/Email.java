package com.alier.ecommerced.core.domain.shared;

import com.alier.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.regex.Pattern;

/**
 * Email value object representing a valid email address.
 * This is a shared value object that can be used across different bounded contexts.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Email extends ValueObject {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String value;

    public Email(String value) {
        this.value = value != null ? value.trim().toLowerCase() : null;
        validate();
    }

    /**
     * Creates an Email from a string value.
     *
     * @param value the email string
     * @return the Email object
     */
    public static Email of(String value) {
        return new Email(value);
    }

    /**
     * Gets the domain part of the email.
     *
     * @return the domain part
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * Gets the local part of the email (before @).
     *
     * @return the local part
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    /**
     * Checks if this email belongs to a specific domain.
     *
     * @param domain the domain to check
     * @return true if the email belongs to the domain
     */
    public boolean belongsToDomain(String domain) {
        return getDomain().equals(domain.toLowerCase());
    }

    @Override
    protected void validate() {
        require(value != null, "Email cannot be null");
        require(!value.trim().isEmpty(), "Email cannot be empty");
        require(value.length() <= 254, "Email cannot be longer than 254 characters");
        require(EMAIL_PATTERN.matcher(value).matches(), "Invalid email format: " + value);
    }
} 