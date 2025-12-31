
CREATE TABLE central (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         coord_x REAL NOT NULL,
                         coord_y REAL NOT NULL
);

CREATE TABLE waste (
                       id BIGSERIAL PRIMARY KEY,
                       waste_type VARCHAR(100) NOT NULL
);

CREATE TABLE container (
                           id BIGSERIAL PRIMARY KEY,
                           id_waste BIGINT REFERENCES waste(id) ON DELETE CASCADE,
                           coord_x REAL NOT NULL,
                           coord_y REAL NOT NULL,
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
                       id_central_finish BIGINT REFERENCES central(id) ON DELETE SET NULL
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

-- =========================================================
--  Insertar datos iniciales (para pruebas)
-- =========================================================

-- Centrales
INSERT INTO central (name, coord_x, coord_y) VALUES
                                                 ('Central Norte', 10.5, 20.2),
                                                 ('Central Sur', 30.1, 40.8);

-- Tipos de residuos
INSERT INTO waste (waste_type) VALUES
                                   ('Plástico'),
                                   ('Vidrio'),
                                   ('Orgánico');

-- Contenedores
INSERT INTO container (id_waste, coord_x, coord_y, weight, status) VALUES
                                                                       (1, 12.3, 22.1, 50.0, 'Disponible'),
                                                                       (2, 14.8, 25.7, 70.5, 'Disponible'),
                                                                       (3, 15.2, 27.0, 60.0, 'Disponible'),
                                                                       (1, 16.0, 28.5, 55.5, 'Disponible'),
                                                                       (2, 17.5, 29.0, 58.0, 'Disponible'),
                                                                       (3, 18.0, 30.0, 62.0, 'Disponible'),
                                                                       (1, 19.0, 31.0, 59.0, 'Disponible'),
                                                                       (2, 20.0, 32.0, 70.0, 'Disponible'),
                                                                       (3, 21.0, 33.0, 65.0, 'Disponible'),
                                                                       (1, 22.0, 34.0, 68.0, 'Disponible');

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
    p_id_central_finish BIGINT,  -- Parámetro para la central de finalización
    p_start_time TIME,           -- Hora de inicio como parámetro
    p_end_time TIME              -- Hora de fin como parámetro
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_contenedor_id BIGINT;
    v_error BOOLEAN := FALSE;
    rec RECORD;
    new_route_id BIGINT;
    contenedores_jsonb JSONB := p_contenedores::JSONB;  -- Convertir JSON a JSONB
    contenedor_count INT;  -- Variable para contar los contenedores

    -- Variables para orden greedy
    cur_x DOUBLE PRECISION := 0.0;
    cur_y DOUBLE PRECISION := 0.0;
    sel_id BIGINT;
    sel_x DOUBLE PRECISION;
    sel_y DOUBLE PRECISION;
    i INT;

BEGIN
    -- Contar los elementos en el JSONB
    contenedor_count := jsonb_array_length(contenedores_jsonb);

    IF contenedor_count = 0 THEN
        RAISE EXCEPTION 'Error: lista de contenedores vacía';
    END IF;

    -- Validar existencia y disponibilidad de los contenedores
    FOR rec IN SELECT * FROM jsonb_array_elements_text(contenedores_jsonb) AS contenedor(id)
    LOOP
        -- Convertir rec.id a BIGINT y verificar que el contenedor existe y no está asignado a una ruta pendiente
        SELECT COUNT(*) INTO v_contenedor_id
        FROM container
        WHERE id = rec.id::BIGINT
        AND NOT EXISTS (
            SELECT 1
            FROM route_container rc
            JOIN route r ON r.id = rc.id_route
            WHERE r.route_status = 'Pendiente'
            AND rc.id_container = rec.id::BIGINT
        );

        -- Si el contenedor no existe o está asignado a una ruta pendiente
        IF v_contenedor_id = 0 THEN
            v_error := TRUE;
        END IF;
    END LOOP;

    -- Si hay un error, lanzar una excepción
    IF v_error THEN
        RAISE EXCEPTION 'Error: uno o más contenedores no existen o están asignados a una ruta pendiente';
    END IF;

    -- Crear tabla temporal para almacenar contenedores con coordenadas y estado de selección
    CREATE TEMP TABLE tmp_route_containers (
        id BIGINT PRIMARY KEY,
        coord_x DOUBLE PRECISION,
        coord_y DOUBLE PRECISION,
        selected BOOLEAN DEFAULT FALSE
    ) ON COMMIT DROP;

    -- Rellenar la tabla temporal con los contenedores provistos
    INSERT INTO tmp_route_containers (id, coord_x, coord_y)
    SELECT contenedor.id::BIGINT, c.coord_x::DOUBLE PRECISION, c.coord_y::DOUBLE PRECISION
    FROM jsonb_array_elements_text(contenedores_jsonb) AS contenedor(id)
    JOIN container c ON c.id = contenedor.id::BIGINT;

    -- Crear la nueva ruta con las horas de inicio y fin
    INSERT INTO route (id_driver, date_, start_time, end_time, route_status, id_central, id_central_finish)
    VALUES (p_id_driver, NOW(), p_start_time, p_end_time, 'Pendiente', p_id_central, p_id_central_finish)
    RETURNING id INTO new_route_id;

    -- Obtener coordenadas iniciales (central de inicio)
    BEGIN
        SELECT coord_x, coord_y INTO cur_x, cur_y FROM central WHERE id = p_id_central LIMIT 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        cur_x := 0.0;
        cur_y := 0.0;
    END;

    -- Ordenar los contenedores usando algoritmo greedy (nearest neighbour)
    FOR i IN 1..contenedor_count LOOP
        -- Seleccionar el contenedor no seleccionado más cercano al punto actual
        SELECT id, coord_x, coord_y INTO sel_id, sel_x, sel_y
        FROM tmp_route_containers
        WHERE selected = FALSE
        ORDER BY (
            2 * 6371 * asin(
                sqrt(
                    power(sin(radians((coord_x - cur_x) / 2)), 2) +
                    cos(radians(cur_x)) * cos(radians(coord_x)) * power(sin(radians((coord_y - cur_y) / 2)), 2)
                )
            )
        )
        LIMIT 1;

        -- Si no se encontró ninguno, salir
        IF NOT FOUND THEN
            EXIT;
        END IF;

        -- Marcar como seleccionado
        UPDATE tmp_route_containers SET selected = TRUE WHERE id = sel_id;

        -- Insertar la relación contenedor-ruta en la tabla `route_container` en el orden calculado
        INSERT INTO route_container (id_container, id_route)
        VALUES (sel_id, new_route_id);

        -- Actualizar el estado del contenedor a "Ocupado"
        UPDATE container
        SET status = 'Ocupado'
        WHERE id = sel_id;

        -- Mover el punto actual al contenedor seleccionado
        cur_x := sel_x;
        cur_y := sel_y;
    END LOOP;

    -- Confirmación del éxito
    RAISE NOTICE 'Ruta planificada con ID % y % contenedores asignados (orden optimizado por distancia)', new_route_id, contenedor_count;
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