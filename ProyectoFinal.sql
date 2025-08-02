--FUNCIONES
--------------------------------------------------------------------------------
-- fn_nombre_cliente_por_id
CREATE OR REPLACE FUNCTION fn_nombre_cliente_por_id (
    p_cliente_id IN NUMBER
) RETURN VARCHAR2
IS
    v_nombre_cliente VARCHAR2(200);
BEGIN
    SELECT nombre || ' ' || apellidos
    INTO v_nombre_cliente
    FROM cliente
    WHERE id = p_cliente_id;

    RETURN v_nombre_cliente;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_total_ventas_por_cliente
CREATE OR REPLACE FUNCTION fn_total_ventas_por_cliente (
    p_cliente_id IN NUMBER
) RETURN NUMBER
IS
    v_total NUMBER := 0;
BEGIN
    SELECT NVL(SUM(monto_total), 0)
    INTO v_total
    FROM venta
    WHERE cliente_id = p_cliente_id;

    RETURN v_total;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_estado_venta
CREATE OR REPLACE FUNCTION fn_estado_venta (
    p_venta_id IN NUMBER
) RETURN VARCHAR2
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM venta
    WHERE id = p_venta_id;

    RETURN v_estado;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_producto_en_categoria
CREATE OR REPLACE FUNCTION fn_producto_en_categoria (
    p_codigo_producto IN VARCHAR2,
    p_categoria_id IN NUMBER
) RETURN BOOLEAN
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM producto
    WHERE codigo = p_codigo_producto
      AND categoria_id = p_categoria_id;

    RETURN v_count > 0;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_verificar_usuario_activo
CREATE OR REPLACE FUNCTION fn_verificar_usuario_activo (
    p_email IN VARCHAR2
) RETURN BOOLEAN
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM cliente
    WHERE email = p_email;

    RETURN v_estado = 'Activo';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_existe_cliente_email
CREATE OR REPLACE FUNCTION fn_existe_cliente_email (
    p_email IN VARCHAR2
) RETURN BOOLEAN
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM cliente
    WHERE email = p_email;

    RETURN v_count > 0;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_fecha_ultima_venta_cliente
CREATE OR REPLACE FUNCTION fn_fecha_ultima_venta_cliente (
    p_cliente_id IN NUMBER
) RETURN DATE
IS
    v_fecha DATE;
BEGIN
    SELECT MAX(fecha) INTO v_fecha
    FROM venta
    WHERE cliente_id = p_cliente_id;

    RETURN v_fecha;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_categoria_de_producto
CREATE OR REPLACE FUNCTION fn_categoria_de_producto (
    p_codigo_producto VARCHAR2
) RETURN VARCHAR2
IS
    v_categoria_nombre VARCHAR2(100);
BEGIN
    SELECT c.nombre INTO v_categoria_nombre
    FROM producto p
    JOIN categoria c ON p.categoria_id = c.id
    WHERE p.codigo = p_codigo_producto;

    RETURN v_categoria_nombre;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_cantidad_productos_por_proveedor
CREATE OR REPLACE FUNCTION fn_cantidad_productos_por_proveedor (
    p_proveedor_id IN NUMBER
) RETURN NUMBER
IS
    v_cantidad NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_cantidad
    FROM asociacion_proveedor_producto
    WHERE proveedor_id = p_proveedor_id
      AND estado = 'Activo';

    RETURN v_cantidad;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_proveedor_por_producto
CREATE OR REPLACE FUNCTION fn_proveedor_por_producto (
    p_producto_id IN NUMBER
) RETURN VARCHAR2
IS
    v_nombre_proveedor VARCHAR2(200);
BEGIN
    SELECT pr.nombre || ' ' || pr.apellidos
    INTO v_nombre_proveedor
    FROM asociacion_proveedor_producto a
    JOIN proveedor pr ON a.proveedor_id = pr.id
    WHERE a.producto_id = p_producto_id
      AND a.estado = 'Activo'
      AND ROWNUM = 1;

    RETURN v_nombre_proveedor;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_verificar_producto_activo
CREATE OR REPLACE FUNCTION fn_verificar_producto_activo (
    p_codigo IN VARCHAR2
) RETURN BOOLEAN
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM producto
    WHERE codigo = p_codigo;

    RETURN v_estado = 'Activo';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_cliente_tiene_ventas
CREATE OR REPLACE FUNCTION fn_cliente_tiene_ventas (
    p_cliente_id IN NUMBER
) RETURN BOOLEAN
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM venta
    WHERE cliente_id = p_cliente_id;

    RETURN v_count > 0;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_venta_es_pendiente
CREATE OR REPLACE FUNCTION fn_venta_es_pendiente (
    p_venta_id IN NUMBER
) RETURN BOOLEAN
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM venta
    WHERE id = p_venta_id;

    RETURN v_estado = 'Pendiente';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_contar_productos_activos
CREATE OR REPLACE FUNCTION fn_contar_productos_activos RETURN NUMBER
IS
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_total
    FROM producto
    WHERE estado = 'Activo';

    RETURN v_total;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_recuento_ventas_pendientes
CREATE OR REPLACE FUNCTION fn_recuento_ventas_pendientes RETURN NUMBER
IS
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_total
    FROM venta
    WHERE estado = 'Pendiente';

    RETURN v_total;
END;
/
--------------------------------------------------------------------------------


