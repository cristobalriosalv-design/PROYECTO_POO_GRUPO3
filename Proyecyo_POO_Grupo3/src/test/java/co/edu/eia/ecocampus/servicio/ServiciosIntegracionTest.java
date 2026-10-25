package co.edu.eia.ecocampus.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.EventoSistema;
import co.edu.eia.ecocampus.modelo.Estudiante;
import co.edu.eia.ecocampus.modelo.MovimientoEcoPuntos;
import co.edu.eia.ecocampus.modelo.Notificable;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.Prioridad;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.modelo.TipoEvento;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.modelo.TipoReporte;
import co.edu.eia.ecocampus.modelo.UnidadMedida;
import co.edu.eia.ecocampus.patron.ConfiguracionSistema;
import co.edu.eia.ecocampus.patron.EstrategiaPorMaterial;
import co.edu.eia.ecocampus.patron.EstrategiaPorPesoTotal;
import co.edu.eia.ecocampus.patron.PublicadorEventos;
import co.edu.eia.ecocampus.patron.ReporteFactory;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;

/** Pruebas de los casos de uso conectando servicios, repositorios y patrones. */
class ServiciosIntegracionTest {

    private PublicadorEventos publicador;
    private EcoPuntosService ecoPuntos;
    private ReporteService reportes;
    private PuntoEcologicoService puntos;
    private CampaniaService campanias;

    private Estudiante ana;
    private Operador carlos;
    private PuntoEcologico p1;

    @BeforeEach
    void setUp() throws Exception {
        ConfiguracionSistema.getInstancia().setEstrategiaPuntos(new EstrategiaPorMaterial());
        publicador = new PublicadorEventos();
        ecoPuntos = new EcoPuntosService(ConfiguracionSistema.getInstancia());
        RepositorioMemoria<ReporteResiduo> repoReportes = new RepositorioMemoria<>("un reporte");
        reportes = new ReporteService(repoReportes, new ReporteFactory(), publicador, ecoPuntos);
        puntos = new PuntoEcologicoService(new RepositorioMemoria<>("un punto"), repoReportes);
        campanias = new CampaniaService(new RepositorioMemoria<>("una campania"), publicador, ecoPuntos);

        ana = new Estudiante("E1", "Ana", "ana@eia.edu.co");
        carlos = new Operador("O1", "Carlos", "carlos@eia.edu.co", true);
        p1 = new PuntoEcologico("P1", "Bloque A", 100);
        puntos.registrar(p1);
    }

    @AfterEach
    void restaurarEstrategia() {
        ConfiguracionSistema.getInstancia().setEstrategiaPuntos(new EstrategiaPorMaterial());
    }

    private void llevarHastaAtencion(String codigo) throws Exception {
        reportes.clasificar(codigo, Prioridad.ALTA);
        reportes.asignarAOperador(codigo, carlos);
        reportes.iniciarAtencion(codigo);
    }

    @Test
    @DisplayName("Cerrar un reporte otorga eco-puntos al autor y le notifica cada cambio (Observer)")
    void cerrarOtorgaPuntosYNotifica() throws Exception {
        reportes.crearReporte("R1", ana, p1, TipoReporte.DESBORDAMIENTO, "Caneca llena");
        llevarHastaAtencion("R1");
        Recoleccion rec = new Recoleccion("REC1", carlos, "ok");
        rec.agregarDetalle(TipoMaterial.RECICLABLE, 3); // 3 kg x 10

        assertEquals(30, reportes.cerrar("R1", rec));
        assertEquals(30, ana.getSaldoPuntos());
        assertEquals(5, ana.getNotificaciones().size()); // REGISTRADO..CERRADO
        assertEquals(3.0, ecoPuntos.materialesRecolectados().get(TipoMaterial.RECICLABLE));
    }

    @Test
    @DisplayName("Strategy: cambiar la politica cambia los puntos sin tocar las entidades")
    void cambiarEstrategia() throws Exception {
        ecoPuntos.cambiarEstrategia(new EstrategiaPorPesoTotal(1));
        reportes.crearReporte("R1", ana, p1, TipoReporte.CONTAMINACION, "Mezcla");
        llevarHastaAtencion("R1");
        Recoleccion rec = new Recoleccion("REC1", carlos, "ok");
        rec.agregarDetalle(TipoMaterial.RECICLABLE, 3);

        assertEquals(3, reportes.cerrar("R1", rec));
    }

    @Test
    @DisplayName("Busqueda: pendientes ordenados por prioridad; lista vacia si no hay")
    void pendientesPorPrioridad() throws Exception {
        assertTrue(reportes.pendientesPorPrioridad().isEmpty());
        reportes.crearReporte("R-BAJA", ana, p1, TipoReporte.OTRO, "Tapa rota");
        reportes.crearReporte("R-ALTA", ana, p1, TipoReporte.MATERIAL_ESPECIAL, "Baterias");
        reportes.crearReporte("R-MEDIA", ana, p1, TipoReporte.CONTAMINACION, "Mezcla");

        List<String> orden = reportes.pendientesPorPrioridad().stream().map(ReporteResiduo::getId).toList();
        assertEquals(List.of("R-ALTA", "R-MEDIA", "R-BAJA"), orden);
        assertTrue(reportes.pendientesPorPunto("NO-EXISTE").isEmpty());
    }

    @Test
    @DisplayName("Excepcion: no se retira un punto con reportes abiertos")
    void noRetiraPuntoConReportesAbiertos() throws Exception {
        reportes.crearReporte("R1", ana, p1, TipoReporte.OTRO, "x");
        assertThrows(OperacionNoPermitidaException.class, () -> puntos.retirar("P1"));
        assertEquals(1, puntos.listar().size());
    }

    @Test
    @DisplayName("Observer: al completar el cupo se notifica a suscriptores y participantes")
    void cupoCompletoNotifica() throws Exception {
        List<EventoSistema> recibidos = new ArrayList<>();
        publicador.suscribir(TipoEvento.CUPO_CAMPANIA_ALCANZADO, new Notificable() {
            @Override
            public void recibirNotificacion(EventoSistema evento) {
                recibidos.add(evento);
            }
        });
        campanias.crearCampania(new Campania("CAM", "Reto", LocalDate.now(), LocalDate.now().plusDays(3), 2));
        campanias.inscribir("CAM", ana);
        assertEquals(0, recibidos.size());
        campanias.inscribir("CAM", new Estudiante("E2", "Luis", "luis@eia.edu.co"));

        assertEquals(1, recibidos.size());
        assertEquals(1, ana.getNotificaciones().size());
    }

    @Test
    @DisplayName("Eco-puntos nunca negativos; cada movimiento conserva concepto y fecha")
    void ecoPuntosNoNegativos() throws Exception {
        campanias.crearCampania(new Campania("CAM", "Reto", "", LocalDate.now(), LocalDate.now(), 5, 20));
        campanias.inscribir("CAM", ana);
        assertEquals(20, campanias.cerrar("CAM"));

        assertThrows(OperacionNoPermitidaException.class, () -> ecoPuntos.canjear(ana, 21, "Termo"));
        ecoPuntos.canjear(ana, 20, "Termo"); // frontera: queda exactamente en cero
        assertEquals(0, ana.getSaldoPuntos());

        MovimientoEcoPuntos canje = ana.getHistorial().get(1);
        assertEquals(-20, canje.getPuntos());
        assertTrue(canje.getConcepto().contains("Termo"));
        assertEquals(0, ecoPuntos.sumarPuntos(ana.getHistorial()));
    }

    @Test
    @DisplayName("Wildcards: copiarDetalles hacia una lista de supertipo y ranking con subtipos")
    void wildcardsEnServicio() throws Exception {
        List<DetalleRecoleccion> origen = List.of(new DetalleRecoleccion(TipoMaterial.OTRO, 1,
                UnidadMedida.KG));
        List<Object> destino = new ArrayList<>();
        ecoPuntos.copiarDetalles(origen, destino); // List<Object> -> List<? super DetalleRecoleccion>
        assertEquals(1, destino.size());

        Estudiante luis = new Estudiante("E2", "Luis", "luis@eia.edu.co");
        luis.agregarMovimiento(new MovimientoEcoPuntos("M1", "Bono", 50));
        List<Persona> ranking = ecoPuntos.ranking(List.of(ana, luis)); // List<Estudiante>
        assertEquals("Luis", ranking.get(0).getNombre());
    }
}
