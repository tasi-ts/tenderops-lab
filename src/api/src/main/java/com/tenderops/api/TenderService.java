package com.tenderops.api;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenderService {

    private final TenderRepository tenderRepository;

    public TenderService(TenderRepository tenderRepository) {
        this.tenderRepository = tenderRepository;
    }

    public List<Tender> findAll() {
        return tenderRepository.findAll();
    }

    public Optional<Tender> findById(UUID id) {
        return tenderRepository.findById(id);
    }

    public Tender create(CreateTenderRequest request) {
        return tenderRepository.create(request);
    }
}