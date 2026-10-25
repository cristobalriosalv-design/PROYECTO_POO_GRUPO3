package co.edu.eia.ecocampus.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.TransicionInvalidaException;
import co.edu.eia.ecocampus.util.Validador;

/**
 * Reporte de una novedad en un punto ecologico. Su estado solo cambia por
 * medio de los metodos de negocio, que validan la transicion y dejan rastro
 * en el historial (clase interna {@link RegistroCambio}).
 */
public class ReporteResiduo implements Identificable {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String codigo;
    private final LocalDateTime fecha;
    private final String descripcion;
    private final TipoReporte tipo;
    private Prioridad prioridad;
    private EstadoReporte estado;
    private final Persona autor;
    private final PuntoEcologico punto;
    private Operador operadorAsignado;
    private RutaRecoleccion rutaAsignada;
    private Recoleccion recoleccion;
    private final List<RegistroCambio> historial;

    public ReporteResiduo(String codigo, Persona autor, PuntoEcologico punto, TipoReporte tipo, String descripcion)
            throws OperacionNoPermitidaException {
        this(codigo, autor, punto, tipo, descripcion, Prioridad.MEDIA);
    }

    public ReporteResiduo(String codigo, Persona autor, PuntoEcologico punto, TipoReporte tipo, String descripcion,
            Prioridad prioridad) throws OperacionNoPermitidaException {
        this.codigo = Validador.validarTexto(codigo, "codigo");
        this.autor = Objects.requireNonNull(autor, "El reporte necesita un autor");
        this.punto = Objects.requireNonNull(punto, "El reporte necesita un punto ecologico");
        this.tipo = Objects.requireNonNull(tipo, "El reporte necesita un tipo");
        this.descripcion = Validador.validarTexto(descripcion, "descripcion");
        this.prioridad = Objects.requireNonNull(prioridad, "prioridad");
        if (!punto.estaActivo()) {
            throw new OperacionNoPermitidaException(
                    "El punto " + punto.getId() + " esta " + punto.getEstado() + " y no recibe nuevos reportes");
        }
        this.fecha = LocalDateTime.now();
        this.estado = EstadoReporte.REGISTRADO;
        this.historial = new ArrayList<>();
        historial.add(new RegistroCambio(null, EstadoReporte.REGISTRADO, "Reporte creado por " + autor.getNombre()));
    }

    // ------------------------------------------------------------ negocio

    public void clasificar(Prioridad nuevaPrioridad) throws TransicionInvalidaException {
        Objects.requireNonNull(nuevaPrioridad, "prioridad");
        cambiarEstado(EstadoReporte.CLASIFICADO, "Clasificado con prioridad " + nuevaPrioridad);
        this.prioridad = nuevaPrioridad;
    }

    public void asignar(Operador operador) throws TransicionInvalidaException, OperacionNoPermitidaException {
        Objects.requireNonNull(operador, "operador");
        verificarTransicion(EstadoReporte.ASIGNADO);
        validarOperador(operador);
        this.operadorAsignado = operador;
        cambiarEstado(EstadoReporte.ASIGNADO, "Asignado al operador " + operador.getNombre());
    }

    public void asignarARuta(RutaRecoleccion ruta) throws TransicionInvalidaException, OperacionNoPermitidaException {
        Objects.requireNonNull(ruta, "ruta");
        verificarTransicion(EstadoReporte.ASIGNADO);
        if (ruta.getEstado() == EstadoRuta.CERRADA) {
            throw new OperacionNoPermitidaException("La ruta " + ruta.getId() + " esta cerrada");
        }
        if (!ruta.contienePunto(punto)) {
            throw new OperacionNoPermitidaException(
                    "La ruta " + ruta.getId() + " no tiene una parada en el punto " + punto.getId());
        }
        if (ruta.getOperador() != null) {
            validarOperador(ruta.getOperador());
            this.operadorAsignado = ruta.getOperador();
        }
        this.rutaAsignada = ruta;
        ruta.agregarReporte(this);
        cambiarEstado(EstadoReporte.ASIGNADO, "Asignado a la ruta " + ruta.getId());
    }

    public void iniciarAtencion() throws TransicionInvalidaException, OperacionNoPermitidaException {
        verificarTransicion(EstadoReporte.EN_ATENCION);
        if (operadorAsignado == null) {
            throw new OperacionNoPermitidaException("El reporte " + codigo + " no tiene operador responsable");
        }
        cambiarEstado(EstadoReporte.EN_ATENCION, "Atencion iniciada por " + operadorAsignado.getNombre());
    }

    /** Regla: solo se cierra con una recoleccion asociada cuyo peso total sea mayor que cero. */
    public void cerrar(Recoleccion recoleccionAsociada) throws TransicionInvalidaException,
            OperacionNoPermitidaException {
        verificarTransicion(EstadoReporte.CERRADO);
        if (recoleccionAsociada == null || !recoleccionAsociada.esValidaParaCierre()) {
            throw new OperacionNoPermitidaException(
                    "El reporte " + codigo + " solo se cierra con una recoleccion de peso mayor que cero");
        }
        this.recoleccion = recoleccionAsociada;
        cambiarEstado(EstadoReporte.CERRADO, String.format("Cerrado con recoleccion %s (%.2f kg)",
                recoleccionAsociada.getId(), recoleccionAsociada.getPesoTotalKg()));
    }

    /** Reapertura explicita: unica forma de salir de CERRADO. */
    public void reabrir(String motivo) throws TransicionInvalidaException {
        cambiarEstado(EstadoReporte.REABIERTO, Validador.validarTexto(motivo, "motivo"));
        this.recoleccion = null;
    }

    private void verificarTransicion(EstadoReporte nuevo) throws TransicionInvalidaException {
        if (!estado.puedeCambiarA(nuevo)) {
            throw new TransicionInvalidaException(estado, nuevo);
        }
    }

    private void validarOperador(Operador operador) throws OperacionNoPermitidaException {
        if (!operador.puedeAtender(tipo)) {
            String causa = operador.isDisponible() ? "no esta habilitado para residuos especiales"
                    : "no esta disponible";
            throw new OperacionNoPermitidaException("El operador " + operador.getNombre() + " " + causa);
        }
    }

    private void cambiarEstado(EstadoReporte nuevo, String motivo) throws TransicionInvalidaException {
        verificarTransicion(nuevo);
        historial.add(new RegistroCambio(estado, nuevo, motivo));
        estado = nuevo;
    }

    // ------------------------------------------------------------ consultas

    public boolean estaAbierto() {
        return estado.esAbierto();
    }

    @Override
    public String getId() {
        return codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public Persona getAutor() {
        return autor;
    }

    public PuntoEcologico getPunto() {
        return punto;
    }

    public Operador getOperadorAsignado() {
        return operadorAsignado;
    }

    public RutaRecoleccion getRutaAsignada() {
        return rutaAsignada;
    }

    public Recoleccion getRecoleccion() {
        return recoleccion;
    }

    public List<RegistroCambio> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    @Override
    public String toString() {
        return String.format("Reporte %s [%s | %s | %s] punto %s por %s: %s", codigo, tipo, prioridad, estado,
                punto.getId(), autor.getNombre(), descripcion);
    }

    /**
     * Clase interna (no estatica): cada registro pertenece a un reporte concreto
     * y usa {@code ReporteResiduo.this} para mostrar a que reporte corresponde.
     */
    public class RegistroCambio {

        private final EstadoReporte estadoAnterior;
        private final EstadoReporte estadoNuevo;
        private final LocalDateTime fechaCambio;
        private final String motivo;

        private RegistroCambio(EstadoReporte estadoAnterior, EstadoReporte estadoNuevo, String motivo) {
            this.estadoAnterior = estadoAnterior;
            this.estadoNuevo = estadoNuevo;
            this.fechaCambio = LocalDateTime.now();
            this.motivo = motivo;
        }

        public EstadoReporte getEstadoAnterior() {
            return estadoAnterior;
        }

        public EstadoReporte getEstadoNuevo() {
            return estadoNuevo;
        }

        public LocalDateTime getFecha() {
            return fechaCambio;
        }

        public String getMotivo() {
            return motivo;
        }

        @Override
        public String toString() {
            String origen = estadoAnterior == null ? "-" : estadoAnterior.toString();
            return String.format("%s | %s: %s -> %s | %s", fechaCambio.format(FORMATO), ReporteResiduo.this.codigo,
                    origen, estadoNuevo, motivo);
        }
    }
}
