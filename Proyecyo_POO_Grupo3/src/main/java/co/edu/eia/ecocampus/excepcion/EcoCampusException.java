package co.edu.eia.ecocampus.excepcion;

/**
 * Excepcion base (verificada) del dominio EcoCampus. Todas las excepciones
 * personalizadas heredan de ella para poder capturarlas de forma comun.
 */
public class EcoCampusException extends Exception {

    private static final long serialVersionUID = 1L;

    public EcoCampusException(String mensaje) {
        super(mensaje);
    }

    public EcoCampusException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
