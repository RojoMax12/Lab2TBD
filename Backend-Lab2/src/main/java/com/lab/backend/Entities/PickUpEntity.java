package com.lab.backend.Entities;

import java.util.Date;

public class PickUpEntity {
    private Long id;
    private Long id_container;
    private Long id_route;
    private Date date_hour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_container() {
        return id_container;
    }

    public void setId_container(Long id_container) {
        this.id_container = id_container;
    }

    public Long getId_route() {
        return id_route;
    }

    public void setId_route(Long id_route) {
        this.id_route = id_route;
    }

    public Date getDate_hour() {
        return date_hour;
    }

    public void setDate_hour(Date date_hour) {
        this.date_hour = date_hour;
    }
}
