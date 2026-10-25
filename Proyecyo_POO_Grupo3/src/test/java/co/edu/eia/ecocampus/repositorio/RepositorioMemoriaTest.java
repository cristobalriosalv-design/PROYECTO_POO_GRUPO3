package co.edu.eia.ecocampus.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.IdentificadorDuplicadoException;
import co.edu.eia.ecocampus.excepcion.RecursoNoEncontradoException;
import co.edu.eia.ecocampus.modelo.Estudiante;
import co.edu.eia.ecocampus.modelo.Identificable;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;

class RepositorioMemoriaTest {

    private RepositorioMemoria<Persona> repo;
    private Estudiante ana;

    @BeforeEach
    void setUp() throws IdentificadorDuplicadoException {
        repo = new RepositorioMemoria<>("una persona");
        ana = new Estudiante("E1", "Ana", "ana@eia.edu.co");
        repo.guardar(ana);
    }

    @Test
    @DisplayName("Normal: guardar y buscar por identificador")
    void guardarYBuscar() throws RecursoNoEncontradoException {
        assertSame(ana, repo.obtener("E1"));
        assertTrue(repo.buscarPorId("E1").isPresent());
        assertEquals(1, repo.tamanio());
    }

    @Test
    @DisplayName("Excepcion: un identificador repetido se rechaza")
    void identificadorDuplicado() {
        IdentificadorDuplicadoException ex = assertThrows(IdentificadorDuplicadoException.class,
                () -> repo.guardar(new Estudiante("E1", "Otra", "otra@eia.edu.co")));
        assertEquals("E1", ex.getId());
        assertEquals(1, repo.tamanio());
    }

    @Test
    @DisplayName("Busqueda sin resultados: Optional vacio o excepcion clara")
    void busquedaSinResultados() {
        assertFalse(repo.buscarPorId("X9").isPresent());
        assertThrows(RecursoNoEncontradoException.class, () -> repo.obtener("X9"));
        assertThrows(RecursoNoEncontradoException.class, () -> repo.eliminar("X9"));
    }

    @Test
    @DisplayName("Wildcards: agregarTodos acepta subtipos y filtrar acepta criterios de supertipos")
    void wildcards() throws IdentificadorDuplicadoException {
        List<Operador> operadores = List.of(new Operador("O1", "Carlos", "c@eia.edu.co"),
                new Operador("O2", "Pedro", "p@eia.edu.co"));
        repo.agregarTodos(operadores); // List<Operador> -> Collection<? extends Persona>

        Predicate<Identificable> empiezaPorO = e -> e.getId().startsWith("O");
        assertEquals(2, repo.filtrar(empiezaPorO).size()); // Predicate<Identificable> -> Predicate<? super Persona>
        assertEquals(3, repo.listar().size());
    }
}
