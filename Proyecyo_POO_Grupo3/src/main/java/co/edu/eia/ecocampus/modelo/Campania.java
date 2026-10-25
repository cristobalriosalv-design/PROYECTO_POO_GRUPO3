package co.edu.eia.ecocampus.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.util.Validador;

/**
 * Campania ambiental. Las actividades son partes de la campania (composicion)
 * y los participantes se guardan en un Set para impedir duplicados.
 */
public class Campania implements Identificable {

    public static final int PUNTOS_POR_DEFECTO = 10;

    private final String codigo;
    private String nombre;
    private String descripcion;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int cupo;
    private final int puntosPorParticipacion;
    private EstadoCampania estado;
    private final Set<Persona> participantes;
    private final List<Actividad> actividades;

    public Campania(String codigo, String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo) {
        this(codigo, nombre, "", fechaInicio, fechaFin, cupo, PUNTOS_POR_DEFECTO);
    }

    public Campania(String codigo, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin,
            int cupo, int puntosPorParticipacion) {
        this.codigo = Validador.validarTexto(codigo, "codigo");
        this.nombre = Validador.validarTexto(nombre, "nombre");
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fechaInicio");
        this.fechaFin = Objects.requireNonNull(fechaFin, "fechaFin");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        this.cupo = Validador.validarPositivo(cupo, "cupo");
        this.puntosPorParticipacion = Validador.validarPositivo(puntosPorParticipacion, "puntosPorParticipacion");
        this.estado = EstadoCampania.PLANIFICADA;
        this.participantes = new LinkedHashSet<>();
        this.actividades = new ArrayList<>();
    }

    /** La campania crea la actividad: su ciclo de vida depende de ella. */
    public Actividad agregarActividad(String id, String nombreActividad, LocalDateTime fecha, int cupoActividad)
            throws OperacionNoPermitidaException {
        validarNoCerrada("actividades");
        for (Actividad actividad : actividades) {
            if (actividad.getId().equals(id)) {
                throw new OperacionNoPermitidaException("La campania ya tiene una actividad con id " + id);
            }
        }
        LocalDate dia = Objects.requireNonNull(fecha, "fecha").toLocalDate();
        if (dia.isBefore(fechaInicio) || dia.isAfter(fechaFin)) {
            throw new OperacionNoPermitidaException("La actividad debe estar entre " + fechaInicio + " y " + fechaFin);
        }
        Actividad actividad = new Actividad(id, nombreActividad, fecha, cupoActividad);
        actividades.add(actividad);
        return actividad;
    }

    /** Reglas: sin duplicados, con cupo, campania no cerrada y persona habilitada. */
    public void inscribir(Persona persona) throws OperacionNoPermitidaException {
        Objects.requireNonNull(persona, "persona");
        validarNoCerrada("inscripciones");
        if (!persona.puedeParticiparEnCampanias()) {
            throw new OperacionNoPermitidaException(
                    persona.getNombre() + " (" + persona.getTipo() + ") no puede inscribirse en campanias");
        }
        if (participantes.contains(persona)) {
            throw new OperacionNoPermitidaException(
                    persona.getNombre() + " ya esta inscrito en la campania " + codigo);
        }
        if (!tieneCupo()) {
            throw new OperacionNoPermitidaException("La campania " + codigo + " no tiene cupos disponibles");
        }
        participantes.add(persona);
    }

    public void retirar(Persona persona) throws OperacionNoPermitidaException {
        validarNoCerrada("retiros");
        if (!participantes.remove(persona)) {
            throw new OperacionNoPermitidaException(
                    persona.getNombre() + " no esta inscrito en la campania " + codigo);
        }
    }

    public void activar() throws OperacionNoPermitidaException {
        if (estado != EstadoCampania.PLANIFICADA) {
            throw new OperacionNoPermitidaException("Solo se activa una campania PLANIFICADA; esta " + estado);
        }
        estado = EstadoCampania.ACTIVA;
    }

    public void cerrar() throws OperacionNoPermitidaException {
        if (estado == EstadoCampania.CERRADA) {
            throw new OperacionNoPermitidaException("La campania " + codigo + " ya esta cerrada");
        }
        estado = EstadoCampania.CERRADA;
    }

    private void validarNoCerrada(String operacion) throws OperacionNoPermitidaException {
        if (estado == EstadoCampania.CERRADA) {
            throw new OperacionNoPermitidaException("La campania " + codigo + " esta cerrada y no admite " + operacion);
        }
    }

    public boolean tieneCupo() {
        return participantes.size() < cupo;
    }

    public boolean alcanzoCupo() {
        return participantes.size() >= cupo;
    }

    public boolean tieneParticipantes() {
        return !participantes.isEmpty();
    }

    public boolean esParticipante(Persona persona) {
        return participantes.contains(persona);
    }

    @Override
    public String getId() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getCupo() {
        return cupo;
    }

    public int getPuntosPorParticipacion() {
        return puntosPorParticipacion;
    }

    public EstadoCampania getEstado() {
        return estado;
    }

    public Set<Persona> getParticipantes() {
        return Collections.unmodifiableSet(participantes);
    }

    public List<Actividad> getActividades() {
        return Collections.unmodifiableList(actividades);
    }

    @Override
    public String toString() {
        return String.format("Campania %s - %s [%s] %s a %s, %d/%d inscritos, %d actividades", codigo, nombre, estado,
                fechaInicio, fechaFin, participantes.size(), cupo, actividades.size());
    }
}
