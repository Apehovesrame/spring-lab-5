package ru.kafpin.springlab5.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;

public class Rank {
    private Long id;

    private String title;

    @JsonBackReference
    private List<Serviceman> servicemen;

    public Rank() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Serviceman> getServicemen() {
        return servicemen;
    }

    public void setServicemen(List<Serviceman> servicemen) {
        this.servicemen = servicemen;
    }
}