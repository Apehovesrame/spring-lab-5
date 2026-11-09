package ru.kafpin.springlab5.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kafpin.springlab5.dto.ServicemanDTO;
import ru.kafpin.springlab5.dto.ServicemanResponseDTO;
import ru.kafpin.springlab5.model.Address;
import ru.kafpin.springlab5.model.Rank;
import ru.kafpin.springlab5.model.Serviceman;
import ru.kafpin.springlab5.repository.AddressJdbcRepository;
import ru.kafpin.springlab5.repository.RankJdbcRepository;
import ru.kafpin.springlab5.repository.ServicemanJdbcRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ServicemanRestController {

    private final ServicemanJdbcRepository servicemanRepository;
    private final RankJdbcRepository rankRepository;
    private final AddressJdbcRepository addressRepository;

    public ServicemanRestController(ServicemanJdbcRepository servicemanRepository,
                                    RankJdbcRepository rankRepository,
                                    AddressJdbcRepository addressRepository) {
        this.servicemanRepository = servicemanRepository;
        this.rankRepository = rankRepository;
        this.addressRepository = addressRepository;
    }

    @GetMapping("/servicemen")
    public List<Serviceman> getAllServicemen() {
        return servicemanRepository.findAll();
    }

    @GetMapping("/ranks")
    public List<Rank> getAllRanks() {
        return rankRepository.findAll();
    }

    @GetMapping("/addresses")
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @GetMapping("/servicemen/{id}")
    public Serviceman getServiceman(@PathVariable("id") Long id) {
        return servicemanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Военнослужащий с ID " + id + " не найден"));
    }

    @PostMapping("/servicemen")
    @ResponseStatus(HttpStatus.CREATED)
    public ServicemanResponseDTO createServiceman(@RequestBody ServicemanDTO dto) {
        Rank rank = rankRepository.findByTitleIgnoreCase(dto.getRankTitle())
                .orElseThrow(() -> new RuntimeException("Звание не найдено: " + dto.getRankTitle()));
        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new RuntimeException("Адрес не найден"));

        Serviceman serviceman = new Serviceman();
        serviceman.setSurname(dto.getSurname());
        serviceman.setName(dto.getName());
        serviceman.setPatronymic(dto.getPatronymic());
        serviceman.setNationality(dto.getNationality());
        serviceman.setBirthDate(dto.getBirthDate());
        serviceman.setPosition(dto.getPosition());
        serviceman.setRank(rank);
        serviceman.setAddress(address);

        // В JDBC мы получаем ID, а затем вытаскиваем собранный объект из БД
        Long newId = servicemanRepository.save(serviceman);
        Serviceman savedServiceman = servicemanRepository.findById(newId).get();

        ServicemanResponseDTO responseDTO = new ServicemanResponseDTO();
        responseDTO.setId(savedServiceman.getId());
        responseDTO.setSurname(savedServiceman.getSurname());
        responseDTO.setName(savedServiceman.getName());
        responseDTO.setPatronymic(savedServiceman.getPatronymic());
        responseDTO.setNationality(savedServiceman.getNationality());
        responseDTO.setBirthDate(savedServiceman.getBirthDate());
        responseDTO.setPosition(savedServiceman.getPosition());
        responseDTO.setRankTitle(savedServiceman.getRank().getTitle());
        responseDTO.setAddressId(savedServiceman.getAddress().getId());

        return responseDTO;
    }

    @PutMapping("/servicemen/{id}")
    public Serviceman updateServiceman(@PathVariable("id") Long id, @RequestBody ServicemanDTO dto) {
        Serviceman serviceman = servicemanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Военнослужащий не найден"));
        Rank rank = rankRepository.findByTitleIgnoreCase(dto.getRankTitle())
                .orElseThrow(() -> new RuntimeException("Некорректное звание"));
        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new RuntimeException("Некорректный ID адреса"));

        serviceman.setSurname(dto.getSurname());
        serviceman.setName(dto.getName());
        serviceman.setPatronymic(dto.getPatronymic());
        serviceman.setNationality(dto.getNationality());
        serviceman.setBirthDate(dto.getBirthDate());
        serviceman.setPosition(dto.getPosition());
        serviceman.setRank(rank);
        serviceman.setAddress(address);

        servicemanRepository.update(serviceman); // Обновляем через JDBC
        return servicemanRepository.findById(id).get();
    }

    @DeleteMapping("/servicemen/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteServiceman(@PathVariable("id") Long id) {
        servicemanRepository.deleteById(id);
    }

    @GetMapping("/servicemen/statistics")
    public Map<String, Object> getStatistics(@RequestParam(required = false) String city) {
        List<Serviceman> list = servicemanRepository.findAll();

        if (city != null && !city.isBlank()) {
            list = list.stream()
                    .filter(s -> s.getAddress() != null && city.equalsIgnoreCase(s.getAddress().getCity()))
                    .collect(Collectors.toList());
        }

        Map<String, Long> countByRank = list.stream()
                .filter(s -> s.getRank() != null)
                .collect(Collectors.groupingBy(s -> s.getRank().getTitle(), Collectors.counting()));

        double averageAge = list.stream()
                .filter(s -> s.getBirthDate() != null)
                .mapToInt(s -> Period.between(s.getBirthDate(), LocalDate.now()).getYears())
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCount", list.size());
        stats.put("averageAge", Math.round(averageAge * 10.0) / 10.0);
        stats.put("distributionByRank", countByRank);
        return stats;
    }

    @PostMapping("/servicemen/{id}/promote")
    public Map<String, Object> promoteServiceman(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Serviceman serviceman = servicemanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Военнослужащий не найден"));

        String newRankTitle = request.get("newRank");
        String newPosition = request.get("newPosition");
        String oldRankTitle = serviceman.getRank().getTitle();

        Rank newRank = rankRepository.findByTitleIgnoreCase(newRankTitle)
                .orElseThrow(() -> new RuntimeException("Звание не найдено"));

        serviceman.setRank(newRank);
        if (newPosition != null && !newPosition.isBlank()) {
            serviceman.setPosition(newPosition);
        }

        servicemanRepository.update(serviceman); // Обновляем через JDBC

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Военнослужащий успешно повышен");
        response.put("serviceman", serviceman.getSurname() + " " + serviceman.getName());
        response.put("oldRank", oldRankTitle);
        response.put("newRank", newRank.getTitle());
        response.put("currentPosition", serviceman.getPosition());
        return response;
    }

    @PatchMapping("/servicemen/{id}")
    public Serviceman partialUpdateServiceman(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Serviceman serviceman = servicemanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Военнослужащий не найден"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "surname" -> serviceman.setSurname((String) value);
                case "name" -> serviceman.setName((String) value);
                case "patronymic" -> serviceman.setPatronymic((String) value);
                case "nationality" -> serviceman.setNationality((String) value);
                case "position" -> serviceman.setPosition((String) value);
                case "birthDate" -> serviceman.setBirthDate(LocalDate.parse((String) value));
                case "rankTitle" -> {
                    Rank rank = rankRepository.findByTitleIgnoreCase((String) value)
                            .orElseThrow(() -> new RuntimeException("Звание не найдено"));
                    serviceman.setRank(rank);
                }
                case "addressId" -> {
                    Long addrId = Long.valueOf(value.toString());
                    Address address = addressRepository.findById(addrId)
                            .orElseThrow(() -> new RuntimeException("Адрес не найден"));
                    serviceman.setAddress(address);
                }
            }
        });

        servicemanRepository.update(serviceman); // Обновляем через JDBC
        return servicemanRepository.findById(id).get();
    }
}