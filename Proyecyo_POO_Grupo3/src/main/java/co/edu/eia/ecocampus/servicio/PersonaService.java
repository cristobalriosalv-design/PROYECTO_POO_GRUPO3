package co.edu.eia.ecocampus.servicio;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.MovimientoEcoPuntos;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.TipoPersona;
import co.edu.eia.ecocampus.patron.PersonaFactory;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;

/** Casos de uso de personas, operadores y responsables. */
public class PersonaService {

    private final RepositorioMemoria<Persona> personas;
    private final PersonaFactory fabrica;

    public PersonaService(RepositorioMemoria<Persona> personas, PersonaFactory fabrica) {
        this.personas = personas;
        this.fabrica = fabrica;
    }

    public Persona registrar(TipoPersona tipo, String id, String nombre, String correo)
            throws IdentificadorDuplicadoException {
        if (personas.existe(id)) {
            throw new IdentificadorDuplicadoException("una persona", id);
        }
        Persona persona = fabrica.crearPersona(tipo, id, nombre, correo);
        personas.guardar(persona);
        return persona;
    }

    /** Sobrecarga para registrar subtipos con datos adicionales (programa, dependencia...). */
    public <P extends Persona> P registrar(P persona) throws IdentificadorDuplicadoException {
        personas.guardar(persona);
        return persona;
    }

    public Persona buscar(String id) throws RecursoNoEncontradoException {
        return personas.obtener(id);
    }

    public Operador buscarOperador(String id) throws RecursoNoEncontradoException {
        Persona persona = personas.obtener(id);
        if (persona instanceof Operador operador) {
            return operador;
        }
        throw new RecursoNoEncontradoException("un operador", id);
    }

    public List<MovimientoEcoPuntos> historial(String id) throws RecursoNoEncontradoException {
        return personas.obtener(id).getHistorial();
    }

    public List<Operador> operadoresDisponibles() {
        List<Operador> disponibles = new ArrayList<>();
        for (Persona persona : personas.listar()) {
            if (persona instanceof Operador operador && operador.isDisponible()) {
                disponibles.add(operador);
            }
        }
        return disponibles;
    }

    public List<Persona> listar() {
        return personas.listar();
    }
}
