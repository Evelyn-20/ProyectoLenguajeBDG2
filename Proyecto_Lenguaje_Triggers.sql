--------------------------------------------------------------------------------
/*Triggers*/
--------------------------------------------------------------------------------
-- Trigger #1: Asigna automáticamente la fecha de registro
-- cuando se inserta un nuevo producto
CREATE OR REPLACE TRIGGER trg_producto_fecha
BEFORE INSERT ON producto
FOR EACH ROW
BEGIN
    :NEW.fecha_registro := SYSDATE;
END;
/
-- Trigger #2: Convierte siempre el correo de cliente a minúsculas
-- para evitar duplicados entre mayúsculas y minúsculas
CREATE OR REPLACE TRIGGER trg_cliente_email
BEFORE INSERT OR UPDATE ON cliente
FOR EACH ROW
BEGIN
    :NEW.email := LOWER(:NEW.email);
END;
/
-- Trigger #3: Registra automáticamente en la tabla de auditoría
-- cada vez que se inserta una nueva venta
-- (requiere la tabla auditoria_ventas y la secuencia seq_auditoria_ventas)
CREATE OR REPLACE TRIGGER trg_auditoria_ventas
AFTER INSERT ON venta
FOR EACH ROW
BEGIN
    INSERT INTO auditoria_ventas(id, venta_id, fecha_auditoria, accion)
    VALUES(seq_auditoria_ventas.NEXTVAL, :NEW.id, SYSDATE, 'NUEVA_VENTA');
END;
/
-- Trigger #4: Evita que se creen asociaciones con productos inactivos
CREATE OR REPLACE TRIGGER trg_asociacion_estado
BEFORE INSERT ON asociacion_proveedor_producto
FOR EACH ROW
DECLARE
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM producto
    WHERE id = :NEW.producto_id;

    IF v_estado <> 'Activo' THEN
        RAISE_APPLICATION_ERROR(-20001, 'No se puede asociar un producto inactivo.');
    END IF;
END;
/
-- Trigger #5: Resta automáticamente el stock del producto
-- cuando se registra un detalle de venta
CREATE OR REPLACE TRIGGER trg_actualiza_stock
AFTER INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    UPDATE producto
    SET stock = stock - :NEW.cantidad
    WHERE id = :NEW.producto_id;
END;
/