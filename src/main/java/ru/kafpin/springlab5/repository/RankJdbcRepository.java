package ru.kafpin.springlab5.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.kafpin.springlab5.model.Rank;
import java.util.List;
import java.util.Optional;

@Repository
public class RankJdbcRepository {
    private final JdbcTemplate jdbc;
    public RankJdbcRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Rank> findAll() {
        return jdbc.query("SELECT * FROM ranks", (rs, rowNum) -> {
            Rank r = new Rank(); r.setId(rs.getLong("id")); r.setTitle(rs.getString("title")); return r;
        });
    }

    public Optional<Rank> findByTitleIgnoreCase(String title) {
        List<Rank> list = jdbc.query("SELECT * FROM ranks WHERE LOWER(title) = LOWER(?)",
                (rs, rowNum) -> { Rank r = new Rank(); r.setId(rs.getLong("id")); r.setTitle(rs.getString("title")); return r; }, title);
        return list.stream().findFirst();
    }
}