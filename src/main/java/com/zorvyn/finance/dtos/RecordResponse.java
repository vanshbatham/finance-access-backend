package com.zorvyn.finance.dtos;

import com.zorvyn.finance.entities.Category;
import com.zorvyn.finance.entities.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RecordResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private Category category;
    private LocalDate transactionDate;
    private String notes;
    private String createdBy; // We'll just return the email, not the whole User object
    private LocalDateTime createdAt;
}