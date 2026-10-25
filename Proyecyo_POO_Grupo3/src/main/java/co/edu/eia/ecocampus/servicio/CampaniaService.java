package co.edu.eia.ecocampus.servicio;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.EventoSistema;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.TipoEvento;
import co.edu.eia.ecocampus.patron.PublicadorEventos;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;

public class CampaniaService {

    private final RepositorioMemoria<Campania> campanias;
    private final PublicadorEventos publicador;
    private final EcoPuntosService ecoPuntos;

    public CampaniaService(RepositorioMemoria<Campania> campanias, PublicadorEventos publicador,
            EcoPuntosService ecoPuntos) {
        this.campanias = campanias;
        this.publicador = publicador;
        this.ecoPuntos = ecoPuntos;
    }

    public void crearCampania(Campania campania) throws IdentificadorDuplicadoException {
        campanias.guardar(campania);
    }

    public Campania buscar(String codigo) throws RecursoNoEncontradoException {
        return campanias.obtener(codigo);
    }

    public void agregarActividad(String codigo, String id, String nombre, LocalDateTime fecha, int cupo)
            throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        campanias.obtener(codigo).agregarActividad(id, nombre, fecha, cupo);
    }

    public void activar(String codigo) throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        campanias.obtener(codigo).activar();
    }

    /** Si con esta inscripcion se completa el cupo, se notifica a los participantes. */
    public void inscribir(String codigo, Persona persona)
            throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        Campania campania = campanias.obtener(codigo);
        campania.inscribir(persona);
        if (campania.alcanzoCupo()) {
            publicador.publicar(new EventoSistema(TipoEvento.CUPO_CAMPANIA_ALCANZADO,
                    "La campania " + campania.getNombre() + " completo su cupo de " + campania.getCupo(),
                    campania.getId()), campania.getParticipantes());
        }
    }

    public void retirarParticipante(String codigo, Persona persona)
            throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        campanias.obtener(codigo).retirar(persona);
    }

    /** Cierra la campania y otorga los puntos de participacion. Devuelve el total otorgado. */
    public int cerrar(String codigo) throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        Campania campania = campanias.obtener(codigo);
        campania.cerrar();
        int total = 0;
        for (Persona participante : campania.getParticipantes()) {
            total += ecoPuntos.otorgarPorCampania(participante, campania);
        }
        return total;
    }

    /** Regla: no se puede retirar una campania con participaciones activas. */
    public void retirarCampania(String codigo) throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        Campania campania = campanias.obtener(codigo);
        if (campania.tieneParticipantes()) {
            throw new OperacionNoPermitidaException("No se puede retirar la campania " + codigo + ": tiene "
                    + campania.getParticipantes().size() + " participante(s)");
        }
        campanias.eliminar(codigo);
    }

    public List<Campania> listar() {
        return campanias.listar();
    }
}
