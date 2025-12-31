package com.lab.backend.Repository;

import com.lab.backend.Entities.WasteEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

@Repository
public class WasteRepository {

    private Sql2o sql2o;

    public WasteRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    //CRUD Operations

    public WasteEntity CreateWaste(WasteEntity wasteEntity) {
        String sql = "INSERT INTO waste (waste_type) VALUES (:waste_type)";
        Connection connection = null;
        try {
            connection = sql2o.open();
            Long id = connection.createQuery(sql, true)
                .addParameter("waste_type", wasteEntity.getWaste_type())
                .executeUpdate()
                .getKey(Long.class);
            wasteEntity.setId(id);
            return wasteEntity;
        } catch (Sql2oException e) {
            System.err.println("Error al insertar el waste: " + e.getMessage());
            throw new RuntimeException("No se pudo crear el waste");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<WasteEntity> GetAllWastes() {
        String sql = "SELECT * FROM waste";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                .executeAndFetch(WasteEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener los wastes");
            throw new RuntimeException("No se pudieron obtener los wastes");
        }
    }

    public WasteEntity GetWasteById(int id) {
        String sql = "SELECT * FROM waste WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(WasteEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener el waste por id");
            throw new RuntimeException("No se pudo obtener el waste por id");
        }
    }

    public void updateWaste(int id, WasteEntity wasteEntity) {
        String sql = "UPDATE waste SET waste_type = :waste_type WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                .addParameter("waste_type", wasteEntity.getWaste_type())
                .addParameter("id", id)
                .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al actualizar el waste: " + e.getMessage());
            throw new RuntimeException("No se pudo actualizar el waste");
        }
    }

    public void deleteWaste(int id) {
        String sql = "DELETE FROM waste WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                .addParameter("id", id)
                .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al eliminar el waste: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar el waste");
        }
    }
}
