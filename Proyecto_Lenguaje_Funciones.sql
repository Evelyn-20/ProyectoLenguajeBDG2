--------------------------------------------------------------------------------
/*FUNCIONES*/
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
    WHERE id_cliente = p_cliente_id;

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
    SELECT NVL(SUM(total), 0)
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
    WHERE venta_id = p_venta_id;

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
    p_codigo_producto IN NUMBER,
    p_categoria_id IN NUMBER
) RETURN BOOLEAN
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM producto
    WHERE codigo_producto = p_codigo_producto
      AND id_categoria = p_categoria_id;

    RETURN v_count > 0;
END;
/

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_verificar_usuario_activo
CREATE OR REPLACE FUNCTION fn_verificar_usuario_activo (
    p_email IN VARCHAR2
) RETURN NUMBER
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM cliente
    WHERE email = p_email;

    -- Retorna 1 si está activo, 0 si no está activo
    RETURN CASE WHEN v_estado = 'Activo' THEN 1 ELSE 0 END;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0; -- Retorna 0 si no se encuentra el usuario
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_existe_cliente_email
CREATE OR REPLACE FUNCTION fn_existe_cliente_email (
    p_email IN VARCHAR2
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM cliente
    WHERE email = p_email;
    
    -- Retorna 1 si existe, 0 si no existe
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- fn_fecha_ultima_venta_cliente
CREATE OR REPLACE FUNCTION fn_fecha_ultima_venta_cliente (
    p_cliente_id IN NUMBER
) RETURN DATE
IS
    v_fecha DATE;
BEGIN
    SELECT MAX(fecha_venta) INTO v_fecha
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
    p_codigo_producto NUMBER
) RETURN VARCHAR2
IS
    v_categoria_nombre VARCHAR2(100);
BEGIN
    SELECT c.nombre_categoria INTO v_categoria_nombre
    FROM producto p
    JOIN categoria c ON p.id_categoria = c.id_categoria
    WHERE p.codigo_producto = p_codigo_producto;

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
    FROM producto_proveedor
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
    FROM producto_proveedor a
    JOIN proveedor pr ON a.producto_proveedor_id = pr.id_proveedor
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
    p_codigo IN NUMBER
) RETURN BOOLEAN
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM producto
    WHERE codigo_producto = p_codigo;

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
    WHERE venta_id = p_venta_id;

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

CREATE OR REPLACE FUNCTION OBTENER_CLIENTE_POR_CREDENCIAL(
    p_username IN VARCHAR2,
    p_es_email IN NUMBER
) RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    IF p_es_email = 1 THEN
        OPEN v_cursor FOR
        SELECT * FROM CLIENTE 
        WHERE EMAIL = p_username;
    ELSE
        OPEN v_cursor FOR
        SELECT * FROM CLIENTE 
        WHERE CEDULA = p_username;
    END IF;
    
    RETURN v_cursor;
END;
/

-- Ya tienes estas funciones, pero las incluyo para completitud
CREATE OR REPLACE FUNCTION fn_existe_cliente_email (
    p_email IN VARCHAR2
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM cliente
    WHERE email = p_email;
    
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

CREATE OR REPLACE FUNCTION fn_verificar_usuario_activo (
    p_username IN VARCHAR2,
    p_es_email IN NUMBER
) RETURN NUMBER
IS
    v_estado VARCHAR2(20);
BEGIN
    IF p_es_email = 1 THEN
        SELECT estado INTO v_estado
        FROM cliente
        WHERE email = p_username;
    ELSE
        SELECT estado INTO v_estado
        FROM cliente
        WHERE cedula = p_username;
    END IF;

    RETURN CASE WHEN v_estado = 'Activo' THEN 1 ELSE 0 END;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

-- Función para verificar si existe una asociación activa
CREATE OR REPLACE FUNCTION fn_existe_asociacion_activa (
    p_producto_id IN NUMBER,
    p_proveedor_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM PRODUCTO_PROVEEDOR
    WHERE PRODUCTO_ID = p_producto_id 
      AND PROVEEDOR_ID = p_proveedor_id 
      AND ESTADO = 'Activo';
    
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

-- Función para obtener el precio de compra de un producto con un proveedor
CREATE OR REPLACE FUNCTION fn_precio_compra_producto_proveedor (
    p_producto_id IN NUMBER,
    p_proveedor_id IN NUMBER
) RETURN NUMBER
IS
    v_precio NUMBER;
BEGIN
    SELECT PRECIO_COMPRA INTO v_precio
    FROM PRODUCTO_PROVEEDOR
    WHERE PRODUCTO_ID = p_producto_id 
      AND PROVEEDOR_ID = p_proveedor_id 
      AND ESTADO = 'Activo'
      AND ROWNUM = 1;
    
    RETURN v_precio;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

-- Función para contar asociaciones activas por producto
CREATE OR REPLACE FUNCTION fn_contar_asociaciones_por_producto (
    p_producto_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM PRODUCTO_PROVEEDOR
    WHERE PRODUCTO_ID = p_producto_id 
      AND ESTADO = 'Activo';
    
    RETURN v_count;
END;
/

-- Función para contar asociaciones activas por proveedor
CREATE OR REPLACE FUNCTION fn_contar_asociaciones_por_proveedor (
    p_proveedor_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM PRODUCTO_PROVEEDOR
    WHERE PROVEEDOR_ID = p_proveedor_id 
      AND ESTADO = 'Activo';
    
    RETURN v_count;
END;
/

CREATE OR REPLACE FUNCTION fn_info_categoria_por_id (
    p_categoria_id IN NUMBER
) RETURN VARCHAR2
IS
    v_info VARCHAR2(500);
BEGIN
    SELECT 'ID: ' || ID_CATEGORIA || 
           ', Código: ' || CODIGO_CATEGORIA || 
           ', Nombre: ' || NOMBRE_CATEGORIA || 
           ', Estado: ' || ESTADO
    INTO v_info
    FROM CATEGORIA
    WHERE ID_CATEGORIA = p_categoria_id;

    RETURN v_info;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

CREATE OR REPLACE FUNCTION fn_categoria_activa (
    p_categoria_id IN NUMBER
) RETURN BOOLEAN
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT ESTADO 
    INTO v_estado
    FROM CATEGORIA
    WHERE ID_CATEGORIA = p_categoria_id;

    RETURN v_estado = 'Activo';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
END;
/

--------------------------------------------------------------------------------
-- Nueva función para contar productos por categoría
CREATE OR REPLACE FUNCTION fn_contar_productos_por_categoria (
    p_categoria_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) 
    INTO v_count
    FROM PRODUCTO
    WHERE ID_CATEGORIA = p_categoria_id 
      AND ESTADO = 'Activo';

    RETURN v_count;
END;
/

-- Función para obtener categorías por rango de códigos
CREATE OR REPLACE FUNCTION fn_categorias_por_rango (
    p_codigo_inicio IN NUMBER,
    p_codigo_fin IN NUMBER
) RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, 
               DESRIPCION_CATEGORIA, ESTADO
        FROM CATEGORIA
        WHERE CODIGO_CATEGORIA BETWEEN p_codigo_inicio AND p_codigo_fin
          AND ESTADO = 'Activo'
        ORDER BY CODIGO_CATEGORIA;
    
    RETURN v_cursor;
END;
/

-- Función para obtener la primera imagen de producto de una categoría
CREATE OR REPLACE FUNCTION fn_primera_imagen_producto (
    p_categoria_id IN NUMBER
) RETURN VARCHAR2
IS
    v_imagen VARCHAR2(255);
BEGIN
    SELECT p.IMAGEN INTO v_imagen
    FROM PRODUCTO p
    WHERE p.ID_CATEGORIA = p_categoria_id 
      AND p.ESTADO = 'Activo'
      AND p.IMAGEN IS NOT NULL
      AND ROWNUM = 1
    ORDER BY p.ID_PRODUCTO;
    
    RETURN NVL(v_imagen, 'default-image.jpg');
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'default-image.jpg';
END;
/

CREATE OR REPLACE FUNCTION fn_obtener_url_categoria (
    p_categoria_id IN NUMBER
) RETURN VARCHAR2
IS
    v_url VARCHAR2(100);
BEGIN
    -- Mapeo directo basado en ID de categoría
    v_url := CASE p_categoria_id
        -- Categorías de Mujer
        WHEN 8 THEN 'vestidos'
        WHEN 9 THEN 'blusas-camisetas'
        WHEN 10 THEN 'faldas'
        WHEN 11 THEN 'pantalones-mujer'
        WHEN 12 THEN 'chaquetas-sudaderas-mujer'
        -- Categorías de Hombre
        WHEN 5 THEN 'camisas-camisetas'
        WHEN 6 THEN 'pantalones-hombre'
        WHEN 7 THEN 'chaquetas-sudaderas-hombre'
        -- Categorías de Zapatos
        WHEN 13 THEN 'tenis'
        WHEN 14 THEN 'botas'
        WHEN 15 THEN 'zapatos-formales'
        WHEN 16 THEN 'sandalias'
        WHEN 17 THEN 'zapatos-casuales'
        -- Categorías de Accesorios
        WHEN 18 THEN 'bolso-mochila'
        WHEN 19 THEN 'joyería'
        WHEN 20 THEN 'gorras-sombreros'
        WHEN 21 THEN 'cinturones'
        WHEN 22 THEN 'lentes'
        WHEN 23 THEN 'bufandas-guantes'
        ELSE 'categoria-' || p_categoria_id
    END;
    
    RETURN v_url;
END;
/

-- Función para verificar si una categoría está activa (ya existe, pero la incluyo para completitud)
CREATE OR REPLACE FUNCTION fn_categoria_activa (
    p_categoria_id IN NUMBER
) RETURN BOOLEAN
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT ESTADO 
    INTO v_estado
    FROM CATEGORIA
    WHERE ID_CATEGORIA = p_categoria_id;

    RETURN v_estado = 'Activo';
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
END;
/

-- Función para contar productos por categoría (ya existe, pero la incluyo para completitud)
CREATE OR REPLACE FUNCTION fn_contar_productos_por_categoria (
    p_categoria_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) 
    INTO v_count
    FROM PRODUCTO
    WHERE ID_CATEGORIA = p_categoria_id 
      AND ESTADO = 'Activo';

    RETURN v_count;
END;
/

-- Función para verificar si existe código de categoría duplicado
CREATE OR REPLACE FUNCTION fn_existe_codigo_categoria (
    p_codigo IN NUMBER,
    p_excluir_id IN NUMBER DEFAULT NULL
) RETURN BOOLEAN
IS
    v_count NUMBER;
BEGIN
    IF p_excluir_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count
        FROM CATEGORIA
        WHERE CODIGO_CATEGORIA = p_codigo
          AND ID_CATEGORIA != p_excluir_id;
    ELSE
        SELECT COUNT(*) INTO v_count
        FROM CATEGORIA
        WHERE CODIGO_CATEGORIA = p_codigo;
    END IF;
    
    RETURN v_count > 0;
END;
/

-- Función para verificar si existe nombre de categoría duplicado
CREATE OR REPLACE FUNCTION fn_existe_nombre_categoria (
    p_nombre IN VARCHAR2,
    p_excluir_id IN NUMBER DEFAULT NULL
) RETURN BOOLEAN
IS
    v_count NUMBER;
BEGIN
    IF p_excluir_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count
        FROM CATEGORIA
        WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(p_nombre)
          AND ID_CATEGORIA != p_excluir_id;
    ELSE
        SELECT COUNT(*) INTO v_count
        FROM CATEGORIA
        WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(p_nombre);
    END IF;
    
    RETURN v_count > 0;
END;
/

-- Función para verificar si un cliente puede ser eliminado (no tiene ventas)
CREATE OR REPLACE FUNCTION fn_cliente_puede_eliminarse (
    p_cliente_id IN NUMBER
) RETURN NUMBER
IS
    v_count_ventas NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count_ventas
    FROM VENTA
    WHERE CLIENTE_ID = p_cliente_id;
    
    -- Retorna 1 si puede eliminarse (no tiene ventas), 0 si no puede
    RETURN CASE WHEN v_count_ventas = 0 THEN 1 ELSE 0 END;
END;
/

-- Función para obtener estadísticas de cliente
CREATE OR REPLACE FUNCTION fn_estadisticas_cliente (
    p_cliente_id IN NUMBER
) RETURN VARCHAR2
IS
    v_total_ventas NUMBER;
    v_monto_total NUMBER;
    v_ultima_venta DATE;
    v_estadisticas VARCHAR2(500);
BEGIN
    SELECT COUNT(*), NVL(SUM(TOTAL), 0), MAX(FECHA_VENTA)
    INTO v_total_ventas, v_monto_total, v_ultima_venta
    FROM VENTA
    WHERE CLIENTE_ID = p_cliente_id;
    
    v_estadisticas := 'Ventas: ' || v_total_ventas || 
                     ', Monto Total: ' || v_monto_total ||
                     ', Última Venta: ' || TO_CHAR(v_ultima_venta, 'DD/MM/YYYY');
    
    RETURN v_estadisticas;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'Sin datos de ventas';
END;
/

-- Función para contar total de productos
CREATE OR REPLACE FUNCTION fn_contar_total_productos RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) 
    INTO v_count
    FROM PRODUCTO;
    
    RETURN v_count;
END;
/

-- Función para contar productos por estado
CREATE OR REPLACE FUNCTION fn_contar_productos_por_estado (
    p_estado IN VARCHAR2
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) 
    INTO v_count
    FROM PRODUCTO
    WHERE ESTADO = p_estado;
    
    RETURN v_count;
END;
/

-- Función para obtener nombre de categoría
CREATE OR REPLACE FUNCTION fn_obtener_nombre_categoria (
    p_categoria_id IN NUMBER
) RETURN VARCHAR2
IS
    v_nombre VARCHAR2(100);
BEGIN
    SELECT NOMBRE_CATEGORIA
    INTO v_nombre
    FROM CATEGORIA
    WHERE ID_CATEGORIA = p_categoria_id;
    
    RETURN v_nombre;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'Categoria no encontrada';
END;
/

-- Función para verificar si un producto existe por código
CREATE OR REPLACE FUNCTION fn_existe_codigo_producto (
    p_codigo IN NUMBER,
    p_excluir_id IN NUMBER DEFAULT NULL
) RETURN BOOLEAN
IS
    v_count NUMBER;
BEGIN
    IF p_excluir_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count
        FROM PRODUCTO
        WHERE CODIGO_PRODUCTO = p_codigo
          AND ID_PRODUCTO != p_excluir_id;
    ELSE
        SELECT COUNT(*) INTO v_count
        FROM PRODUCTO
        WHERE CODIGO_PRODUCTO = p_codigo;
    END IF;
    
    RETURN v_count > 0;
END;
/

-- Función para obtener ID de producto por código
CREATE OR REPLACE FUNCTION fn_obtener_id_producto_por_codigo (
    p_codigo IN NUMBER
) RETURN NUMBER
IS
    v_id NUMBER;
BEGIN
    SELECT ID_PRODUCTO 
    INTO v_id
    FROM PRODUCTO
    WHERE CODIGO_PRODUCTO = p_codigo;
    
    RETURN v_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

-- Función para contar total de ventas
CREATE OR REPLACE FUNCTION fn_contar_ventas_totales RETURN NUMBER
IS
    v_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_total FROM VENTA;
    RETURN v_total;
END;
/

-- Función para obtener total de ventas por período
CREATE OR REPLACE FUNCTION fn_total_ventas_periodo (
    p_fecha_inicio IN TIMESTAMP,
    p_fecha_fin    IN TIMESTAMP
) RETURN NUMBER
IS
    v_total NUMBER;
BEGIN
    SELECT COALESCE(SUM(TOTAL), 0) 
    INTO v_total
    FROM VENTA 
    WHERE FECHA_VENTA BETWEEN p_fecha_inicio AND p_fecha_fin;
    
    RETURN v_total;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

-- Función para verificar si existe una venta
CREATE OR REPLACE FUNCTION fn_existe_venta (
    p_venta_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count 
    FROM VENTA 
    WHERE VENTA_ID = p_venta_id;
    
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

-- Función para buscar cliente por username
CREATE OR REPLACE FUNCTION fn_buscar_cliente_por_username (
    p_username IN VARCHAR2
) RETURN NUMBER
IS
    v_cliente_id NUMBER;
BEGIN
    
    BEGIN
        SELECT ID_CLIENTE 
        INTO v_cliente_id
        FROM CLIENTE 
        WHERE EMAIL = p_username;
        
        RETURN v_cliente_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL; -- Continuar con la siguiente búsqueda
    END;
    
    -- Si no se encuentra por username ni email, intentar parsear como ID
    BEGIN
        v_cliente_id := TO_NUMBER(p_username);
        
        -- Verificar que el ID existe
        SELECT COUNT(*) INTO v_cliente_id
        FROM CLIENTE
        WHERE ID_CLIENTE = TO_NUMBER(p_username);
        
        IF v_cliente_id > 0 THEN
            RETURN TO_NUMBER(p_username);
        ELSE
            RETURN NULL;
        END IF;
    EXCEPTION
        WHEN VALUE_ERROR THEN
            RETURN NULL;
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END;
END;
/

-- Función para verificar si un cliente está activo
CREATE OR REPLACE FUNCTION fn_cliente_esta_activo (
    p_cliente_id IN NUMBER
) RETURN NUMBER
IS
    v_activo NUMBER;
BEGIN
    SELECT ESTADO 
    INTO v_activo 
    FROM CLIENTE 
    WHERE ID_CLIENTE = p_cliente_id;
    
    RETURN CASE WHEN v_activo = 1 THEN 1 ELSE 0 END;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        RETURN 0;
END;
/
