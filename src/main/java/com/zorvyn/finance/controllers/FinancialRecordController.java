package com.zorvyn.finance.controllers;

import com.zorvyn.finance.dtos.RecordRequest;
import com.zorvyn.finance.dtos.RecordResponse;
import com.zorvyn.finance.services.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    // only Admins can create records
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponse> createRecord(
            @Valid @RequestBody RecordRequest request,
            Principal principal) { // Principal holds the current authenticated user's details

        RecordResponse response = recordService.createRecord(request, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // everyone (Admin, Analyst, Viewer) can view the list of records
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<List<RecordResponse>> getAllRecords() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    // everyone can view a specific record
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<RecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    // only Admins can delete records
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}