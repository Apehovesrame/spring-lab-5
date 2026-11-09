-- справочник званий
INSERT INTO ranks (title) VALUES ('Рядовой');
INSERT INTO ranks (title) VALUES ('Лейтенант');
INSERT INTO ranks (title) VALUES ('Майор');
INSERT INTO ranks (title) VALUES ('Подполковник');
INSERT INTO ranks (title) VALUES ('Полковник');
INSERT INTO ranks (title) VALUES ('Генерал майор');

--  адреса
INSERT INTO addresses (post_index, country, region, district, city, street, house, apartment)
VALUES ('602267', 'Россия', 'Владимирская область', 'Муромский', 'Муром', 'Орловская', '23', '15');

INSERT INTO addresses (post_index, country, region, district, city, street, house, apartment)
VALUES ('101000', 'Россия', 'Московская область', 'Центральный', 'Москва', 'Тверская', '1', '10');

-- военнослужащие
INSERT INTO servicemen (surname, name, patronymic, nationality, birth_date, position, rank_id, address_id)
VALUES ('Иванов', 'Иван', 'Иванович', 'Русский', '1990-05-15', 'Командир взвода', 1, 1);

INSERT INTO servicemen (surname, name, patronymic, nationality, birth_date, position, rank_id, address_id)
VALUES ('Петров', 'Петр', 'Петрович', 'Белорус', '1985-11-20', 'Начальник штаба', 2, 2);