package co.edu.eia.ecocampus.app;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;

/** Accion que puede violar una regla de negocio; se ejecuta dentro de un try/catch comun. */
@FunctionalInterface
public interface AccionConsola {

    void ejecutar() throws EcoCampusException;
}
