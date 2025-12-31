package com.lab.backend.Repository;

import com.lab.backend.Entities.Route_ContainerEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

@Repository
public class Route_ContainerRepository {

    private final Sql2o sql2o;

    public Route_ContainerRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    // Crear nuevo Route_Container
    public Route_ContainerEntity createRouteContainer(Route_ContainerEntity routeContainerEntity) {
        String sql = "INSERT INTO route_container (container_id, route_id) VALUES (:container_id, :route_id)";
        try (Connection connection = sql2o.open()) {
            Long id = connection.createQuery(sql, true)
                    .addParameter("container_id", routeContainerEntity.getId_container())
                    .addParameter("route_id", routeContainerEntity.getId_route())
                    .executeUpdate()
                    .getKey(Long.class);
            routeContainerEntity.setId(id);
            return routeContainerEntity;
        } catch (Sql2oException e) {
            System.err.println("Error al insertar el route_container: " + e.getMessage());
            throw new RuntimeException("No se pudo crear el route_container");
        }
    }

    // Obtener todos los Route_Container
    public List<Route_ContainerEntity> getAllRouteContainers() {
        String sql = "SELECT * FROM route_container";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(com.lab.backend.Entities.Route_ContainerEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener los route_containers");
            throw new RuntimeException("No se pudieron obtener los route_containers");
        }
    }

    // Obtener Route_Container por ID
    public Route_ContainerEntity getRouteContainerById(Long id) {
        String sql = "SELECT * FROM route_container WHERE route_container_id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(com.lab.backend.Entities.Route_ContainerEntity.class);
        } catch (Sql2oException e) {
            System.err.println("Error al obtener el route_container por id");
            throw new RuntimeException("No se pudo obtener el route_container");
        }
    }

    public List<Route_ContainerEntity> getRouteContainersByRouteId(Long routeId) {
        String sql = """
        SELECT 
            id,
            id_route,
            id_container
        FROM route_container
        WHERE id_route = :id_route
    """;

        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id_route", routeId)
                    .executeAndFetch(Route_ContainerEntity.class);
        } catch (Sql2oException e) {
            System.err.println("❌ Error al obtener contenedores por id de ruta: " + e.getMessage());
            throw new RuntimeException("No se pudieron obtener los contenedores de la ruta " + routeId, e);
        }
    }

    // Eliminar Route_Container
    public void deleteRouteContainer(Long id) {
        String sql = "DELETE FROM route_container WHERE route_container_id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException e) {
            System.err.println("Error al eliminar el route_container: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar el route_container");
        }
    }

    public List<Route_ContainerEntity> getRouteContainerByContainerId(Long containerId) {
        String sql = """
        SELECT * 
        FROM route_container
        WHERE id_container = :containerId
    """;

        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("containerId", containerId)
                    .executeAndFetch(Route_ContainerEntity.class);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener rutas por id de contenedor: " + e.getMessage());
            throw new RuntimeException("No se pudieron obtener las rutas asociadas al contenedor " + containerId, e);
        }
    }

}
