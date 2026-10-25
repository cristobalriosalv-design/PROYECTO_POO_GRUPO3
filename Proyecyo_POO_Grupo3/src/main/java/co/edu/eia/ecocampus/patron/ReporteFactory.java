package co.edu.eia.ecocampus.patron;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.Prioridad;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.modelo.TipoReporte;

/**
 * Factory: centraliza la creacion de reportes y decide la prioridad inicial
 * segun el tipo, para que ningun cliente tenga que conocer esa regla.
 */
public class ReporteFactory {

    public ReporteResiduo crearReporte(String codigo, Persona autor, PuntoEcologico punto, TipoReporte tipo,
            String descripcion) throws OperacionNoPermitidaException {
        return new ReporteResiduo(codigo, autor, punto, tipo, descripcion, prioridadInicial(tipo));
    }

    Prioridad prioridadInicial(TipoReporte tipo) {
        return switch (tipo) {
            case MATERIAL_ESPECIAL, DESBORDAMIENTO -> Prioridad.ALTA;
            case CONTAMINACION -> Prioridad.MEDIA;
            case OTRO -> Prioridad.BAJA;
        };
    }
}
