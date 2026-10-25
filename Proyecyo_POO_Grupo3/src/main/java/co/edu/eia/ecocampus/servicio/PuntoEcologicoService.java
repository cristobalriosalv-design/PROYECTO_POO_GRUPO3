package co.edu.eia.ecocampus.servicio;

import java.util.List;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;

public class PuntoEcologicoService {

    private final RepositorioMemoria<PuntoEcologico> puntos;
    private final RepositorioMemoria<ReporteResiduo> reportes;

    public PuntoEcologicoService(RepositorioMemoria<PuntoEcologico> puntos,
            RepositorioMemoria<ReporteResiduo> reportes) {
        this.puntos = puntos;
        this.reportes = reportes;
    }

    public void registrar(PuntoEcologico punto) throws IdentificadorDuplicadoException {
        puntos.guardar(punto);
    }

    public PuntoEcologico buscar(String codigo) throws RecursoNoEncontradoException {
        return puntos.obtener(codigo);
    }

    public void desactivar(String codigo) throws RecursoNoEncontradoException {
        puntos.obtener(codigo).desactivar();
    }

    public void activar(String codigo) throws RecursoNoEncontradoException {
        puntos.obtener(codigo).activar();
    }

    /** Regla: no se puede retirar un punto con reportes abiertos. */
    public void retirar(String codigo) throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        PuntoEcologico punto = puntos.obtener(codigo);
        List<ReporteResiduo> abiertos = reportes
                .filtrar(r -> r.getPunto().getId().equals(punto.getId()) && r.estaAbierto());
        if (!abiertos.isEmpty()) {
            throw new OperacionNoPermitidaException(
                    "No se puede retirar el punto " + codigo + ": tiene " + abiertos.size() + " reporte(s) abierto(s)");
        }
        puntos.eliminar(codigo);
    }

    public List<PuntoEcologico> listar() {
        return puntos.listar();
    }
}
