package co.edu.eia.ecocampus.app;

import java.util.Scanner;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.EventoSistema;
import co.edu.eia.ecocampus.modelo.Notificable;
import co.edu.eia.ecocampus.modelo.TipoEvento;
import co.edu.eia.ecocampus.patron.PublicadorEventos;

/** Punto de entrada de la aplicacion de consola. */
public class Main {

    public static void main(String[] args) {
        ContextoAplicacion contexto = ContextoAplicacion.crear();
        registrarBitacora(contexto.getConfiguracion().getPublicador());

        try {
            DatosDemo.cargar(contexto);
            System.out.println("Datos de demostracion cargados.");
        } catch (EcoCampusException e) {
            System.out.println("No se pudieron cargar todos los datos de demostracion: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        try {
            new MenuConsola(contexto, scanner).iniciar();
        } finally {
            scanner.close();
            System.out.println("Hasta pronto.");
        }
    }

    /**
     * Implementacion anonima de Notificable: una bitacora de consola que solo
     * se necesita aqui, por eso no merece una clase con nombre propio.
     */
    private static void registrarBitacora(PublicadorEventos publicador) {
        Notificable bitacora = new Notificable() {
            @Override
            public void recibirNotificacion(EventoSistema evento) {
                System.out.println("  [BITACORA] " + evento);
            }
        };
        publicador.suscribir(TipoEvento.RUTA_CERRADA, bitacora);
        publicador.suscribir(TipoEvento.CUPO_CAMPANIA_ALCANZADO, bitacora);
    }
}
