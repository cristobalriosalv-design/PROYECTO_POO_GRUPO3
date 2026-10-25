package co.edu.eia.ecocampus.modelo;

/**
 * Estados del ciclo de vida de un reporte. Cada estado conoce a cuales puede
 * pasar, asi la regla de transiciones queda en un solo lugar.
 */
public enum EstadoReporte {
    REGISTRADO,
    CLASIFICADO,
    ASIGNADO,
    EN_ATENCION,
    CERRADO,
    REABIERTO;

    public boolean puedeCambiarA(EstadoReporte destino) {
        return switch (this) {
            case REGISTRADO -> destino == CLASIFICADO;
            case CLASIFICADO -> destino == ASIGNADO;
            case ASIGNADO -> destino == ASIGNADO || destino == EN_ATENCION;
            case EN_ATENCION -> destino == CERRADO;
            case CERRADO -> destino == REABIERTO;
            case REABIERTO -> destino == CLASIFICADO || destino == ASIGNADO;
        };
    }

    public boolean esAbierto() {
        return this != CERRADO;
    }
}
