package co.edu.eia.ecocampus.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.util.Validador;

/** Ruta compuesta por paradas ordenadas (composicion: la ruta crea sus paradas). */
public class RutaRecoleccion implements Identificable {

    private final String codigo;
    private String nombre;
    private EstadoRuta estado;
    private Operador operador;
    private final List<Parada> paradas;
    private final List<ReporteResiduo> reportes;

    public RutaRecoleccion(String codigo, String nombre) {
        this.codigo = Validador.validarTexto(codigo, "codigo");
        this.nombre = Validador.validarTexto(nombre, "nombre");
        this.estado = EstadoRuta.PLANIFICADA;
        this.paradas = new ArrayList<>();
        this.reportes = new ArrayList<>();
    }

    /** Regla: un punto inactivo no puede formar parte de una nueva ruta. */
    public Parada agregarParada(PuntoEcologico punto, String accionEsperada) throws OperacionNoPermitidaException {
        Objects.requireNonNull(punto, "punto");
        validarNoCerrada();
        if (!punto.estaActivo()) {
            throw new OperacionNoPermitidaException(
                    "El punto " + punto.getId() + " esta " + punto.getEstado() + " y no puede agregarse a una ruta");
        }
        if (contienePunto(punto)) {
            throw new OperacionNoPermitidaException("La ruta " + codigo + " ya visita el punto " + punto.getId());
        }
        Parada parada = new Parada(paradas.size() + 1, punto, accionEsperada);
        paradas.add(parada);
        return parada;
    }

    public void quitarParada(int orden) throws OperacionNoPermitidaException {
        validarNoCerrada();
        if (orden < 1 || orden > paradas.size()) {
            throw new OperacionNoPermitidaException("La ruta " + codigo + " no tiene una parada con orden " + orden);
        }
        paradas.remove(orden - 1);
        for (int i = 0; i < paradas.size(); i++) {
            paradas.get(i).setOrden(i + 1);
        }
    }

    public void asignarOperador(Operador nuevoOperador) throws OperacionNoPermitidaException {
        Objects.requireNonNull(nuevoOperador, "operador");
        validarNoCerrada();
        if (!nuevoOperador.isDisponible()) {
            throw new OperacionNoPermitidaException("El operador " + nuevoOperador.getNombre() + " no esta disponible");
        }
        this.operador = nuevoOperador;
    }

    /** Lo invoca ReporteResiduo.asignarARuta para mantener la relacion en ambos sentidos. */
    void agregarReporte(ReporteResiduo reporte) {
        if (!reportes.contains(reporte)) {
            reportes.add(reporte);
        }
    }

    public void iniciar() throws OperacionNoPermitidaException {
        if (estado != EstadoRuta.PLANIFICADA) {
            throw new OperacionNoPermitidaException("Solo se inicia una ruta PLANIFICADA; la ruta esta " + estado);
        }
        if (paradas.isEmpty() || operador == null) {
            throw new OperacionNoPermitidaException("La ruta necesita al menos una parada y un operador");
        }
        estado = EstadoRuta.EN_CURSO;
    }

    public void cerrar() throws OperacionNoPermitidaException {
        if (estado != EstadoRuta.EN_CURSO) {
            throw new OperacionNoPermitidaException("Solo se cierra una ruta EN_CURSO; la ruta esta " + estado);
        }
        for (Parada parada : paradas) {
            parada.marcarCompletada();
        }
        estado = EstadoRuta.CERRADA;
    }

    public boolean contienePunto(PuntoEcologico punto) {
        for (Parada parada : paradas) {
            if (parada.getPunto().getId().equals(punto.getId())) {
                return true;
            }
        }
        return false;
    }

    private void validarNoCerrada() throws OperacionNoPermitidaException {
        if (estado == EstadoRuta.CERRADA) {
            throw new OperacionNoPermitidaException("La ruta " + codigo + " esta cerrada");
        }
    }

    @Override
    public String getId() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadoRuta getEstado() {
        return estado;
    }

    public Operador getOperador() {
        return operador;
    }

    public List<Parada> getParadas() {
        return Collections.unmodifiableList(paradas);
    }

    public List<ReporteResiduo> getReportes() {
        return Collections.unmodifiableList(reportes);
    }

    @Override
    public String toString() {
        String nombreOperador = operador == null ? "sin operador" : operador.getNombre();
        return String.format("Ruta %s - %s [%s] %d paradas, %s", codigo, nombre, estado, paradas.size(),
                nombreOperador);
    }
}
