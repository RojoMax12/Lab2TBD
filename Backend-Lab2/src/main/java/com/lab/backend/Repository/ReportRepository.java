package com.lab.backend.Repository;

import com.lab.backend.DTO.DailyWeight;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ReportRepository {

    private final Sql2o sql2o;

    public ReportRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public List<DailyWeight> getDailyCollectedWeight() {
        String sql = "SELECT fecha, peso_total FROM peso_recolectado_diario ORDER BY fecha";
        try (Connection conn = sql2o.open()) {
            List<Map<String, Object>> rows = conn.createQuery(sql).executeAndFetchTable().asList();
            List<DailyWeight> result = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Date fecha = (Date) row.get("fecha");
                Number peso = (Number) row.get("peso_total");
                result.add(new DailyWeight(fecha, peso != null ? peso.doubleValue() : 0.0));
            }
            return result;
        }
    }
}
