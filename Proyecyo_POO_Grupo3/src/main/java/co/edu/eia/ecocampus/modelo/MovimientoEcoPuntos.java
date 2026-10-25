package co.edu.eia.ecocampus.modelo;

import java.time.LocalDateTime;

import co.edu.eia.ecocampus.util.Validador;

/** Movimiento de eco-puntos: siempre conserva concepto y fecha. */
public class MovimientoEcoPuntos implements Identificable {

    private final String id;
    private final String concepto;
    private final int puntos;
    private final LocalDateTime fecha;

    public MovimientoEcoPuntos(String id, String concepto, int puntos) {
        this(id, concepto, puntos, LocalDateTime.now());
    }

    public MovimientoEcoPuntos(String id, String concepto, int puntos, LocalDateTime fecha) {
        if (puntos == 0) {
            throw new IllegalArgumentException("Un movimiento de eco-puntos no puede ser de cero puntos");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del movimiento es obligatoria");
        }
        this.id = Validador.validarTexto(id, "id");
        this.concepto = Validador.validarTexto(concepto, "concepto");
        this.puntos = puntos;
        this.fecha = fecha;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getConcepto() {
        return concepto;
    }

    public int getPuntos() {
        return puntos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return String.format("%s | %+d pts | %s | %s", id, puntos, concepto, fecha.toLocalDate());
    }
}
