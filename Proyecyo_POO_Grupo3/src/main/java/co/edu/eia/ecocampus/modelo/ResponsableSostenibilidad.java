package co.edu.eia.ecocampus.modelo;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.TransicionInvalidaException;

/** Clasifica reportes y gestiona campanias; no compite por eco-puntos en ellas. */
public class ResponsableSostenibilidad extends Persona {

    private String area;

    public ResponsableSostenibilidad(String id, String nombre, String correo) {
        this(id, nombre, correo, "Sostenibilidad");
    }

    public ResponsableSostenibilidad(String id, String nombre, String correo, String area) {
        super(id, nombre, correo);
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void clasificarReporte(ReporteResiduo reporte, Prioridad prioridad) throws TransicionInvalidaException {
        reporte.clasificar(prioridad);
    }

    public void cerrarCampania(Campania campania) throws OperacionNoPermitidaException {
        campania.cerrar();
    }

    @Override
    public TipoPersona getTipo() {
        return TipoPersona.RESPONSABLE;
    }

    @Override
    public double getFactorBonificacion() {
        return 1.0;
    }

    /** Organiza las campanias, por eso no puede inscribirse como participante. */
    @Override
    public boolean puedeParticiparEnCampanias() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " | area " + area;
    }
}
