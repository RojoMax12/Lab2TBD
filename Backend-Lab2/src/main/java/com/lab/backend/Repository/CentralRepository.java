package com.lab.backend.Repository;

import com.lab.backend.Entities.CentralEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

@Repository
public class CentralRepository {

    private Sql2o sql2o;

    public CentralRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    public CentralEntity createCentral(CentralEntity central) {
        String sql = "Insert into central (name, coord_x, coord_y) values (:name, :coord_x, :coord_y)";
        Connection connection = null;
        try {
            connection = sql2o.open();
            Long id = connection.createQuery(sql, true)
                    .addParameter("name", central.getName())
                    .addParameter("coord_x", central.getCoord_x())
                    .addParameter("coord_y", central.getCoord_y())
                    .executeUpdate()
                    .getKey(Long.class);
            central.setId(id);
            return central;
        } catch (Exception e) {
            System.err.println("Error al insertar central: " + e.getMessage());
            throw new RuntimeException("No se pudo crear la central", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<CentralEntity> getAllCentrals() {
        String sql = "SELECT * FROM central";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(CentralEntity.class);
        }

    }

    public CentralEntity getCentralById(Long id) {
        String sql = "SELECT * FROM central  WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(CentralEntity.class);
        }
        catch (Exception e) {
            System.err.println("Error al obtener central por id: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener central por id", e);
        }
    }

    public void deleteCentral(Long id) {
        String sql = "DELETE FROM central WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al eliminar central: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar la central", e);
        }
    }

    public void updateCentral(Long id ,CentralEntity central) {
        String sql = "UPDATE central SET name = :name, coord_x = :coord_x, coord_y = :coord_y WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("name", central.getName())
                    .addParameter("coord_x", central.getCoord_x())
                    .addParameter("coord_y", central.getCoord_y())
                    .addParameter("id", central.getId())
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al actualizar central: " + e.getMessage());
            throw new RuntimeException("No se pudo actualizar la central", e);
        }
    }


}

