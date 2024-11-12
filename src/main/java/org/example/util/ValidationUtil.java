package org.example.util;

import org.example.model.Transaction;

/**
 * Utility class responsible for validating transaction data.
 * It contains methods to ensure that transaction data adheres to specified limits.
 */
public class ValidationUtil {

    /**
     * Validates if the price and quantity of a transaction are within the specified range.
     *
     * @param transaction The transaction to be validated.
     * @param minPrice The minimum allowable price.
     * @param maxPrice The maximum allowable price.
     * @param minQuantity The minimum allowable quantity.
     * @param maxQuantity The maximum allowable quantity.
     * @return True if the transaction's price and quantity are within the allowable ranges, false otherwise.
     */
    public static boolean validateTransactionRange(Transaction transaction, double minPrice, double maxPrice, int minQuantity, int maxQuantity) {
        // Validate the price and quantity of the transaction
        return transaction.getPrice() >= minPrice && transaction.getPrice() <= maxPrice
                && transaction.getQuantity() >= minQuantity && transaction.getQuantity() <= maxQuantity;
    }
}
