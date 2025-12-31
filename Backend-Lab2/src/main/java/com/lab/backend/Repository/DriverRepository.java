package com.lab.backend.Repository;

import com.lab.backend.Entities.DriverEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

@Repository
public class DriverRepository {

    private Sql2o sql2o;

    public DriverRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    //CRUD Operations

    public DriverEntity CreateDriver(DriverEntity driverEntity) {
        // Let the database generate the id (serial). Do not include id in INSERT.
        String sql = "INSERT INTO driver (name, last_name, email, password, role) VALUES (:name, :last_name, :email, :password, :role   )";
        Connection connection = null;
        try {
            connection = sql2o.open();
            Long id = connection.createQuery(sql, true)
                .addParameter("name", driverEntity.getName())
                .addParameter("last_name", driverEntity.getLast_name())
                .addParameter("email", driverEntity.getEmail())
                .addParameter("password", driverEntity.getPassword())
                .addParameter("role", "driver")
                .executeUpdate()
                .getKey(Long.class);
            driverEntity.setId(id);
            return driverEntity;
        } catch (Sql2oException e) {
            System.err.println("Error al insertar el driver: " + e.getMessage());
            throw new RuntimeException("No se pudo crear el driver");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<DriverEntity> getAllDrivers() {
        String sql = "SELECT * FROM driver";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                .executeAndFetch(DriverEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener los drivers");
            throw new RuntimeException("No se pudieron obtener los drivers");
        }
    }

    public DriverEntity getDriverById(Long id) {
        String sql = "SELECT * FROM driver WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(DriverEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener el driver por ID");
            throw new RuntimeException("No se pudo obtener el driver");
        }
    }

    public DriverEntity getDriverByEmail(String email) {
        String sql = "SELECT * FROM driver WHERE email = :email";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                .addParameter("email", email)
                .executeAndFetchFirst(DriverEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener el driver por email");
            throw new RuntimeException("No se pudo obtener el driver");
        }
    }

    public void updateDriver(Long id, DriverEntity driverEntity) {
        String sql = "UPDATE driver SET name = :name, last_name = :last_name, email = :email, password = :password WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("name", driverEntity.getName())
                    .addParameter("last_name", driverEntity.getLast_name())
                    .addParameter("email", driverEntity.getEmail())
                    .addParameter("password", driverEntity.getPassword())
                    .addParameter("id", id)  // Aquí añades el parámetro 'id'
                    .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al actualizar el driver");
            throw new RuntimeException("No se pudo actualizar el driver", e);
        }
    }


    public void deleteDriver(Long id) {
        String sql = "DELETE FROM driver WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                .addParameter("id", id)
                .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al eliminar el driver");
            throw new RuntimeException("No se pudo eliminar el driver");
        }
    }
}
