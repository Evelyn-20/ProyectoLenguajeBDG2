--Mostrar producto segun su categoria que me pidieron
CREATE OR REPLACE PROCEDURE listar_productos_por_categoria (
    p_categoria_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            p.codigo,
            p.nombre,
            p.descripcion,
            p.marca,
            p.genero,
            p.material
        FROM 
            producto p
        WHERE 
            p.categoria_id = p_categoria_id
            AND p.estado = 'Activo';
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- registrar_asociacion_proveedor_producto
CREATE OR REPLACE PROCEDURE registrar_asociacion_proveedor_producto (
    p_producto_id  IN NUMBER,
    p_proveedor_id IN NUMBER
)
IS
BEGIN
    INSERT INTO asociacion_proveedor_producto (
        id, producto_id, proveedor_id, estado
    ) VALUES (
        asociacion_seq.NEXTVAL, p_producto_id, p_proveedor_id, 'Activo'
    );
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20021, 'Error al asociar producto con proveedor: ' || SQLERRM);
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- consultar_asociacion_producto_proveedor
CREATE OR REPLACE PROCEDURE consultar_asociacion_producto_proveedor (
    p_codigo_producto IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT a.id,
               p.nombre AS producto,
               pr.nombre AS proveedor,
               pr.apellidos,
               a.estado
        FROM asociacion_proveedor_producto a
        JOIN producto p ON a.producto_id = p.id
        JOIN proveedor pr ON a.proveedor_id = pr.id
        WHERE p.codigo = p_codigo_producto;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- actualizar_asociacion_producto_proveedor
CREATE OR REPLACE PROCEDURE actualizar_asociacion_producto_proveedor (
    p_asociacion_id     IN NUMBER,
    p_nuevo_producto_id IN NUMBER,
    p_nuevo_proveedor_id IN NUMBER
)
IS
BEGIN
    UPDATE asociacion_proveedor_producto
    SET producto_id = p_nuevo_producto_id,
        proveedor_id = p_nuevo_proveedor_id
    WHERE id = p_asociacion_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20022, 'Asociación no encontrada');
    END IF;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- deshabilitar_asociacion_producto_proveedor
CREATE OR REPLACE PROCEDURE deshabilitar_asociacion_producto_proveedor (
    p_asociacion_id IN NUMBER
)
IS
BEGIN
    UPDATE asociacion_proveedor_producto
    SET estado = 'Inactivo'
    WHERE id = p_asociacion_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20023, 'Asociación no encontrada');
    END IF;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- listar_asociaciones_producto_proveedor
CREATE OR REPLACE PROCEDURE listar_asociaciones_producto_proveedor (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            a.id,
            p.codigo AS codigo_producto,
            p.nombre AS producto,
            pr.nombre AS proveedor,
            pr.apellidos,
            a.estado
        FROM asociacion_proveedor_producto a
        JOIN producto p ON a.producto_id = p.id
        JOIN proveedor pr ON a.proveedor_id = pr.id;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- iniciar_sesion_usuario
CREATE OR REPLACE PROCEDURE iniciar_sesion_usuario (
    p_email    IN VARCHAR2,
    p_password IN VARCHAR2,
    p_result   OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT id, nombre, apellidos, email
        FROM cliente
        WHERE email = p_email
          AND contrasena = p_password
          AND estado = 'Activo';
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- recuperar_contrasena
CREATE OR REPLACE PROCEDURE recuperar_contrasena (
    p_email IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT id, nombre, apellidos, email, contrasena
        FROM cliente
        WHERE email = p_email
          AND estado = 'Activo';
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- buscar_producto_por_nombre
CREATE OR REPLACE PROCEDURE buscar_producto_por_nombre (
    p_nombre  IN VARCHAR2,
    p_result  OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            codigo,
            nombre,
            descripcion,
            marca,
            genero,
            material,
            estado
        FROM producto
        WHERE LOWER(nombre) LIKE '%' || LOWER(p_nombre) || '%';
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- productos_por_categoria
CREATE OR REPLACE PROCEDURE productos_por_categoria (
    p_categoria_id IN NUMBER,
    p_result       OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            codigo,
            nombre,
            descripcion,
            marca,
            genero,
            material,
            estado
        FROM producto
        WHERE categoria_id = p_categoria_id;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- listar_historial_compras_por_cliente
CREATE OR REPLACE PROCEDURE listar_historial_compras_por_cliente (
    p_cliente_id IN NUMBER,
    p_result     OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            v.id AS id_venta,
            v.fecha,
            v.monto_total,
            v.estado
        FROM venta v
        WHERE v.cliente_id = p_cliente_id
        ORDER BY v.fecha DESC;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- listar_ventas_totales
CREATE OR REPLACE PROCEDURE listar_ventas_totales (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            v.id,
            c.nombre || ' ' || c.apellidos AS cliente,
            v.fecha,
            v.monto_total,
            v.estado
        FROM venta v
        JOIN cliente c ON v.cliente_id = c.id
        ORDER BY v.fecha DESC;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- ventas_por_fecha
CREATE OR REPLACE PROCEDURE ventas_por_fecha (
    p_fecha_inicio IN DATE,
    p_fecha_fin    IN DATE,
    p_result       OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            v.id,
            c.nombre || ' ' || c.apellidos AS cliente,
            v.fecha,
            v.monto_total,
            v.estado
        FROM venta v
        JOIN cliente c ON v.cliente_id = c.id
        WHERE TRUNC(v.fecha) BETWEEN TRUNC(p_fecha_inicio) AND TRUNC(p_fecha_fin)
        ORDER BY v.fecha DESC;
END;
/
--------------------------------------------------------------------------------