package com.zorvyn.finance.services;

import com.zorvyn.finance.dtos.DashboardSummaryResponse;
import com.zorvyn.finance.dtos.RecordRequest;
import com.zorvyn.finance.dtos.RecordResponse;
import com.zorvyn.finance.entities.Category;
import com.zorvyn.finance.entities.FinancialRecord;
import com.zorvyn.finance.entities.TransactionType;
import com.zorvyn.finance.entities.User;
import com.zorvyn.finance.repositories.FinancialRecordRepository;
import com.zorvyn.finance.repositories.UserRepository;
import com.zorvyn.finance.specifications.FinancialRecordSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public RecordResponse createRecord(RecordRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .createdBy(user)
                .build();

        FinancialRecord savedRecord = recordRepository.save(record);
        return mapToResponse(savedRecord);
    }

    public List<RecordResponse> getAllRecords() {
        return recordRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RecordResponse getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with ID: " + id));
        return mapToResponse(record);
    }

    public void deleteRecord(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new IllegalArgumentException("Record not found with ID: " + id);
        }
        recordRepository.deleteById(id);
    }

    // helper method to reduce code duplication when mapping entities to DTOs
    private RecordResponse mapToResponse(FinancialRecord record) {
        return RecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .transactionDate(record.getTransactionDate())
                .notes(record.getNotes())
                .createdBy(record.getCreatedBy().getEmail())
                .createdAt(record.getCreatedAt())
                .build();
    }

    public DashboardSummaryResponse getDashboardSummary() {
        BigDecimal totalIncome = recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE);

        // if no records exist yet
        totalIncome = (totalIncome != null) ? totalIncome : BigDecimal.ZERO;
        totalExpenses = (totalExpenses != null) ? totalExpenses : BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // map the Category totals from List<Object[]> to a clean map
        Map<String, BigDecimal> categoryMap = recordRepository.getCategoryTotals()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        obj -> obj[0].toString(),
                        obj -> (BigDecimal) obj[1]
                ));

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryMap)
                .build();
    }

    public List<RecordResponse> filterRecords(LocalDate startDate, LocalDate endDate, TransactionType type, Category category) {
        Specification<FinancialRecord> spec = FinancialRecordSpecification.filterRecords(startDate, endDate, type, category);

        return recordRepository.findAll(spec)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}