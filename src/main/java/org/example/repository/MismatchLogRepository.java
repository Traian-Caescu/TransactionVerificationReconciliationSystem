// src/main/java/org/example/repository/MismatchLogRepository.java
package org.example.repository;

import org.example.model.MismatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MismatchLogRepository extends JpaRepository<MismatchLog, Long> {
    // Custom query methods if needed
}
