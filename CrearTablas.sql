-- =====================================
-- EXTENSIÓN POSTGIS
-- =====================================
CREATE EXTENSION IF NOT EXISTS postgis;

-- =====================================
-- ELIMINAR TABLAS SI EXISTEN
-- =====================================
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS sector CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================
-- TABLA USERS
-- =====================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password TEXT,
    location GEOMETRY(Point, 4326), -- Almacena en coordenadas geográficas
    region_id BIGINT,
    provincia_id BIGINT,
    comuna_id BIGINT,
    calle_id BIGINT
);

CREATE INDEX idx_users_location ON users USING GIST (location);

-- =====================================
-- TABLA SECTOR
-- =====================================
CREATE TABLE sector (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location GEOMETRY(Geometry, 4326) NOT NULL,
    calle_id INTEGER -- Relación interna con la tabla 'calles'
);

CREATE INDEX sidx_sector_location ON sector USING GIST (location);
-- =====================================
-- TABLA TASKS
-- =====================================
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    sector_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_tasks_sector
        FOREIGN KEY (sector_id)
        REFERENCES sector(id)
        ON DELETE RESTRICT
);
-- =====================================
-- DATOS DE PRUEBA
-- =====================================

-- USER
-- Usamos 32719 porque es el SRID que ya tiene tu redvial según el error
INSERT INTO users (username, email, password, location, region_id, provincia_id, comuna_id, calle_id)
SELECT 
    'Juan', 'juan.perez@mail.com', '1234', 
    ST_SetSRID(ST_MakePoint(-70.6693, -33.4489), 4326), -- El punto se guarda en grados
    -- 1. Buscar Región (Transformamos el punto de búsqueda al SRID de la tabla)
    (SELECT id FROM regiones 
     ORDER BY geom <-> ST_Transform(ST_SetSRID(ST_MakePoint(-70.6693, -33.4489), 4326), 3857) LIMIT 1),
    
    -- 2. Provincias (usa 3857 según tu error)
    (SELECT id FROM provincias 
     ORDER BY geom <-> ST_Transform(ST_SetSRID(ST_MakePoint(-70.6693, -33.4489), 4326), 3857) LIMIT 1),
    
    -- 3. Comunas (probablemente 3857)
    (SELECT id FROM comunas 
     ORDER BY geom <-> ST_Transform(ST_SetSRID(ST_MakePoint(-70.6693, -33.4489), 4326), 3857) LIMIT 1),
    
    -- 4. Calles (usa 32719 según el error anterior)
    (SELECT id FROM calles 
     ORDER BY geom <-> ST_Transform(ST_SetSRID(ST_MakePoint(-70.6693, -33.4489), 4326), 32719) LIMIT 1);

-- =========================
-- SECTORES
-- =========================
-- Inserción de sectores de prueba
INSERT INTO sector (name, location)
VALUES ('Reparación Semáforo', ST_SetSRID(ST_Point(-70.432, -33.412), 4326));

INSERT INTO sector (name, location)
VALUES ('Bache Calle Principal', ST_SetSRID(ST_Point(-70.450, -30.420), 4326));

INSERT INTO sector (name, location)
VALUES ('Alumbrado Barrio Norte', ST_SetSRID(ST_Point(-70.460, -33.430), 4326));

INSERT INTO sector (name, location)
VALUES ('Señalización Vial', ST_SetSRID(ST_Point(-70.470, -31.440), 4326));



-- =========================
-- TAREAS
-- =========================
INSERT INTO tasks (sector_id, user_id, title, description, due_date, completed) VALUES
(1, 1, 'Reparar semáforo', 
 'Semáforo apagado en cruce principal del centro', 
 '2025-10-20', FALSE),

(1, 1, 'Limpieza de veredas', 
 'Acumulación de basura en veredas céntricas', 
 '2025-10-22', TRUE),

(2, 1, 'Bache en la calle', 
 'Bache profundo frente al colegio municipal', 
 '2025-10-18', FALSE),

(2, 1, 'Señalización preventiva', 
 'Instalar señalética por obras viales', 
 '2025-10-25', FALSE),

(3, 1, 'Alumbrado público', 
 'Poste sin luz en barrio residencial', 
 '2025-10-21', TRUE),

(4, 1, 'Señal de pare dañada', 
 'Señal de pare rota por accidente', 
 '2025-10-28', FALSE);


CREATE OR REPLACE FUNCTION procesar_sector_valido()
RETURNS TRIGGER AS $$
BEGIN
    -- 1. Buscar la calle más cercana transformando el punto al SRID de las calles (32719)
    -- Esto permite que el operador <-> compare peras con peras (metros con metros)
    SELECT id INTO NEW.calle_id
    FROM calles
    ORDER BY ST_Transform(NEW.location, 32719) <-> geom
    LIMIT 1;

    -- 2. Convertir el punto en una aureola de 50 metros (Buffer)
    -- Como ya sabemos que el entorno usa metros (32719), hacemos el buffer ahí
    IF ST_GeometryType(NEW.location) = 'ST_Point' THEN
        NEW.location := ST_Transform(
            ST_Buffer(
                ST_Transform(NEW.location, 32719), 
                100 -- 50 metros exactos
            ), 
            4326 -- Lo devolvemos a 4326 para que tu backend lo reciba en Lat/Lng
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar el trigger a la tabla sector
CREATE TRIGGER tr_validar_y_expandir_sector
BEFORE INSERT ON sector
FOR EACH ROW EXECUTE FUNCTION procesar_sector_valido();