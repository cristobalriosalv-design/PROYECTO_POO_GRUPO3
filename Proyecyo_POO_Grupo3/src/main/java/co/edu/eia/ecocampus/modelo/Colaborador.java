package co.edu.eia.ecocampus.modelo;

public class Colaborador extends Persona {

    private String dependencia;

    public Colaborador(String id, String nombre, String correo) {
        this(id, nombre, correo, "Sin dependencia");
    }

    public Colaborador(String id, String nombre, String correo, String dependencia) {
        super(id, nombre, correo);
        this.dependencia = dependencia;
    }

    public String getDependencia() {
        return dependencia;
    }

    @Override
    public TipoPersona getTipo() {
        return TipoPersona.COLABORADOR;
    }

    /** Incentivo para que el personal administrativo participe: +20 %. */
    @Override
    public double getFactorBonificacion() {
        return 1.2;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + dependencia;
    }
}
