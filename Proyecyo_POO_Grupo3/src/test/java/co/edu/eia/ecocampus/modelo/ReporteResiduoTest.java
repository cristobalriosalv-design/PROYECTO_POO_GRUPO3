package co.edu.eia.ecocampus.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.TransicionInvalidaException;

class ReporteResiduoTest {

    private Estudiante autor;
    private PuntoEcologico punto;
    private Operador operador;
    private ReporteResiduo reporte;

    @BeforeEach
    void setUp() throws OperacionNoPermitidaException {
        autor = new Estudiante("E1", "Ana", "ana@eia.edu.co");
        punto = new PuntoEcologico("P1", "Bloque A", 100);
        operador = new Operador("O1", "Carlos", "carlos@eia.edu.co", true);
        reporte = new ReporteResiduo("R1", autor, punto, TipoReporte.DESBORDAMIENTO, "Caneca llena");
    }

    private Recoleccion recoleccionDe(double kg) throws OperacionNoPermitidaException {
        Recoleccion recoleccion = new Recoleccion("REC1", operador, "ok");
        recoleccion.agregarDetalle(TipoMaterial.RECICLABLE, kg);
        return recoleccion;
    }

    @Test
    @DisplayName("Normal: el reporte recorre todos los estados hasta CERRADO y guarda historial")
    void flujoCompletoHastaCerrado() throws Exception {
        reporte.clasificar(Prioridad.ALTA);
        reporte.asignar(operador);
        reporte.iniciarAtencion();
        reporte.cerrar(recoleccionDe(5));

        assertEquals(EstadoReporte.CERRADO, reporte.getEstado());
        assertFalse(reporte.estaAbierto());
        assertEquals(5, reporte.getHistorial().size()); // creacion + 4 cambios
        assertEquals(EstadoReporte.EN_ATENCION, reporte.getHistorial().get(4).getEstadoAnterior());
    }

    @Test
    @DisplayName("Excepcion: un reporte cerrado no pasa a ASIGNADO sin reapertura")
    void cerradoNoPuedeAsignarseSinReabrir() throws Exception {
        reporte.clasificar(Prioridad.MEDIA);
        reporte.asignar(operador);
        reporte.iniciarAtencion();
        reporte.cerrar(recoleccionDe(1));

        TransicionInvalidaException ex = assertThrows(TransicionInvalidaException.class,
                () -> reporte.asignar(operador));
        assertEquals(EstadoReporte.CERRADO, ex.getOrigen());
        assertEquals(EstadoReporte.ASIGNADO, ex.getDestino());

        reporte.reabrir("El punto volvio a llenarse");
        reporte.asignar(operador);
        assertEquals(EstadoReporte.ASIGNADO, reporte.getEstado());
        assertNull(reporte.getRecoleccion());
    }

    @Test
    @DisplayName("Frontera: no se cierra con una recoleccion sin detalles (peso 0)")
    void noCierraConPesoCero() throws Exception {
        reporte.clasificar(Prioridad.MEDIA);
        reporte.asignar(operador);
        reporte.iniciarAtencion();
        Recoleccion vacia = new Recoleccion("REC0", operador, "sin material");

        assertThrows(OperacionNoPermitidaException.class, () -> reporte.cerrar(vacia));
        assertThrows(OperacionNoPermitidaException.class, () -> reporte.cerrar(null));
        assertEquals(EstadoReporte.EN_ATENCION, reporte.getEstado());
    }

    @Test
    @DisplayName("Excepcion: no se crean reportes sobre puntos inactivos")
    void puntoInactivoNoRecibeReportes() {
        punto.desactivar();
        assertThrows(OperacionNoPermitidaException.class,
                () -> new ReporteResiduo("R2", autor, punto, TipoReporte.OTRO, "x"));
    }

    @Test
    @DisplayName("Excepcion: residuo especial exige operador habilitado y disponible")
    void residuoEspecialRequiereOperadorHabilitado() throws Exception {
        ReporteResiduo especial = new ReporteResiduo("R3", autor, punto, TipoReporte.MATERIAL_ESPECIAL, "Baterias");
        especial.clasificar(Prioridad.CRITICA);
        Operador noHabilitado = new Operador("O2", "Pedro", "pedro@eia.edu.co", false);

        assertThrows(OperacionNoPermitidaException.class, () -> especial.asignar(noHabilitado));
        assertEquals(EstadoReporte.CLASIFICADO, especial.getEstado());

        operador.cambiarDisponibilidad(false);
        assertThrows(OperacionNoPermitidaException.class, () -> especial.asignar(operador));

        operador.cambiarDisponibilidad(true);
        especial.asignar(operador);
        assertTrue(especial.estaAbierto());
        assertEquals(operador, especial.getOperadorAsignado());
    }
}
