package com.lab.backend.DTO;

import java.util.Date;

public class DailyWeight {
    private Date fecha;
    private Double peso_total;

    public DailyWeight() {}

    public DailyWeight(Date fecha, Double peso_total) {
        this.fecha = fecha;
        this.peso_total = peso_total;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getPeso_total() {
        return peso_total;
    }

    public void setPeso_total(Double peso_total) {
        this.peso_total = peso_total;
    }
}
