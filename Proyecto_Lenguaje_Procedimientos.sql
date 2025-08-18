--------------------------------------------------------------------------------
/*Producto*/
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE registrar_producto (
    p_codigo        IN  NUMBER,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2,
    p_material      IN  VARCHAR2,
    p_categoria_id  IN  NUMBER,
    p_imagen        IN  VARCHAR2 DEFAULT NULL,
    p_talla         IN  VARCHAR2,
    p_color         IN  VARCHAR2,
    p_stock_actual  IN  NUMBER,
    p_stock_minimo  IN  NUMBER,
    p_precio_venta  IN  NUMBER
) IS
    v_producto_id NUMBER;
    v_count NUMBER;
BEGIN
    -- Verificar si el código ya existe de manera más eficiente
    SELECT COUNT(*) INTO v_count 
    FROM PRODUCTO 
    WHERE CODIGO_PRODUCTO = p_codigo;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Código de producto duplicado: ' || p_codigo);
    END IF;

    -- Verificar que la categoría existe
    SELECT COUNT(*) INTO v_count 
    FROM CATEGORIA 
    WHERE ID_CATEGORIA = p_categoria_id;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Categoría no existe: ' || p_categoria_id);
    END IF;

    -- Insertar producto
    INSERT INTO PRODUCTO (
        ID_PRODUCTO, CODIGO_PRODUCTO, NOMBRE_PRODUCTO, DESRIPCION_PRODUCTO, 
        MATERIAL, ID_CATEGORIA, ESTADO, IMAGEN
    )
    VALUES (
        producto_seq.NEXTVAL, p_codigo, p_nombre, p_descripcion, 
        p_material, p_categoria_id, 'Activo', NVL(p_imagen, 'default-image.jpg')
    )
    RETURNING ID_PRODUCTO INTO v_producto_id;

    -- Insertar inventario
    INSERT INTO INVENTARIO (
        INVENTARIO_ID, PRODUCTO_ID, TALLA, COLOR, STOCK_ACTUAL, 
        STOCK_MINIMO, PRECIO_VENTA
    )
    VALUES (
        inventario_seq.NEXTVAL, v_producto_id, p_talla, p_color, 
        p_stock_actual, p_stock_minimo, p_precio_venta
    );
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20002, 'Error al registrar producto: ' || SQLERRM);
END;

/*Consultar producto*/
CREATE OR REPLACE PROCEDURE consultar_producto (
    p_codigo      IN  VARCHAR2,
    p_result      OUT SYS_REFCURSOR
)
IS
    v_codigo_numero NUMBER;
    v_is_numeric NUMBER;
BEGIN
    -- Verificar si el parámetro es numérico
    BEGIN
        v_codigo_numero := TO_NUMBER(p_codigo);
        v_is_numeric := 1;
    EXCEPTION
        WHEN VALUE_ERROR THEN
            v_is_numeric := 0;
    END;
    
    OPEN p_result FOR
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
        LEFT JOIN 
            INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
        WHERE 
            (v_is_numeric = 1 AND p.CODIGO_PRODUCTO = v_codigo_numero)
            OR (v_is_numeric = 0 AND LOWER(p.NOMBRE_PRODUCTO) LIKE '%' || LOWER(p_codigo) || '%');
END;
/

/*Actualizar producto*/
CREATE OR REPLACE PROCEDURE actualizar_producto (
    p_producto_id   IN  NUMBER,
    p_codigo        IN  NUMBER,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2,
    p_material      IN  VARCHAR2,
    p_categoria_id  IN  NUMBER,
    p_imagen        IN  VARCHAR2 DEFAULT NULL,
    p_talla         IN  VARCHAR2,
    p_color         IN  VARCHAR2,
    p_stock_actual  IN  NUMBER,
    p_stock_minimo  IN  NUMBER,
    p_precio_venta  IN  NUMBER
)
IS
    v_count NUMBER;
BEGIN
    -- Verificar si el producto existe por ID
    SELECT COUNT(*) INTO v_count 
    FROM PRODUCTO 
    WHERE ID_PRODUCTO = p_producto_id;
    
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Producto no encontrado con ID: ' || p_producto_id);
    END IF;
    
    -- Actualizar producto usando el ID directamente
    UPDATE PRODUCTO
    SET CODIGO_PRODUCTO = p_codigo,
        NOMBRE_PRODUCTO = p_nombre,
        DESRIPCION_PRODUCTO = p_descripcion,
        MATERIAL = p_material,
        ID_CATEGORIA = p_categoria_id,
        IMAGEN = NVL(p_imagen, IMAGEN)
    WHERE ID_PRODUCTO = p_producto_id;
    
    -- Actualizar inventario usando el ID del producto directamente
    UPDATE INVENTARIO
    SET TALLA = p_talla,
        COLOR = p_color,
        STOCK_ACTUAL = p_stock_actual,
        STOCK_MINIMO = p_stock_minimo,
        PRECIO_VENTA = p_precio_venta
    WHERE PRODUCTO_ID = p_producto_id;
    
    COMMIT;
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20004, 'Error al actualizar producto: ' || SQLERRM);
END;
/

/*Deshabilitar producto*/
CREATE OR REPLACE PROCEDURE deshabilitar_producto (
    p_codigo IN VARCHAR2
)
IS
BEGIN
    UPDATE PRODUCTO
       SET ESTADO = 'Inactivo'
     WHERE CODIGO_PRODUCTO = p_codigo;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Producto no encontrado');
    END IF;
END;
/

/*Habilitar producto*/
CREATE OR REPLACE PROCEDURE habilitar_producto (
    p_codigo IN VARCHAR2
)
IS
BEGIN
    UPDATE PRODUCTO
       SET ESTADO = 'Activo'
     WHERE CODIGO_PRODUCTO = p_codigo;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Producto no encontrado');
    END IF;
END;
/

--Mostrar producto segun su categoria que me pidieron
CREATE OR REPLACE PROCEDURE listar_productos_por_categoria (
    p_categoria_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
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
        LEFT JOIN 
            INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
        WHERE 
            p.ID_CATEGORIA = p_categoria_id
            AND p.ESTADO = 'Activo'
        ORDER BY p.ID_PRODUCTO;
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
        LEFT JOIN 
            INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
        WHERE 
            (LOWER(p.NOMBRE_PRODUCTO) LIKE '%' || LOWER(p_nombre) || '%'
            OR LOWER(p.DESRIPCION_PRODUCTO) LIKE '%' || LOWER(p_nombre) || '%'
            OR TO_CHAR(p.CODIGO_PRODUCTO) LIKE '%' || p_nombre || '%')
        ORDER BY p.ID_PRODUCTO;
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
            CODIGO_PRODUCTO,
            NOMBRE_PRODUCTO,
            DESRIPCION_PRODUCTO,
            MATERIAL,
            IMAGEN,
            ESTADO
        FROM producto
        WHERE ID_CATEGORIA = p_categoria_id;
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
            CODIGO_PRODUCTO,
            NOMBRE_PRODUCTO,
            DESRIPCION_PRODUCTO,
            MATERIAL,
            ESTADO
        FROM producto
        WHERE ID_CATEGORIA = p_categoria_id;
END;
/
--------------------------------------------------------------------------------
/*Categoria*/
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE registrar_categoria (
    p_codigo        IN  NUMBER,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    -- Verificar si el código ya existe
    SELECT COUNT(*) INTO v_count FROM CATEGORIA WHERE CODIGO_CATEGORIA = p_codigo;
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'El código de categoría ya existe');
    END IF;
    
    -- Verificar si el nombre ya existe
    SELECT COUNT(*) INTO v_count FROM CATEGORIA WHERE UPPER(NOMBRE_CATEGORIA) = UPPER(p_nombre);
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'El nombre de categoría ya existe');
    END IF;
    
    -- Insertar si no existe
    INSERT INTO CATEGORIA (
        ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, DESRIPCION_CATEGORIA, ESTADO
    )
    VALUES (
        categoria_seq.NEXTVAL, p_codigo, p_nombre, p_descripcion, 'Activo'
    );
END;
/

CREATE OR REPLACE PROCEDURE actualizar_categoria (
    p_categoria_id  IN  NUMBER,
    p_codigo        IN  NUMBER,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2,
    p_estado        IN  VARCHAR2 DEFAULT 'Activo'
) IS 
BEGIN 
    -- Actualizar la categoría directamente
    UPDATE CATEGORIA 
    SET CODIGO_CATEGORIA      = p_codigo,
        NOMBRE_CATEGORIA      = p_nombre,
        DESRIPCION_CATEGORIA = p_descripcion,
        ESTADO               = p_estado
    WHERE ID_CATEGORIA = p_categoria_id;
    
    -- Verificar que se actualizó al menos una fila
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Categoría no encontrada');
    END IF;
    
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(-20005, 'Código o nombre de categoría duplicado');
    WHEN OTHERS THEN
        -- Re-lanzar la excepción original
        RAISE;
END;
/

-- Deshabilitar categoría
CREATE OR REPLACE PROCEDURE deshabilitar_categoria (
    p_categoria_id IN NUMBER
)
IS
BEGIN
    UPDATE CATEGORIA
       SET ESTADO = 'Inactivo'
     WHERE ID_CATEGORIA = p_categoria_id;
    
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'Categoría no encontrada');
    END IF;
END;
/

-- Habilitar categoría
CREATE OR REPLACE PROCEDURE habilitar_categoria (
    p_categoria_id IN NUMBER
)
IS
BEGIN
    UPDATE CATEGORIA
       SET ESTADO = 'Activo'
     WHERE ID_CATEGORIA = p_categoria_id;
    
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20008, 'Categoría no encontrada');
    END IF;
END;
/

--------------------------------------------------------------------------------
/*Cliente*/
--------------------------------------------------------------------------------
/*Registrar cliente*/
CREATE OR REPLACE PROCEDURE registrar_cliente (
    p_cedula      IN  VARCHAR2,
    p_nombre      IN  VARCHAR2,
    p_apellidos   IN  VARCHAR2,
    p_email       IN  VARCHAR2,
    p_telefono    IN  VARCHAR2,
    p_direccion   IN  VARCHAR2,
    p_password    IN  VARCHAR2
)
IS
BEGIN
    INSERT INTO CLIENTE (
        ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL,
        TELEFONO, DIRECCION, CONTRASENA, ESTADO
    )
    VALUES (
        cliente_seq.NEXTVAL, p_cedula, p_nombre, p_apellidos, p_email,
        p_telefono, p_direccion, p_password, 'Activo'
    );
EXCEPTION
    WHEN dup_val_on_index THEN
        RAISE_APPLICATION_ERROR(-20008, 'Cédula o correo ya registrado');
END;
/

/*Actualizar cliente*/
CREATE OR REPLACE PROCEDURE actualizar_cliente (
    p_cedula      IN  VARCHAR2,
    p_nombre      IN  VARCHAR2,
    p_apellidos   IN  VARCHAR2,
    p_email       IN  VARCHAR2,
    p_telefono    IN  VARCHAR2,
    p_direccion   IN  VARCHAR2,
    p_contrasena  IN  VARCHAR2 DEFAULT NULL
)
IS
BEGIN
    IF p_contrasena IS NULL THEN
        UPDATE CLIENTE
           SET NOMBRE     = p_nombre,
               APELLIDOS  = p_apellidos,
               EMAIL      = p_email,
               TELEFONO   = p_telefono,
               DIRECCION  = p_direccion
         WHERE CEDULA = p_cedula;
    ELSE
        UPDATE CLIENTE
           SET NOMBRE     = p_nombre,
               APELLIDOS  = p_apellidos,
               EMAIL      = p_email,
               TELEFONO   = p_telefono,
               DIRECCION  = p_direccion,
               CONTRASENA = p_contrasena
         WHERE CEDULA = p_cedula;
    END IF;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20009, 'Cliente no encontrado');
    END IF;
END;
/

/*Deshabilitar cliente*/
CREATE OR REPLACE PROCEDURE deshabilitar_cliente (
    p_cedula IN VARCHAR2
)
IS
BEGIN
    UPDATE CLIENTE
       SET ESTADO = 'Inactivo'
     WHERE CEDULA = p_cedula;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Cliente no encontrado');
    END IF;
END;
/

/*Habilitar cliente*/
CREATE OR REPLACE PROCEDURE habilitar_cliente (
    p_cedula IN VARCHAR2
)
IS
BEGIN
    UPDATE CLIENTE
       SET ESTADO = 'Activo'
     WHERE CEDULA = p_cedula;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Cliente no encontrado');
    END IF;
END;
/

--------------------------------------------------------------------------------
/*Proveedor/
--------------------------------------------------------------------------------
/*Registrar proveedor*/
CREATE OR REPLACE PROCEDURE registrar_proveedor (
    p_cedula      IN  VARCHAR2,
    p_nombre      IN  VARCHAR2,
    p_apellidos   IN  VARCHAR2,
    p_email       IN  VARCHAR2,
    p_telefono    IN  VARCHAR2,
    p_direccion   IN  VARCHAR2
)
IS
BEGIN
    INSERT INTO PROVEEDOR (
        ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL,
        TELEFONO, DIRECCION, ESTADO
    )
    VALUES (
        proveedor_seq.NEXTVAL, p_cedula, p_nombre, p_apellidos, p_email,
        p_telefono, p_direccion, 'Activo'
    );
EXCEPTION
    WHEN dup_val_on_index THEN
        RAISE_APPLICATION_ERROR(-20011, 'Cédula de proveedor duplicada');
END;
/

/*Actualizar proveedor*/
CREATE OR REPLACE PROCEDURE actualizar_proveedor (
    p_cedula      IN  VARCHAR2,
    p_nombre      IN  VARCHAR2,
    p_apellidos   IN  VARCHAR2,
    p_email       IN  VARCHAR2,
    p_telefono    IN  VARCHAR2,
    p_direccion   IN  VARCHAR2
)
IS
BEGIN
    UPDATE PROVEEDOR
       SET NOMBRE     = p_nombre,
           APELLIDOS  = p_apellidos,
           EMAIL      = p_email,
           TELEFONO   = p_telefono,
           DIRECCION  = p_direccion
     WHERE CEDULA = p_cedula;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20012, 'Proveedor no encontrado');
    END IF;
END;
/

/**Deshabilitar proveedor*/
CREATE OR REPLACE PROCEDURE deshabilitar_proveedor (
    p_cedula IN VARCHAR2
)
IS
BEGIN
    UPDATE PROVEEDOR
       SET ESTADO = 'Inactivo'
     WHERE CEDULA = p_cedula;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20013, 'Proveedor no encontrado');
    END IF;
END;
/

/**Habilitar proveedor*/
CREATE OR REPLACE PROCEDURE habilitar_proveedor (
    p_cedula IN VARCHAR2
)
IS
BEGIN
    UPDATE PROVEEDOR
       SET ESTADO = 'Activo'
     WHERE CEDULA = p_cedula;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20013, 'Proveedor no encontrado');
    END IF;
END;
/

--------------------------------------------------------------------------------
/*Ventas*/
--------------------------------------------------------------------------------
/*Registrar venta*/
CREATE OR REPLACE PROCEDURE registrar_venta (
    p_cliente_id  IN  NUMBER,
    p_monto_total IN  NUMBER,
    p_estado      IN  VARCHAR2 DEFAULT 'Pendiente',
    p_result      OUT NUMBER
)
IS
    v_codigo_venta VARCHAR2(50);
BEGIN
    -- Generar código de venta único
    v_codigo_venta := 'V' || TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') || p_cliente_id;
    
    -- Obtener el siguiente ID de la secuencia
    SELECT venta_seq.NEXTVAL INTO p_result FROM DUAL;
    
    -- Insertar venta con valores por defecto para campos requeridos
    INSERT INTO VENTA (
        VENTA_ID, CODIGO_VENTA, CLIENTE_ID, FECHA_VENTA, 
        SUBTOTAL, IMPUESTO, DESCUENTO, TOTAL, MEDIO_PAGO, ESTADO
    ) VALUES (
        p_result, v_codigo_venta, p_cliente_id, SYSDATE,
        p_monto_total, 0, 0, p_monto_total, 'Efectivo', p_estado
    );
    
    COMMIT;
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20014, 'Error al registrar venta: ' || SQLERRM);
END registrar_venta;
/

/*Cambiar estado de venta*/
CREATE OR REPLACE PROCEDURE cambiar_estado_venta (
    p_venta_id      IN NUMBER,
    p_nuevo_estado  IN VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    -- Verificar que la venta existe
    SELECT COUNT(*) INTO v_count FROM VENTA WHERE VENTA_ID = p_venta_id;
    
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Venta no encontrada con ID: ' || p_venta_id);
    END IF;
    
    -- Actualizar el estado
    UPDATE VENTA 
    SET ESTADO = p_nuevo_estado
    WHERE VENTA_ID = p_venta_id;
    
    COMMIT;
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20003, 'Error al cambiar estado de venta: ' || SQLERRM);
END cambiar_estado_venta;
/

CREATE OR REPLACE PROCEDURE registrar_venta_completa (
    p_codigo_venta IN  VARCHAR2,
    p_cliente_id   IN  NUMBER,
    p_subtotal     IN  NUMBER,
    p_impuesto     IN  NUMBER,
    p_descuento    IN  NUMBER DEFAULT 0,
    p_total        IN  NUMBER,
    p_medio_pago   IN  VARCHAR2,
    p_estado       IN  VARCHAR2 DEFAULT 'Pendiente',
    p_venta_id     OUT NUMBER
)
IS
BEGIN
    -- Obtener el siguiente ID de la secuencia
    SELECT venta_seq.NEXTVAL INTO p_venta_id FROM DUAL;
    
    -- Insertar la venta
    INSERT INTO VENTA (
        VENTA_ID, CODIGO_VENTA, CLIENTE_ID, FECHA_VENTA,
        SUBTOTAL, IMPUESTO, DESCUENTO, TOTAL, MEDIO_PAGO, ESTADO
    ) VALUES (
        p_venta_id, p_codigo_venta, p_cliente_id, SYSDATE,
        p_subtotal, p_impuesto, NVL(p_descuento, 0), p_total, p_medio_pago, p_estado
    );
    
    COMMIT;
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Error al registrar venta completa: ' || SQLERRM);
END registrar_venta_completa;
/

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- registrar_asociacion_proveedor_producto
CREATE OR REPLACE PROCEDURE REGISTRAR_ASOCIACION_PROVEEDOR_PRODUCTO (
    p_producto_id  IN NUMBER,
    p_proveedor_id IN NUMBER,
    p_precio_compra IN NUMBER
)
IS
    v_count NUMBER;
BEGIN
    -- Verificar que el producto existe y está activo
    SELECT COUNT(*) INTO v_count 
    FROM PRODUCTO 
    WHERE ID_PRODUCTO = p_producto_id AND ESTADO = 'Activo';

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'Producto no existe o no está activo');
    END IF;

    -- Verificar que el proveedor existe y está activo
    SELECT COUNT(*) INTO v_count 
    FROM PROVEEDOR 
    WHERE ID_PROVEEDOR = p_proveedor_id AND ESTADO = 'Activo';

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20022, 'Proveedor no existe o no está activo');
    END IF;

    -- Verificar que no existe ya una asociación activa entre producto y proveedor
    SELECT COUNT(*) INTO v_count 
    FROM PRODUCTO_PROVEEDOR 
    WHERE PRODUCTO_ID = p_producto_id AND PROVEEDOR_ID = p_proveedor_id AND ESTADO = 'Activo';

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20023, 'Ya existe una asociación activa entre este producto y proveedor');
    END IF;

    -- Validar precio
    IF p_precio_compra IS NULL OR p_precio_compra <= 0 THEN
        RAISE_APPLICATION_ERROR(-20024, 'El precio de compra debe ser mayor que cero');
    END IF;

    -- Insertar la asociación
    INSERT INTO PRODUCTO_PROVEEDOR (
        PRODUCTO_PROVEEDOR_ID, PRODUCTO_ID, PROVEEDOR_ID, PRECIO_COMPRA, ESTADO
    ) VALUES (
        asociacion_seq.NEXTVAL, p_producto_id, p_proveedor_id, p_precio_compra, 'Activo'
    );

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = -20021 OR SQLCODE = -20022 OR SQLCODE = -20023 OR SQLCODE = -20024 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20025, 'Error al registrar asociación: ' || SQLERRM);
        END IF;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- consultar_asociacion_producto_proveedor
CREATE OR REPLACE PROCEDURE CONSULTAR_ASOCIACION_PRODUCTO_PROVEEDOR (
    p_codigo_producto IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
    v_codigo_numero NUMBER;
    v_is_numeric NUMBER;
BEGIN
    -- Verificar si el parámetro es numérico
    BEGIN
        v_codigo_numero := TO_NUMBER(p_codigo_producto);
        v_is_numeric := 1;
    EXCEPTION
        WHEN VALUE_ERROR THEN
            v_is_numeric := 0;
    END;
    
    OPEN p_result FOR
        SELECT 
            a.PRODUCTO_PROVEEDOR_ID,
            a.PRODUCTO_ID,
            a.PROVEEDOR_ID,
            a.PRECIO_COMPRA,
            a.ESTADO,
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            pr.NOMBRE AS NOMBRE_PROVEEDOR,
            pr.APELLIDOS AS APELLIDOS_PROVEEDOR
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        WHERE 
            (v_is_numeric = 1 AND p.CODIGO_PRODUCTO = v_codigo_numero)
            OR (v_is_numeric = 0 AND UPPER(p.NOMBRE_PRODUCTO) LIKE '%' || UPPER(p_codigo_producto) || '%')
        ORDER BY a.PRODUCTO_PROVEEDOR_ID DESC;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- actualizar_asociacion_producto_proveedor
CREATE OR REPLACE PROCEDURE ACTUALIZAR_ASOCIACION_PRODUCTO_PROVEEDOR (
    p_asociacion_id     IN NUMBER,
    p_nuevo_producto_id IN NUMBER,
    p_nuevo_proveedor_id IN NUMBER,
    p_nuevo_precio IN NUMBER
)
IS
    v_count NUMBER;
    v_producto_actual NUMBER;
    v_proveedor_actual NUMBER;
BEGIN
    -- Verificar que la asociación existe
    SELECT COUNT(*), MAX(PRODUCTO_ID), MAX(PROVEEDOR_ID) 
    INTO v_count, v_producto_actual, v_proveedor_actual
    FROM PRODUCTO_PROVEEDOR 
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20031, 'Asociación no encontrada');
    END IF;

    -- Verificar que el nuevo producto existe y está activo
    SELECT COUNT(*) INTO v_count 
    FROM PRODUCTO 
    WHERE ID_PRODUCTO = p_nuevo_producto_id AND ESTADO = 'Activo';

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20032, 'Producto no existe o no está activo');
    END IF;

    -- Verificar que el nuevo proveedor existe y está activo
    SELECT COUNT(*) INTO v_count 
    FROM PROVEEDOR 
    WHERE ID_PROVEEDOR = p_nuevo_proveedor_id AND ESTADO = 'Activo';

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20033, 'Proveedor no existe o no está activo');
    END IF;

    -- Solo verificar duplicados si se cambió el producto o proveedor
    IF v_producto_actual != p_nuevo_producto_id OR v_proveedor_actual != p_nuevo_proveedor_id THEN
        SELECT COUNT(*) INTO v_count 
        FROM PRODUCTO_PROVEEDOR 
        WHERE PRODUCTO_ID = p_nuevo_producto_id 
          AND PROVEEDOR_ID = p_nuevo_proveedor_id 
          AND ESTADO = 'Activo'
          AND PRODUCTO_PROVEEDOR_ID != p_asociacion_id;

        IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20034, 'Ya existe otra asociación activa entre este producto y proveedor');
        END IF;
    END IF;

    -- Validar precio
    IF p_nuevo_precio IS NULL OR p_nuevo_precio <= 0 THEN
        RAISE_APPLICATION_ERROR(-20035, 'El precio de compra debe ser mayor que cero');
    END IF;

    -- Actualizar la asociación
    UPDATE PRODUCTO_PROVEEDOR
    SET PRODUCTO_ID = p_nuevo_producto_id,
        PROVEEDOR_ID = p_nuevo_proveedor_id,
        PRECIO_COMPRA = p_nuevo_precio
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE BETWEEN -20031 AND -20035 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20036, 'Error al actualizar asociación: ' || SQLERRM);
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
    UPDATE PRODUCTO_PROVEEDOR
    SET ESTADO = 'Inactivo'
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20023, 'Asociación no encontrada');
    END IF;
END;
/

-- habilitar_asociacion_producto_proveedor
CREATE OR REPLACE PROCEDURE habilitar_asociacion_producto_proveedor (
    p_asociacion_id IN NUMBER
)
IS
BEGIN
    UPDATE PRODUCTO_PROVEEDOR
    SET ESTADO = 'Activo'
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;

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
            a.PRODUCTO_PROVEEDOR_ID,
            p.CODIGO_PRODUCTO AS codigo_producto,
            p.NOMBRE_PRODUCTO AS producto,
            pr.NOMBRE || ' ' || pr.APELLIDOS AS proveedor,
            a.ESTADO
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- iniciar_sesion_usuario
CREATE OR REPLACE PROCEDURE INICIAR_SESION_USUARIO (
    p_email    IN VARCHAR2,
    p_password IN VARCHAR2,
    p_result   OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE
        WHERE (EMAIL = p_email OR CEDULA = p_email)
          AND CONTRASENA = p_password
          AND ESTADO = 'Activo';
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
        SELECT ID_CLIENTE, nombre, apellidos, email, contrasena
        FROM cliente
        WHERE email = p_email
          AND estado = 'Activo';
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
            v.CODIGO_VENTA AS codigo_venta,
            v.FECHA_VENTA,
            v.TOTAL,
            v.ESTADO
        FROM venta v
        WHERE v.CLIENTE_ID = p_cliente_id
        ORDER BY v.FECHA_VENTA DESC;
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
            v.CODIGO_VENTA,
            c.NOMBRE || ' ' || c.APELLIDOS AS cliente,
            v.FECHA_VENTA,
            v.TOTAL,
            v.ESTADO
        FROM venta v
        JOIN cliente c ON v.CLIENTE_ID = c.ID_CLIENTE
        ORDER BY v.FECHA_VENTA DESC;
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
            v.CODIGO_VENTA,
            c.NOMBRE || ' ' || c.APELLIDOS AS cliente,
            v.FECHA_VENTA,
            v.TOTAL,
            v.ESTADO
        FROM venta v
        JOIN cliente c ON v.cliente_id = c.ID_CLIENTE
        WHERE TRUNC(v.FECHA_VENTA) BETWEEN TRUNC(p_fecha_inicio) AND TRUNC(p_fecha_fin)
        ORDER BY v.FECHA_VENTA DESC;
END;
/

-- Procedimiento para obtener categorías principales
CREATE OR REPLACE PROCEDURE sp_obtener_categorias_principales (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, 
               DESRIPCION_CATEGORIA, ESTADO
        FROM CATEGORIA
        WHERE CODIGO_CATEGORIA IN (100, 200, 300, 400)
          AND ESTADO = 'Activo'
        ORDER BY CODIGO_CATEGORIA;
END;
/

-- Procedimiento para obtener todas las categorías activas
CREATE OR REPLACE PROCEDURE sp_obtener_todas_categorias_activas (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, 
               DESRIPCION_CATEGORIA, ESTADO
        FROM CATEGORIA
        WHERE ESTADO = 'Activo'
        ORDER BY CODIGO_CATEGORIA;
END;
/

-- Procedimiento para obtener todas las categorías (activas e inactivas)
CREATE OR REPLACE PROCEDURE sp_obtener_todas_categorias (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, 
               DESRIPCION_CATEGORIA, ESTADO
        FROM CATEGORIA
        ORDER BY CODIGO_CATEGORIA;
END;
/

-- Procedimiento para obtener categorías inactivas
CREATE OR REPLACE PROCEDURE sp_obtener_categorias_inactivas (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, 
               DESRIPCION_CATEGORIA, ESTADO
        FROM CATEGORIA
        WHERE ESTADO = 'Inactivo'
        ORDER BY CODIGO_CATEGORIA;
END;
/

-- Procedimiento para buscar categorías por nombre
CREATE OR REPLACE PROCEDURE sp_buscar_categorias_por_nombre (
    p_nombre IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, 
               DESRIPCION_CATEGORIA, ESTADO
        FROM CATEGORIA
        WHERE LOWER(NOMBRE_CATEGORIA) LIKE '%' || LOWER(p_nombre) || '%'
           OR LOWER(DESRIPCION_CATEGORIA) LIKE '%' || LOWER(p_nombre) || '%'
        ORDER BY CODIGO_CATEGORIA;
END;
/

-- Procedimiento para obtener categoría por ID
CREATE OR REPLACE PROCEDURE sp_obtener_categoria_por_id (
    p_categoria_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CATEGORIA, CODIGO_CATEGORIA, NOMBRE_CATEGORIA, 
               DESRIPCION_CATEGORIA, ESTADO
        FROM CATEGORIA
        WHERE ID_CATEGORIA = p_categoria_id;
END;
/


-- Procedimiento para obtener categorías con cantidad de productos
CREATE OR REPLACE PROCEDURE sp_obtener_categorias_con_productos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            c.ID_CATEGORIA,
            c.CODIGO_CATEGORIA,
            c.NOMBRE_CATEGORIA,
            c.DESRIPCION_CATEGORIA,
            c.ESTADO,
            COUNT(p.ID_PRODUCTO) AS cantidad_productos
        FROM CATEGORIA c
        LEFT JOIN PRODUCTO p ON c.ID_CATEGORIA = p.ID_CATEGORIA AND p.ESTADO = 'Activo'
        WHERE c.ESTADO = 'Activo'
        GROUP BY c.ID_CATEGORIA, c.CODIGO_CATEGORIA, c.NOMBRE_CATEGORIA, c.DESRIPCION_CATEGORIA, c.ESTADO
        ORDER BY c.CODIGO_CATEGORIA;
END;
/

-- Procedimiento para buscar clientes (activos solamente)
CREATE OR REPLACE PROCEDURE sp_buscar_clientes (
    p_busqueda IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        WHERE (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
               UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
               CEDULA LIKE '%' || p_busqueda || '%')
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para obtener cliente por ID
CREATE OR REPLACE PROCEDURE sp_obtener_cliente_por_id (
    p_cliente_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        WHERE ID_CLIENTE = p_cliente_id;
END;
/

-- Procedimiento para obtener cliente por cédula
CREATE OR REPLACE PROCEDURE sp_obtener_cliente_por_cedula (
    p_cedula IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        WHERE CEDULA = p_cedula;
END;
/

-- Procedimiento para obtener cliente por email
CREATE OR REPLACE PROCEDURE sp_obtener_cliente_por_email (
    p_email IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        WHERE EMAIL = p_email;
END;
/

-- Procedimiento para obtener clientes activos
CREATE OR REPLACE PROCEDURE sp_obtener_clientes_activos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        WHERE ESTADO = 'Activo'
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para obtener clientes inactivos
CREATE OR REPLACE PROCEDURE sp_obtener_clientes_inactivos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        WHERE ESTADO = 'Inactivo'
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para obtener todos los clientes
CREATE OR REPLACE PROCEDURE sp_obtener_todos_clientes (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para buscar todos los clientes (activos e inactivos)
CREATE OR REPLACE PROCEDURE sp_buscar_todos_clientes (
    p_busqueda IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, CONTRASENA, ESTADO
        FROM CLIENTE 
        WHERE (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
               UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
               CEDULA LIKE '%' || p_busqueda || '%')
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Función para verificar si una cédula existe
CREATE OR REPLACE FUNCTION fn_existe_cliente_cedula (
    p_cedula IN VARCHAR2
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM CLIENTE
    WHERE CEDULA = p_cedula;
    
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

-- Función para contar clientes activos
CREATE OR REPLACE FUNCTION fn_contar_clientes_activos RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM CLIENTE
    WHERE ESTADO = 'Activo';
    
    RETURN v_count;
END;
/

-- Función para contar clientes inactivos
CREATE OR REPLACE FUNCTION fn_contar_clientes_inactivos RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM CLIENTE
    WHERE ESTADO = 'Inactivo';
    
    RETURN v_count;
END;
/

-- Función para contar total de clientes
CREATE OR REPLACE FUNCTION fn_contar_total_clientes RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM CLIENTE;
    
    RETURN v_count;
END;
/

-- Función para verificar si un cliente existe por ID
CREATE OR REPLACE FUNCTION fn_existe_cliente_por_id (
    p_cliente_id IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM CLIENTE
    WHERE ID_CLIENTE = p_cliente_id;
    
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

-- Función para obtener el estado de un cliente
CREATE OR REPLACE FUNCTION fn_estado_cliente (
    p_cliente_id IN NUMBER
) RETURN VARCHAR2
IS
    v_estado VARCHAR2(20);
BEGIN
    SELECT ESTADO INTO v_estado
    FROM CLIENTE
    WHERE ID_CLIENTE = p_cliente_id;
    
    RETURN v_estado;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

-- Función para verificar credenciales (alternativa a iniciar sesión)
CREATE OR REPLACE FUNCTION fn_verificar_credenciales_cliente (
    p_username IN VARCHAR2,
    p_password IN VARCHAR2,
    p_es_email IN NUMBER
) RETURN NUMBER
IS
    v_count NUMBER;
BEGIN
    IF p_es_email = 1 THEN
        -- Buscar por email
        SELECT COUNT(*) INTO v_count
        FROM CLIENTE
        WHERE EMAIL = p_username 
          AND CONTRASENA = p_password
          AND ESTADO = 'Activo';
    ELSE
        -- Buscar por cédula
        SELECT COUNT(*) INTO v_count
        FROM CLIENTE
        WHERE CEDULA = p_username 
          AND CONTRASENA = p_password
          AND ESTADO = 'Activo';
    END IF;
    
    RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

-- Función para obtener ID de cliente por email
CREATE OR REPLACE FUNCTION fn_obtener_id_cliente_por_email (
    p_email IN VARCHAR2
) RETURN NUMBER
IS
    v_id NUMBER;
BEGIN
    SELECT ID_CLIENTE INTO v_id
    FROM CLIENTE
    WHERE EMAIL = p_email
      AND ESTADO = 'Activo';
    
    RETURN v_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

-- Función para obtener ID de cliente por cédula
CREATE OR REPLACE FUNCTION fn_obtener_id_cliente_por_cedula (
    p_cedula IN VARCHAR2
) RETURN NUMBER
IS
    v_id NUMBER;
BEGIN
    SELECT ID_CLIENTE INTO v_id
    FROM CLIENTE
    WHERE CEDULA = p_cedula
      AND ESTADO = 'Activo';
    
    RETURN v_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

-- Función para obtener nombre completo del cliente
CREATE OR REPLACE FUNCTION fn_nombre_completo_cliente (
    p_cliente_id IN NUMBER
) RETURN VARCHAR2
IS
    v_nombre_completo VARCHAR2(200);
BEGIN
    SELECT NOMBRE || ' ' || APELLIDOS
    INTO v_nombre_completo
    FROM CLIENTE
    WHERE ID_CLIENTE = p_cliente_id;

    RETURN v_nombre_completo;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

-- Procedimiento para validar datos de cliente antes de insertar
CREATE OR REPLACE PROCEDURE sp_validar_datos_cliente (
    p_cedula IN VARCHAR2,
    p_email IN VARCHAR2,
    p_excluir_id IN NUMBER DEFAULT NULL,
    p_resultado OUT NUMBER -- 0=OK, 1=Cedula duplicada, 2=Email duplicado
)
IS
    v_count_cedula NUMBER;
    v_count_email NUMBER;
BEGIN
    -- Verificar cédula duplicada
    IF p_excluir_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count_cedula
        FROM CLIENTE
        WHERE CEDULA = p_cedula AND ID_CLIENTE != p_excluir_id;
    ELSE
        SELECT COUNT(*) INTO v_count_cedula
        FROM CLIENTE
        WHERE CEDULA = p_cedula;
    END IF;
    
    IF v_count_cedula > 0 THEN
        p_resultado := 1; -- Cédula duplicada
        RETURN;
    END IF;
    
    -- Verificar email duplicado
    IF p_excluir_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count_email
        FROM CLIENTE
        WHERE EMAIL = p_email AND ID_CLIENTE != p_excluir_id;
    ELSE
        SELECT COUNT(*) INTO v_count_email
        FROM CLIENTE
        WHERE EMAIL = p_email;
    END IF;
    
    IF v_count_email > 0 THEN
        p_resultado := 2; -- Email duplicado
        RETURN;
    END IF;
    
    p_resultado := 0; -- Todo OK
END;
/

-- Procedimiento para obtener clientes con filtros avanzados
CREATE OR REPLACE PROCEDURE sp_obtener_clientes_filtrados (
    p_estado IN VARCHAR2 DEFAULT NULL,
    p_busqueda IN VARCHAR2 DEFAULT NULL,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    IF p_estado IS NOT NULL AND p_busqueda IS NOT NULL THEN
        OPEN p_result FOR
            SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, CONTRASENA, ESTADO
            FROM CLIENTE 
            WHERE ESTADO = p_estado
              AND (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   CEDULA LIKE '%' || p_busqueda || '%')
            ORDER BY NOMBRE, APELLIDOS;
    ELSIF p_estado IS NOT NULL THEN
        OPEN p_result FOR
            SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, CONTRASENA, ESTADO
            FROM CLIENTE 
            WHERE ESTADO = p_estado
            ORDER BY NOMBRE, APELLIDOS;
    ELSIF p_busqueda IS NOT NULL THEN
        OPEN p_result FOR
            SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, CONTRASENA, ESTADO
            FROM CLIENTE 
            WHERE (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   CEDULA LIKE '%' || p_busqueda || '%')
            ORDER BY NOMBRE, APELLIDOS;
    ELSE
        OPEN p_result FOR
            SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, CONTRASENA, ESTADO
            FROM CLIENTE 
            ORDER BY NOMBRE, APELLIDOS;
    END IF;
END;
/

-- Procedimiento para recuperar contraseña
CREATE OR REPLACE PROCEDURE sp_recuperar_contrasena (
    p_email IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, NOMBRE, APELLIDOS, EMAIL, CONTRASENA
        FROM CLIENTE
        WHERE EMAIL = p_email
          AND ESTADO = 'Activo';
END;
/

-- Procedimiento para cambiar contraseña
CREATE OR REPLACE PROCEDURE sp_cambiar_contrasena (
    p_cliente_id IN NUMBER,
    p_nueva_contrasena IN VARCHAR2
)
IS
    v_count NUMBER;
BEGIN
    -- Verificar que el cliente existe
    SELECT COUNT(*) INTO v_count
    FROM CLIENTE
    WHERE ID_CLIENTE = p_cliente_id;
    
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Cliente no encontrado');
    END IF;
    
    -- Actualizar contraseña
    UPDATE CLIENTE
    SET CONTRASENA = p_nueva_contrasena
    WHERE ID_CLIENTE = p_cliente_id;
    
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Error al actualizar contraseña');
    END IF;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Procedimiento para obtener top clientes por monto de ventas
CREATE OR REPLACE PROCEDURE sp_top_clientes_por_ventas (
    p_limite IN NUMBER DEFAULT 10,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT c.ID_CLIENTE, c.CEDULA, c.NOMBRE, c.APELLIDOS, c.EMAIL,
               COUNT(v.VENTA_ID) as total_ventas,
               NVL(SUM(v.TOTAL), 0) as monto_total
        FROM CLIENTE c
        LEFT JOIN VENTA v ON c.ID_CLIENTE = v.CLIENTE_ID
        WHERE c.ESTADO = 'Activo'
        GROUP BY c.ID_CLIENTE, c.CEDULA, c.NOMBRE, c.APELLIDOS, c.EMAIL
        ORDER BY monto_total DESC
        FETCH FIRST p_limite ROWS ONLY;
END;
/

-- Procedimiento para obtener clientes sin ventas
CREATE OR REPLACE PROCEDURE sp_clientes_sin_ventas (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT c.ID_CLIENTE, c.CEDULA, c.NOMBRE, c.APELLIDOS, c.EMAIL
        FROM CLIENTE c
        WHERE c.ESTADO = 'Activo'
          AND NOT EXISTS (
              SELECT 1 FROM VENTA v WHERE v.CLIENTE_ID = c.ID_CLIENTE
          )
        ORDER BY c.NOMBRE, c.APELLIDOS;
END;
/

-- Procedimiento para obtener clientes registrados en un período
CREATE OR REPLACE PROCEDURE sp_clientes_por_periodo (
    p_fecha_inicio IN DATE,
    p_fecha_fin IN DATE,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM CLIENTE
        WHERE TRUNC(SYSDATE) BETWEEN TRUNC(p_fecha_inicio) AND TRUNC(p_fecha_fin) -- Nota: Necesitarías una columna fecha_registro
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- 1. Procedimiento para obtener asociaciones activas
CREATE OR REPLACE PROCEDURE OBTENER_ASOCIACIONES_ACTIVAS (
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT 
            a.PRODUCTO_PROVEEDOR_ID,
            a.PRODUCTO_ID,
            a.PROVEEDOR_ID,
            a.PRECIO_COMPRA,
            a.ESTADO,
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            pr.NOMBRE AS NOMBRE_PROVEEDOR,
            pr.APELLIDOS AS APELLIDOS_PROVEEDOR
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        WHERE a.ESTADO = 'Activo'
        ORDER BY a.PRODUCTO_PROVEEDOR_ID DESC;
END OBTENER_ASOCIACIONES_ACTIVAS;
/

-- 2. Procedimiento para obtener asociaciones inactivas
CREATE OR REPLACE PROCEDURE OBTENER_ASOCIACIONES_INACTIVAS (
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT 
            a.PRODUCTO_PROVEEDOR_ID,
            a.PRODUCTO_ID,
            a.PROVEEDOR_ID,
            a.PRECIO_COMPRA,
            a.ESTADO,
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            pr.NOMBRE AS NOMBRE_PROVEEDOR,
            pr.APELLIDOS AS APELLIDOS_PROVEEDOR
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        WHERE a.ESTADO = 'Inactivo'
        ORDER BY a.PRODUCTO_PROVEEDOR_ID DESC;
END OBTENER_ASOCIACIONES_INACTIVAS;
/

-- 3. Procedimiento para obtener todas las asociaciones
CREATE OR REPLACE PROCEDURE OBTENER_TODAS_ASOCIACIONES (
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT 
            a.PRODUCTO_PROVEEDOR_ID,
            a.PRODUCTO_ID,
            a.PROVEEDOR_ID,
            a.PRECIO_COMPRA,
            a.ESTADO,
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            pr.NOMBRE AS NOMBRE_PROVEEDOR,
            pr.APELLIDOS AS APELLIDOS_PROVEEDOR
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        ORDER BY a.PRODUCTO_PROVEEDOR_ID DESC;
END OBTENER_TODAS_ASOCIACIONES;
/

-- 4. Procedimiento para buscar asociaciones
CREATE OR REPLACE PROCEDURE BUSCAR_ASOCIACIONES (
    p_busqueda IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT 
            a.PRODUCTO_PROVEEDOR_ID,
            a.PRODUCTO_ID,
            a.PROVEEDOR_ID,
            a.PRECIO_COMPRA,
            a.ESTADO,
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            pr.NOMBRE AS NOMBRE_PROVEEDOR,
            pr.APELLIDOS AS APELLIDOS_PROVEEDOR
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        WHERE (UPPER(p.NOMBRE_PRODUCTO) LIKE UPPER('%' || p_busqueda || '%') OR 
               UPPER(pr.NOMBRE) LIKE UPPER('%' || p_busqueda || '%') OR 
               UPPER(pr.APELLIDOS) LIKE UPPER('%' || p_busqueda || '%') OR
               TO_CHAR(p.CODIGO_PRODUCTO) LIKE '%' || p_busqueda || '%')
        ORDER BY a.PRODUCTO_PROVEEDOR_ID DESC;
END BUSCAR_ASOCIACIONES;
/

-- 5. Procedimiento para obtener asociación por ID
CREATE OR REPLACE PROCEDURE OBTENER_ASOCIACION_POR_ID (
    p_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT 
            a.PRODUCTO_PROVEEDOR_ID,
            a.PRODUCTO_ID,
            a.PROVEEDOR_ID,
            a.PRECIO_COMPRA,
            a.ESTADO,
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            pr.NOMBRE AS NOMBRE_PROVEEDOR,
            pr.APELLIDOS AS APELLIDOS_PROVEEDOR
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        WHERE a.PRODUCTO_PROVEEDOR_ID = p_id;
END OBTENER_ASOCIACION_POR_ID;
/

-- 6. Procedimiento para obtener productos activos
CREATE OR REPLACE PROCEDURE OBTENER_PRODUCTOS_ACTIVOS (
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT ID_PRODUCTO, CODIGO_PRODUCTO, NOMBRE_PRODUCTO 
        FROM PRODUCTO 
        WHERE ESTADO = 'Activo' 
        ORDER BY NOMBRE_PRODUCTO;
END OBTENER_PRODUCTOS_ACTIVOS;
/

-- 7. Procedimiento para obtener proveedores activos
CREATE OR REPLACE PROCEDURE OBTENER_PROVEEDORES_ACTIVOS (
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, NOMBRE, APELLIDOS 
        FROM PROVEEDOR 
        WHERE ESTADO = 'Activo' 
        ORDER BY NOMBRE, APELLIDOS;
END OBTENER_PROVEEDORES_ACTIVOS;
/

-- 8. Procedimiento para verificar si existe asociación activa
CREATE OR REPLACE PROCEDURE EXISTE_ASOCIACION_ACTIVA (
    p_producto_id IN NUMBER,
    p_proveedor_id IN NUMBER,
    p_existe OUT NUMBER
) AS
BEGIN
    SELECT COUNT(*) 
    INTO p_existe
    FROM PRODUCTO_PROVEEDOR 
    WHERE PRODUCTO_ID = p_producto_id 
    AND PROVEEDOR_ID = p_proveedor_id 
    AND ESTADO = 'Activo';
END EXISTE_ASOCIACION_ACTIVA;
/

-- 9. Procedimientos que ya existen (verificar si están en tu script)
-- Si no existen, aquí están:

CREATE OR REPLACE PROCEDURE REGISTRAR_ASOCIACION_PROVEEDOR_PRODUCTO (
    p_producto_id IN NUMBER,
    p_proveedor_id IN NUMBER,
    p_precio_compra IN NUMBER
) AS
BEGIN
    INSERT INTO PRODUCTO_PROVEEDOR (
        PRODUCTO_PROVEEDOR_ID,
        PRODUCTO_ID,
        PROVEEDOR_ID,
        PRECIO_COMPRA,
        ESTADO
    ) VALUES (
        asociacion_seq.NEXTVAL,
        p_producto_id,
        p_proveedor_id,
        p_precio_compra,
        'Activo'
    );
    COMMIT;
END REGISTRAR_ASOCIACION_PROVEEDOR_PRODUCTO;
/

CREATE OR REPLACE PROCEDURE ACTUALIZAR_ASOCIACION_PRODUCTO_PROVEEDOR (
    p_asociacion_id IN NUMBER,
    p_nuevo_producto_id IN NUMBER,
    p_nuevo_proveedor_id IN NUMBER,
    p_nuevo_precio IN NUMBER
) AS
BEGIN
    UPDATE PRODUCTO_PROVEEDOR
    SET PRODUCTO_ID = p_nuevo_producto_id,
        PROVEEDOR_ID = p_nuevo_proveedor_id,
        PRECIO_COMPRA = p_nuevo_precio
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;
    COMMIT;
END ACTUALIZAR_ASOCIACION_PRODUCTO_PROVEEDOR;
/

CREATE OR REPLACE PROCEDURE DESHABILITAR_ASOCIACION_PRODUCTO_PROVEEDOR (
    p_asociacion_id IN NUMBER
) AS
BEGIN
    UPDATE PRODUCTO_PROVEEDOR
    SET ESTADO = 'Inactivo'
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;
    COMMIT;
END DESHABILITAR_ASOCIACION_PRODUCTO_PROVEEDOR;
/

CREATE OR REPLACE PROCEDURE HABILITAR_ASOCIACION_PRODUCTO_PROVEEDOR (
    p_asociacion_id IN NUMBER
) AS
BEGIN
    UPDATE PRODUCTO_PROVEEDOR
    SET ESTADO = 'Activo'
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;
    COMMIT;
END HABILITAR_ASOCIACION_PRODUCTO_PROVEEDOR;
/

CREATE OR REPLACE PROCEDURE CONSULTAR_ASOCIACION_PRODUCTO_PROVEEDOR (
    p_codigo_producto IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_result FOR
        SELECT 
            a.PRODUCTO_PROVEEDOR_ID,
            a.PRODUCTO_ID,
            a.PROVEEDOR_ID,
            a.PRECIO_COMPRA,
            a.ESTADO,
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            pr.NOMBRE AS NOMBRE_PROVEEDOR,
            pr.APELLIDOS AS APELLIDOS_PROVEEDOR
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        WHERE TO_CHAR(p.CODIGO_PRODUCTO) = p_codigo_producto;
END CONSULTAR_ASOCIACION_PRODUCTO_PROVEEDOR;
/

-- Procedimiento para obtener nombre de categoría por ID
CREATE OR REPLACE PROCEDURE obtener_nombre_categoria (
    p_categoria_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT NOMBRE_CATEGORIA
        FROM CATEGORIA 
        WHERE ID_CATEGORIA = p_categoria_id;
END;
/

-- Procedimiento para listar todos los productos con inventario
CREATE OR REPLACE PROCEDURE listar_todos_productos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
               p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
               p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
               i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
               i.STOCK_MINIMO, i.PRECIO_VENTA
        FROM PRODUCTO p
        JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
        LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
        ORDER BY p.ID_PRODUCTO;
END;
/

-- Procedimiento para buscar producto por ID
CREATE OR REPLACE PROCEDURE buscar_producto_por_id (
    p_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
               p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
               p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
               i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
               i.STOCK_MINIMO, i.PRECIO_VENTA
        FROM PRODUCTO p
        JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
        LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
        WHERE p.ID_PRODUCTO = p_id;
END;
/

-- Procedimiento para listar productos activos
CREATE OR REPLACE PROCEDURE listar_productos_activos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
               p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
               p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
               i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
               i.STOCK_MINIMO, i.PRECIO_VENTA
        FROM PRODUCTO p
        JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
        LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
        WHERE p.ESTADO = 'Activo'
        ORDER BY p.ID_PRODUCTO;
END;
/

-- Procedimiento para listar productos inactivos
CREATE OR REPLACE PROCEDURE listar_productos_inactivos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
               p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
               p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
               i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
               i.STOCK_MINIMO, i.PRECIO_VENTA
        FROM PRODUCTO p
        JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
        LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
        WHERE p.ESTADO = 'Inactivo'
        ORDER BY p.ID_PRODUCTO;
END;
/

-- Procedimiento para verificar existencia de categoría
CREATE OR REPLACE PROCEDURE verificar_categoria_existe (
    p_categoria_id IN NUMBER,
    p_existe OUT NUMBER
)
IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) 
    INTO v_count
    FROM CATEGORIA
    WHERE ID_CATEGORIA = p_categoria_id;
    
    p_existe := CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
END;
/

-- Procedimiento para obtener estadísticas de productos
CREATE OR REPLACE PROCEDURE obtener_estadisticas_productos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT 
            'Total' as estado,
            COUNT(*) as cantidad
        FROM PRODUCTO
        UNION ALL
        SELECT 
            ESTADO as estado,
            COUNT(*) as cantidad
        FROM PRODUCTO
        GROUP BY ESTADO
        ORDER BY estado;
END;
/

-- Procedimiento para buscar productos por múltiples criterios
CREATE OR REPLACE PROCEDURE buscar_productos_avanzado (
    p_criterio IN VARCHAR2,
    p_valor IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    IF p_criterio = 'CODIGO' THEN
        OPEN p_result FOR
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
                   p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
                   i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
                   i.STOCK_MINIMO, i.PRECIO_VENTA
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
            WHERE TO_CHAR(p.CODIGO_PRODUCTO) = p_valor
            ORDER BY p.ID_PRODUCTO;
    ELSIF p_criterio = 'NOMBRE' THEN
        OPEN p_result FOR
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
                   p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
                   i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
                   i.STOCK_MINIMO, i.PRECIO_VENTA
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
            WHERE LOWER(p.NOMBRE_PRODUCTO) LIKE '%' || LOWER(p_valor) || '%'
            ORDER BY p.ID_PRODUCTO;
    ELSIF p_criterio = 'CATEGORIA' THEN
        OPEN p_result FOR
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
                   p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
                   i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
                   i.STOCK_MINIMO, i.PRECIO_VENTA
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
            WHERE p.ID_CATEGORIA = TO_NUMBER(p_valor)
            ORDER BY p.ID_PRODUCTO;
    ELSE
        OPEN p_result FOR
            SELECT p.ID_PRODUCTO, p.CODIGO_PRODUCTO, p.NOMBRE_PRODUCTO, 
                   p.DESRIPCION_PRODUCTO, p.MATERIAL, c.NOMBRE_CATEGORIA, 
                   p.ESTADO, p.IMAGEN, c.ID_CATEGORIA,
                   i.INVENTARIO_ID, i.TALLA, i.COLOR, i.STOCK_ACTUAL, 
                   i.STOCK_MINIMO, i.PRECIO_VENTA
            FROM PRODUCTO p
            JOIN CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
            LEFT JOIN INVENTARIO i ON p.ID_PRODUCTO = i.PRODUCTO_ID
            WHERE (LOWER(p.NOMBRE_PRODUCTO) LIKE '%' || LOWER(p_valor) || '%'
                OR LOWER(p.DESRIPCION_PRODUCTO) LIKE '%' || LOWER(p_valor) || '%'
                OR TO_CHAR(p.CODIGO_PRODUCTO) LIKE '%' || p_valor || '%')
            ORDER BY p.ID_PRODUCTO;
    END IF;
END;
/

-- Procedimiento para agregar detalle de venta
CREATE OR REPLACE PROCEDURE agregar_detalle_venta (
    p_venta_id          IN NUMBER,
    p_inventario_id     IN NUMBER,
    p_cantidad          IN NUMBER,
    p_precio_unitario   IN NUMBER,
    p_subtotal          IN NUMBER,
    p_descuento_item    IN NUMBER DEFAULT 0
)
IS
BEGIN
    INSERT INTO DETALLE_VENTA (
        DETALLE_ID, VENTA_ID, INVENTARIO_ID, 
        CANTIDAD, PRECIO_UNITARIO, SUBTOTAL, DESCUENTO_ITEM
    ) VALUES (
        detalle_venta_seq.NEXTVAL, p_venta_id, p_inventario_id, 
        p_cantidad, p_precio_unitario, p_subtotal, NVL(p_descuento_item, 0)
    );
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20101, 'Error al agregar detalle de venta: ' || SQLERRM);
END;
/

-- Procedimiento para listar ventas por cliente
CREATE OR REPLACE PROCEDURE listar_ventas_por_cliente (
    p_cliente_id IN NUMBER,
    p_result     OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT VENTA_ID, CODIGO_VENTA, CLIENTE_ID, FECHA_VENTA, 
               SUBTOTAL, IMPUESTO, DESCUENTO, TOTAL, MEDIO_PAGO, ESTADO,
               'Cliente ID: ' || CLIENTE_ID AS CLIENTE
        FROM VENTA
        WHERE CLIENTE_ID = p_cliente_id
        ORDER BY FECHA_VENTA DESC;
END;
/

-- Procedimiento para listar detalles por venta
CREATE OR REPLACE PROCEDURE listar_detalles_por_venta (
    p_venta_id IN NUMBER,
    p_result   OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT DETALLE_ID, VENTA_ID, INVENTARIO_ID, CANTIDAD, 
               PRECIO_UNITARIO, SUBTOTAL, DESCUENTO_ITEM
        FROM DETALLE_VENTA
        WHERE VENTA_ID = p_venta_id
        ORDER BY DETALLE_ID;
END;
/

-- Procedimiento para listar ventas por estado
CREATE OR REPLACE PROCEDURE listar_ventas_por_estado (
    p_estado IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT VENTA_ID, CODIGO_VENTA, CLIENTE_ID, FECHA_VENTA, 
               SUBTOTAL, IMPUESTO, DESCUENTO, TOTAL, MEDIO_PAGO, ESTADO,
               'Cliente ID: ' || CLIENTE_ID AS CLIENTE
        FROM VENTA
        WHERE ESTADO = p_estado
        ORDER BY FECHA_VENTA DESC;
END;
/

-- Procedimiento para listar ventas del día
CREATE OR REPLACE PROCEDURE listar_ventas_del_dia (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT VENTA_ID, CODIGO_VENTA, CLIENTE_ID, FECHA_VENTA, 
               SUBTOTAL, IMPUESTO, DESCUENTO, TOTAL, MEDIO_PAGO, ESTADO,
               'Cliente ID: ' || CLIENTE_ID AS CLIENTE
        FROM VENTA
        WHERE TRUNC(FECHA_VENTA) = TRUNC(SYSDATE)
        ORDER BY FECHA_VENTA DESC;
END;
/

-- Procedimiento para actualizar totales de venta
CREATE OR REPLACE PROCEDURE actualizar_totales_venta (
    p_venta_id  IN NUMBER,
    p_subtotal  IN NUMBER,
    p_impuesto  IN NUMBER,
    p_total     IN NUMBER
)
IS
BEGIN
    UPDATE VENTA 
    SET SUBTOTAL = p_subtotal, 
        IMPUESTO = p_impuesto, 
        TOTAL = p_total
    WHERE VENTA_ID = p_venta_id;
    
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20102, 'Venta no encontrada para actualizar');
    END IF;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20103, 'Error al actualizar totales de venta: ' || SQLERRM);
END;
/

-- Procedimiento para eliminar detalle de venta
CREATE OR REPLACE PROCEDURE eliminar_detalle_venta (
    p_detalle_id IN NUMBER
)
IS
BEGIN
    DELETE FROM DETALLE_VENTA 
    WHERE DETALLE_ID = p_detalle_id;
    
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20104, 'Detalle de venta no encontrado');
    END IF;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20105, 'Error al eliminar detalle de venta: ' || SQLERRM);
END;
/

-- Procedimiento para listar detalles de venta con productos
CREATE OR REPLACE PROCEDURE listar_detalles_venta_con_productos (
    p_venta_id IN NUMBER,
    p_result   OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT dv.DETALLE_ID, dv.VENTA_ID, dv.INVENTARIO_ID, 
               dv.CANTIDAD, dv.PRECIO_UNITARIO, dv.SUBTOTAL, dv.DESCUENTO_ITEM,
               i.DESCRIPCION as NOMBRE_PRODUCTO
        FROM DETALLE_VENTA dv
        LEFT JOIN INVENTARIO i ON dv.INVENTARIO_ID = i.INVENTARIO_ID
        WHERE dv.VENTA_ID = p_venta_id
        ORDER BY dv.DETALLE_ID;
END;
/

-- Procedimiento para obtener estadísticas de ventas
CREATE OR REPLACE PROCEDURE obtener_estadisticas_ventas (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ESTADO, COUNT(*) as CANTIDAD
        FROM VENTA
        GROUP BY ESTADO;
END;
/

-- Procedimiento para buscar venta por ID
CREATE OR REPLACE PROCEDURE buscar_venta_por_id (
    p_venta_id IN NUMBER,
    p_result   OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT VENTA_ID, CODIGO_VENTA, CLIENTE_ID, FECHA_VENTA, 
               SUBTOTAL, IMPUESTO, DESCUENTO, TOTAL, MEDIO_PAGO, ESTADO,
               'Cliente ID: ' || CLIENTE_ID AS CLIENTE
        FROM VENTA
        WHERE VENTA_ID = p_venta_id;
END;
/

-- Procedimiento para buscar ventas con filtros
CREATE OR REPLACE PROCEDURE buscar_ventas_filtros (
    p_busqueda IN VARCHAR2,
    p_estado   IN VARCHAR2,
    p_result   OUT SYS_REFCURSOR
)
IS
    v_sql VARCHAR2(4000);
BEGIN
    v_sql := 'SELECT VENTA_ID, CODIGO_VENTA, CLIENTE_ID, FECHA_VENTA, 
                     SUBTOTAL, IMPUESTO, DESCUENTO, TOTAL, MEDIO_PAGO, ESTADO,
                     ''Cliente ID: '' || CLIENTE_ID AS CLIENTE
              FROM VENTA WHERE 1=1';
    
    IF p_busqueda IS NOT NULL AND TRIM(p_busqueda) IS NOT NULL THEN
        v_sql := v_sql || ' AND (UPPER(CODIGO_VENTA) LIKE UPPER(''%' || p_busqueda || '%'')';
        
        -- Verificar si la búsqueda es numérica para buscar por cliente ID
        BEGIN
            DECLARE
                v_cliente_id NUMBER;
            BEGIN
                v_cliente_id := TO_NUMBER(TRIM(p_busqueda));
                v_sql := v_sql || ' OR CLIENTE_ID = ' || v_cliente_id;
            EXCEPTION
                WHEN VALUE_ERROR THEN
                    NULL; -- No hacer nada si no es numérico
            END;
        END;
        
        v_sql := v_sql || ')';
    END IF;
    
    IF p_estado IS NOT NULL AND TRIM(p_estado) IS NOT NULL AND UPPER(TRIM(p_estado)) != 'TODOS' THEN
        v_sql := v_sql || ' AND ESTADO = ''' || p_estado || '''';
    END IF;
    
    v_sql := v_sql || ' ORDER BY FECHA_VENTA DESC';
    
    OPEN p_result FOR v_sql;
END;
/

-- Procedimiento para obtener información básica del cliente
CREATE OR REPLACE PROCEDURE obtener_info_basica_cliente (
    p_cliente_id IN NUMBER,
    p_result     OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, USERNAME, NOMBRE, APELLIDOS, EMAIL, ACTIVO
        FROM CLIENTE 
        WHERE ID_CLIENTE = p_cliente_id;
END;
/

-- Procedimiento para obtener proveedores activos
CREATE OR REPLACE PROCEDURE sp_obtener_proveedores_activos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM PROVEEDOR 
        WHERE ESTADO = 'Activo'
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para obtener proveedores inactivos
CREATE OR REPLACE PROCEDURE sp_obtener_proveedores_inactivos (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM PROVEEDOR 
        WHERE ESTADO = 'Inactivo'
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para obtener todos los proveedores
CREATE OR REPLACE PROCEDURE sp_obtener_todos_proveedores (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM PROVEEDOR 
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para buscar proveedores (solo activos)
CREATE OR REPLACE PROCEDURE sp_buscar_proveedores (
    p_busqueda IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM PROVEEDOR 
        WHERE (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
               UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
               CEDULA LIKE '%' || p_busqueda || '%')
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para buscar todos los proveedores (activos e inactivos)
CREATE OR REPLACE PROCEDURE sp_buscar_todos_proveedores (
    p_busqueda IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM PROVEEDOR 
        WHERE (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
               UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
               CEDULA LIKE '%' || p_busqueda || '%')
        ORDER BY NOMBRE, APELLIDOS;
END;
/

-- Procedimiento para obtener proveedor por ID
CREATE OR REPLACE PROCEDURE sp_obtener_proveedor_por_id (
    p_id IN NUMBER,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM PROVEEDOR 
        WHERE ID_PROVEEDOR = p_id;
END;
/

-- Procedimiento para obtener proveedor por cédula
CREATE OR REPLACE PROCEDURE sp_obtener_proveedor_por_cedula (
    p_cedula IN VARCHAR2,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
               TELEFONO, DIRECCION, ESTADO
        FROM PROVEEDOR 
        WHERE CEDULA = p_cedula;
END;
/

-- Procedimiento para verificar si un proveedor tiene productos
CREATE OR REPLACE PROCEDURE sp_proveedor_tiene_productos (
    p_id_proveedor IN NUMBER,
    p_tiene_productos OUT NUMBER
)
IS
    v_cantidad NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_cantidad
    FROM PRODUCTO_PROVEEDOR
    WHERE PROVEEDOR_ID = p_id_proveedor
      AND ESTADO = 'Activo';
    
    p_tiene_productos := CASE WHEN v_cantidad > 0 THEN 1 ELSE 0 END;
END;
/

-- Procedimientos adicionales útiles

-- Procedimiento para obtener proveedores con filtros avanzados
CREATE OR REPLACE PROCEDURE sp_obtener_proveedores_filtrados (
    p_estado IN VARCHAR2 DEFAULT NULL,
    p_busqueda IN VARCHAR2 DEFAULT NULL,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    IF p_estado IS NOT NULL AND p_busqueda IS NOT NULL THEN
        OPEN p_result FOR
            SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, ESTADO
            FROM PROVEEDOR 
            WHERE ESTADO = p_estado
              AND (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   CEDULA LIKE '%' || p_busqueda || '%')
            ORDER BY NOMBRE, APELLIDOS;
    ELSIF p_estado IS NOT NULL THEN
        OPEN p_result FOR
            SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, ESTADO
            FROM PROVEEDOR 
            WHERE ESTADO = p_estado
            ORDER BY NOMBRE, APELLIDOS;
    ELSIF p_busqueda IS NOT NULL THEN
        OPEN p_result FOR
            SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, ESTADO
            FROM PROVEEDOR 
            WHERE (UPPER(NOMBRE) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   UPPER(APELLIDOS) LIKE '%' || UPPER(p_busqueda) || '%' OR
                   CEDULA LIKE '%' || p_busqueda || '%')
            ORDER BY NOMBRE, APELLIDOS;
    ELSE
        OPEN p_result FOR
            SELECT ID_PROVEEDOR, CEDULA, NOMBRE, APELLIDOS, EMAIL, 
                   TELEFONO, DIRECCION, ESTADO
            FROM PROVEEDOR 
            ORDER BY NOMBRE, APELLIDOS;
    END IF;
END;
/

-- Procedimiento para contar proveedores por estado
CREATE OR REPLACE PROCEDURE sp_contar_proveedores_por_estado (
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ESTADO, COUNT(*) as CANTIDAD
        FROM PROVEEDOR
        GROUP BY ESTADO
        UNION ALL
        SELECT 'TOTAL', COUNT(*) as CANTIDAD
        FROM PROVEEDOR
        ORDER BY ESTADO;
END;
/

-- Procedimiento para obtener top proveedores con más productos
CREATE OR REPLACE PROCEDURE sp_top_proveedores_productos (
    p_limite IN NUMBER DEFAULT 10,
    p_result OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT p.ID_PROVEEDOR, p.CEDULA, p.NOMBRE, p.APELLIDOS,
               COUNT(pp.PRODUCTO_PROVEEDOR_ID) as total_productos
        FROM PROVEEDOR p
        LEFT JOIN PRODUCTO_PROVEEDOR pp ON p.ID_PROVEEDOR = pp.PROVEEDOR_ID AND pp.ESTADO = 'Activo'
        WHERE p.ESTADO = 'Activo'
        GROUP BY p.ID_PROVEEDOR, p.CEDULA, p.NOMBRE, p.APELLIDOS
        ORDER BY total_productos DESC
        FETCH FIRST p_limite ROWS ONLY;
END;
/

-- Procedimiento para validar datos de proveedor antes de insertar/actualizar
CREATE OR REPLACE PROCEDURE sp_validar_datos_proveedor (
    p_cedula IN VARCHAR2,
    p_email IN VARCHAR2,
    p_excluir_id IN NUMBER DEFAULT NULL,
    p_resultado OUT NUMBER -- 0=OK, 1=Cedula duplicada, 2=Email duplicado
)
IS
    v_count_cedula NUMBER;
    v_count_email NUMBER;
BEGIN
    -- Verificar cédula duplicada
    IF p_excluir_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count_cedula
        FROM PROVEEDOR
        WHERE CEDULA = p_cedula AND ID_PROVEEDOR != p_excluir_id;
    ELSE
        SELECT COUNT(*) INTO v_count_cedula
        FROM PROVEEDOR
        WHERE CEDULA = p_cedula;
    END IF;
    
    IF v_count_cedula > 0 THEN
        p_resultado := 1; -- Cédula duplicada
        RETURN;
    END IF;
    
    -- Verificar email duplicado
    IF p_excluir_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count_email
        FROM PROVEEDOR
        WHERE EMAIL = p_email AND ID_PROVEEDOR != p_excluir_id;
    ELSE
        SELECT COUNT(*) INTO v_count_email
        FROM PROVEEDOR
        WHERE EMAIL = p_email;
    END IF;
    
    IF v_count_email > 0 THEN
        p_resultado := 2; -- Email duplicado
        RETURN;
    END IF;
    
    p_resultado := 0; -- Todo OK
END;
/