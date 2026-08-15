package com.ProyectoFinal.domain;

// Enum con las formas de pago que se pueden registrar en el historial (Pago).
// NO_REGISTRADO se usa para pagos migrados de datos antiguos sin ese dato.
public enum MetodoPago {
    EFECTIVO,
    TARJETA,
    TRANSFERENCIA,
    SINPE_MOVIL,
    NO_REGISTRADO
}
