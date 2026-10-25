package co.edu.eia.ecocampus.excepcion;

import co.edu.eia.ecocampus.modelo.EstadoReporte;

/** Se lanza cuando un reporte intenta pasar a un estado no permitido. */
public class TransicionInvalidaException extends EcoCampusException {

    private static final long serialVersionUID = 1L;

    private final EstadoReporte origen;
    private final EstadoReporte destino;

    public TransicionInvalidaException(EstadoReporte origen, EstadoReporte destino) {
        super("Transicion invalida: un reporte no puede pasar de " + origen + " a " + destino);
        this.origen = origen;
        this.destino = destino;
    }

    public EstadoReporte getOrigen() {
        return origen;
    }

    public EstadoReporte getDestino() {
        return destino;
    }
}
