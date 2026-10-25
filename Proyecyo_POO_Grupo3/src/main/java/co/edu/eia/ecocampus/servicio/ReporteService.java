package co.edu.eia.ecocampus.servicio;

import java.util.Comparator;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.excepcion.TransicionInvalidaException;
import co.edu.eia.ecocampus.modelo.EventoSistema;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.Prioridad;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.TipoEvento;
import co.edu.eia.ecocampus.modelo.TipoReporte;
import co.edu.eia.ecocampus.patron.PublicadorEventos;
import co.edu.eia.ecocampus.patron.ReporteFactory;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;

/**
 * Casos de uso del ciclo de vida de un reporte. Despues de cada cambio de
 * estado publica un evento para el autor y los suscriptores (Observer).
 */
public class ReporteService {

    /** Mayor prioridad primero y, a igual prioridad, el mas antiguo primero. */
    private static final Comparator<ReporteResiduo> POR_PRIORIDAD = Comparator
            .comparing(ReporteResiduo::getPrioridad, Comparator.reverseOrder())
            .thenComparing(ReporteResiduo::getFecha);

    private final RepositorioMemoria<ReporteResiduo> reportes;
    private final ReporteFactory fabrica;
    private final PublicadorEventos publicador;
    private final EcoPuntosService ecoPuntos;

    public ReporteService(RepositorioMemoria<ReporteResiduo> reportes, ReporteFactory fabrica,
            PublicadorEventos publicador, EcoPuntosService ecoPuntos) {
        this.reportes = reportes;
        this.fabrica = fabrica;
        this.publicador = publicador;
        this.ecoPuntos = ecoPuntos;
    }

    public ReporteResiduo crearReporte(String codigo, Persona autor, PuntoEcologico punto, TipoReporte tipo,
            String descripcion) throws IdentificadorDuplicadoException, OperacionNoPermitidaException {
        if (reportes.existe(codigo)) {
            throw new IdentificadorDuplicadoException("un reporte", codigo);
        }
        ReporteResiduo reporte = fabrica.crearReporte(codigo, autor, punto, tipo, descripcion);
        reportes.guardar(reporte);
        notificarCambio(reporte);
        return reporte;
    }

    public void clasificar(String codigo, Prioridad prioridad)
            throws RecursoNoEncontradoException, TransicionInvalidaException {
        ReporteResiduo reporte = reportes.obtener(codigo);
        reporte.clasificar(prioridad);
        notificarCambio(reporte);
    }

    public void asignarAOperador(String codigo, Operador operador)
            throws RecursoNoEncontradoException, TransicionInvalidaException, OperacionNoPermitidaException {
        ReporteResiduo reporte = reportes.obtener(codigo);
        reporte.asignar(operador);
        notificarCambio(reporte);
    }

    public void asignarARuta(String codigo, RutaRecoleccion ruta)
            throws RecursoNoEncontradoException, TransicionInvalidaException, OperacionNoPermitidaException {
        ReporteResiduo reporte = reportes.obtener(codigo);
        reporte.asignarARuta(ruta);
        notificarCambio(reporte);
    }

    public void iniciarAtencion(String codigo)
            throws RecursoNoEncontradoException, TransicionInvalidaException, OperacionNoPermitidaException {
        ReporteResiduo reporte = reportes.obtener(codigo);
        reporte.iniciarAtencion();
        notificarCambio(reporte);
    }

    /** Cierra el reporte y otorga eco-puntos al autor segun la estrategia vigente. */
    public int cerrar(String codigo, Recoleccion recoleccion)
            throws RecursoNoEncontradoException, TransicionInvalidaException, OperacionNoPermitidaException {
        ReporteResiduo reporte = reportes.obtener(codigo);
        reporte.cerrar(recoleccion);
        notificarCambio(reporte);
        return ecoPuntos.otorgarPorRecoleccion(reporte.getAutor(), recoleccion);
    }

    public void reabrir(String codigo, String motivo) throws RecursoNoEncontradoException, TransicionInvalidaException {
        ReporteResiduo reporte = reportes.obtener(codigo);
        reporte.reabrir(motivo);
        notificarCambio(reporte);
    }

    public ReporteResiduo buscar(String codigo) throws RecursoNoEncontradoException {
        return reportes.obtener(codigo);
    }

    public List<ReporteResiduo> pendientesPorPrioridad() {
        List<ReporteResiduo> pendientes = reportes.filtrar(ReporteResiduo::estaAbierto);
        pendientes.sort(POR_PRIORIDAD);
        return pendientes;
    }

    public List<ReporteResiduo> pendientesPorPunto(String codigoPunto) {
        List<ReporteResiduo> pendientes = reportes
                .filtrar(r -> r.estaAbierto() && r.getPunto().getId().equals(codigoPunto));
        pendientes.sort(POR_PRIORIDAD);
        return pendientes;
    }

    public List<ReporteResiduo> reportesDePersona(String idPersona) {
        return reportes.filtrar(r -> r.getAutor().getId().equals(idPersona));
    }

    public List<ReporteResiduo> listar() {
        return reportes.listar();
    }

    private void notificarCambio(ReporteResiduo reporte) {
        EventoSistema evento = new EventoSistema(TipoEvento.CAMBIO_ESTADO_REPORTE,
                "El reporte " + reporte.getId() + " ahora esta " + reporte.getEstado(), reporte.getId());
        publicador.publicar(evento, List.of(reporte.getAutor()));
    }
}
