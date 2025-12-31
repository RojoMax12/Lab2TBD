package com.lab.backend.Entities;

import java.sql.Date;
import java.sql.Time;

public class RouteEntity {
    private Long id;
    private Long id_driver;
    private Date date_;
    private Time start_time;
    private Time end_time;
    private String route_status;
    private Long id_central;
    private Long id_central_finish;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getId_driver() { return id_driver; }
    public void setId_driver(Long id_driver) { this.id_driver = id_driver; }



    public Time getStart_time() { return start_time; }
    public void setStart_time(Time start_time) { this.start_time = start_time; }

    public Time getEnd_time() { return end_time; }
    public void setEnd_time(Time end_time) { this.end_time = end_time; }

    public String getRoute_status() { return route_status; }
    public void setRoute_status(String route_status) { this.route_status = route_status; }

    public Long getId_central() { return id_central; }
    public void setId_central(Long id_central) { this.id_central = id_central; }

    public Long getId_central_finish() { return id_central_finish; }
    public void setId_central_finish(Long id_central_finish) { this.id_central_finish = id_central_finish; }


    @Override
    public String toString() {
        return "RouteEntity{" +
                "id=" + id +
                ", id_driver=" + id_driver +
                ", date_=" + date_ +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", route_status='" + route_status + '\'' +
                ", id_central=" + id_central +
                ", id_central_finish=" + id_central_finish +
                '}';
    }

    public Date getDate_() {
        return date_;
    }

    public void setDate_(Date date_) {
        this.date_ = date_;
    }
}
