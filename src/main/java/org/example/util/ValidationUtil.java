package org.example.util;

import org.example.model.Transaction;

public class ValidationUtil {

    public static boolean validateTransactionRange(Transaction transaction, double minPrice, double maxPrice, int minQuantity, int maxQuantity) {
        return transaction.getPrice() >= minPrice && transaction.getPrice() <= maxPrice
                && transaction.getQuantity() >= minQuantity && transaction.getQuantity() <= maxQuantity;
    }

}
