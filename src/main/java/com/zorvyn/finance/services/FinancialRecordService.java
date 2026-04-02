package com.zorvyn.finance.services;

import com.zorvyn.finance.dtos.RecordRequest;
import com.zorvyn.finance.dtos.RecordResponse;
import com.zorvyn.finance.entities.FinancialRecord;
import com.zorvyn.finance.entities.User;
import com.zorvyn.finance.repositories.FinancialRecordRepository;
import com.zorvyn.finance.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}