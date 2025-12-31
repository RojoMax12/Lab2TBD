package com.lab.backend.Repository;

import com.lab.backend.Entities.PickUpEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

@Repository
public class PickUpRepository {

    private Sql2o sql2o;

    public PickUpRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    //CRUD Operations

    public PickUpEntity CreatePickUp(PickUpEntity pickUpEntity) {
        // Use COALESCE to fall back to NOW() when date_hour is null
        String sql = "INSERT INTO pickup (id_container, id_route, date_hour) VALUES (:id_container, :id_route, COALESCE(:date_hour, NOW()))";
        Connection connection = null;
        try {
            connection = sql2o.open();
            Long id = connection.createQuery(sql, true)
                .addParameter("id_container", pickUpEntity.getId_container())
                .addParameter("id_route", pickUpEntity.getId_route())
                .addParameter("date_hour", pickUpEntity.getDate_hour())
                .executeUpdate()
                .getKey(Long.class);
            pickUpEntity.setId(id);
            return pickUpEntity;
        } catch (Sql2oException e) {
            System.err.println("Error al insertar el pick up: " + e.getMessage());
            throw new RuntimeException("No se pudo crear el pick up: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<PickUpEntity> getAllPickUps() {
        String sql = "SELECT * FROM pickup";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                .executeAndFetch(PickUpEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener los pick ups");
            throw new RuntimeException("No se pudieron obtener los pick ups");
        }
    }

    public PickUpEntity getPickUpById(Long id) {
        String sql = "SELECT * FROM pickup WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(PickUpEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener el pick up por id");
            throw new RuntimeException("No se pudo obtener el pick up");
        }
    }

    public void updatePickUp(Long id, PickUpEntity pickUpEntity) {
        String sql = "UPDATE pickup SET id_container = :id_container, id_route = :id_route, date_hour = :date_hour WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                .addParameter("id_container", pickUpEntity.getId_container())
                .addParameter("id_route", pickUpEntity.getId_route())
                .addParameter("date_hour", pickUpEntity.getDate_hour())
                .addParameter("id", id)
                .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al actualizar el pick up: " + e.getMessage());
            throw new RuntimeException("No se pudo actualizar el pick up");
        }
    }

    public void deletePickUp(Long id) {
        String sql = "DELETE FROM pickup WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                .addParameter("id", id)
                .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al eliminar el pick up: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar el pick up");
        }
    }
}
