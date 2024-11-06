// src/main/java/org/example/service/VerificationService.java
package org.example.service;

import org.example.model.MismatchLog;
import org.example.model.Transaction;
import org.example.repository.MismatchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {
    private final MismatchLogRepository mismatchLogRepository;

    @Autowired
    public VerificationService(MismatchLogRepository mismatchLogRepository) {
        this.mismatchLogRepository = mismatchLogRepository;
    }

    public void verifyTransaction(Transaction transaction, Transaction externalData) {
        if (transaction.getPrice() != externalData.getPrice()) {
            MismatchLog mismatch = new MismatchLog();
            mismatch.setTransactionId(transaction.getTransactionId());
            mismatch.setField("price");
            mismatch.setInternalValue(String.valueOf(transaction.getPrice()));
            mismatch.setExternalValue(String.valueOf(externalData.getPrice()));
            mismatchLogRepository.save(mismatch);
        }
        // Add further checks for other fields like quantity, UID
    }
}
