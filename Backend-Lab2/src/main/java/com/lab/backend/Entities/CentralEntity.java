package com.lab.backend.Entities;


public class CentralEntity {
    private Long id;
    private String name;
    private float coord_x;
    private float coord_y;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

