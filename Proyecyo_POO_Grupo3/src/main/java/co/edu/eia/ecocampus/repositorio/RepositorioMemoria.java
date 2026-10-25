package co.edu.eia.ecocampus.repositorio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.Identificable;

/**
 * Repositorio generico en memoria. El tipo acotado {@code T extends Identificable}
 * garantiza que todo elemento tiene getId(), que se usa como clave del Map.
 * LinkedHashMap: busqueda por id en O(1) y conserva el orden de registro.
 */
public class RepositorioMemoria<T extends Identificable> {

    private final String nombreEntidad;
    private final Map<String, T> elementos;

    public RepositorioMemoria(String nombreEntidad) {
        this.nombreEntidad = nombreEntidad;
        this.elementos = new LinkedHashMap<>();
    }

    /** Regla: los identificadores no pueden repetirse. */
    public void guardar(T elemento) throws IdentificadorDuplicadoException {
        Objects.requireNonNull(elemento, "elemento");
        if (elementos.containsKey(elemento.getId())) {
            throw new IdentificadorDuplicadoException(nombreEntidad, elemento.getId());
        }
        elementos.put(elemento.getId(), elemento);
    }

    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(elementos.get(id));
    }

    public T obtener(String id) throws RecursoNoEncontradoException {
        T elemento = elementos.get(id);
        if (elemento == null) {
            throw new RecursoNoEncontradoException(nombreEntidad, id);
        }
        return elemento;
    }

    public T eliminar(String id) throws RecursoNoEncontradoException {
        T eliminado = elementos.remove(id);
        if (eliminado == null) {
            throw new RecursoNoEncontradoException(nombreEntidad, id);
        }
        return eliminado;
    }

    public boolean existe(String id) {
        return elementos.containsKey(id);
    }

    public List<T> listar() {
        return new ArrayList<>(elementos.values());
    }

    /** El criterio consume T: acepta un Predicate de T o de cualquier supertipo (? super T). */
    public List<T> filtrar(Predicate<? super T> criterio) {
        List<T> resultado = new ArrayList<>();
        for (T elemento : elementos.values()) {
            if (criterio.test(elemento)) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }

    /** La coleccion produce T: acepta una coleccion de T o de subtipos (? extends T). */
    public void agregarTodos(Collection<? extends T> otros) throws IdentificadorDuplicadoException {
        for (T elemento : otros) {
            guardar(elemento);
        }
    }

    public int tamanio() {
        return elementos.size();
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }
}
