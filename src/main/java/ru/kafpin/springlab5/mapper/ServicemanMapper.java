package ru.kafpin.springlab5.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.kafpin.springlab5.model.Address;
import ru.kafpin.springlab5.model.Rank;
import ru.kafpin.springlab5.model.Serviceman;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicemanMapper implements RowMapper<Serviceman> {

    @Override
    public Serviceman mapRow(ResultSet rs, int rowNum) throws SQLException {
        // 1. Создаем военнослужащего и заполняем базовые поля
        Serviceman serviceman = new Serviceman();
        serviceman.setId(rs.getLong("s_id"));
        serviceman.setSurname(rs.getString("surname"));
        serviceman.setName(rs.getString("name"));
        serviceman.setPatronymic(rs.getString("patronymic"));
        serviceman.setNationality(rs.getString("nationality"));

        if (rs.getDate("birth_date") != null) {
            serviceman.setBirthDate(rs.getDate("birth_date").toLocalDate());
        }
        serviceman.setPosition(rs.getString("position"));

        // 2. Достаем данные звания (Rank) из той же строки и привязываем
        Rank rank = new Rank();
        rank.setId(rs.getLong("r_id"));
        rank.setTitle(rs.getString("title"));
        serviceman.setRank(rank);

        // 3. Достаем данные адреса (Address)
        Address address = new Address();
        address.setId(rs.getLong("a_id"));
        address.setPostIndex(rs.getString("post_index"));
        address.setCountry(rs.getString("country"));
        address.setRegion(rs.getString("region"));
        address.setDistrict(rs.getString("district"));
        address.setCity(rs.getString("city"));
        address.setStreet(rs.getString("street"));
        address.setHouse(rs.getString("house"));
        address.setApartment(rs.getString("apartment"));
        serviceman.setAddress(address);

        return serviceman; // Возвращаем полностью собранный объект
    }
}