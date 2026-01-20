-- =====================================
-- EXTENSIÓN POSTGIS
-- =====================================
CREATE EXTENSION IF NOT EXISTS postgis;

-- =====================================
-- CREACIÓN DE TABLAS
-- =====================================
CREATE TABLE central (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         location GEOMETRY(POINT, 4326)
);

CREATE TABLE waste (
                       id BIGSERIAL PRIMARY KEY,
                       waste_type VARCHAR(100) NOT NULL
);

CREATE TABLE container (
                           id BIGSERIAL PRIMARY KEY,
                           id_waste BIGINT REFERENCES waste(id) ON DELETE CASCADE,
                           location GEOMETRY(POINT, 4326),
                           weight REAL,
                           status VARCHAR(50)
);

CREATE TABLE driver (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        email VARCHAR(150) UNIQUE NOT NULL,
                        password VARCHAR(150) NOT NULL,
                        role VARCHAR(50) NOT NULL
);

CREATE TABLE admin (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        email VARCHAR(150) UNIQUE NOT NULL,
                        password VARCHAR(150) NOT NULL,
                        role VARCHAR(50) NOT NULL
);

CREATE TABLE route (
                       id BIGSERIAL PRIMARY KEY,
                       id_driver BIGINT REFERENCES driver(id) ON DELETE SET NULL,
                       date_ DATE NOT NULL,
                       start_time TIME,
                       end_time TIME,
                       route_status VARCHAR(50) NOT NULL,
                       id_central BIGINT REFERENCES central(id) ON DELETE SET NULL,
                       id_central_finish BIGINT REFERENCES central(id) ON DELETE SET NULL,
					   trayecto GEOMETRY(LineString, 4326)
);

CREATE TABLE route_container (
                               id BIGSERIAL PRIMARY KEY,
                               id_route BIGINT REFERENCES route(id) ON DELETE CASCADE,
                               id_container BIGINT REFERENCES container(id) ON DELETE CASCADE
);

CREATE TABLE pickup (
                        id BIGSERIAL PRIMARY KEY,
                        id_container BIGINT REFERENCES container(id) ON DELETE CASCADE,
                        id_route BIGINT REFERENCES route(id) ON DELETE CASCADE,
                        date_hour TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE collection_zone (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location GEOMETRY(POLYGON, 4326)
);

-- =====================================
-- POBLACIÓN DE DATOS INICIALES
-- =====================================

-- Centrales
INSERT INTO central (name, location) VALUES 
    ('Central Norte', ST_SetSRID(ST_MakePoint(-70.6693, -33.4489), 4326)), 
    ('Central Sur', ST_SetSRID(ST_MakePoint(-70.6483, -33.4843), 4326));   

-- Ejemplo de Zona de Recolección (Polígono)
INSERT INTO collection_zone (name, location) VALUES
	('Sector Centro', ST_GeomFromText('POLYGON((10 20, 20 20, 20 30, 10 30, 10 20))', 4326));

-- Tipos de residuos
INSERT INTO waste (waste_type) VALUES
                                   ('Plástico'),
                                   ('Vidrio'),
                                   ('Orgánico');

-- Contenedores
INSERT INTO container (id_waste, location, weight, status) VALUES
    (1, ST_SetSRID(ST_MakePoint(-70.6539, -33.4429), 4326), 50.0, 'Disponible'), -- ID 1: Stgo Centro (cerca de La Moneda)
    (2, ST_SetSRID(ST_MakePoint(-70.6150, -33.4310), 4326), 70.5, 'Disponible'), -- ID 2: Providencia (cerca de Manuel Montt)
    (3, ST_SetSRID(ST_MakePoint(-70.6914, -33.4550), 4326), 60.0, 'Disponible'), -- ID 3: Estación Central
    (1, ST_SetSRID(ST_MakePoint(-70.6030, -33.4580), 4326), 55.5, 'Disponible'), -- ID 4: Ñuñoa (Plaza Ñuñoa)
    (2, ST_SetSRID(ST_MakePoint(-70.6400, -33.4150), 4326), 58.0, 'Disponible'), -- ID 5: Recoleta
    (3, ST_SetSRID(ST_MakePoint(-70.6520, -33.4900), 4326), 62.0, 'Disponible'), -- ID 6: San Miguel
    (1, ST_SetSRID(ST_MakePoint(-70.5980, -33.4850), 4326), 59.0, 'Disponible'), -- ID 7: Macul
    (2, ST_SetSRID(ST_MakePoint(-70.5750, -33.4100), 4326), 70.0, 'Disponible'), -- ID 8: Las Condes (Escuela Militar)
    (3, ST_SetSRID(ST_MakePoint(-70.5890, -33.5200), 4326), 65.0, 'Disponible'), -- ID 9: La Florida
    (1, ST_SetSRID(ST_MakePoint(-70.7550, -33.5110), 4326), 68.0, 'Disponible'); -- ID 10: Maipú

-- Conductores
INSERT INTO driver (name, last_name, email, password, role) VALUES
                                                          ('Juan', 'Pérez', 'juan.perez@mail.com', '1234', 'driver'),
                                                          ('Ana', 'Gómez', 'ana.gomez@mail.com', 'abcd', 'driver'),
                                                        ('Pedro', 'Ramírez', 'pedro.ramirez@mail.com', '1234', 'driver'),
                                                        ('Lucía', 'Torres', 'lucia.torres@mail.com', '1234', 'driver'),
                                                        ('Mario', 'Soto', 'mario.soto@mail.com', '1234', 'driver'),
                                                        ('Elena', 'Castro', 'elena.castro@mail.com', '1234', 'driver');

-- Administradores
INSERT INTO admin (name, last_name, email, password, role) VALUES
                                                        ('Carlos', 'López', 'carlos.lopez@mail.com', 'pass123', 'admin');

-- Rutas
INSERT INTO route (id_driver, date_, start_time, end_time, route_status, id_central, id_central_finish) VALUES
                                                                                                           (1, '2025-10-20', '07:00:00', '09:30:00', 'Finalizada', 1, 1),   -- 2.5h
                                                                                                           (1, '2025-10-20', '08:00:00', '10:00:00', 'Finalizada', 1, 2),   -- 2h
                                                                                                           (2, '2025-10-20', '09:00:00', '11:00:00', 'Finalizada', 2, 1),   -- 2h
                                                                                                           (2, '2025-10-20', '06:00:00', '08:00:00', 'Finalizada', 2, 2),   -- 2h
                                                                                                           (1, '2025-10-20', '07:30:00', '09:30:00', 'Finalizada', 1, 1),   -- 2h
                                                                                                           (2, '2025-10-20', '08:00:00', '11:00:00', 'Finalizada', 2, 1),   -- 3h
                                                                                                           (1, '2025-10-20', '06:30:00', '08:30:00', 'Finalizada', 1, 2),   -- 2h
                                                                                                           (2, '2025-10-20', '10:00:00', '12:30:00', 'Finalizada', 2, 2),   -- 2.5h
                                                                                                           (1, '2025-10-20', '09:00:00', '12:00:00', 'Finalizada', 1, 1),   -- 3h
                                                                                                           (2, '2025-10-20', '07:00:00', '09:30:00', 'Finalizada', 2, 1),   -- 2.5h
                                                                                                           (1, '2025-10-20', '08:00:00', '08:40:00', 'Finalizada', 1, 1),   -- 0.67h
                                                                                                           (2, '2025-10-20', '07:00:00', '07:45:00', 'Finalizada', 2, 1),   -- 0.75h
                                                                                                           (1, '2025-10-20', '06:00:00', '06:30:00', 'Finalizada', 1, 2),   -- 0.5h
                                                                                                           (2, '2025-10-20', '09:30:00', '10:10:00', 'Finalizada', 2, 2),   -- 0.67h
                                                                                                           (1, '2025-10-20', '05:30:00', '06:15:00', 'Finalizada', 1, 1),   -- 0.75h
                                                                                                           (2, '2025-10-20', '10:15:00', '10:45:00', 'Finalizada', 2, 2),   -- 0.5h
                                                                                                           (1, '2025-10-20', '07:00:00', '11:00:00', 'Finalizada', 1, 2),   -- 4h
                                                                                                           (2, '2025-10-20', '08:30:00', '12:30:00', 'Finalizada', 2, 1),   -- 4h
                                                                                                           (1, '2025-10-20', '06:00:00', '09:30:00', 'Finalizada', 1, 1),   -- 3.5h
                                                                                                           (2, '2025-10-20', '05:45:00', '09:15:00', 'Finalizada', 2, 2),
                                                                                                           (1, '2025-11-01', '07:00', '09:00', 'Finalizada', 1, 2),
                                                                                                            (2, '2025-11-02', '08:00', '10:00', 'Finalizada', 1, 2),
                                                                                                            (3, '2025-11-03', '09:00', '11:00', 'Finalizada', 1, 2),
                                                                                                            (4, '2025-11-04', '10:00', '12:00', 'Finalizada', 1, 2),
                                                                                                            (5, '2025-11-05', '07:00', '09:00', 'Finalizada', 1, 2),
                                                                                                            (6, '2025-11-06', '08:00', '10:00', 'Finalizada', 1, 2),
                                                                                                            (3, '2025-11-01', '07:00:00', '09:00:00', 'Finalizada', 1, 2),
                                                                                                            (4, '2025-11-02', '08:00:00', '10:00:00', 'Finalizada', 2, 1),
                                                                                                            (5, '2025-11-03', '06:30:00', '09:00:00', 'Finalizada', 1, 2),
                                                                                                            (6, '2025-11-04', '07:15:00', '09:45:00', 'Finalizada', 2, 1); -- 3.5h

-- Pickup
INSERT INTO pickup (id_container, id_route, date_hour) VALUES
                                                           (1,1,'2025-10-20 07:20:00'),
                                                           (2,1,'2025-10-20 08:00:00'),
                                                           (3,1,'2025-10-20 09:00:00'),
                                                           (1,2,'2025-10-20 08:30:00'),
                                                           (2,2,'2025-10-20 09:20:00'),
                                                           (3,2,'2025-10-20 09:45:00'),
                                                           (1,3,'2025-10-20 09:30:00'),
                                                           (2,3,'2025-10-20 10:00:00'),
                                                           (3,3,'2025-10-20 10:45:00'),
                                                           (4,3,'2025-10-20 10:55:00'),
                                                           (1,4,'2025-10-20 06:30:00'),
                                                           (2,4,'2025-10-20 07:10:00'),
                                                           (3,4,'2025-10-20 07:45:00'),
                                                           (1,5,'2025-10-20 07:45:00'),
                                                           (2,5,'2025-10-20 08:30:00'),
                                                           (1,6,'2025-10-20 09:00:00'),
                                                           (2,6,'2025-10-20 09:30:00'),
                                                           (3,6,'2025-10-20 10:30:00'),
                                                           (4,6,'2025-10-20 10:45:00'),
                                                           (1,7,'2025-10-20 06:45:00'),
                                                           (2,7,'2025-10-20 07:15:00'),
                                                           (3,7,'2025-10-20 08:15:00'),
                                                           (1,8,'2025-10-20 10:15:00'),
                                                           (2,8,'2025-10-20 11:00:00'),
                                                           (3,8,'2025-10-20 12:00:00'),
                                                           (1,9,'2025-10-20 09:30:00'),
                                                           (2,9,'2025-10-20 10:15:00'),
                                                           (3,9,'2025-10-20 11:30:00'),
                                                           (1,10,'2025-10-20 07:45:00'),
                                                           (2,10,'2025-10-20 08:30:00'),
                                                           (1,11,'2025-10-20 08:05:00'),
                                                           (2,11,'2025-10-20 08:10:00'),
                                                           (3,11,'2025-10-20 08:15:00'),
                                                           (4,11,'2025-10-20 08:25:00'),
                                                           (5,11,'2025-10-20 08:30:00'),
                                                           (1,12,'2025-10-20 07:05:00'),
                                                           (2,12,'2025-10-20 07:15:00'),
                                                           (3,12,'2025-10-20 07:25:00'),
                                                           (4,12,'2025-10-20 07:40:00'),
                                                           (1,13,'2025-10-20 06:05:00'),
                                                           (2,13,'2025-10-20 06:10:00'),
                                                           (3,13,'2025-10-20 06:20:00'),
                                                           (4,13,'2025-10-20 06:25:00'),
                                                           (5,13,'2025-10-20 06:28:00'),
                                                           (1,14,'2025-10-20 09:35:00'),
                                                           (2,14,'2025-10-20 09:40:00'),
                                                           (3,14,'2025-10-20 09:50:00'),
                                                           (4,14,'2025-10-20 09:55:00'),
                                                           (1,15,'2025-10-20 05:35:00'),
                                                           (2,15,'2025-10-20 05:45:00'),
                                                           (3,15,'2025-10-20 06:00:00'),
                                                           (4,15,'2025-10-20 06:10:00'),
                                                           (1,16,'2025-10-20 10:20:00'),
                                                           (2,16,'2025-10-20 10:25:00'),
                                                           (3,16,'2025-10-20 10:30:00'),
                                                           (4,16,'2025-10-20 10:40:00'),
                                                           (5,16,'2025-10-20 10:45:00'),
                                                           (1,17,'2025-10-20 07:30:00'),
                                                           (2,17,'2025-10-20 08:00:00'),
                                                           (3,17,'2025-10-20 09:00:00'),
                                                           (1,18,'2025-10-20 09:00:00'),
                                                           (2,18,'2025-10-20 10:00:00'),
                                                           (3,18,'2025-10-20 11:00:00'),
                                                           (1,19,'2025-10-20 06:45:00'),
                                                           (2,19,'2025-10-20 08:00:00'),
                                                           (3,19,'2025-10-20 09:00:00'),
                                                           (1,20,'2025-10-20 06:00:00'),
                                                           (2,20,'2025-10-20 07:30:00'),
                                                           (3,20,'2025-10-20 09:00:00'),
                                                           (1, 2, '2025-09-15 09:00:00'),
                                                            (2, 3, '2025-09-18 10:00:00'),
                                                            (3, 4, '2025-08-20 11:00:00'),
                                                            (1, 5, '2025-11-02 07:00:00'),
                                                            (1, 21, '2025-11-01 07:30:00'), -- ruta de Pedro
                                                            (1, 22, '2025-11-02 08:30:00'), -- ruta de Lucía
                                                            (1, 23, '2025-11-03 07:45:00'), -- ruta de Mario
                                                            (1, 24, '2025-11-04 08:15:00'),
                                                            (2, 21, '2025-11-01 07:40:00'),
                                                            (2, 22, '2025-11-02 08:50:00'),
                                                            (3, 21, '2025-11-01 07:50:00'),
                                                            (3, 22, '2025-11-02 09:00:00'),
                                                            (3, 23, '2025-11-03 08:00:00'); -- ruta de Elena -- driver 5



INSERT INTO route_container (id_route, id_container) VALUES
(1, 1),  -- Ruta 1, Contenedor 1
(1, 2),  -- Ruta 1, Contenedor 2
(1, 3),  -- Ruta 1, Contenedor 3
(2, 4),  -- Ruta 2, Contenedor 4
(2, 5),  -- Ruta 2, Contenedor 5
(2, 6),  -- Ruta 2, Contenedor 6
(3, 7),  -- Ruta 3, Contenedor 7
(3, 8),  -- Ruta 3, Contenedor 8
(3, 9),  -- Ruta 3, Contenedor 9
(4, 10); -- Ruta 4, Contenedor 10

-- =========================================================
-- Procedimiento almacenado: actualizar_peso_contenedores
-- =========================================================

CREATE OR REPLACE PROCEDURE actualizar_peso_contenedores(
    p_id_ruta BIGINT,
    p_nuevo_peso DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_nuevo_peso < 0 THEN
        RAISE EXCEPTION 'El peso no puede ser negativo. Se revierte la transacción.';
END IF;

UPDATE container
SET weight = p_nuevo_peso
WHERE id IN (
    SELECT id_container
    FROM pickup
    WHERE id_route = p_id_ruta
);

RAISE NOTICE 'Contenedores de la ruta % actualizados correctamente a peso % kg', p_id_ruta, p_nuevo_peso;
END;
$$;

-- =========================================================
-- Procedimiento almacenado: planificar_ruta
-- =========================================================

CREATE OR REPLACE PROCEDURE planificar_ruta(
    p_contenedores JSON,
    p_id_driver BIGINT,
    p_id_central BIGINT,
    p_id_central_finish BIGINT,
    p_start_time TIME,
    p_end_time TIME
)
LANGUAGE plpgsql
AS $$
DECLARE
    new_route_id BIGINT;
    cur_geom GEOMETRY;
    sel_id BIGINT;
    sel_geom GEOMETRY;
BEGIN
    -- 1. Crear la ruta
    INSERT INTO route (id_driver, date_, start_time, end_time, route_status, id_central, id_central_finish)
    VALUES (p_id_driver, CURRENT_DATE, p_start_time, p_end_time, 'Pendiente', p_id_central, p_id_central_finish)
    RETURNING id INTO new_route_id;

    -- 2. Punto de partida (Geometría de la central)
    SELECT location INTO cur_geom FROM central WHERE id = p_id_central;

    -- 3. Crear tabla temporal con los contenedores solicitados
    CREATE TEMP TABLE tmp_sel ON COMMIT DROP AS
    SELECT id, location FROM container 
    WHERE id IN (SELECT json_array_elements_text(p_contenedores)::BIGINT);

    -- 4. Algoritmo Greedy usando ST_Distance
    WHILE EXISTS (SELECT 1 FROM tmp_sel) LOOP
        SELECT id, location INTO sel_id, sel_geom
        FROM tmp_sel
        ORDER BY location <-> cur_geom -- Operador de distancia indexada (muy rápido)
        LIMIT 1;

        INSERT INTO route_container (id_container, id_route) VALUES (sel_id, new_route_id);
        UPDATE container SET status = 'Ocupado' WHERE id = sel_id;
        
        cur_geom := sel_geom;
        DELETE FROM tmp_sel WHERE id = sel_id;
    END LOOP;

    RAISE NOTICE 'Ruta % planificada con PostGIS.', new_route_id;
END;
$$;

-- =========================================================
-- Ejemplo de uso del procedimiento
-- =========================================================
-- (Asegúrate de haber creado previamente planificar_ruta)

--CALL planificar_ruta('[1,2,3]'::json, 1, 1, 1);



-- Esta sentencia crea una vista materializada que resume el peso total recolectado por día.
-- Migration: crea vista materializada peso_recolectado_diario
-- Esta vista agrega el peso total recolectado por día.

-- La vista se crea con la opción de refresco CONCURRENTLY para permitir actualizaciones concurrentes.
CREATE MATERIALIZED VIEW IF NOT EXISTS peso_recolectado_diario AS
SELECT
    date_trunc('day', p.date_hour)::date AS fecha,
    SUM(c.weight) AS peso_total
FROM pickup p
JOIN container c ON p.id_container = c.id
GROUP BY fecha
ORDER BY fecha;

CREATE UNIQUE INDEX IF NOT EXISTS idx_peso_recolectado_diario_fecha ON peso_recolectado_diario (fecha);


-- =========================================================
-- FUNCIÓN DEL TRIGGER: Actualiza el estado del contenedor
-- =========================================================
CREATE OR REPLACE FUNCTION actualizar_estado_contenedor_func()
RETURNS TRIGGER AS $$
BEGIN
    -- Actualiza el estado del contenedor a 'Recogido' (o similar)
    -- usando el ID del contenedor insertado en la tabla pickup (NEW.id_container)
    UPDATE container
    SET status = 'Recogido' -- O 'Vaciado' / 'En tránsito', según la lógica
    WHERE id = NEW.id_container;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- =========================================================
-- TRIGGER: Se dispara después de una inserción en 'pickup'
-- =========================================================
CREATE OR REPLACE TRIGGER tr_actualizar_estado_contenedor
AFTER INSERT ON pickup
FOR EACH ROW
EXECUTE FUNCTION actualizar_estado_contenedor_func();

-- =========================================================
-- ✅ FIN DEL SCRIPT
-- =========================================================