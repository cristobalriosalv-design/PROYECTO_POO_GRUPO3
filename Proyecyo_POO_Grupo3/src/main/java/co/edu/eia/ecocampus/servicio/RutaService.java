package co.edu.eia.ecocampus.servicio;

import java.util.ArrayList;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.EventoSistema;
import co.edu.eia.ecocampus.modelo.Notificable;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.TipoEvento;
import co.edu.eia.ecocampus.patron.PublicadorEventos;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;

public class RutaService {

    private final RepositorioMemoria<RutaRecoleccion> rutas;
    private final PublicadorEventos publicador;

    public RutaService(RepositorioMemoria<RutaRecoleccion> rutas, PublicadorEventos publicador) {
        this.rutas = rutas;
        this.publicador = publicador;
    }

    public RutaRecoleccion crearRuta(String codigo, String nombre) throws IdentificadorDuplicadoException {
        RutaRecoleccion ruta = new RutaRecoleccion(codigo, nombre);
        rutas.guardar(ruta);
        return ruta;
    }

    public RutaRecoleccion buscar(String codigo) throws RecursoNoEncontradoException {
        return rutas.obtener(codigo);
    }

    public void agregarParada(String codigoRuta, PuntoEcologico punto, String accion)
            throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        rutas.obtener(codigoRuta).agregarParada(punto, accion);
    }

    public void asignarOperador(String codigoRuta, Operador operador)
            throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        rutas.obtener(codigoRuta).asignarOperador(operador);
    }

    public void iniciarRuta(String codigoRuta) throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        rutas.obtener(codigoRuta).iniciar();
    }

    /** Al cerrar la ruta se notifica al operador y a los autores de sus reportes. */
    public void cerrarRuta(String codigoRuta) throws RecursoNoEncontradoException, OperacionNoPermitidaException {
        RutaRecoleccion ruta = rutas.obtener(codigoRuta);
        ruta.cerrar();
        List<Notificable> interesados = new ArrayList<>();
        if (ruta.getOperador() != null) {
            interesados.add(ruta.getOperador());
        }
        ruta.getReportes().forEach(r -> interesados.add(r.getAutor()));
        publicador.publicar(new EventoSistema(TipoEvento.RUTA_CERRADA,
                "La ruta " + ruta.getId() + " (" + ruta.getNombre() + ") fue cerrada", ruta.getId()), interesados);
    }

    public List<RutaRecoleccion> listar() {
        return rutas.listar();
    }
}
