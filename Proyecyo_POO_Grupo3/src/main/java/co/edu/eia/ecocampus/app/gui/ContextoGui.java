package co.edu.eia.ecocampus.app.gui;

import co.edu.eia.ecocampus.app.ContextoAplicacion;
import co.edu.eia.ecocampus.app.DatosDemo;
import co.edu.eia.ecocampus.excepcion.EcoCampusException;

/**
 * Da a todos los controladores de la interfaz el mismo ContextoAplicacion.
 * Hace falta porque FXMLLoader crea los controladores con un constructor vacio.
 */
public final class ContextoGui {

    private static ContextoAplicacion contexto;

    private ContextoGui() {
    }

    public static ContextoAplicacion get() {
        if (contexto == null) {
            contexto = ContextoAplicacion.crear();
            try {
                DatosDemo.cargar(contexto);
            } catch (EcoCampusException e) {
                System.err.println("No se cargaron los datos de demostracion: " + e.getMessage());
            }
        }
        return contexto;
    }
}
