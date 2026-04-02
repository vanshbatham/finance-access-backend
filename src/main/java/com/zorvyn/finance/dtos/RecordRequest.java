package com.zorvyn.finance.dtos;

import com.zorvyn.finance.entities.Category;
import com.zorvyn.finance.entities.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be strictly positive")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Date is required")
    private LocalDate transactionDate;

    private String notes;
}