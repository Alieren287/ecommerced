package com.devcart.ecommerced.core.domain.shared;

import com.devcart.ecommerced.core.domain.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Money value object representing a monetary amount with currency.
 * This is a shared value object that can be used across different bounded contexts.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Money extends ValueObject {

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
        validate();
    }

    public Money(double amount, Currency currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    public Money(double amount, String currencyCode) {
        this(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    /**
     * Creates a Money object with zero amount.
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Creates a Money object with zero amount in USD.
     */
    public static Money zeroUSD() {
        return zero(Currency.getInstance("USD"));
    }

    /**
     * Adds another Money object to this one.
     * Both must have the same currency.
     */
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtracts another Money object from this one.
     * Both must have the same currency.
     */
    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Multiplies this Money by a factor.
     */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }

    /**
     * Multiplies this Money by a factor.
     */
    public Money multiply(double factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    /**
     * Checks if this Money is greater than another.
     */
    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Checks if this Money is less than another.
     */
    public boolean isLessThan(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Checks if this Money is positive.
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if this Money is negative.
     */
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Checks if this Money is zero.
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    protected void validate() {
        require(amount != null, "Amount cannot be null");
        require(currency != null, "Currency cannot be null");
    }

    private void requireSameCurrency(Money other) {
        require(this.currency.equals(other.currency),
                "Cannot perform operation with different currencies: " +
                        this.currency + " and " + other.currency);
    }
} 