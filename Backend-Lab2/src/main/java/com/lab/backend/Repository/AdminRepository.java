package com.lab.backend.Repository;

import com.lab.backend.Entities.AdminEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.awt.*;
import java.util.List;

@Repository
public class AdminRepository {

    private Sql2o sql2o;

    public AdminRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public AdminEntity CreateAdmin(AdminEntity admin) {
        String sql = "Insert into admin (name, last_name, email, password, role) values (:name, :last_name, :email, :password, :role)";
        Connection connection = null;
        try {
            connection = sql2o.open();
            Long id = connection.createQuery(sql, true)
                    .addParameter("name", admin.getName())
                    .addParameter("last_name", admin.getLast_name())
                    .addParameter("email", admin.getEmail())
                    .addParameter("password", admin.getPassword())
                    .addParameter("role", "admin")
                    .executeUpdate()
                    .getKey(Long.class);
            admin.setId(id);
            return admin;
        }
        catch (Sql2oException e) {
            System.err.println("Error al insertar el admin: " + e.getMessage());
            throw new RuntimeException("No se pudo crear el admin", e);
        }finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<AdminEntity> getAllAdmins() {
        String sql = "select * from admin";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(AdminEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener los admins: ");
            throw new RuntimeException("No se pudo obtener los admins", e);
        }

    }

    public AdminEntity getAdminById(Long id) {
        String sql = "select * from admin where id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(AdminEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener el admin por ID");
            throw new RuntimeException("No se pudo obtener el admin", e);
        }

    }

    public AdminEntity getAdminByEmail(String email) {
        String sql = "select * from admin where email = :email";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("email", email)
                    .executeAndFetchFirst(AdminEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener el admin por email");
            throw new RuntimeException("No se pudo obtener el admin");
        }
    }

    public void deleteAdminById(Long id) {
        String sql = "delete from admin where id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al eliminar el admin por ID");
            throw new RuntimeException("No se pudo eliminar el admin", e);
        }

    }

    public void updateAdmin(Long id, AdminEntity admin) {
        String sql = "UPDATE admin SET name = :name, last_name = :last_name, email = :email, password = :password WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("name", admin.getName())
                    .addParameter("last_name", admin.getLast_name())
                    .addParameter("email", admin.getEmail())
                    .addParameter("password", admin.getPassword())
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al actualizar el admin: ");
            throw new RuntimeException("No se pudo actualizar el admin", e);
        }
    }
}
