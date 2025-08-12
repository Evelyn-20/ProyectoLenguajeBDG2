--------------------------------------------------------------------------------
/*Producto*/
--------------------------------------------------------------------------------
/*Registrar producto*/
CREATE OR REPLACE PROCEDURE registrar_producto (
    p_codigo        IN  VARCHAR2,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2,
    p_material      IN  VARCHAR2,
    p_categoria_id  IN  NUMBER,
    p_imagen        IN  VARCHAR2 DEFAULT NULL
)
IS
BEGIN
    INSERT INTO PRODUCTO (
        ID_PRODUCTO, CODIGO_PRODUCTO, NOMBRE_PRODUCTO, DESRIPCION_PRODUCTO, 
        MATERIAL, ID_CATEGORIA, ESTADO, IMAGEN
    )
    VALUES (
        producto_seq.NEXTVAL, p_codigo, p_nombre, p_descripcion, 
        p_material, p_categoria_id, 'Activo', p_imagen
    );
EXCEPTION
    WHEN dup_val_on_index THEN
        RAISE_APPLICATION_ERROR(-20001, 'Código de producto duplicado');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20002, 'Error al registrar producto: '||SQLERRM);
END;
/

/*Consultar producto*/
CREATE OR REPLACE PROCEDURE consultar_producto (
    p_codigo      IN  VARCHAR2,
    p_result      OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT * FROM PRODUCTO
        WHERE CODIGO_PRODUCTO = p_codigo;
END;
/

/*Actualizar producto*/
CREATE OR REPLACE PROCEDURE actualizar_producto (
    p_codigo        IN  VARCHAR2,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2,
    p_material      IN  VARCHAR2,
    p_categoria_id  IN  NUMBER,
    p_imagen        IN  VARCHAR2 DEFAULT NULL
)
IS
BEGIN
    IF p_imagen IS NULL THEN
        UPDATE PRODUCTO
           SET NOMBRE_PRODUCTO       = p_nombre,
               DESRIPCION_PRODUCTO   = p_descripcion,
               MATERIAL              = p_material,
               ID_CATEGORIA          = p_categoria_id
         WHERE CODIGO_PRODUCTO = p_codigo;
    ELSE
        UPDATE PRODUCTO
           SET NOMBRE_PRODUCTO       = p_nombre,
               DESRIPCION_PRODUCTO   = p_descripcion,
               MATERIAL              = p_material,
               ID_CATEGORIA          = p_categoria_id,
               IMAGEN                = p_imagen
         WHERE CODIGO_PRODUCTO = p_codigo;
    END IF;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Producto no encontrado');
    END IF;
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
            p.CODIGO_PRODUCTO,
            p.NOMBRE_PRODUCTO,
            p.DESRIPCION_PRODUCTO,
            p.MATERIAL,
            p.IMAGEN,
            P.ESTADO
        FROM 
            producto p
        WHERE 
            p.ID_CATEGORIA = p_categoria_id
            AND p.ESTADO = 'Activo';
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
            CODIGO_PRODUCTO,
            NOMBRE_PRODUCTO,
            DESRIPCION_PRODUCTO,
            MATERIAL,
            IMAGEN,
            ESTADO
        FROM producto
        WHERE LOWER(NOMBRE_PRODUCTO) LIKE '%' || LOWER(p_nombre) || '%';
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
/*Registrar categoria*/
CREATE OR REPLACE PROCEDURE registrar_categoria (
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2
)
IS
BEGIN
    INSERT INTO CATEGORIA (
        ID_CATEGORIA, NOMBRE_CATEGORIA, DESRIPCION_CATEGORIA, ESTADO
    )
    VALUES (
        categoria_seq.NEXTVAL, p_nombre, p_descripcion, 'Activo'
    );
EXCEPTION
    WHEN dup_val_on_index THEN
        RAISE_APPLICATION_ERROR(-20005, 'Nombre de categoría duplicado');
END;
/

/*Actualizar categoria*/
CREATE OR REPLACE PROCEDURE actualizar_categoria (
    p_categoria_id  IN  NUMBER,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2
)
IS
BEGIN
    UPDATE CATEGORIA
       SET NOMBRE_CATEGORIA      = p_nombre,
           DESRIPCION_CATEGORIA  = p_descripcion
     WHERE ID_CATEGORIA = p_categoria_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Categoría no encontrada');
    END IF;
END;
/

/*Deshabilitar categoria*/
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

/*Habilitar categoria*/
CREATE OR REPLACE PROCEDURE habilitar_categoria (
    p_categoria_id IN NUMBER
)
IS
BEGIN
    UPDATE CATEGORIA
       SET ESTADO = 'Activo'
     WHERE ID_CATEGORIA = p_categoria_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'Categoría no encontrada');
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
BEGIN
    INSERT INTO VENTA (
        VENTA_ID, CLIENTE_ID, FECHA_VENTA, TOTAL, ESTADO
    )
    VALUES (
        venta_seq.NEXTVAL, p_cliente_id, SYSDATE, p_monto_total, p_estado
    )
    RETURNING VENTA_ID INTO p_result;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20014, 'Error al registrar venta: '||SQLERRM);
END;
/

/*Cambiar estado de venta*/
CREATE OR REPLACE PROCEDURE cambiar_estado_venta (
    p_venta_id IN NUMBER,
    p_nuevo_estado IN VARCHAR2
)
IS
BEGIN
    UPDATE VENTA
       SET ESTADO = p_nuevo_estado
     WHERE VENTA_ID = p_venta_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20015, 'Venta no encontrada');
    END IF;
END;
/


--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- registrar_asociacion_proveedor_producto
CREATE OR REPLACE PROCEDURE registrar_asociacion_proveedor_producto (
    p_producto_id  IN NUMBER,
    p_proveedor_id IN NUMBER,
    p_precio_compra IN NUMBER
)
IS
BEGIN
    INSERT INTO PRODUCTO_PROVEEDOR (
        PRODUCTO_PROVEEDOR_ID, PRODUCTO_ID, PROVEEDOR_ID, PRECIO_COMPRA, ESTADO
    ) VALUES (
        asociacion_seq.NEXTVAL, p_producto_id, p_proveedor_id, p_precio_compra, 'Activo'
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
        SELECT a.PRODUCTO_PROVEEDOR_ID,
               p.NOMBRE_PRODUCTO AS producto,
               pr.NOMBRE AS proveedor,
               pr.APELLIDOS,
               a.ESTADO
        FROM PRODUCTO_PROVEEDOR a
        JOIN PRODUCTO p ON a.PRODUCTO_ID = p.ID_PRODUCTO
        JOIN PROVEEDOR pr ON a.PROVEEDOR_ID = pr.ID_PROVEEDOR
        WHERE p.CODIGO_PRODUCTO = p_codigo_producto;
END;
/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- actualizar_asociacion_producto_proveedor
CREATE OR REPLACE PROCEDURE actualizar_asociacion_producto_proveedor (
    p_asociacion_id     IN NUMBER,
    p_nuevo_producto_id IN NUMBER,
    p_nuevo_proveedor_id IN NUMBER,
    p_nuevo_precio IN NUMBER
)
IS
BEGIN
    UPDATE PRODUCTO_PROVEEDOR
    SET PRODUCTO_ID = p_nuevo_producto_id,
        PROVEEDOR_ID = p_nuevo_proveedor_id,
        PRECIO_COMPRA = p_nuevo_precio
    WHERE PRODUCTO_PROVEEDOR_ID = p_asociacion_id;

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
CREATE OR REPLACE PROCEDURE iniciar_sesion_usuario (
    p_email    IN VARCHAR2,
    p_password IN VARCHAR2,
    p_result   OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_result FOR
        SELECT ID_CLIENTE, nombre, apellidos, email
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