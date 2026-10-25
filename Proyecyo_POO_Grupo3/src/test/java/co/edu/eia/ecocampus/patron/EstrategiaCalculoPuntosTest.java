package co.edu.eia.ecocampus.patron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.Colaborador;
import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.Estudiante;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.modelo.UnidadMedida;

class EstrategiaCalculoPuntosTest {

    private final Estudiante estudiante = new Estudiante("E1", "Ana", "ana@eia.edu.co");
    private final List<DetalleRecoleccion> detalles = List.of(
            new DetalleRecoleccion(TipoMaterial.RECICLABLE, 2, UnidadMedida.KG),
            new DetalleRecoleccion(TipoMaterial.ORGANICO, 500, UnidadMedida.GRAMO));

    @Test
    @DisplayName("Normal: por material = 2 kg x 10 + 0,5 kg x 5 = 22,5 -> 23")
    void porMaterial() {
        assertEquals(23, new EstrategiaPorMaterial().calcular(estudiante, detalles));
    }

    @Test
    @DisplayName("Polimorfismo: el factor de cada tipo de persona cambia el resultado")
    void factorPorTipoDePersona() {
        EstrategiaCalculoPuntos estrategia = new EstrategiaPorPesoTotal(10); // 2,5 kg x 10 = 25 base
        assertEquals(25, estrategia.calcular(estudiante, detalles));
        assertEquals(30, estrategia.calcular(new Colaborador("C1", "Marta", "m@eia.edu.co"), detalles));
        assertEquals(13, estrategia.calcular(new Operador("O1", "Carlos", "c@eia.edu.co"), detalles));
    }

    @Test
    @DisplayName("Frontera: sin detalles los puntos son cero, nunca negativos")
    void sinDetallesDaCero() {
        assertEquals(0, new EstrategiaPorMaterial().calcular(estudiante, List.of()));
        assertEquals(0, new EstrategiaPorPesoTotal(5).calcular(estudiante, List.of()));
    }

    @Test
    @DisplayName("Regla de campania: solo multiplica a participantes de una campania activa")
    void bonificacionCampania() throws Exception {
        Campania campania = new Campania("CAM", "Reto", LocalDate.now(), LocalDate.now().plusDays(5), 10);
        EstrategiaCalculoPuntos bonificada = new EstrategiaBonificacionCampania(new EstrategiaPorMaterial(),
                campania, 2.0);
        campania.inscribir(estudiante);

        assertEquals(23, bonificada.calcular(estudiante, detalles)); // campania aun PLANIFICADA
        campania.activar();
        assertEquals(46, bonificada.calcular(estudiante, detalles));
        Estudiante noInscrito = new Estudiante("E2", "Luis", "luis@eia.edu.co");
        assertEquals(23, bonificada.calcular(noInscrito, detalles));

        assertThrows(IllegalArgumentException.class,
                () -> new EstrategiaBonificacionCampania(new EstrategiaPorMaterial(), campania, 0.5));
    }

    @Test
    @DisplayName("Singleton: siempre se obtiene la misma configuracion")
    void configuracionEsSingleton() {
        assertSame(ConfiguracionSistema.getInstancia(), ConfiguracionSistema.getInstancia());
        assertSame(ConfiguracionSistema.getInstancia().getPublicador(),
                ConfiguracionSistema.getInstancia().getPublicador());
    }
}
