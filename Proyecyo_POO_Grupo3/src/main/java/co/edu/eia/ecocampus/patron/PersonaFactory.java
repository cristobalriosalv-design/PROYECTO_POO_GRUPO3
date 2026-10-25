package co.edu.eia.ecocampus.patron;

import co.edu.eia.ecocampus.modelo.Colaborador;
import co.edu.eia.ecocampus.modelo.Estudiante;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.ResponsableSostenibilidad;
import co.edu.eia.ecocampus.modelo.TipoPersona;

/** Factory: crea el tipo concreto de persona a partir del TipoPersona elegido en el menu. */
public class PersonaFactory {

    public Persona crearPersona(TipoPersona tipo, String id, String nombre, String correo) {
        return switch (tipo) {
            case ESTUDIANTE -> new Estudiante(id, nombre, correo);
            case COLABORADOR -> new Colaborador(id, nombre, correo);
            case OPERADOR -> new Operador(id, nombre, correo);
            case RESPONSABLE -> new ResponsableSostenibilidad(id, nombre, correo);
        };
    }
}
