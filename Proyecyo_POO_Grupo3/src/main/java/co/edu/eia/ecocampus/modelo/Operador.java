package co.edu.eia.ecocampus.modelo;

/** Persona que realiza las recolecciones. Controla disponibilidad y habilitacion. */
public class Operador extends Persona {

    private boolean disponible;
    private boolean habilitadoResiduosEspeciales;

    public Operador(String id, String nombre, String correo) {
        this(id, nombre, correo, false);
    }

    public Operador(String id, String nombre, String correo, boolean habilitadoResiduosEspeciales) {
        super(id, nombre, correo);
        this.disponible = true;
        this.habilitadoResiduosEspeciales = habilitadoResiduosEspeciales;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void cambiarDisponibilidad(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isHabilitadoResiduosEspeciales() {
        return habilitadoResiduosEspeciales;
    }

    public void setHabilitadoResiduosEspeciales(boolean habilitado) {
        this.habilitadoResiduosEspeciales = habilitado;
    }

    /** Puede atender si esta disponible y, para residuos especiales, si esta habilitado. */
    public boolean puedeAtender(TipoReporte tipo) {
        return disponible && (!tipo.requiereOperadorEspecial() || habilitadoResiduosEspeciales);
    }

    @Override
    public TipoPersona getTipo() {
        return TipoPersona.OPERADOR;
    }

    /** La recoleccion es parte de su trabajo: recibe la mitad de los puntos. */
    @Override
    public double getFactorBonificacion() {
        return 0.5;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + (disponible ? "disponible" : "no disponible")
                + (habilitadoResiduosEspeciales ? ", habilitado especiales" : "");
    }
}
