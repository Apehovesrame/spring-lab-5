package ru.kafpin.springlab5.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.kafpin.springlab5.model.Address;
import java.util.List;
import java.util.Optional;

@Repository
public class AddressJdbcRepository {
    private final JdbcTemplate jdbc;
    public AddressJdbcRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Address> findAll() {
        return jdbc.query("SELECT * FROM addresses", (rs, rowNum) -> {
            Address a = new Address(); a.setId(rs.getLong("id")); a.setCity(rs.getString("city")); return a;
        });
    }

    public Optional<Address> findById(Long id) {
        List<Address> list = jdbc.query("SELECT * FROM addresses WHERE id = ?",
                (rs, rowNum) -> { Address a = new Address(); a.setId(rs.getLong("id")); a.setCity(rs.getString("city")); return a; }, id);
        return list.stream().findFirst();
    }
}