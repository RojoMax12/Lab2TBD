package com.lab.backend.Repository;

import com.lab.backend.Entities.CentralEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

@Repository
public class CentralRepository {

    private final Sql2o sql2o;

    public CentralRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public CentralEntity createCentral(CentralEntity central) {
        String sql = """
            INSERT INTO central (name, location)
            VALUES (:name, ST_GeomFromText(:location, 4326))
        """;

        try (Connection conn = sql2o.open()) {
            Long id = conn.createQuery(sql, true)
                    .addParameter("name", central.getName())
                    .addParameter("location", central.getLocation()) // WKT
                    .executeUpdate()
                    .getKey(Long.class);

            central.setId(id);
            return central;
        }
    }

    public List<CentralEntity> getAllCentrals() {
        String sql = """
            SELECT
              id,
              name,
              ST_AsText(location) AS location
            FROM central
        """;

        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .executeAndFetch(CentralEntity.class);
        }
    }

    public CentralEntity getCentralById(Long id) {
        String sql = """
            SELECT
              id,
              name,
              ST_AsText(location) AS location
            FROM central
            WHERE id = :id
        """;

        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(CentralEntity.class);
        }
    }

    public void updateCentral(Long id, CentralEntity central) {
        String sql = """
            UPDATE central
            SET
              name = :name,
              location = ST_GeomFromText(:location, 4326)
            WHERE id = :id
        """;

        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("name", central.getName())
                    .addParameter("location", central.getLocation())
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }

    public void deleteCentral(Long id) {
        String sql = "DELETE FROM central WHERE id = :id";

        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }
}

