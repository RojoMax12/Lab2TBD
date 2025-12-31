package com.lab.backend.Repository;

import com.lab.backend.Entities.ContainerEntity;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;
import java.util.Map;

@Repository
public class ContainerRepository {

    private Sql2o sql2o;

    public ContainerRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    //CRUD Operations

    public ContainerEntity CreateContainer(ContainerEntity containerEntity) {
        String sql = "INSERT INTO container (id_waste, coord_x, coord_y, weight, status) VALUES (:id_waste, :coord_x, :coord_y, :weight, :status)";
        Connection connection = null;
        try {
            connection = sql2o.open();
            Long id = connection.createQuery(sql, true)
                    .addParameter("id_waste", containerEntity.getId_waste())
                    .addParameter("coord_x", containerEntity.getCoord_x())
                    .addParameter("coord_y", containerEntity.getCoord_y())
                    .addParameter("weight", containerEntity.getWeight())
                    .addParameter("status", containerEntity.getStatus())
                    .executeUpdate()
                    .getKey(Long.class);
            containerEntity.setId(id);
            return containerEntity;
        } catch (Exception e) {
            System.err.println("Error al insertar el container");
            throw new RuntimeException("No se pudo crear el container");
        } finally {
            if (connection != null) {
                connection.close();
            }



        }
    }

    public Void DeleteContainer(Long id) {
        String sql = "DELETE FROM container WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
            return null;
        } catch (Exception e) {
            System.err.println("Error al eliminar el container");
            throw new RuntimeException("No se pudo eliminar el container");
        }
    }

    public Void UpdateContainer(Long id, ContainerEntity containerEntity) {
        String sql = "UPDATE container SET id_waste = :id_waste, coord_x = :coord_x, coord_y = :coord_y, weight = :weight, status = :status WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            connection.createQuery(sql)
                    .addParameter("id_waste", containerEntity.getId_waste())
                    .addParameter("coord_x", containerEntity.getCoord_x())
                    .addParameter("coord_y", containerEntity.getCoord_y())
                    .addParameter("weight", containerEntity.getWeight())
                    .addParameter("status", containerEntity.getStatus())
                    .addParameter("id", containerEntity.getId())
                    .executeUpdate();
            return null;
        } catch (Exception e) {
            System.err.println("Error al actualizar el container");
            throw new RuntimeException("No se pudo actualizar el container");
        }
    }

    public List<ContainerEntity> GetAllContainers() {
        String sql = "SELECT * FROM container";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .executeAndFetch(ContainerEntity.class);
        } catch (Exception e) {
            System.err.println("Error al obtener los containers");
            throw new RuntimeException("No se pudieron obtener los containers");
        }
    }

    public ContainerEntity GetContainerById(Long id) {
        String sql = "SELECT * FROM container WHERE id = :id";
        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(ContainerEntity.class);
        } catch (Exception e) {
            System.err.println("Error al obtener el container por ID");
            throw new RuntimeException("No se pudo obtener el container por ID");
        }
    }

    //Sentencia 2: Obtiene los contenedores con problemas; es decir, aquellos que han sido recogidos por más de 3 conductores diferentes en el último año.
    //Entrada: -
    //Salida: Lista con id_container y el conteo de conductores únicos para cada contenedor problemático.

    public List<Map<String, Object>> getProblematicContainers() {
        String sql = """
            WITH PickUpsLastYear AS (
                SELECT
                    id_container,
                    id_route
                FROM pickup
                WHERE date_hour >= NOW() - INTERVAL '1 year'
            )
            SELECT
                py.id_container,
                COUNT(DISTINCT re.id_driver) AS driverCount
            FROM PickUpsLastYear AS py
            JOIN route AS re
            ON py.id_route = re.id
            GROUP BY py.id_container
            HAVING
                COUNT(DISTINCT re.id_driver) > 3;
        """;

        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql).executeAndFetchTable().asList();
        } catch (Exception e) {
            System.err.println("Error al obtener datos de contenedores: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener la información", e);
        }
    }


    //Explicación en palabras de la sentencia SQL:
    /*
    1. Se crea la CTE PickUpsLastYear que es una tabla con id_container e id_route de la tabla pick_up_entity, filtrando solo las recolecciones de los últimos 12 meses.
    2. Entre las tablas PickUpsLastYear y route_entity, se hace una unión (JOIN) usando id_route para obtener id_driver.
    3. Se agrupan los resultados por id_container.
    4. Se cuenta el número de conductores únicos (DISTINCT id_driver) para cada contenedor.
    5. Finalmente, se filtran los resultados para incluir solo aquellos contenedores que han sido recogidos por más de 3 conductores diferentes en el último año.

    Esto se muestra con el SELECT de la línea 57 que muestra el id_container y el conteo final de conductores únicos como driverCount.
     */


    //Sentencia 5: Análisis mensual de la densidad de contenedores
    //Entrada: -
    //Salida: Lista con mes, promedio de contenedores por ruta (densidad) y la diferencia con el mes anterior.
    public List<Map<String, Object>> getMonthlyContainerDensityAnalysis() {
        String sql = """
            WITH MonthlyDensity AS (
                SELECT
                    DATE_TRUNC('month', pu.date_hour) AS month_start_date,
                    COUNT(pu.id_container) AS containers_per_month,
                    COUNT(DISTINCT pu.id_route) AS total_routes
                FROM pickup AS pu
                WHERE pu.date_hour >= NOW() - INTERVAL '12 months'
                GROUP BY month_start_date
            ),
            MonthlyAverage AS (
                SELECT
                    month_start_date,
                    (CAST(containers_per_month AS numeric) / total_routes) AS average_containers_per_route
                FROM MonthlyDensity
            )
            SELECT
                TO_CHAR(ma.month_start_date, 'YYYY-MM') AS month,
                ROUND(ma.average_containers_per_route, 2) AS average_containers,
                ROUND(
                    ma.average_containers_per_route - LAG(ma.average_containers_per_route, 1)
                    OVER (ORDER BY ma.month_start_date), 2
                ) AS diff_vs_prev_month
            FROM MonthlyAverage AS ma
            ORDER BY ma.month_start_date DESC;
        """;

        try (Connection connection = sql2o.open()) {
            return connection.createQuery(sql).executeAndFetchTable().asList();
        } catch (Exception e) {
            System.err.println("Error al calcular la densidad mensual de contenedores: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener el análisis de densidad", e);
        }
    }

    //Explicación en palabras de la sentencia SQL:
    /*
    1. CTE MonthlyDensity:
    - Agrupa las recolecciones por mes (month_start_date).
    - Cuenta el número total de contenedores recogidos (containers_per_month) y el número total de rutas únicas (total_routes) para cada mes en los últimos 12 meses.
    2. CTE MonthlyAverage:
    - Calcula el promedio de contenedores por ruta para cada mes dividiendo containers_per_month por total_routes.
    3. Consulta final:
    - Selecciona el mes formateado como 'YYYY-MM', el promedio de contenedores por ruta redondeado a 2 decimales (average_containers).
    - Calcula la diferencia con el mes anterior usando la función LAG() y redondea el resultado a 2 decimales (diff_vs_prev_month).
    - Ordena los resultados por mes en orden descendente para mostrar primero los meses más recientes.
     */

    //FUNCION DE VENTANA: LAG()
    /* Se utiliza para acceder al valor del promedio de contenedores del mes anterior sin necesidad de hacer una auto-unión (self-join) en la tabla.
       La función LAG() toma dos argumentos principales: la columna de la cual se quiere obtener el valor previo (en este caso, average_containers_per_route)
       y el número de filas hacia atrás desde la fila actual (1 en este caso, para obtener el mes anterior).
       La cláusula OVER (ORDER BY ma.month_start_date) define el orden en el que se aplicará la función LAG(), asegurando que se compare con el mes inmediatamente anterior.
     */


    public List<Map<String, Object>> getContenedoresSinRecoleccionReciente() {
        String sql = """
            SELECT
                c.id AS id_contenedor,
                w.waste_type AS tipo_residuo,
                c.coord_x,
                c.coord_y,
                COALESCE(TO_CHAR(MAX(p.date_hour), 'YYYY-MM-DD HH24:MI:SS'), 'Nunca recolectado') AS ultima_recoleccion
            FROM container c
            LEFT JOIN waste w ON c.id_waste = w.id
            LEFT JOIN pickup p ON c.id = p.id_container
            GROUP BY c.id, w.waste_type, c.coord_x, c.coord_y
            HAVING
                MAX(p.date_hour) IS NULL
                OR MAX(p.date_hour) < (NOW() - INTERVAL '90 days')
            ORDER BY ultima_recoleccion DESC NULLS LAST;
        """;

        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        } catch (Exception e) {
            System.err.println("Error al obtener contenedores sin recolección reciente: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener la lista de contenedores sin recolección reciente", e);
        }
    }

}
