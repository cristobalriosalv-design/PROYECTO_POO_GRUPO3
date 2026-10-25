package co.edu.eia.ecocampus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.edu.eia.ecocampus.modelo.Identificable;

/** Metodos genericos de apoyo para colecciones del dominio. */
public final class ColeccionesUtil {

    private ColeccionesUtil() {
    }

    /**
     * Metodo generico con tipo acotado: solo acepta elementos Identificable.
     * La coleccion es productora de T, por eso usa {@code ? extends T}.
     */
    public static <T extends Identificable> Optional<T> buscarPorId(Collection<? extends T> elementos, String id) {
        for (T elemento : elementos) {
            if (elemento.getId().equals(id)) {
                return Optional.of(elemento);
            }
        }
        return Optional.empty();
    }

    /**
     * Ordena las entradas de un mapa de mayor a menor valor. V debe poder
     * compararse consigo mismo o con un supertipo ({@code Comparable<? super V>}).
     */
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> ordenarPorValorDesc(Map<K, V> mapa) {
        List<Map.Entry<K, V>> entradas = new ArrayList<>(mapa.entrySet());
        entradas.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return entradas;
    }
}
