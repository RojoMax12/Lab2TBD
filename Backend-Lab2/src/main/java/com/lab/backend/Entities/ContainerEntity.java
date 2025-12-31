package com.lab.backend.Entities;

public class ContainerEntity {
    private Long id;
    private Long id_waste;
    private String location;
    private float weight;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_waste() {
        return id_waste;
    }

    public void setId_waste(Long id_waste) {
        this.id_waste = id_waste;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
