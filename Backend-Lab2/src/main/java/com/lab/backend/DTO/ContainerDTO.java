package com.lab.backend.DTO;

public class ContainerDTO {
    private Long id;
    private String description; // Opcional, según tu tabla
    private Double latitude;    // Debe coincidir con el nombre de propiedad del Frontend
    private Double longitude;   // Debe coincidir con el nombre de propiedad del Frontend

    // Constructores
    public ContainerDTO() {}

    // Getters y Setters
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}