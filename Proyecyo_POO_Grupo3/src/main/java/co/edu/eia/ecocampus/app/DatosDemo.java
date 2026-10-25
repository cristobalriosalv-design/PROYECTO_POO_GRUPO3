package co.edu.eia.ecocampus.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.Colaborador;
import co.edu.eia.ecocampus.modelo.Estudiante;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Prioridad;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.ResponsableSostenibilidad;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.modelo.TipoReporte;
import co.edu.eia.ecocampus.modelo.UnidadMedida;
import co.edu.eia.ecocampus.servicio.CampaniaService;
import co.edu.eia.ecocampus.servicio.PersonaService;
import co.edu.eia.ecocampus.servicio.PuntoEcologicoService;
import co.edu.eia.ecocampus.servicio.ReporteService;
import co.edu.eia.ecocampus.servicio.RutaService;

/** Escenario de demostracion con objetos relacionados y casos invalidos. */
public final class DatosDemo {

    private DatosDemo() {
    }

    public static void cargar(ContextoAplicacion ctx) throws EcoCampusException {
        PersonaService personas = ctx.getPersonas();
        PuntoEcologicoService puntos = ctx.getPuntos();
        ReporteService reportes = ctx.getReportes();
        RutaService rutas = ctx.getRutas();
        CampaniaService campanias = ctx.getCampanias();

        // Personas: cuatro tipos concretos de la jerarquia polimorfica.
        Estudiante ana = personas.registrar(
                new Estudiante("E001", "Ana Gomez", "ana.gomez@eia.edu.co", "Ingenieria de Sistemas", 5));
        Estudiante luis = personas.registrar(
                new Estudiante("E002", "Luis Perez", "luis.perez@eia.edu.co", "Ingenieria Ambiental", 3));
        Colaborador marta = personas.registrar(
                new Colaborador("C001", "Marta Rios", "marta.rios@eia.edu.co", "Biblioteca"));
        Operador carlos = personas.registrar(new Operador("O001", "Carlos Diaz", "carlos.diaz@eia.edu.co", true));
        personas.registrar(new Operador("O002", "Pedro Ruiz", "pedro.ruiz@eia.edu.co", false));
        personas.registrar(new ResponsableSostenibilidad("R001", "Laura Mejia", "laura.mejia@eia.edu.co"));

        // Puntos ecologicos (P04 queda inactivo para demostrar la regla).
        PuntoEcologico p01 = new PuntoEcologico("P01", "Bloque A - primer piso", 120,
                EnumSet.of(TipoMaterial.RECICLABLE, TipoMaterial.ORGANICO));
        PuntoEcologico p02 = new PuntoEcologico("P02", "Cafeteria central", 200,
                EnumSet.of(TipoMaterial.ORGANICO, TipoMaterial.RECICLABLE, TipoMaterial.OTRO));
        PuntoEcologico p03 = new PuntoEcologico("P03", "Laboratorios", 80,
                EnumSet.of(TipoMaterial.ESPECIAL, TipoMaterial.RECICLABLE));
        PuntoEcologico p04 = new PuntoEcologico("P04", "Parqueadero", 100, EnumSet.of(TipoMaterial.OTRO));
        puntos.registrar(p01);
        puntos.registrar(p02);
        puntos.registrar(p03);
        puntos.registrar(p04);
        puntos.desactivar("P04");

        // Ruta con paradas ordenadas y operador.
        rutas.crearRuta("RUT-01", "Ruta de la manana");
        rutas.agregarParada("RUT-01", p01, "Vaciar caneca de reciclables");
        rutas.agregarParada("RUT-01", p02, "Separar organicos");
        rutas.asignarOperador("RUT-01", carlos);

        // Flujo completo de un reporte: registrado -> ... -> cerrado.
        reportes.crearReporte("REP-001", ana, p01, TipoReporte.DESBORDAMIENTO, "Caneca de reciclables llena");
        reportes.clasificar("REP-001", Prioridad.ALTA);
        reportes.asignarARuta("REP-001", rutas.buscar("RUT-01"));
        rutas.iniciarRuta("RUT-01");
        reportes.iniciarAtencion("REP-001");
        Recoleccion rec1 = new Recoleccion("REC-001", carlos, "Caneca vaciada y material separado");
        rec1.agregarDetalle(TipoMaterial.RECICLABLE, 12.5, UnidadMedida.KG);
        rec1.agregarDetalle(TipoMaterial.ORGANICO, 3000, UnidadMedida.GRAMO);
        reportes.cerrar("REP-001", rec1);
        rutas.cerrarRuta("RUT-01");

        // Reportes pendientes con distintas prioridades.
        reportes.crearReporte("REP-002", marta, p02, TipoReporte.CONTAMINACION, "Organicos mezclados con plastico");
        reportes.clasificar("REP-002", Prioridad.MEDIA);
        reportes.crearReporte("REP-003", luis, p03, TipoReporte.MATERIAL_ESPECIAL, "Baterias en caneca comun");
        reportes.clasificar("REP-003", Prioridad.CRITICA);
        reportes.asignarAOperador("REP-003", carlos);
        reportes.crearReporte("REP-004", ana, p02, TipoReporte.OTRO, "Tapa de la caneca rota");

        // Campania con actividades (composicion) y cupo que se completa.
        LocalDate hoy = LocalDate.now();
        campanias.crearCampania(new Campania("CAM-01", "Semana sin plastico", "Reducir plastico de un solo uso", hoy,
                hoy.plusDays(14), 3, 15));
        campanias.agregarActividad("CAM-01", "ACT-1", "Taller de compostaje", hoy.plusDays(2).atTime(10, 0), 30);
        campanias.agregarActividad("CAM-01", "ACT-2", "Jornada de limpieza", hoy.plusDays(7).atTime(8, 0), 50);
        campanias.activar("CAM-01");
        campanias.inscribir("CAM-01", ana);
        campanias.inscribir("CAM-01", luis);
        campanias.inscribir("CAM-01", marta);

        campanias.crearCampania(new Campania("CAM-02", "Reto reciclaje", hoy.plusDays(20), hoy.plusDays(40), 50));
    }

    /** Ejecuta acciones que deben fallar y devuelve lo ocurrido en cada una. */
    public static List<String> demostrarCasosInvalidos(ContextoAplicacion ctx) {
        List<String> resultados = new ArrayList<>();
        PersonaService personas = ctx.getPersonas();
        ReporteService reportes = ctx.getReportes();

        intentar(resultados, "Registrar persona con id repetido E001",
                () -> personas.registrar(new Estudiante("E001", "Otra Ana", "otra@eia.edu.co")));
        intentar(resultados, "Crear reporte sobre el punto inactivo P04",
                () -> reportes.crearReporte("REP-900", personas.buscar("E002"), ctx.getPuntos().buscar("P04"),
                        TipoReporte.OTRO, "Prueba"));
        intentar(resultados, "Asignar residuo especial (REP-003) a operador no habilitado O002",
                () -> reportes.asignarAOperador("REP-003", personas.buscarOperador("O002")));
        intentar(resultados, "Cerrar REP-002 sin recoleccion ni atencion",
                () -> reportes.cerrar("REP-002", null));
        intentar(resultados, "Pasar REP-001 de CERRADO a ASIGNADO sin reapertura",
                () -> reportes.asignarAOperador("REP-001", personas.buscarOperador("O001")));
        intentar(resultados, "Inscribir dos veces a Ana en CAM-01",
                () -> ctx.getCampanias().inscribir("CAM-01", personas.buscar("E001")));
        intentar(resultados, "Inscribir a la responsable R001 en CAM-02",
                () -> ctx.getCampanias().inscribir("CAM-02", personas.buscar("R001")));
        intentar(resultados, "Retirar el punto P02 con reportes abiertos",
                () -> ctx.getPuntos().retirar("P02"));
        intentar(resultados, "Retirar la campania CAM-01 con participantes",
                () -> ctx.getCampanias().retirarCampania("CAM-01"));
        intentar(resultados, "Canjear 10 000 puntos de Luis (saldo insuficiente)",
                () -> ctx.getEcoPuntos().canjear(personas.buscar("E002"), 10_000, "Termo"));
        intentar(resultados, "Buscar la persona inexistente X999",
                () -> personas.buscar("X999"));
        return resultados;
    }

    private static void intentar(List<String> resultados, String descripcion, AccionConsola accion) {
        try {
            accion.ejecutar();
            resultados.add("[NO RECHAZADO] " + descripcion);
        } catch (EcoCampusException | IllegalArgumentException e) {
            resultados.add("[RECHAZADO] " + descripcion + "\n      -> " + e.getClass().getSimpleName() + ": "
                    + e.getMessage());
        }
    }
}
