--Paquetes
--------------------------------------------------------------------------------
-- pkg_productos (gestión de productos)
CREATE OR REPLACE PACKAGE pkg_productos AS
    PROCEDURE registrar_producto(
        p_codigo       IN VARCHAR2,
        p_nombre       IN VARCHAR2,
        p_descripcion  IN VARCHAR2,
        p_marca        IN VARCHAR2,
        p_genero       IN VARCHAR2,
        p_material     IN VARCHAR2,
        p_categoria_id IN NUMBER
    );

    PROCEDURE actualizar_producto(
        p_codigo       IN VARCHAR2,
        p_nombre       IN VARCHAR2,
        p_descripcion  IN VARCHAR2,
        p_marca        IN VARCHAR2,
        p_genero       IN VARCHAR2,
        p_material     IN VARCHAR2,
        p_categoria_id IN NUMBER
    );

    PROCEDURE deshabilitar_producto(p_codigo IN VARCHAR2);

    FUNCTION fn_categoria_de_producto(p_codigo_producto VARCHAR2) RETURN VARCHAR2;
    FUNCTION fn_verificar_producto_activo(p_codigo VARCHAR2) RETURN BOOLEAN;
    FUNCTION fn_contar_productos_activos RETURN NUMBER;
END pkg_productos;
/

CREATE OR REPLACE PACKAGE BODY pkg_productos AS
    PROCEDURE registrar_producto(
        p_codigo       IN VARCHAR2,
        p_nombre       IN VARCHAR2,
        p_descripcion  IN VARCHAR2,
        p_marca        IN VARCHAR2,
        p_genero       IN VARCHAR2,
        p_material     IN VARCHAR2,
        p_categoria_id IN NUMBER
    )
    IS
    BEGIN
        INSERT INTO producto(codigo, nombre, descripcion, marca, genero, material, categoria_id, estado)
        VALUES(p_codigo, p_nombre, p_descripcion, p_marca, p_genero, p_material, p_categoria_id, 'Activo');
    END;

    PROCEDURE actualizar_producto(
        p_codigo       IN VARCHAR2,
        p_nombre       IN VARCHAR2,
        p_descripcion  IN VARCHAR2,
        p_marca        IN VARCHAR2,
        p_genero       IN VARCHAR2,
        p_material     IN VARCHAR2,
        p_categoria_id IN NUMBER
    )
    IS
    BEGIN
        UPDATE producto
        SET nombre = p_nombre,
            descripcion = p_descripcion,
            marca = p_marca,
            genero = p_genero,
            material = p_material,
            categoria_id = p_categoria_id
        WHERE codigo = p_codigo;
    END;

    PROCEDURE deshabilitar_producto(p_codigo IN VARCHAR2)
    IS
    BEGIN
        UPDATE producto
        SET estado = 'Inactivo'
        WHERE codigo = p_codigo;
    END;

    FUNCTION fn_categoria_de_producto(p_codigo_producto VARCHAR2) RETURN VARCHAR2
    IS
        v_categoria VARCHAR2(100);
    BEGIN
        SELECT c.nombre INTO v_categoria
        FROM producto p
        JOIN categoria c ON p.categoria_id = c.id
        WHERE p.codigo = p_codigo_producto;
        RETURN v_categoria;
    END;

    FUNCTION fn_verificar_producto_activo(p_codigo VARCHAR2) RETURN BOOLEAN
    IS
        v_estado VARCHAR2(20);
    BEGIN
        SELECT estado INTO v_estado FROM producto WHERE codigo = p_codigo;
        RETURN v_estado = 'Activo';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN FALSE;
    END;

    FUNCTION fn_contar_productos_activos RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total FROM producto WHERE estado = 'Activo';
        RETURN v_total;
    END;
END pkg_productos;
/
--------------------------------------------------------------------------------
-- pkg_categorias (gestión de categorías)
CREATE OR REPLACE PACKAGE pkg_categorias AS
    PROCEDURE registrar_categoria(
        p_nombre        IN VARCHAR2,
        p_descripcion   IN VARCHAR2
    );

    PROCEDURE actualizar_categoria(
        p_id            IN NUMBER,
        p_nombre        IN VARCHAR2,
        p_descripcion   IN VARCHAR2
    );

    PROCEDURE deshabilitar_categoria(p_id IN NUMBER);
END pkg_categorias;
/

CREATE OR REPLACE PACKAGE BODY pkg_categorias AS
    PROCEDURE registrar_categoria(
        p_nombre        IN VARCHAR2,
        p_descripcion   IN VARCHAR2
    )
    IS
    BEGIN
        INSERT INTO categoria(nombre, descripcion, estado)
        VALUES(p_nombre, p_descripcion, 'Activo');
    END;

    PROCEDURE actualizar_categoria(
        p_id            IN NUMBER,
        p_nombre        IN VARCHAR2,
        p_descripcion   IN VARCHAR2
    )
    IS
    BEGIN
        UPDATE categoria
        SET nombre = p_nombre,
            descripcion = p_descripcion
        WHERE id = p_id;
    END;

    PROCEDURE deshabilitar_categoria(p_id IN NUMBER)
    IS
    BEGIN
        UPDATE categoria
        SET estado = 'Inactivo'
        WHERE id = p_id;
    END;
END pkg_categorias;
/
--------------------------------------------------------------------------------
-- pkg_clientes (gestión de clientes)
CREATE OR REPLACE PACKAGE pkg_clientes AS
    PROCEDURE registrar_cliente(
        p_nombre     IN VARCHAR2,
        p_apellidos  IN VARCHAR2,
        p_email      IN VARCHAR2,
        p_contrasena IN VARCHAR2
    );

    PROCEDURE actualizar_cliente(
        p_id         IN NUMBER,
        p_nombre     IN VARCHAR2,
        p_apellidos  IN VARCHAR2,
        p_email      IN VARCHAR2
    );

    PROCEDURE deshabilitar_cliente(p_id IN NUMBER);

    FUNCTION fn_nombre_cliente_por_id(p_cliente_id IN NUMBER) RETURN VARCHAR2;
    FUNCTION fn_total_ventas_por_cliente(p_cliente_id IN NUMBER) RETURN NUMBER;
    FUNCTION fn_existe_cliente_email(p_email IN VARCHAR2) RETURN BOOLEAN;
END pkg_clientes;
/

CREATE OR REPLACE PACKAGE BODY pkg_clientes AS
    PROCEDURE registrar_cliente(
        p_nombre     IN VARCHAR2,
        p_apellidos  IN VARCHAR2,
        p_email      IN VARCHAR2,
        p_contrasena IN VARCHAR2
    )
    IS
    BEGIN
        INSERT INTO cliente(nombre, apellidos, email, contrasena, estado)
        VALUES(p_nombre, p_apellidos, p_email, p_contrasena, 'Activo');
    END;

    PROCEDURE actualizar_cliente(
        p_id         IN NUMBER,
        p_nombre     IN VARCHAR2,
        p_apellidos  IN VARCHAR2,
        p_email      IN VARCHAR2
    )
    IS
    BEGIN
        UPDATE cliente
        SET nombre = p_nombre,
            apellidos = p_apellidos,
            email = p_email
        WHERE id = p_id;
    END;

    PROCEDURE deshabilitar_cliente(p_id IN NUMBER)
    IS
    BEGIN
        UPDATE cliente
        SET estado = 'Inactivo'
        WHERE id = p_id;
    END;

    FUNCTION fn_nombre_cliente_por_id(p_cliente_id IN NUMBER) RETURN VARCHAR2
    IS
        v_nombre VARCHAR2(200);
    BEGIN
        SELECT nombre || ' ' || apellidos
        INTO v_nombre
        FROM cliente
        WHERE id = p_cliente_id;
        RETURN v_nombre;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END;

    FUNCTION fn_total_ventas_por_cliente(p_cliente_id IN NUMBER) RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT NVL(SUM(monto_total),0)
        INTO v_total
        FROM venta
        WHERE cliente_id = p_cliente_id;
        RETURN v_total;
    END;

    FUNCTION fn_existe_cliente_email(p_email IN VARCHAR2) RETURN BOOLEAN
    IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM cliente
        WHERE email = p_email;
        RETURN v_count > 0;
    END;
END pkg_clientes;
/
--------------------------------------------------------------------------------
-- pkg_proveedores (gestión de proveedores)
CREATE OR REPLACE PACKAGE pkg_proveedores AS
    PROCEDURE registrar_proveedor(
        p_nombre     IN VARCHAR2,
        p_apellidos  IN VARCHAR2
    );

    PROCEDURE actualizar_proveedor(
        p_id        IN NUMBER,
        p_nombre    IN VARCHAR2,
        p_apellidos IN VARCHAR2
    );

    PROCEDURE deshabilitar_proveedor(p_id IN NUMBER);

    FUNCTION fn_cantidad_productos_por_proveedor(p_proveedor_id IN NUMBER) RETURN NUMBER;
    FUNCTION fn_proveedor_por_producto(p_producto_id IN NUMBER) RETURN VARCHAR2;
END pkg_proveedores;
/

CREATE OR REPLACE PACKAGE BODY pkg_proveedores AS
    PROCEDURE registrar_proveedor(
        p_nombre     IN VARCHAR2,
        p_apellidos  IN VARCHAR2
    )
    IS
    BEGIN
        INSERT INTO proveedor(nombre, apellidos, estado)
        VALUES(p_nombre, p_apellidos, 'Activo');
    END;

    PROCEDURE actualizar_proveedor(
        p_id        IN NUMBER,
        p_nombre    IN VARCHAR2,
        p_apellidos IN VARCHAR2
    )
    IS
    BEGIN
        UPDATE proveedor
        SET nombre = p_nombre,
            apellidos = p_apellidos
        WHERE id = p_id;
    END;

    PROCEDURE deshabilitar_proveedor(p_id IN NUMBER)
    IS
    BEGIN
        UPDATE proveedor
        SET estado = 'Inactivo'
        WHERE id = p_id;
    END;

    FUNCTION fn_cantidad_productos_por_proveedor(p_proveedor_id IN NUMBER) RETURN NUMBER
    IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM asociacion_proveedor_producto
        WHERE proveedor_id = p_proveedor_id
          AND estado = 'Activo';
        RETURN v_count;
    END;

    FUNCTION fn_proveedor_por_producto(p_producto_id IN NUMBER) RETURN VARCHAR2
    IS
        v_proveedor VARCHAR2(200);
    BEGIN
        SELECT pr.nombre || ' ' || pr.apellidos
        INTO v_proveedor
        FROM asociacion_proveedor_producto a
        JOIN proveedor pr ON a.proveedor_id = pr.id
        WHERE a.producto_id = p_producto_id
          AND a.estado = 'Activo'
          AND ROWNUM = 1;
        RETURN v_proveedor;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END;
END pkg_proveedores;
/
--------------------------------------------------------------------------------
-- pkg_asociaciones (relación producto - proveedor)
CREATE OR REPLACE PACKAGE pkg_asociaciones AS
    PROCEDURE registrar_asociacion(
        p_producto_id  IN NUMBER,
        p_proveedor_id IN NUMBER
    );

    PROCEDURE actualizar_asociacion(
        p_id           IN NUMBER,
        p_producto_id  IN NUMBER,
        p_proveedor_id IN NUMBER
    );

    PROCEDURE deshabilitar_asociacion(p_id IN NUMBER);
END pkg_asociaciones;
/

CREATE OR REPLACE PACKAGE BODY pkg_asociaciones AS
    PROCEDURE registrar_asociacion(
        p_producto_id  IN NUMBER,
        p_proveedor_id IN NUMBER
    )
    IS
    BEGIN
        INSERT INTO asociacion_proveedor_producto(producto_id, proveedor_id, estado)
        VALUES(p_producto_id, p_proveedor_id, 'Activo');
    END;

    PROCEDURE actualizar_asociacion(
        p_id           IN NUMBER,
        p_producto_id  IN NUMBER,
        p_proveedor_id IN NUMBER
    )
    IS
    BEGIN
        UPDATE asociacion_proveedor_producto
        SET producto_id = p_producto_id,
            proveedor_id = p_proveedor_id
        WHERE id = p_id;
    END;

    PROCEDURE deshabilitar_asociacion(p_id IN NUMBER)
    IS
    BEGIN
        UPDATE asociacion_proveedor_producto
        SET estado = 'Inactivo'
        WHERE id = p_id;
    END;
END pkg_asociaciones;
/
--------------------------------------------------------------------------------
-- pkg_autenticacion (login y recuperación de contraseña)
CREATE OR REPLACE PACKAGE pkg_autenticacion AS
    PROCEDURE iniciar_sesion(p_email IN VARCHAR2, p_contrasena IN VARCHAR2, p_result OUT VARCHAR2);
    PROCEDURE recuperar_contrasena(p_email IN VARCHAR2, p_nueva_contrasena IN VARCHAR2);
    FUNCTION fn_verificar_usuario_activo(p_email IN VARCHAR2) RETURN BOOLEAN;
END pkg_autenticacion;
/

CREATE OR REPLACE PACKAGE BODY pkg_autenticacion AS
    PROCEDURE iniciar_sesion(p_email IN VARCHAR2, p_contrasena IN VARCHAR2, p_result OUT VARCHAR2)
    IS
        v_contra VARCHAR2(100);
    BEGIN
        SELECT contrasena INTO v_contra
        FROM cliente
        WHERE email = p_email
          AND estado = 'Activo';

        IF v_contra = p_contrasena THEN
            p_result := 'OK';
        ELSE
            p_result := 'ERROR';
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_result := 'NO_EXISTE';
    END;

    PROCEDURE recuperar_contrasena(p_email IN VARCHAR2, p_nueva_contrasena IN VARCHAR2)
    IS
    BEGIN
        UPDATE cliente
        SET contrasena = p_nueva_contrasena
        WHERE email = p_email;
    END;

    FUNCTION fn_verificar_usuario_activo(p_email IN VARCHAR2) RETURN BOOLEAN
    IS
        v_estado VARCHAR2(20);
    BEGIN
        SELECT estado INTO v_estado FROM cliente WHERE email = p_email;
        RETURN v_estado = 'Activo';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN FALSE;
    END;
END pkg_autenticacion;
/
--------------------------------------------------------------------------------
-- pkg_ventas (gestión de ventas)

CREATE OR REPLACE PACKAGE pkg_ventas AS
    PROCEDURE registrar_venta(p_cliente_id IN NUMBER, p_monto_total IN NUMBER, p_estado IN VARCHAR2);
    FUNCTION fn_estado_venta(p_venta_id IN NUMBER) RETURN VARCHAR2;
    FUNCTION fn_fecha_ultima_venta_cliente(p_cliente_id IN NUMBER) RETURN DATE;
    FUNCTION fn_cliente_tiene_ventas(p_cliente_id IN NUMBER) RETURN BOOLEAN;
    FUNCTION fn_venta_es_pendiente(p_venta_id IN NUMBER) RETURN BOOLEAN;
    FUNCTION fn_recuento_ventas_pendientes RETURN NUMBER;
END pkg_ventas;
/

CREATE OR REPLACE PACKAGE BODY pkg_ventas AS
    PROCEDURE registrar_venta(p_cliente_id IN NUMBER, p_monto_total IN NUMBER, p_estado IN VARCHAR2)
    IS
    BEGIN
        INSERT INTO venta(cliente_id, fecha, monto_total, estado)
        VALUES(p_cliente_id, SYSDATE, p_monto_total, p_estado);
    END;

    FUNCTION fn_estado_venta(p_venta_id IN NUMBER) RETURN VARCHAR2
    IS
        v_estado VARCHAR2(20);
    BEGIN
        SELECT estado INTO v_estado FROM venta WHERE id = p_venta_id;
        RETURN v_estado;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END;

    FUNCTION fn_fecha_ultima_venta_cliente(p_cliente_id IN NUMBER) RETURN DATE
    IS
        v_fecha DATE;
    BEGIN
        SELECT MAX(fecha) INTO v_fecha FROM venta WHERE cliente_id = p_cliente_id;
        RETURN v_fecha;
    END;

    FUNCTION fn_cliente_tiene_ventas(p_cliente_id IN NUMBER) RETURN BOOLEAN
    IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count FROM venta WHERE cliente_id = p_cliente_id;
        RETURN v_count > 0;
    END;

    FUNCTION fn_venta_es_pendiente(p_venta_id IN NUMBER) RETURN BOOLEAN
    IS
        v_estado VARCHAR2(20);
    BEGIN
        SELECT estado INTO v_estado FROM venta WHERE id = p_venta_id;
        RETURN v_estado = 'Pendiente';
    END;

    FUNCTION fn_recuento_ventas_pendientes RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total FROM venta WHERE estado = 'Pendiente';
        RETURN v_total;
    END;
END pkg_ventas;
/
--------------------------------------------------------------------------------
-- pkg_reportes (reportes y listados)

CREATE OR REPLACE PACKAGE pkg_reportes AS
    PROCEDURE listar_historial_compras_por_cliente(p_cliente_id IN NUMBER, p_result OUT SYS_REFCURSOR);
    PROCEDURE listar_ventas_totales(p_result OUT SYS_REFCURSOR);
    PROCEDURE ventas_por_fecha(p_fecha_inicio IN DATE, p_fecha_fin IN DATE, p_result OUT SYS_REFCURSOR);
END pkg_reportes;
/

CREATE OR REPLACE PACKAGE BODY pkg_reportes AS
    PROCEDURE listar_historial_compras_por_cliente(p_cliente_id IN NUMBER, p_result OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN p_result FOR
            SELECT v.id, v.fecha, v.monto_total, v.estado
            FROM venta v
            WHERE v.cliente_id = p_cliente_id
            ORDER BY v.fecha DESC;
    END;

    PROCEDURE listar_ventas_totales(p_result OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN p_result FOR
            SELECT v.id, c.nombre || ' ' || c.apellidos AS cliente,
                   v.fecha, v.monto_total, v.estado
            FROM venta v
            JOIN cliente c ON v.cliente_id = c.id
            ORDER BY v.fecha DESC;
    END;

    PROCEDURE ventas_por_fecha(p_fecha_inicio IN DATE, p_fecha_fin IN DATE, p_result OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN p_result FOR
            SELECT v.id, c.nombre || ' ' || c.apellidos AS cliente,
                   v.fecha, v.monto_total, v.estado
            FROM venta v
            JOIN cliente c ON v.cliente_id = c.id
            WHERE TRUNC(v.fecha) BETWEEN TRUNC(p_fecha_inicio) AND TRUNC(p_fecha_fin)
            ORDER BY v.fecha DESC;
    END;
END pkg_reportes;
/
--------------------------------------------------------------------------------
-- pkg_consultas (consultas generales de productos)

CREATE OR REPLACE PACKAGE pkg_consultas AS
    PROCEDURE consultar_producto(p_codigo IN VARCHAR2, p_result OUT SYS_REFCURSOR);
    PROCEDURE buscar_producto_por_nombre(p_nombre IN VARCHAR2, p_result OUT SYS_REFCURSOR);
    PROCEDURE productos_por_categoria(p_categoria_id IN NUMBER, p_result OUT SYS_REFCURSOR);
END pkg_consultas;
/

CREATE OR REPLACE PACKAGE BODY pkg_consultas AS
    PROCEDURE consultar_producto(p_codigo IN VARCHAR2, p_result OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN p_result FOR
            SELECT * FROM producto WHERE codigo = p_codigo;
    END;

    PROCEDURE buscar_producto_por_nombre(p_nombre IN VARCHAR2, p_result OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN p_result FOR
            SELECT * FROM producto
            WHERE LOWER(nombre) LIKE '%' || LOWER(p_nombre) || '%';
    END;

    PROCEDURE productos_por_categoria(p_categoria_id IN NUMBER, p_result OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN p_result FOR
            SELECT * FROM producto
            WHERE categoria_id = p_categoria_id
              AND estado = 'Activo';
    END;
END pkg_consultas;
/
--------------------------------------------------------------------------------
-- pkg_utilidades (funciones y utilidades generales)

CREATE OR REPLACE PACKAGE pkg_utilidades AS
    FUNCTION fn_producto_en_categoria(p_codigo_producto IN VARCHAR2, p_categoria_id IN NUMBER) RETURN BOOLEAN;
    FUNCTION fn_contar_productos_activos RETURN NUMBER;
END pkg_utilidades;
/

CREATE OR REPLACE PACKAGE BODY pkg_utilidades AS
    FUNCTION fn_producto_en_categoria(p_codigo_producto IN VARCHAR2, p_categoria_id IN NUMBER) RETURN BOOLEAN
    IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM producto
        WHERE codigo = p_codigo_producto
          AND categoria_id = p_categoria_id;
        RETURN v_count > 0;
    END;

    FUNCTION fn_contar_productos_activos RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total
        FROM producto
        WHERE estado = 'Activo';
        RETURN v_total;
    END;
END pkg_utilidades;
/
--------------------------------------------------------------------------------