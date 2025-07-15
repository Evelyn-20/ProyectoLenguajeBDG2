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
    INSERT INTO producto (
        id, codigo, nombre, descripcion, marca,
        genero, material, categoria_id, estado
    )
    VALUES (
        producto_seq.NEXTVAL, p_codigo, p_nombre, p_descripcion, p_marca,
        p_genero, p_material, p_categoria_id, 'Activo'
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
        SELECT * FROM producto
        WHERE codigo = p_codigo;
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
    UPDATE producto
       SET nombre        = p_nombre,
           descripcion   = p_descripcion,
           marca         = p_marca,
           genero        = p_genero,
           material      = p_material,
           categoria_id  = p_categoria_id
     WHERE codigo = p_codigo;

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
    UPDATE producto
       SET estado = 'Inactivo'
     WHERE codigo = p_codigo;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Producto no encontrado');
    END IF;
END;
/
--------------------------------------------------------------------------------
/*Categoria*/
--------------------------------------------------------------------------------
/*Registrar categoría*/
CREATE OR REPLACE PROCEDURE registrar_categoria (
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2
)
IS
BEGIN
    INSERT INTO categoria (
        id, nombre, descripcion, estado
    )
    VALUES (
        categoria_seq.NEXTVAL, p_nombre, p_descripcion, 'Activo'
    );
EXCEPTION
    WHEN dup_val_on_index THEN
        RAISE_APPLICATION_ERROR(-20005, 'Nombre de categoría duplicado');
END;
/

/*Actualizar categoría*/
CREATE OR REPLACE PROCEDURE actualizar_categoria (
    p_categoria_id  IN  NUMBER,
    p_nombre        IN  VARCHAR2,
    p_descripcion   IN  VARCHAR2
)
IS
BEGIN
    UPDATE categoria
       SET nombre       = p_nombre,
           descripcion  = p_descripcion
     WHERE id = p_categoria_id;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Categoría no encontrada');
    END IF;
END;
/

/*Deshabilitar categoría*/
CREATE OR REPLACE PROCEDURE deshabilitar_categoria (
    p_categoria_id IN NUMBER
)
IS
BEGIN
    UPDATE categoria
       SET estado = 'Inactivo'
     WHERE id = p_categoria_id;

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
    INSERT INTO cliente (
        id, cedula, nombre, apellidos, email,
        telefono, direccion, password_hash, estado
    )
    VALUES (
        cliente_seq.NEXTVAL, p_cedula, p_nombre, p_apellidos, p_email,
        p_telefono, p_direccion, DBMS_CRYPTO.HASH(UTL_I18N.STRING_TO_RAW(p_password,'AL32UTF8'),2),
        'Activo'
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
    UPDATE cliente
       SET nombre    = p_nombre,
           apellidos = p_apellidos,
           email     = p_email,
           telefono  = p_telefono,
           direccion = p_direccion
     WHERE cedula = p_cedula;

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
    UPDATE cliente
       SET estado = 'Inactivo'
     WHERE cedula = p_cedula;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Cliente no encontrado');
    END IF;
END;
/
--------------------------------------------------------------------------------
/*Proveedor*/
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
    INSERT INTO proveedor (
        id, cedula, nombre, apellidos, email,
        telefono, direccion, estado
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
    UPDATE proveedor
       SET nombre    = p_nombre,
           apellidos = p_apellidos,
           email     = p_email,
           telefono  = p_telefono,
           direccion = p_direccion
     WHERE cedula = p_cedula;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20012, 'Proveedor no encontrado');
    END IF;
END;
/

/*Deshabilitar proveedor*/
CREATE OR REPLACE PROCEDURE deshabilitar_proveedor (
    p_cedula IN VARCHAR2
)
IS
BEGIN
    UPDATE proveedor
       SET estado = 'Inactivo'
     WHERE cedula = p_cedula;

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
    p_result      OUT NUMBER          -- devolverá el ID de la venta
)
IS
BEGIN
    INSERT INTO venta (
        id, cliente_id, fecha, monto_total, estado
    )
    VALUES (
        venta_seq.NEXTVAL, p_cliente_id, SYSDATE, p_monto_total, p_estado
    )
    RETURNING id INTO p_result;
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
    UPDATE venta
       SET estado = p_nuevo_estado
     WHERE id = p_venta_id;

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
    p.codigo,
    p.nombre,
    p.descripcion,
    p.marca,
    p.genero,
    p.material,
    c.nombre AS categoria,
    p.estado
FROM 
    producto p
JOIN 
    categoria c ON p.categoria_id = c.id
WHERE 
    p.estado = 'Activo';
--------------------------------------------------------------------------------
-- Vista que muestra todos los clientes activos
CREATE OR REPLACE VIEW vista_clientes_activos AS
SELECT 
    cedula,
    nombre,
    apellidos,
    email,
    telefono,
    direccion
FROM 
    cliente
WHERE 
    estado = 'Activo';
--------------------------------------------------------------------------------
-- Vista que muestra todos los proveedores activos
CREATE OR REPLACE VIEW vista_proveedores_activos AS
SELECT 
    cedula,
    nombre,
    apellidos,
    email,
    telefono,
    direccion
FROM 
    proveedor
WHERE 
    estado = 'Activo';
--------------------------------------------------------------------------------
-- Vista que muestra la relación entre productos y sus proveedores
CREATE OR REPLACE VIEW vista_asociaciones_productos_proveedores AS
SELECT 
    a.id AS asociacion_id,
    p.codigo AS codigo_producto,
    p.nombre AS nombre_producto,
    pr.cedula AS cedula_proveedor,
    pr.nombre AS nombre_proveedor,
    a.estado
FROM 
    asociacion_proveedor_producto a
JOIN 
    producto p ON a.producto_id = p.id
JOIN 
    proveedor pr ON a.proveedor_id = pr.id
WHERE 
    a.estado = 'Activo';
--------------------------------------------------------------------------------
-- Vista que muestra todas las ventas realizadas por cada cliente
CREATE OR REPLACE VIEW vista_historial_compras_por_cliente AS
SELECT 
    v.id AS venta_id,
    c.cedula AS cliente_cedula,
    c.nombre || ' ' || c.apellidos AS cliente_nombre,
    v.fecha,
    v.monto_total,
    v.estado
FROM 
    venta v
JOIN 
    cliente c ON v.cliente_id = c.id;
--------------------------------------------------------------------------------





--------------------------------------------------------------------------------





--------------------------------------------------------------------------------