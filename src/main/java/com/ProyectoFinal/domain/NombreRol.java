package com.ProyectoFinal.domain;

/**
 * Nombres de los roles reconocidos por el sistema.
 */
public enum NombreRol {
    ADMINISTRADOR,
    ENTRENADOR,
    CLIENTE;

    public boolean coincideCon(String valor) {
        return valor != null && name().equalsIgnoreCase(valor.trim());
    }
}
