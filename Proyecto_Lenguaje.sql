--------------------------------------------------------------------------------
/*Tablas*/
--------------------------------------------------------------------------------
/*Tabla Categoria*/
CREATE TABLE CATEGORIA (ID_CATEGORIA NUMBER(10) NOT NULL, CODIGO_CATEGORIA NUMBER(10) NOT NULL, 
NOMBRE_CATEGORIA VARCHAR2(200) NOT NULL, DESRIPCION_CATEGORIA VARCHAR2(200) NOT NULL, 
ESTADO VARCHAR2(20) NOT NULL,
CONSTRAINT CATEGORIA_PK PRIMARY KEY (ID_CATEGORIA));

/*Tabla Producto*/
CREATE TABLE PRODUCTO (ID_PRODUCTO NUMBER(10) NOT NULL, CODIGO_PRODUCTO NUMBER(10) NOT NULL, 
ID_CATEGORIA NUMBER NOT NULL, NOMBRE_PRODUCTO VARCHAR2(200) NOT NULL, 
DESRIPCION_PRODUCTO VARCHAR2(200) NOT NULL, MATERIAL VARCHAR2(100) NOT NULL, ESTADO VARCHAR2(20) NOT NULL,
CONSTRAINT PRODUCTO_PK PRIMARY KEY (ID_PRODUCTO),
CONSTRAINT FK_CATEGORIA FOREIGN KEY (ID_CATEGORIA) REFERENCES CATEGORIA (ID_CATEGORIA));

/*Tabla Inventario*/
CREATE TABLE INVENTARIO (INVENTARIO_ID NUMBER(10) NOT NULL, PRODUCTO_ID NUMBER(10) NOT NULL,
TALLA VARCHAR2(50), COLOR VARCHAR2(50) NOT NULL, STOCK_ACTUAL NUMBER(10) NOT NULL,
STOCK_MINIMO NUMBER(10) NOT NULL, PRECIO_VENTA NUMBER(10,2) NOT NULL,
CONSTRAINT INVENTARIO_PK PRIMARY KEY (INVENTARIO_ID),
CONSTRAINT FK_INVENTARIO FOREIGN KEY (PRODUCTO_ID)REFERENCES PRODUCTO (ID_PRODUCTO));

/*Tabla Proveedor*/
CREATE TABLE PROVEEDOR (ID_PROVEEDOR NUMBER(10) NOT NULL, CEDULA VARCHAR2(20) NOT NULL, 
NOMBRE VARCHAR2(100) NOT NULL, APELLIDOS VARCHAR2(100) NOT NULL, EMAIL VARCHAR2(100), 
TELEFONO VARCHAR2(20), DIRECCION VARCHAR2(200), ESTADO VARCHAR2(20) NOT NULL,
CONSTRAINT PROVEEDOR_PK PRIMARY KEY (ID_PROVEEDOR));

/*Tabla Producto_Proveedor*/
CREATE TABLE PRODUCTO_PROVEEDOR (PRODUCTO_PROVEEDOR_ID NUMBER(10) NOT NULL, PRODUCTO_ID NUMBER(10) NOT NULL, 
PROVEEDOR_ID NUMBER(10) NOT NULL, PRECIO_COMPRA NUMBER(10,2) NOT NULL, ESTADO VARCHAR2(20) NOT NULL,
CONSTRAINT PRODUCTO_PROVEEDOR_PK PRIMARY KEY (PRODUCTO_PROVEEDOR_ID),
CONSTRAINT FK_PRODUCTO FOREIGN KEY (PRODUCTO_ID) REFERENCES PRODUCTO (ID_PRODUCTO),
CONSTRAINT FK_PROVEEDOR FOREIGN KEY (PROVEEDOR_ID) REFERENCES PROVEEDOR (ID_PROVEEDOR));

/*Tabla Cliente*/
CREATE TABLE CLIENTE (ID_CLIENTE NUMBER(10) NOT NULL, CEDULA VARCHAR2(20) NOT NULL, NOMBRE VARCHAR2(100) NOT NULL,
APELLIDOS VARCHAR2(100) NOT NULL, EMAIL VARCHAR2(100), TELEFONO VARCHAR2(20), DIRECCION VARCHAR2(200),
CONTRASENA VARCHAR2(100), ESTADO VARCHAR2(20) NOT NULL,
CONSTRAINT CLIENTE_PK PRIMARY KEY (ID_CLIENTE));

/*Tabla Venta*/
CREATE TABLE VENTA (VENTA_ID NUMBER(10) NOT NULL, CODIGO_VENTA NUMBER(10) NOT NULL, CLIENTE_ID NUMBER(10) NOT NULL,
FECHA_VENTA DATE NOT NULL, SUBTOTAL NUMBER(10,2) NOT NULL, IMPUESTO NUMBER(10,2) NOT NULL, DESCUENTO NUMBER(10,2),
TOTAL NUMBER(10,2) NOT NULL, MEDIO_PAGO VARCHAR2(50) NOT  NULL, ESTADO VARCHAR2(20) NOT NULL,
CONSTRAINT VENTA_PK PRIMARY KEY (VENTA_ID),
CONSTRAINT FK_CLIENTE FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE (ID_CLIENTE));

/*Tabla Detalle_Venta*/
CREATE TABLE DETALLE_VENTA (DETALLE_ID NUMBER(10) NOT NULL, VENTA_ID NUMBER(10) NOT NULL, 
INVENTARIO_ID NUMBER(10) NOT NULL, CANTIDAD NUMBER(10) NOT NULL, PRECIO_UNITARIO NUMBER(10,2) NOT NULL,
SUBTOTAL NUMBER(10,2) NOT NULL, DESCUENTO_ITEM NUMBER(10,2),
CONSTRAINT DETALLE_PK PRIMARY KEY (DETALLE_ID),
CONSTRAINT FK_VENTA_DETALLE FOREIGN KEY (VENTA_ID) REFERENCES VENTA (VENTA_ID),
CONSTRAINT FK_INVENTARIO_DETALLE FOREIGN KEY (INVENTARIO_ID) REFERENCES INVENTARIO (INVENTARIO_ID));

--------------------------------------------------------------------------------
-- SECUENCIAS
--------------------------------------------------------------------------------
CREATE SEQUENCE producto_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE categoria_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE cliente_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE proveedor_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE venta_seq START WITH 1 INCREMENT BY 1;

--------------------------------------------------------------------------------
/*Producto*/
--------------------------------------------------------------------------------
/*Registrar producto*/
CREATE OR REPLACE PROCEDURE registrar_producto (
    p_codigo        IN  VARCHAR2,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2,
    p_marca         IN  VARCHAR2,
    p_genero        IN  VARCHAR2,
    p_material      IN  VARCHAR2,
    p_categoria_id  IN  NUMBER
)
IS
BEGIN
    INSERT INTO PRODUCTO (
        ID_PRODUCTO, CODIGO_PRODUCTO, NOMBRE_PRODUCTO, DESRIPCION_PRODUCTO, MATERIAL, ID_CATEGORIA, ESTADO
    )
    VALUES (
        producto_seq.NEXTVAL, p_codigo, p_nombre, p_descripcion, p_material, p_categoria_id, 'Activo'
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
    p_marca         IN  VARCHAR2,
    p_genero        IN  VARCHAR2,
    p_material      IN  VARCHAR2,
    p_categoria_id  IN  NUMBER
)
IS
BEGIN
    UPDATE PRODUCTO
       SET NOMBRE_PRODUCTO       = p_nombre,
           DESRIPCION_PRODUCTO   = p_descripcion,
           MATERIAL              = p_material,
           ID_CATEGORIA          = p_categoria_id
     WHERE CODIGO_PRODUCTO = p_codigo;

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
    p_direccion   IN  VARCHAR2
)
IS
BEGIN
    UPDATE CLIENTE
       SET NOMBRE     = p_nombre,
           APELLIDOS  = p_apellidos,
           EMAIL      = p_email,
           TELEFONO   = p_telefono,
           DIRECCION  = p_direccion
     WHERE CEDULA = p_cedula;

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
/*Vistas*/
--------------------------------------------------------------------------------
/*Vista que muestra todos los productos activos con su información principal*/
CREATE OR REPLACE VIEW vista_productos_activos AS
SELECT 
    p.CODIGO_PRODUCTO,
    p.NOMBRE_PRODUCTO,
    p.DESRIPCION_PRODUCTO,
    p.MATERIAL,
    c.NOMBRE_CATEGORIA AS categoria,
    p.ESTADO
FROM 
    PRODUCTO p
JOIN 
    CATEGORIA c ON p.ID_CATEGORIA = c.ID_CATEGORIA
WHERE 
    p.ESTADO = 'Activo';

--------------------------------------------------------------------------------
-- Vista que muestra todos los clientes activos
CREATE OR REPLACE VIEW vista_clientes AS
SELECT 
    CEDULA,
    NOMBRE,
    APELLIDOS,
    EMAIL,
    TELEFONO,
    DIRECCION
FROM 
    CLIENTE;

--------------------------------------------------------------------------------
-- Vista que muestra todos los proveedores activos
CREATE OR REPLACE VIEW vista_proveedores_activos AS
SELECT 
    CEDULA,
    NOMBRE,
    APELLIDOS,
    EMAIL,
    TELEFONO,
    DIRECCION
FROM 
    PROVEEDOR
WHERE 
    ESTADO = 'Activo';

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