--------------------------------------------------------------------------------
/*Paquetes*/
--------------------------------------------------------------------------------
-- Vista que muestra todos los productos activos con su información principal
CREATE OR REPLACE VIEW vista_productos_activos AS
SELECT 
    p.CODIGO_PRODUCTO,
    p.NOMBRE_PRODUCTO,
    p.DESRIPCION_PRODUCTO,
    p.MATERIAL,
    p.IMAGEN,
    c.NOMBRE_CATEGORIA AS categoria,
    p.ESTADO
FROM 
    PRODUCTO p
JOIN 
    CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
WHERE 
    p.ESTADO = 'Activo';

--------------------------------------------------------------------------------
-- Vista que muestra todos los productos inactivos con su información principal
CREATE OR REPLACE VIEW vista_productos_inactivos AS
SELECT 
    p.CODIGO_PRODUCTO,
    p.NOMBRE_PRODUCTO,
    p.DESRIPCION_PRODUCTO,
    p.MATERIAL,
    p.IMAGEN,
    c.NOMBRE_CATEGORIA AS categoria,
    p.ESTADO
FROM 
    PRODUCTO p
JOIN 
    CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
WHERE 
    p.ESTADO = 'Inactivo';
    
CREATE OR REPLACE VIEW vista_productos_inventario AS
SELECT 
    p.ID_PRODUCTO,
    p.CODIGO_PRODUCTO,
    p.NOMBRE_PRODUCTO,
    p.DESRIPCION_PRODUCTO,
    p.MATERIAL,
    p.IMAGEN,
    p.ESTADO,
    p.ID_CATEGORIA,
    c.NOMBRE_CATEGORIA,
    i.INVENTARIO_ID,
    i.TALLA,
    i.COLOR,
    i.STOCK_ACTUAL,
    i.STOCK_MINIMO,
    i.PRECIO_VENTA
FROM 
    PRODUCTO p
JOIN 
    CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
JOIN 
    INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID;

--------------------------------------------------------------------------------
-- Vista que muestra todos los clientes activos
CREATE OR REPLACE VIEW vista_clientes_activos AS
SELECT 
    ID_CLIENTE,
    CEDULA,
    NOMBRE,
    APELLIDOS,
    EMAIL,
    TELEFONO,
    DIRECCION,
    ESTADO
FROM 
    CLIENTE
WHERE
    ESTADO = 'Activo';
    
--------------------------------------------------------------------------------
-- Vista que muestra todos los clientes inactivos
CREATE OR REPLACE VIEW vista_clientes_inactivos AS
SELECT 
    ID_CLIENTE,
    CEDULA,
    NOMBRE,
    APELLIDOS,
    EMAIL,
    TELEFONO,
    DIRECCION,
    ESTADO
FROM 
    CLIENTE
WHERE
    ESTADO = 'Inactivo';

--------------------------------------------------------------------------------
-- Vista que muestra todos los proveedores activos
CREATE OR REPLACE VIEW vista_proveedores_activos AS
SELECT 
    ID_PROVEEDOR,
    CEDULA,
    NOMBRE,
    APELLIDOS,
    EMAIL,
    TELEFONO,
    DIRECCION,
    ESTADO
FROM 
    PROVEEDOR
WHERE
    ESTADO = 'Activo';

--------------------------------------------------------------------------------
-- Vista que muestra todos los proveedores inactivos
CREATE OR REPLACE VIEW vista_proveedores_inactivos AS
SELECT 
    ID_PROVEEDOR,
    CEDULA,
    NOMBRE,
    APELLIDOS,
    EMAIL,
    TELEFONO,
    DIRECCION,
    ESTADO
FROM 
    PROVEEDOR
WHERE
    ESTADO = 'Inactivo';

--------------------------------------------------------------------------------
-- Vista que muestra la relación entre productos y sus proveedores
CREATE OR REPLACE VIEW vista_asociaciones_productos_proveedores AS
SELECT 
    a.PRODUCTO_PROVEEDOR_ID AS asociacion_id,
    p.CODIGO_PRODUCTO AS codigo_producto,
    p.NOMBRE_PRODUCTO AS nombre_producto,
    pr.CEDULA AS cedula_proveedor,
    pr.NOMBRE AS nombre_proveedor,
    a.ESTADO
FROM 
    PRODUCTO_PROVEEDOR a
JOIN 
    PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
JOIN 
    PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
WHERE 
    a.ESTADO = 'Activo';
    
--------------------------------------------------------------------------------
-- Vista que muestra la relación entre productos y sus proveedores
CREATE OR REPLACE VIEW vista_asociaciones_productos_proveedores_inactivos AS
SELECT 
    a.PRODUCTO_PROVEEDOR_ID AS asociacion_id,
    p.CODIGO_PRODUCTO AS codigo_producto,
    p.NOMBRE_PRODUCTO AS nombre_producto,
    pr.CEDULA AS cedula_proveedor,
    pr.NOMBRE AS nombre_proveedor,
    a.ESTADO
FROM 
    PRODUCTO_PROVEEDOR a
JOIN 
    PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
JOIN 
    PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
WHERE 
    a.ESTADO = 'Inactivo';

--------------------------------------------------------------------------------
-- Vista que muestra todas las ventas realizadas por cada cliente
CREATE OR REPLACE VIEW vista_historial_compras_por_cliente AS
SELECT 
    v.VENTA_ID AS venta_id,
    c.CEDULA AS cliente_cedula,
    c.NOMBRE || ' ' || c.APELLIDOS AS cliente_nombre,
    v.FECHA_VENTA AS fecha,
    v.TOTAL AS monto_total,
    v.ESTADO
FROM 
    VENTA v
JOIN 
    CLIENTE c ON v.CLIENTE_ID = c.ID_CLIENTE;

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- vista_categorias_con_cantidad
CREATE OR REPLACE VIEW vista_categorias_con_cantidad AS
SELECT 
    c.ID_CATEGORIA,
    c.NOMBRE_CATEGORIA,
    COUNT(p.ID_PRODUCTO) AS cantidad_productos
FROM categoria c
LEFT JOIN producto p ON c.ID_CATEGORIA = p.ID_CATEGORIA AND p.estado = 'Activo'
GROUP BY c.ID_CATEGORIA, c.NOMBRE_CATEGORIA;
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- vista_ventas_detalle
CREATE OR REPLACE VIEW vista_ventas_detalle AS
SELECT 
    v.VENTA_ID AS venta_id,
    c.NOMBRE || ' ' || c.APELLIDOS AS cliente,
    v.FECHA_VENTA AS fecha,
    v.TOTAL AS monto_total,
    v.ESTADO
FROM venta v
JOIN cliente c ON v.CLIENTE_ID = c.ID_CLIENTE;