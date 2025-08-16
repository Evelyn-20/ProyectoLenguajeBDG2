
CREATE OR REPLACE FUNCTION validar_login(
    p_usuario VARCHAR2,
    p_contrasena VARCHAR2
) RETURN NUMBER IS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM usuarios
    WHERE usuario = p_usuario
      AND contrasena = p_contrasena;

    RETURN v_count; 
END;
/

CREATE OR REPLACE PROCEDURE login_usuario(
    p_usuario VARCHAR2,
    p_contrasena VARCHAR2,
    p_mensaje OUT VARCHAR2
) IS
    v_valido NUMBER;
BEGIN
    v_valido := validar_login(p_usuario, p_contrasena);

    IF v_valido = 1 THEN
        p_mensaje := 'Login correcto';
    ELSE
        p_mensaje := 'Usuario o contraseña incorrectos';
    END IF;
END;
/
