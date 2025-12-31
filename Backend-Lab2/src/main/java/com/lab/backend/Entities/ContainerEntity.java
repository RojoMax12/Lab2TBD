package com.lab.backend.Entities;

public class ContainerEntity {
    private Long id;
    private Long id_waste;
    private float coord_x;
    private float coord_y;
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

    public float getCoord_x() {
        return coord_x;
    }

    public void setCoord_x(float coord_x) {
        this.coord_x = coord_x;
    }

    public float getCoord_y() {
        return coord_y;
    }

    public void setCoord_y(float coord_y) {
        this.coord_y = coord_y;
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
