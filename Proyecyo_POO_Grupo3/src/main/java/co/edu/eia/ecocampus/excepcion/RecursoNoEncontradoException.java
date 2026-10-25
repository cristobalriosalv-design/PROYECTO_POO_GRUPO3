package co.edu.eia.ecocampus.excepcion;

/** Se lanza cuando una busqueda por identificador no encuentra resultados. */
public class RecursoNoEncontradoException extends EcoCampusException {

    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String entidad, String id) {
        super("No existe " + entidad + " con el identificador '" + id + "'");
    }
}
