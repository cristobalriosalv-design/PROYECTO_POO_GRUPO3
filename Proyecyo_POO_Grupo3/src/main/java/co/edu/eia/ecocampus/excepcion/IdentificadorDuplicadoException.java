package co.edu.eia.ecocampus.excepcion;

/** Se lanza cuando se intenta registrar un identificador que ya existe. */
public class IdentificadorDuplicadoException extends EcoCampusException {

    private static final long serialVersionUID = 1L;

    private final String id;

    public IdentificadorDuplicadoException(String entidad, String id) {
        super("Ya existe " + entidad + " con el identificador '" + id + "'");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
