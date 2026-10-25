package co.edu.eia.ecocampus.excepcion;

/** Se lanza cuando una operacion viola una regla de negocio. */
public class OperacionNoPermitidaException extends EcoCampusException {

    private static final long serialVersionUID = 1L;

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
