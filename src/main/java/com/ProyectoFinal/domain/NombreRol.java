package com.ProyectoFinal.domain;

// Nombres de los roles reconocidos por el sistema.
public enum NombreRol {
    ADMINISTRADOR,
    ENTRENADOR,
    CLIENTE;

    // Compara un filtro recibido como texto sin depender de mayúsculas o espacios.
    public boolean coincideCon(String valor) {
        return valor != null && name().equalsIgnoreCase(valor.trim());
       
    }
}
