package com.tenderops.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenderRepository {

    private final JdbcTemplate jdbcTemplate;

    public TenderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Tender> findAll() {
        return jdbcTemplate.query(
                "SELECT id, title, buyer, status, created_at FROM tenders ORDER BY created_at DESC",
                this::mapRow
        );
    }

    public Optional<Tender> findById(UUID id) {
        List<Tender> results = jdbcTemplate.query(
                "SELECT id, title, buyer, status, created_at FROM tenders WHERE id = ?",
                this::mapRow,
                id
        );

        return results.stream().findFirst();
    }

    public Tender create(CreateTenderRequest request) {
        Tender tender = new Tender(
                UUID.randomUUID(),
                request.title(),
                request.buyer(),
                "OPEN",
                Instant.now()
        );

        jdbcTemplate.update(
                "INSERT INTO tenders (id, title, buyer, status, created_at) VALUES (?, ?, ?, ?, ?)",
                tender.id(),
                tender.title(),
                tender.buyer(),
                tender.status(),
                Timestamp.from(tender.createdAt())
        );

        return tender;
    }

    private Tender mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Tender(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("title"),
                resultSet.getString("buyer"),
                resultSet.getString("status"),
                resultSet.getTimestamp("created_at").toInstant()
        );
    }
}