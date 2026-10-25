package co.edu.eia.ecocampus.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;

class CampaniaTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 11, 1);

    private Campania campania;
    private Estudiante ana;
    private Estudiante luis;

    @BeforeEach
    void setUp() {
        campania = new Campania("CAM", "Semana verde", INICIO, INICIO.plusDays(10), 2);
        ana = new Estudiante("E1", "Ana", "ana@eia.edu.co");
        luis = new Estudiante("E2", "Luis", "luis@eia.edu.co");
    }

    @Test
    @DisplayName("Excepcion: una persona no puede inscribirse dos veces")
    void inscripcionDuplicada() throws Exception {
        campania.inscribir(ana);
        Estudiante mismaAna = new Estudiante("E1", "Ana (copia)", "otra@eia.edu.co");

        assertThrows(OperacionNoPermitidaException.class, () -> campania.inscribir(mismaAna));
        assertEquals(1, campania.getParticipantes().size());
    }

    @Test
    @DisplayName("Frontera: se admite exactamente el cupo y se rechaza el siguiente")
    void cupoExacto() throws Exception {
        campania.inscribir(ana);
        assertFalse(campania.alcanzoCupo());
        campania.inscribir(luis);
        assertTrue(campania.alcanzoCupo());

        Colaborador marta = new Colaborador("C1", "Marta", "marta@eia.edu.co");
        assertThrows(OperacionNoPermitidaException.class, () -> campania.inscribir(marta));
    }

    @Test
    @DisplayName("Excepcion: una campania cerrada no admite inscripciones ni actividades")
    void campaniaCerradaNoAdmiteCambios() throws Exception {
        campania.cerrar();
        assertThrows(OperacionNoPermitidaException.class, () -> campania.inscribir(ana));
        assertThrows(OperacionNoPermitidaException.class,
                () -> campania.agregarActividad("A1", "Taller", INICIO.atTime(9, 0), 10));
    }

    @Test
    @DisplayName("Normal y frontera: actividades dentro del rango; fuera del rango se rechazan")
    void actividadesComoParteDeLaCampania() throws Exception {
        campania.agregarActividad("A1", "Taller", INICIO.atTime(9, 0), 10);
        campania.agregarActividad("A2", "Cierre", INICIO.plusDays(10).atTime(17, 0), 10);
        assertEquals(2, campania.getActividades().size());

        assertThrows(OperacionNoPermitidaException.class,
                () -> campania.agregarActividad("A3", "Tarde", INICIO.plusDays(11).atTime(9, 0), 10));
        assertThrows(OperacionNoPermitidaException.class,
                () -> campania.agregarActividad("A1", "Repetida", INICIO.atTime(9, 0), 10));
    }

    @Test
    @DisplayName("Polimorfismo: el responsable de sostenibilidad no puede inscribirse")
    void responsableNoParticipa() {
        ResponsableSostenibilidad laura = new ResponsableSostenibilidad("R1", "Laura", "laura@eia.edu.co");
        assertThrows(OperacionNoPermitidaException.class, () -> campania.inscribir(laura));
    }
}
