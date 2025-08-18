--------------------------------------------------------------------------------
/*Vistas*/
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-- Cursor #1: Lista los empleados con salario mayor al valor ingresado
/*CREATE OR REPLACE PROCEDURE cur_empleados_mayor_salario(p_salario IN NUMBER) AS
    CURSOR c_empleados IS
        SELECT id, nombre, salario FROM empleado WHERE salario > p_salario;
    v_emp c_empleados%ROWTYPE;
BEGIN
    OPEN c_empleados;
    LOOP
        FETCH c_empleados INTO v_emp;
        EXIT WHEN c_empleados%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Empleado: ' || v_emp.nombre || ' - Salario: ' || v_emp.salario);
    END LOOP;
    CLOSE c_empleados;
END;
/*/
--------------------------------------------------------------------------------
-- Cursor #2: Lista todos los productos que están activos
CREATE OR REPLACE PROCEDURE cur_productos_activos AS
    CURSOR c_prod IS
        SELECT codigo, nombre FROM producto WHERE estado = 'Activo';
    v_prod c_prod%ROWTYPE;
BEGIN
    OPEN c_prod;
    LOOP
        FETCH c_prod INTO v_prod;
        EXIT WHEN c_prod%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Producto: ' || v_prod.codigo || ' - ' || v_prod.nombre);
    END LOOP;
    CLOSE c_prod;
END;
/
--------------------------------------------------------------------------------
-- Cursor #3: Lista todos los clientes que tienen al menos una venta
CREATE OR REPLACE PROCEDURE cur_clientes_con_ventas AS
    CURSOR c_clientes IS
        SELECT DISTINCT c.id, c.nombre, c.apellidos
        FROM cliente c
        JOIN venta v ON c.id = v.cliente_id;
    v_cli c_clientes%ROWTYPE;
BEGIN
    OPEN c_clientes;
    LOOP
        FETCH c_clientes INTO v_cli;
        EXIT WHEN c_clientes%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Cliente: ' || v_cli.nombre || ' ' || v_cli.apellidos);
    END LOOP;
    CLOSE c_clientes;
END;
/
--------------------------------------------------------------------------------
-- Cursor #4: Lista todas las ventas que están en estado Pendiente
CREATE OR REPLACE PROCEDURE cur_ventas_pendientes AS
    CURSOR c_ventas IS
        SELECT id, cliente_id, monto_total FROM venta WHERE estado = 'Pendiente';
    v_ven c_ventas%ROWTYPE;
BEGIN
    OPEN c_ventas;
    LOOP
        FETCH c_ventas INTO v_ven;
        EXIT WHEN c_ventas%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Venta pendiente ID: ' || v_ven.id || ' - Monto: ' || v_ven.monto_total);
    END LOOP;
    CLOSE c_ventas;
END;
/
--------------------------------------------------------------------------------
-- Cursor #5: Lista todos los proveedores activos
CREATE OR REPLACE PROCEDURE cur_proveedores_activos AS
    CURSOR c_prov IS
        SELECT id, nombre, apellidos FROM proveedor WHERE estado = 'Activo';
    v_prov c_prov%ROWTYPE;
BEGIN
    OPEN c_prov;
    LOOP
        FETCH c_prov INTO v_prov;
        EXIT WHEN c_prov%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Proveedor: ' || v_prov.nombre || ' ' || v_prov.apellidos);
    END LOOP;
    CLOSE c_prov;
END;
/
--------------------------------------------------------------------------------
-- Cursor #6: Lista los productos cuyo stock es menor a 10 unidades
CREATE OR REPLACE PROCEDURE cur_productos_bajo_stock AS
    CURSOR c_prod IS
        SELECT id, nombre, stock FROM producto WHERE stock < 10;
    v_prod c_prod%ROWTYPE;
BEGIN
    OPEN c_prod;
    LOOP
        FETCH c_prod INTO v_prod;
        EXIT WHEN c_prod%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Producto: ' || v_prod.nombre || ' - Stock: ' || v_prod.stock);
    END LOOP;
    CLOSE c_prod;
END;
/
--------------------------------------------------------------------------------
-- Cursor #7: Muestra todas las ventas realizadas por un cliente específico
CREATE OR REPLACE PROCEDURE cur_historial_ventas_cliente(p_cliente_id IN NUMBER) AS
    CURSOR c_ventas IS
        SELECT id, fecha, monto_total FROM venta WHERE cliente_id = p_cliente_id;
    v_ven c_ventas%ROWTYPE;
BEGIN
    OPEN c_ventas;
    LOOP
        FETCH c_ventas INTO v_ven;
        EXIT WHEN c_ventas%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Venta ID: ' || v_ven.id || ' - Fecha: ' || v_ven.fecha || ' - Monto: ' || v_ven.monto_total);
    END LOOP;
    CLOSE c_ventas;
END;
/
--------------------------------------------------------------------------------
-- Cursor #8: Lista todas las categorías activas
CREATE OR REPLACE PROCEDURE cur_categorias_activas AS
    CURSOR c_cat IS
        SELECT id, nombre FROM categoria WHERE estado = 'Activo';
    v_cat c_cat%ROWTYPE;
BEGIN
    OPEN c_cat;
    LOOP
        FETCH c_cat INTO v_cat;
        EXIT WHEN c_cat%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Categoría: ' || v_cat.id || ' - ' || v_cat.nombre);
    END LOOP;
    CLOSE c_cat;
END;
/
--------------------------------------------------------------------------------
-- Cursor #9: Lista los productos de una categoría específica
CREATE OR REPLACE PROCEDURE cur_productos_por_categoria(p_categoria_id IN NUMBER) AS
    CURSOR c_prod IS
        SELECT nombre, precio FROM producto WHERE categoria_id = p_categoria_id;
    v_prod c_prod%ROWTYPE;
BEGIN
    OPEN c_prod;
    LOOP
        FETCH c_prod INTO v_prod;
        EXIT WHEN c_prod%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Producto: ' || v_prod.nombre || ' - Precio: ' || v_prod.precio);
    END LOOP;
    CLOSE c_prod;
END;
/
--------------------------------------------------------------------------------
-- Cursor #10: Lista todos los clientes que están inactivos
CREATE OR REPLACE PROCEDURE cur_clientes_inactivos AS
    CURSOR c_cli IS
        SELECT id, nombre, apellidos FROM cliente WHERE estado = 'Inactivo';
    v_cli c_cli%ROWTYPE;
BEGIN
    OPEN c_cli;
    LOOP
        FETCH c_cli INTO v_cli;
        EXIT WHEN c_cli%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Cliente inactivo: ' || v_cli.nombre || ' ' || v_cli.apellidos);
    END LOOP;
    CLOSE c_cli;
END;
/
--------------------------------------------------------------------------------
-- Cursor #11: Lista las ventas realizadas entre dos fechas específicas
CREATE OR REPLACE PROCEDURE cur_ventas_rango_fechas(p_fecha_ini IN DATE, p_fecha_fin IN DATE) AS
    CURSOR c_ventas IS
        SELECT id, fecha, monto_total FROM venta
        WHERE fecha BETWEEN p_fecha_ini AND p_fecha_fin;
    v_ven c_ventas%ROWTYPE;
BEGIN
    OPEN c_ventas;
    LOOP
        FETCH c_ventas INTO v_ven;
        EXIT WHEN c_ventas%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Venta: ' || v_ven.id || ' - Fecha: ' || v_ven.fecha || ' - Monto: ' || v_ven.monto_total);
    END LOOP;
    CLOSE c_ventas;
END;
/
--------------------------------------------------------------------------------
-- Cursor #12: Lista el detalle de ventas de un producto específico
CREATE OR REPLACE PROCEDURE cur_detalle_ventas_producto(p_producto_id IN NUMBER) AS
    CURSOR c_det IS
        SELECT dv.venta_id, dv.cantidad, dv.precio_unitario
        FROM detalle_venta dv
        WHERE dv.producto_id = p_producto_id;
    v_det c_det%ROWTYPE;
BEGIN
    OPEN c_det;
    LOOP
        FETCH c_det INTO v_det;
        EXIT WHEN c_det%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Venta ID: ' || v_det.venta_id || 
                             ' - Cantidad: ' || v_det.cantidad || 
                             ' - Precio Unitario: ' || v_det.precio_unitario);
    END LOOP;
    CLOSE c_det;
END;
/
--------------------------------------------------------------------------------
-- Cursor #13: Lista los productos que nunca se han vendido
CREATE OR REPLACE PROCEDURE cur_productos_sin_ventas AS
    CURSOR c_prod IS
        SELECT p.id, p.nombre
        FROM producto p
        WHERE NOT EXISTS (
            SELECT 1 FROM detalle_venta dv WHERE dv.producto_id = p.id
        );
    v_prod c_prod%ROWTYPE;
BEGIN
    OPEN c_prod;
    LOOP
        FETCH c_prod INTO v_prod;
        EXIT WHEN c_prod%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Producto sin ventas: ' || v_prod.id || ' - ' || v_prod.nombre);
    END LOOP;
    CLOSE c_prod;
END;
/
--------------------------------------------------------------------------------
-- Cursor #14: Lista los proveedores que no tienen productos asociados
CREATE OR REPLACE PROCEDURE cur_proveedores_sin_productos AS
    CURSOR c_prov IS
        SELECT pr.id, pr.nombre, pr.apellidos
        FROM proveedor pr
        WHERE NOT EXISTS (
            SELECT 1 FROM asociacion_proveedor_producto ap
            WHERE ap.proveedor_id = pr.id
        );
    v_prov c_prov%ROWTYPE;
BEGIN
    OPEN c_prov;
    LOOP
        FETCH c_prov INTO v_prov;
        EXIT WHEN c_prov%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Proveedor sin productos: ' || v_prov.nombre || ' ' || v_prov.apellidos);
    END LOOP;
    CLOSE c_prov;
END;
/
--------------------------------------------------------------------------------
-- Cursor #15: Muestra los 5 productos más vendidos (por cantidad)
CREATE OR REPLACE PROCEDURE cur_top5_productos_mas_vendidos AS
    CURSOR c_top IS
        SELECT p.nombre, SUM(dv.cantidad) AS total_vendido
        FROM producto p
        JOIN detalle_venta dv ON p.id = dv.producto_id
        GROUP BY p.nombre
        ORDER BY total_vendido DESC
        FETCH FIRST 5 ROWS ONLY;
    v_top c_top%ROWTYPE;
BEGIN
    OPEN c_top;
    LOOP
        FETCH c_top INTO v_top;
        EXIT WHEN c_top%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Producto: ' || v_top.nombre || ' - Total vendido: ' || v_top.total_vendido);
    END LOOP;
    CLOSE c_top;
END;
/