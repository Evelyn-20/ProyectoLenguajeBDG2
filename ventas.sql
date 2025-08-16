CREATE OR REPLACE PROCEDURE registrar_venta(
    p_id_cliente NUMBER,
    p_id_producto NUMBER,
    p_cantidad NUMBER
) IS
    v_id_venta NUMBER;
    v_precio   NUMBER;
BEGIN

    INSERT INTO ventas (id_venta, id_cliente, fecha_venta)
    VALUES (ventas_seq.NEXTVAL, p_id_cliente, SYSDATE)
    RETURNING id_venta INTO v_id_venta;


    SELECT precio INTO v_precio
    FROM productos
    WHERE id_producto = p_id_producto;

    INSERT INTO detalle_ventas (id_detalle, id_venta, id_producto, cantidad, subtotal)
    VALUES (detalle_ventas_seq.NEXTVAL, v_id_venta, p_id_producto, p_cantidad, (v_precio * p_cantidad));
END;
/

CREATE OR REPLACE FUNCTION calcular_total_venta(
    p_id_venta NUMBER
) RETURN NUMBER IS
    v_total NUMBER;
BEGIN
    SELECT SUM(subtotal) INTO v_total
    FROM detalle_ventas
    WHERE id_venta = p_id_venta;

    RETURN NVL(v_total, 0);
END;
/
