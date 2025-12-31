-- Esta sentencia crea una vista materializada que resume el peso total recolectado por día.
-- Migration: crea vista materializada peso_recolectado_diario
-- Esta vista agrega el peso total recolectado por día.

-- La vista se crea con la opción de refresco CONCURRENTLY para permitir actualizaciones concurrentes.
CREATE MATERIALIZED VIEW IF NOT EXISTS peso_recolectado_diario AS
SELECT
    date_trunc('day', p.date_hour)::date AS fecha,
    SUM(c.weight) AS peso_total
FROM pickup p
JOIN containers c ON p.id_container = c.id
GROUP BY fecha
ORDER BY fecha;

CREATE UNIQUE INDEX IF NOT EXISTS idx_peso_recolectado_diario_fecha ON peso_recolectado_diario (fecha);