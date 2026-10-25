package co.edu.eia.ecocampus.patron;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.modelo.EventoSistema;
import co.edu.eia.ecocampus.modelo.Notificable;
import co.edu.eia.ecocampus.modelo.TipoEvento;

/**
 * Sujeto del patron Observer. Los servicios publican eventos sin conocer a
 * los observadores concretos, asi las clases no quedan acopladas entre si.
 */
public class PublicadorEventos {

    private final Map<TipoEvento, List<Notificable>> suscriptores;
    private final List<EventoSistema> eventosPublicados;

    public PublicadorEventos() {
        suscriptores = new EnumMap<>(TipoEvento.class);
        eventosPublicados = new ArrayList<>();
    }

    public void suscribir(TipoEvento tipo, Notificable observador) {
        suscriptores.computeIfAbsent(tipo, t -> new ArrayList<>()).add(observador);
    }

    public void desuscribir(TipoEvento tipo, Notificable observador) {
        List<Notificable> lista = suscriptores.get(tipo);
        if (lista != null) {
            lista.remove(observador);
        }
    }

    /** Notifica a los suscritos al tipo de evento. */
    public void publicar(EventoSistema evento) {
        publicar(evento, List.of());
    }

    /**
     * Notifica a los suscritos al tipo y ademas a interesados directos (por
     * ejemplo el autor de un reporte). Se aceptan colecciones de cualquier
     * subtipo de Notificable: {@code Set<Persona>}, {@code List<Operador>}...
     */
    public void publicar(EventoSistema evento, Collection<? extends Notificable> interesados) {
        eventosPublicados.add(evento);
        List<Notificable> destinatarios = new ArrayList<>(suscriptores.getOrDefault(evento.getTipo(), List.of()));
        for (Notificable interesado : interesados) {
            if (!destinatarios.contains(interesado)) {
                destinatarios.add(interesado);
            }
        }
        for (Notificable destinatario : destinatarios) {
            try {
                destinatario.recibirNotificacion(evento);
            } catch (RuntimeException e) {
                // Un observador defectuoso no debe impedir que los demas reciban el evento.
                System.err.println("Error notificando " + evento.getTipo() + ": " + e.getMessage());
            }
        }
    }

    public int cantidadSuscriptores(TipoEvento tipo) {
        return suscriptores.getOrDefault(tipo, List.of()).size();
    }

    public List<EventoSistema> getEventosPublicados() {
        return Collections.unmodifiableList(eventosPublicados);
    }
}
