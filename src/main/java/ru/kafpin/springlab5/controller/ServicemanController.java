package ru.kafpin.springlab5.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kafpin.springlab5.model.Serviceman;
import ru.kafpin.springlab5.repository.ServicemanJdbcRepository;

import java.util.Optional;

@Controller
@RequestMapping("/servicemen")
public class ServicemanController {

    private final ServicemanJdbcRepository repository;

    public ServicemanController(ServicemanJdbcRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("servicemen", repository.findAll());
        return "index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model) {
        Optional<Serviceman> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/servicemen";
        }
        model.addAttribute("serviceman", optional.get());
        return "details";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        repository.deleteById(id);
        return "redirect:/servicemen";
    }
}