package co.edu.eia.ecocampus.modelo;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.eia.ecocampus.util.Validador;

/** Parte de una campania: solo la crea Campania (constructor de paquete). */
public class Actividad {

    private final String id;
    private final String nombre;
    private final LocalDateTime fecha;
    private final int cupo;

    Actividad(String id, String nombre, LocalDateTime fecha, int cupo) {
        this.id = Validador.validarTexto(id, "id");
        this.nombre = Validador.validarTexto(nombre, "nombre");
        this.fecha = Objects.requireNonNull(fecha, "fecha");
        this.cupo = Validador.validarPositivo(cupo, "cupo");
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getCupo() {
        return cupo;
    }

    @Override
    public String toString() {
        return String.format("Actividad %s - %s (%s, cupo %d)", id, nombre, fecha.toLocalDate(), cupo);
    }
}
