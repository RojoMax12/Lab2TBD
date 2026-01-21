-- =====================================
-- EXTENSIÓN POSTGIS
-- =====================================
CREATE EXTENSION IF NOT EXISTS postgis;

-- =====================================
-- TABLA CALLES
-- =====================================

CREATE TABLE public.calles (
    id integer NOT NULL,
    geom public.geometry(MultiLineString,32719),
    shape_leng double precision,
    st_length_ double precision,
    nom_ruta character varying(50),
    rol_mop character varying(30),
    clase_ruta integer,
    tipo_carpe character varying(30),
    catego character varying(30),
    cod_region integer
);

-- =====================================
-- TABLA COMUNAS
-- =====================================

CREATE TABLE public.comunas (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,3857),
    objectid bigint,
    shape_leng double precision,
    dis_elec integer,
    cir_sena integer,
    cod_comuna integer,
    codregion integer,
    st_area_sh double precision,
    st_length_ double precision,
    region character varying(60),
    comuna character varying(60),
    provincia character varying(60)
);

-- =====================================
-- TABLA PROVINCIAS
-- =====================================

CREATE TABLE public.provincias (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,3857),
    objectid bigint,
    cir_sena integer,
    codregion integer,
    cod_prov integer,
    st_area_sh double precision,
    st_length_ double precision,
    provincia character varying(60),
    region character varying(60)
);

-- =====================================
-- TABLA REGIONES
-- =====================================

CREATE TABLE public.regiones (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,3857),
    objectid bigint,
    cir_sena integer,
    codregion integer,
    area_km double precision,
    st_area_sh double precision,
    st_length_ double precision,
    region character varying(60)
);

-- Índice para buscar comunas rápido
CREATE INDEX IF NOT EXISTS idx_comunas_geom ON comunas USING GIST (geom);

-- Índice para buscar calles cercanas rápido (KNN)
CREATE INDEX IF NOT EXISTS idx_calles_geom ON calles USING GIST (geom);

-- Índices para tus contenedores y zonas
CREATE INDEX IF NOT EXISTS idx_container_location ON container USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_zones_location ON collection_zone USING GIST (location);