-- vista_productos_activos
CREATE OR REPLACE VIEW vista_productos_activos AS
SELECT 
    codigo,
    nombre,
    descripcion,
    marca,
    genero,
    material,
    categoria_id
FROM producto
WHERE estado = 'Activo';
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- vista_categorias_con_cantidad
CREATE OR REPLACE VIEW vista_categorias_con_cantidad AS
SELECT 
    c.id,
    c.nombre,
    COUNT(p.id) AS cantidad_productos
FROM categoria c
LEFT JOIN producto p ON c.id = p.categoria_id AND p.estado = 'Activo'
GROUP BY c.id, c.nombre;
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- vista_ventas_detalle
CREATE OR REPLACE VIEW vista_ventas_detalle AS
SELECT 
    v.id AS id_venta,
    c.nombre || ' ' || c.apellidos AS cliente,
    v.fecha,
    v.monto_total,
    v.estado
FROM venta v
JOIN cliente c ON v.cliente_id = c.id;
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- vista_asociaciones_activas
CREATE OR REPLACE VIEW vista_asociaciones_activas AS
SELECT 
    a.id AS id_asociacion,
    p.nombre AS producto,
    pr.nombre || ' ' || pr.apellidos AS proveedor,
    a.estado
FROM asociacion_proveedor_producto a
JOIN producto p ON a.producto_id = p.id
JOIN proveedor pr ON a.proveedor_id = pr.id
WHERE a.estado = 'Activo';
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- vista_clientes_activos
CREATE OR REPLACE VIEW vista_clientes_activos AS
SELECT 
    id,
    nombre,
    apellidos,
    email
FROM cliente
WHERE estado = 'Activo';
--------------------------------------------------------------------------------