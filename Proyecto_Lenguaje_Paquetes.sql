CREATE OR REPLACE PACKAGE pkg_productos AS
    -- Vistas
    PROCEDURE get_productos_activos(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_productos_inactivos(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_productos_inventario(p_cursor OUT SYS_REFCURSOR);
    
    -- Procedimientos
    PROCEDURE registrar_producto(
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
    );
    
    PROCEDURE consultar_producto(
        p_codigo      IN  VARCHAR2,
        p_result      OUT SYS_REFCURSOR
    );
    
    PROCEDURE actualizar_producto(
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
    );
    
    PROCEDURE deshabilitar_producto(
        p_codigo IN VARCHAR2
    );
    
    PROCEDURE habilitar_producto(
        p_codigo IN VARCHAR2
    );
    
    PROCEDURE listar_productos_por_categoria(
        p_categoria_id IN NUMBER,
        p_result OUT SYS_REFCURSOR
    );
    
    PROCEDURE buscar_producto_por_nombre(
        p_nombre  IN VARCHAR2,
        p_result  OUT SYS_REFCURSOR
    );
    
    -- Funciones
    FUNCTION fn_producto_en_categoria(
        p_codigo_producto IN NUMBER,
        p_categoria_id IN NUMBER
    ) RETURN BOOLEAN;
    
    FUNCTION fn_verificar_producto_activo(
        p_codigo IN NUMBER
    ) RETURN BOOLEAN;
    
    FUNCTION fn_contar_productos_activos RETURN NUMBER;
    
    -- Triggers
    PROCEDURE trg_valida_codigo_producto;
END pkg_productos;
/

CREATE OR REPLACE PACKAGE pkg_categorias AS
    -- Vistas
    PROCEDURE get_categorias_con_cantidad(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_todas_categorias_con_cantidad(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_categorias_activas(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_categorias_inactivas(p_cursor OUT SYS_REFCURSOR);
    
    -- Procedimientos
    PROCEDURE registrar_categoria(
        p_codigo        IN  NUMBER,
        p_nombre        IN  VARCHAR2,
        p_descripcion   IN  VARCHAR2
    );
    
    PROCEDURE actualizar_categoria(
        p_categoria_id  IN  NUMBER,
        p_codigo        IN  NUMBER,
        p_nombre        IN  VARCHAR2,
        p_descripcion   IN  VARCHAR2,
        p_estado        IN  VARCHAR2 DEFAULT 'Activo'
    );
    
    PROCEDURE deshabilitar_categoria(
        p_categoria_id IN NUMBER
    );
    
    PROCEDURE habilitar_categoria(
        p_categoria_id IN NUMBER
    );
    
    -- Funciones
    FUNCTION fn_categoria_de_producto(
        p_codigo_producto NUMBER
    ) RETURN VARCHAR2;
    
    FUNCTION fn_categoria_activa(
        p_categoria_id IN NUMBER
    ) RETURN BOOLEAN;
    
    FUNCTION fn_contar_productos_por_categoria(
        p_categoria_id IN NUMBER
    ) RETURN NUMBER;
END pkg_categorias;
/

CREATE OR REPLACE PACKAGE pkg_clientes AS
    -- Vistas
    PROCEDURE get_clientes_activos(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_clientes_inactivos(p_cursor OUT SYS_REFCURSOR);
    
    -- Procedimientos
    PROCEDURE registrar_cliente(
        p_cedula      IN  VARCHAR2,
        p_nombre      IN  VARCHAR2,
        p_apellidos   IN  VARCHAR2,
        p_email       IN  VARCHAR2,
        p_telefono    IN  VARCHAR2,
        p_direccion   IN  VARCHAR2,
        p_password    IN  VARCHAR2
    );
    
    PROCEDURE actualizar_cliente(
        p_cedula      IN  VARCHAR2,
        p_nombre      IN  VARCHAR2,
        p_apellidos   IN  VARCHAR2,
        p_email       IN  VARCHAR2,
        p_telefono    IN  VARCHAR2,
        p_direccion   IN  VARCHAR2,
        p_contrasena  IN  VARCHAR2 DEFAULT NULL
    );
    
    PROCEDURE deshabilitar_cliente(
        p_cedula IN VARCHAR2
    );
    
    PROCEDURE habilitar_cliente(
        p_cedula IN VARCHAR2
    );
    
    -- Funciones
    FUNCTION fn_nombre_cliente_por_id(
        p_cliente_id IN NUMBER
    ) RETURN VARCHAR2;
    
    FUNCTION fn_verificar_usuario_activo(
        p_email IN VARCHAR2
    ) RETURN NUMBER;
    
    FUNCTION fn_existe_cliente_email(
        p_email IN VARCHAR2
    ) RETURN NUMBER;
END pkg_clientes;
/

CREATE OR REPLACE PACKAGE pkg_proveedores AS
    -- Vistas
    PROCEDURE get_proveedores_activos(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_proveedores_inactivos(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_asociaciones_productos_proveedores(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_asociaciones_productos_proveedores_inactivos(p_cursor OUT SYS_REFCURSOR);
    
    -- Procedimientos
    PROCEDURE registrar_proveedor(
        p_cedula      IN  VARCHAR2,
        p_nombre      IN  VARCHAR2,
        p_apellidos   IN  VARCHAR2,
        p_email       IN  VARCHAR2,
        p_telefono    IN  VARCHAR2,
        p_direccion   IN  VARCHAR2
    );
    
    PROCEDURE actualizar_proveedor(
        p_cedula      IN  VARCHAR2,
        p_nombre      IN  VARCHAR2,
        p_apellidos   IN  VARCHAR2,
        p_email       IN  VARCHAR2,
        p_telefono    IN  VARCHAR2,
        p_direccion   IN  VARCHAR2
    );
    
    PROCEDURE deshabilitar_proveedor(
        p_cedula IN VARCHAR2
    );
    
    PROCEDURE habilitar_proveedor(
        p_cedula IN VARCHAR2
    );
    
    -- Funciones
    FUNCTION fn_proveedor_por_producto(
        p_producto_id IN NUMBER
    ) RETURN VARCHAR2;
    
    FUNCTION fn_cantidad_productos_por_proveedor(
        p_proveedor_id IN NUMBER
    ) RETURN NUMBER;
END pkg_proveedores;
/

CREATE OR REPLACE PACKAGE pkg_ventas AS
    -- Vistas
    PROCEDURE get_historial_compras_por_cliente(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE get_ventas_detalle(p_cursor OUT SYS_REFCURSOR);
    
    -- Procedimientos
    PROCEDURE registrar_venta(
        p_cliente_id  IN  NUMBER,
        p_monto_total IN  NUMBER,
        p_estado      IN  VARCHAR2 DEFAULT 'Pendiente',
        p_result      OUT NUMBER
    );
    
    PROCEDURE cambiar_estado_venta(
        p_venta_id      IN NUMBER,
        p_nuevo_estado  IN VARCHAR2
    );
    
    PROCEDURE registrar_venta_completa(
        p_codigo_venta IN  VARCHAR2,
        p_cliente_id   IN  NUMBER,
        p_subtotal     IN  NUMBER,
        p_impuesto     IN  NUMBER,
        p_descuento    IN  NUMBER DEFAULT 0,
        p_total        IN  NUMBER,
        p_medio_pago   IN  VARCHAR2,
        p_estado       IN  VARCHAR2 DEFAULT 'Pendiente',
        p_venta_id     OUT NUMBER
    );
    
    -- Funciones
    FUNCTION fn_total_ventas_por_cliente(
        p_cliente_id IN NUMBER
    ) RETURN NUMBER;
    
    FUNCTION fn_estado_venta(
        p_venta_id IN NUMBER
    ) RETURN VARCHAR2;
    
    FUNCTION fn_venta_es_pendiente(
        p_venta_id IN NUMBER
    ) RETURN BOOLEAN;
END pkg_ventas;
/

CREATE OR REPLACE PACKAGE pkg_asociaciones AS
    -- Procedimientos
    PROCEDURE registrar_asociacion_proveedor_producto(
        p_producto_id  IN NUMBER,
        p_proveedor_id IN NUMBER,
        p_precio_compra IN NUMBER
    );
    
    PROCEDURE consultar_asociacion_producto_proveedor(
        p_codigo_producto IN VARCHAR2,
        p_result OUT SYS_REFCURSOR
    );
    
    PROCEDURE actualizar_asociacion_producto_proveedor(
        p_asociacion_id     IN NUMBER,
        p_nuevo_producto_id IN NUMBER,
        p_nuevo_proveedor_id IN NUMBER,
        p_nuevo_precio IN NUMBER
    );
    
    PROCEDURE deshabilitar_asociacion_producto_proveedor(
        p_asociacion_id IN NUMBER
    );
    
    PROCEDURE habilitar_asociacion_producto_proveedor(
        p_asociacion_id IN NUMBER
    );
    
    -- Funciones
    FUNCTION fn_existe_asociacion_activa(
        p_producto_id IN NUMBER,
        p_proveedor_id IN NUMBER
    ) RETURN NUMBER;
    
    FUNCTION fn_precio_compra_producto_proveedor(
        p_producto_id IN NUMBER,
        p_proveedor_id IN NUMBER
    ) RETURN NUMBER;
END pkg_asociaciones;
/

CREATE OR REPLACE PACKAGE pkg_autenticacion AS
    -- Procedimientos
    PROCEDURE iniciar_sesion_usuario(
        p_email    IN VARCHAR2,
        p_password IN VARCHAR2,
        p_result   OUT SYS_REFCURSOR
    );
    
    PROCEDURE recuperar_contrasena(
        p_email IN VARCHAR2,
        p_result OUT SYS_REFCURSOR
    );
    
    -- Funciones
    FUNCTION OBTENER_CLIENTE_POR_CREDENCIAL(
        p_username IN VARCHAR2,
        p_es_email IN NUMBER
    ) RETURN SYS_REFCURSOR;
END pkg_autenticacion;
/

CREATE OR REPLACE PACKAGE pkg_reportes AS
    -- Procedimientos
    PROCEDURE listar_historial_compras_por_cliente(
        p_cliente_id IN NUMBER,
        p_result     OUT SYS_REFCURSOR
    );
    
    PROCEDURE listar_ventas_totales(
        p_result OUT SYS_REFCURSOR
    );
    
    PROCEDURE ventas_por_fecha(
        p_fecha_inicio IN DATE,
        p_fecha_fin    IN DATE,
        p_result       OUT SYS_REFCURSOR
    );
    
    -- Funciones
    FUNCTION fn_fecha_ultima_venta_cliente(
        p_cliente_id IN NUMBER
    ) RETURN DATE;
    
    FUNCTION fn_recuento_ventas_pendientes RETURN NUMBER;
END pkg_reportes;
/