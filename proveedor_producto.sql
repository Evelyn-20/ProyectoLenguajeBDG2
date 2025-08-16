
CREATE OR REPLACE PROCEDURE asociar_proveedor_producto(
    p_id_proveedor NUMBER,
    p_id_producto NUMBER
) IS
BEGIN
    INSERT INTO proveedor_producto (id_proveedor, id_producto)
    VALUES (p_id_proveedor, p_id_producto);
END;
/

CREATE OR REPLACE VIEW vista_proveedor_producto AS
SELECT pr.id_proveedor,
       pr.nombre_proveedor,
       p.id_producto,
       p.nombre_producto
FROM proveedores pr
JOIN proveedor_producto pp ON pr.id_proveedor = pp.id_proveedor
JOIN productos p ON p.id_producto = pp.id_producto;
