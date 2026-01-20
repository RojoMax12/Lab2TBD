package com.lab.backend.DTO;

import java.util.List;

public class RouteRequestDTO {

    private List<ContainerDTO> contenedores;
    // NOTA: Deben coincidir con las claves del payload de JS
    private Long idDriver;
    private Long idCentral;
    private Long idCentralFinish;
    private String date;      // Recibido como "YYYY-MM-DD"
    private String startTime; // Recibido como "HH:mm"
    private String endTime;   // Recibido como "HH:mm"

    // Constructores
    public RouteRequestDTO() {}

    // Getters y Setters
    public List<ContainerDTO> getContenedores() { return contenedores; }
    public void setContenedores(List<ContainerDTO> contenedores) { this.contenedores = contenedores; }

    public Long getIdDriver() { return idDriver; }
    public void setIdDriver(Long idDriver) { this.idDriver = idDriver; }

    public Long getIdCentral() { return idCentral; }
    public void setIdCentral(Long idCentral) { this.idCentral = idCentral; }

    public Long getIdCentralFinish() { return idCentralFinish; }
    public void setIdCentralFinish(Long idCentralFinish) { this.idCentralFinish = idCentralFinish; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}