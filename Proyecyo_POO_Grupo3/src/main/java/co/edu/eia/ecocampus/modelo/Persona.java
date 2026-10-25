package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.util.Validador;

/**
 * Persona del campus. Es abstracta porque cada tipo concreto define su
 * bonificacion de eco-puntos y si puede participar en campanias.
 * Implementa Notificable: es observador de los eventos que le interesan.
 */
public abstract class Persona implements Identificable, Notificable {

    private final String id;
    private String nombre;
    private String correo;
    private int saldoPuntos;
    private final List<MovimientoEcoPuntos> movimientos;
    private final List<String> notificaciones;

    protected Persona(String id, String nombre, String correo) {
        this.id = Validador.validarTexto(id, "id");
        this.nombre = Validador.validarTexto(nombre, "nombre");
        this.correo = Validador.validarCorreo(correo);
        this.movimientos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validador.validarTexto(nombre, "nombre");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = Validador.validarCorreo(correo);
    }

    public int getSaldoPuntos() {
        return saldoPuntos;
    }

    /** Regla: el saldo de eco-puntos nunca puede quedar negativo. */
    public void agregarMovimiento(MovimientoEcoPuntos movimiento) throws OperacionNoPermitidaException {
        Objects.requireNonNull(movimiento, "movimiento");
        if (saldoPuntos + movimiento.getPuntos() < 0) {
            throw new OperacionNoPermitidaException("Saldo insuficiente: " + nombre + " tiene " + saldoPuntos
                    + " pts y el movimiento es de " + movimiento.getPuntos());
        }
        saldoPuntos += movimiento.getPuntos();
        movimientos.add(movimiento);
    }

    public List<MovimientoEcoPuntos> getHistorial() {
        return Collections.unmodifiableList(movimientos);
    }

    @Override
    public void recibirNotificacion(EventoSistema evento) {
        notificaciones.add(evento.toString());
    }

    public List<String> getNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }

    public abstract TipoPersona getTipo();

    /** Multiplicador que aplican las estrategias de calculo de eco-puntos. */
    public abstract double getFactorBonificacion();

    public boolean puedeParticiparEnCampanias() {
        return true;
    }

    /** Dos personas son la misma si tienen el mismo id (necesario para Set). */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Persona persona)) {
            return false;
        }
        return id.equals(persona.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s <%s> - %d pts", getTipo(), id, nombre, correo, saldoPuntos);
    }
}
