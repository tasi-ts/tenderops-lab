package com.tenderops.api;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TenderService {

    private final ConcurrentHashMap<UUID, Tender> tenders = new ConcurrentHashMap<>();

    public TenderService() {
        create(new CreateTenderRequest("Hospital imaging system tender", "Budapest Clinical Center"));
        create(new CreateTenderRequest("Cloud procurement platform extension", "Central Procurement Office"));
    }

    public List<Tender> findAll() {
        return new ArrayList<>(tenders.values());
    }

    public Optional<Tender> findById(UUID id) {
        return Optional.ofNullable(tenders.get(id));
    }

    public Tender create(CreateTenderRequest request) {
        Tender tender = new Tender(
                UUID.randomUUID(),
                request.title(),
                request.buyer(),
                "OPEN",
                Instant.now()
        );

        tenders.put(tender.id(), tender);
        return tender;
    }
}
