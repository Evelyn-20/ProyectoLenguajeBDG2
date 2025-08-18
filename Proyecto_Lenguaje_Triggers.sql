--------------------------------------------------------------------------------
/*Triggers*/
--------------------------------------------------------------------------------
-- Trigger #1: Valida que el codigo del producto no este repetido
CREATE OR REPLACE TRIGGER trg_valida_codigo_producto
BEFORE INSERT OR UPDATE OF CODIGO_PRODUCTO ON PRODUCTO
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM PRODUCTO
    WHERE CODIGO_PRODUCTO = :NEW.CODIGO_PRODUCTO
    AND ID_PRODUCTO != NVL(:NEW.ID_PRODUCTO, -1);
    
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'El código de producto ya existe. Debe ser único.');
    END IF;
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
-- Trigger #3: Valida que haya stock necesario para vender
CREATE OR REPLACE TRIGGER trg_validar_stock_venta
BEFORE INSERT ON DETALLE_VENTA
FOR EACH ROW
DECLARE
    v_stock_actual NUMBER;
BEGIN
    -- Obtener el stock actual del producto
    SELECT STOCK_ACTUAL INTO v_stock_actual
    FROM INVENTARIO
    WHERE INVENTARIO_ID = :NEW.INVENTARIO_ID;
    
    -- Validar que haya suficiente stock
    IF v_stock_actual < :NEW.CANTIDAD THEN
        RAISE_APPLICATION_ERROR(-20001, 
            'No hay suficiente stock para este producto. Stock actual: ' || v_stock_actual);
    END IF;
END;
/
-- Trigger #4: Evita que se creen asociaciones con productos inactivos
CREATE OR REPLACE TRIGGER trg_asociacion_estado
BEFORE INSERT ON PRODUCTO_PROVEEDOR
FOR EACH ROW
DECLARE
    v_estado VARCHAR2(20);
BEGIN
    SELECT estado INTO v_estado
    FROM producto
    WHERE ID_PRODUCTO = :NEW.producto_id;

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
    UPDATE inventario
    SET STOCK_ACTUAL = STOCK_ACTUAL - :NEW.CANTIDAD
    WHERE INVENTARIO_ID = :NEW.INVENTARIO_ID;
END;
/