CREATE OR REPLACE VIEW vista_historial_ventas AS
SELECT v.id_venta,
       v.fecha_venta,
       c.id_cliente,
       c.nombre_cliente,
       p.nombre_producto,
       d.cantidad,
       d.subtotal,
       calcular_total_venta(v.id_venta) AS total_venta
FROM ventas v
JOIN clientes c ON v.id_cliente = c.id_cliente
JOIN detalle_ventas d ON v.id_venta = d.id_venta
JOIN productos p ON d.id_producto = p.id_producto;
