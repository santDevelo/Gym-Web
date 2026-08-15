package com.ProyectoFinal.domain;

// Enum que representa la vigencia de una membresía (no el estado de un pago).
// Se guarda en la columna "estado" de la tabla membresia con @Enumerated(STRING).
public enum EstadoMembresia {
    ACTIVA,
    PENDIENTE,
    VENCIDA,
    INACTIVA
}
