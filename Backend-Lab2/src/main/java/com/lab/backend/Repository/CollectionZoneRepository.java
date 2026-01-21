package com.lab.backend.Repository;

import com.lab.backend.Entities.CollectionZoneEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

@Repository
public class CollectionZoneRepository {

    private Sql2o sql2o;

    public CollectionZoneRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public CollectionZoneEntity CreateCollectionZone(CollectionZoneEntity cz) {
        String sql = "INSERT INTO collection_zone (name, location) VALUES (:name, ST_GeomFromText(:location, 4326))";
        try (Connection connection = sql2o.open()) {
            Long id = connection.createQuery(sql, true)
                    .addParameter("name", cz.getName())
                    .addParameter("location", cz.getLocation())
                    .executeUpdate()
                    .getKey(Long.class);
            cz.setId(id);
            return cz;
        } catch (Exception e) {
            System.err.println("Error al insertar collection_zone: " + e.getMessage());
            throw new RuntimeException("No se pudo crear la zona de recolección", e);
        }
    }

    public List<CollectionZoneEntity> GetAllCollectionZones() {
        String sql = "SELECT id, name, ST_AsText(location) as location FROM collection_zone";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql).executeAndFetch(CollectionZoneEntity.class);
        } catch (Exception e) {
            System.err.println("Error al obtener collection_zones: " + e.getMessage());
            throw new RuntimeException("No se pudieron obtener las zonas de recolección", e);
        }
    }

    public CollectionZoneEntity GetCollectionZoneById(Long id) {
        String sql = "SELECT id, name, ST_AsText(location) as location FROM collection_zone WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(CollectionZoneEntity.class);
        } catch (Exception e) {
            System.err.println("Error al obtener collection_zone por ID: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener la zona de recolección", e);
        }
    }

    public Void UpdateCollectionZone(Long id, CollectionZoneEntity cz) {
        String sql = "UPDATE collection_zone SET name = :name, location = ST_GeomFromText(:location, 4326) WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("name", cz.getName())
                    .addParameter("location", cz.getLocation())
                    .addParameter("id", id)
                    .executeUpdate();
            return null;
        } catch (Exception e) {
            System.err.println("Error al actualizar collection_zone: " + e.getMessage());
            throw new RuntimeException("No se pudo actualizar la zona de recolección", e);
        }
    }

    public Void DeleteCollectionZone(Long id) {
        String sql = "DELETE FROM collection_zone WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
            return null;
        } catch (Exception e) {
            System.err.println("Error al eliminar collection_zone: " + e.getMessage());
            throw new RuntimeException("No se pudo eliminar la zona de recolección", e);
        }
    }
}
