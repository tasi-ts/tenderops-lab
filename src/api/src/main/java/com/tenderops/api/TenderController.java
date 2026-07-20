package com.tenderops.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenders")
public class TenderController {

    private final TenderService tenderService;

    public TenderController(TenderService tenderService) {
        this.tenderService = tenderService;
    }

    @GetMapping
    public List<Tender> getTenders() {
        return tenderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tender> getTenderById(@PathVariable UUID id) {
        return tenderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tender> createTender(@Valid @RequestBody CreateTenderRequest request) {
        Tender createdTender = tenderService.create(request);
        return ResponseEntity.ok(createdTender);
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return Map.of(
                "service", "tenderops-api",
                "tenderCount", tenderService.findAll().size(),
                "status", "running"
        );
    }
}
