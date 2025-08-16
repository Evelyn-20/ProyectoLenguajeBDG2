
CREATE OR REPLACE PROCEDURE crear_categoria(
    p_nombre VARCHAR2
) IS
BEGIN
    INSERT INTO categorias (id_categoria, nombre_categoria)
    VALUES (categorias_seq.NEXTVAL, p_nombre);
END;
/


CREATE OR REPLACE PROCEDURE actualizar_categoria(
    p_id_categoria NUMBER,
    p_nombre VARCHAR2
) IS
BEGIN
    UPDATE categorias
    SET nombre_categoria = p_nombre
    WHERE id_categoria = p_id_categoria;
END;
/


CREATE OR REPLACE PROCEDURE eliminar_categoria(
    p_id_categoria NUMBER
) IS
BEGIN
    DELETE FROM categorias
    WHERE id_categoria = p_id_categoria;
END;
/

CREATE OR REPLACE VIEW vista_categorias AS
SELECT id_categoria, nombre_categoria
FROM categorias;
