package co.edu.eia.ecocampus.modelo;

public class Estudiante extends Persona {

    private String programa;
    private int semestre;

    public Estudiante(String id, String nombre, String correo) {
        this(id, nombre, correo, "Sin programa", 1);
    }

    public Estudiante(String id, String nombre, String correo, String programa, int semestre) {
        super(id, nombre, correo);
        if (semestre < 1 || semestre > 12) {
            throw new IllegalArgumentException("El semestre debe estar entre 1 y 12");
        }
        this.programa = programa;
        this.semestre = semestre;
    }

    public String getPrograma() {
        return programa;
    }

    public int getSemestre() {
        return semestre;
    }

    @Override
    public TipoPersona getTipo() {
        return TipoPersona.ESTUDIANTE;
    }

    /** Los estudiantes reciben los puntos base. */
    @Override
    public double getFactorBonificacion() {
        return 1.0;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + programa + ", semestre " + semestre;
    }
}
