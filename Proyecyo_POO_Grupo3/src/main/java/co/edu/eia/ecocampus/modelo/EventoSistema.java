package co.edu.eia.ecocampus.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Mensaje que el publicador entrega a los observadores. */
public class EventoSistema {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TipoEvento tipo;
    private final String mensaje;
    private final String idOrigen;
    private final LocalDateTime fecha;

    public EventoSistema(TipoEvento tipo, String mensaje, String idOrigen) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.idOrigen = idOrigen;
        this.fecha = LocalDateTime.now();
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getIdOrigen() {
        return idOrigen;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return "[" + fecha.format(FORMATO) + "] " + tipo + ": " + mensaje;
    }
}
