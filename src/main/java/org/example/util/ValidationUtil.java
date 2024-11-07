package org.example.util;

import org.example.model.Transaction;

public class ValidationUtil {

    // Validates transaction data against predefined range boundaries
    public static boolean validateTransactionRange(Transaction transaction, double minPrice, double maxPrice, int minQuantity, int maxQuantity) {
        return transaction.getPrice() >= minPrice && transaction.getPrice() <= maxPrice
                && transaction.getQuantity() >= minQuantity && transaction.getQuantity() <= maxQuantity;
    }

    // Additional validation methods can be added here (e.g., validate UID format, asset class constraints, etc.)
}
