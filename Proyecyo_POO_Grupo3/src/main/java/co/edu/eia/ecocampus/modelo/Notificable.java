package co.edu.eia.ecocampus.modelo;

/** Observador del patron Observer: recibe eventos relevantes del sistema. */
public interface Notificable {

    void recibirNotificacion(EventoSistema evento);
}
