package ru.kafpin.springlab5.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.kafpin.springlab5.mapper.ServicemanMapper;
import ru.kafpin.springlab5.model.Serviceman;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ServicemanJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ServicemanJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Базовый SQL-запрос с JOIN для склеивания трех таблиц
    private static final String SELECT_ALL_JOIN_SQL = """
            SELECT s.id as s_id, s.surname, s.name, s.patronymic, s.nationality, s.birth_date, s.position,
                   r.id as r_id, r.title,
                   a.id as a_id, a.post_index, a.country, a.region, a.district, a.city, a.street, a.house, a.apartment
            FROM servicemen s
            LEFT JOIN ranks r ON s.rank_id = r.id
            LEFT JOIN addresses a ON s.address_id = a.id
            """;

    // 1. Получить всех (GET)
    public List<Serviceman> findAll() {
        return jdbcTemplate.query(SELECT_ALL_JOIN_SQL, new ServicemanMapper());
    }

    // 2. Найти одного по ID (GET)
    public Optional<Serviceman> findById(Long id) {
        String sql = SELECT_ALL_JOIN_SQL + " WHERE s.id = ?";
        List<Serviceman> result = jdbcTemplate.query(sql, new ServicemanMapper(), id);
        return result.stream().findFirst();
    }

    // 3. Создать нового (POST)
    public Long save(Serviceman serviceman) {
        String sql = "INSERT INTO servicemen (surname, name, patronymic, nationality, birth_date, position, rank_id, address_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, serviceman.getSurname());
            ps.setString(2, serviceman.getName());
            ps.setString(3, serviceman.getPatronymic());
            ps.setString(4, serviceman.getNationality());
            ps.setDate(5, java.sql.Date.valueOf(serviceman.getBirthDate()));
            ps.setString(6, serviceman.getPosition());
            ps.setLong(7, serviceman.getRank().getId());
            ps.setLong(8, serviceman.getAddress().getId());
            return ps;
        }, keyHolder);

        // Возвращаем сгенерированный базой данных ID
        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("Не удалось получить сгенерированный ключ");
        return key.longValue();
    }

    // 4. Полностью обновить (PUT)
    public int update(Serviceman serviceman) {
        String sql = "UPDATE servicemen SET surname = ?, name = ?, patronymic = ?, nationality = ?, birth_date = ?, position = ?, rank_id = ?, address_id = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                serviceman.getSurname(),
                serviceman.getName(),
                serviceman.getPatronymic(),
                serviceman.getNationality(),
                java.sql.Date.valueOf(serviceman.getBirthDate()),
                serviceman.getPosition(),
                serviceman.getRank().getId(),
                serviceman.getAddress().getId(),
                serviceman.getId()
        );
    }

    // 5. Удалить (DELETE)
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM servicemen WHERE id = ?", id);
    }
}