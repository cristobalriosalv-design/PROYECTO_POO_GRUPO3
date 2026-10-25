package co.edu.eia.ecocampus.app;

import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.patron.ConfiguracionSistema;
import co.edu.eia.ecocampus.patron.PersonaFactory;
import co.edu.eia.ecocampus.patron.ReporteFactory;
import co.edu.eia.ecocampus.repositorio.RepositorioMemoria;
import co.edu.eia.ecocampus.servicio.CampaniaService;
import co.edu.eia.ecocampus.servicio.EcoPuntosService;
import co.edu.eia.ecocampus.servicio.PersonaService;
import co.edu.eia.ecocampus.servicio.PuntoEcologicoService;
import co.edu.eia.ecocampus.servicio.ReporteService;
import co.edu.eia.ecocampus.servicio.ResumenService;
import co.edu.eia.ecocampus.servicio.RutaService;

/**
 * Composicion de la aplicacion: crea repositorios y servicios una sola vez y
 * los conecta. La usan el menu de consola, la interfaz grafica y las pruebas.
 */
public class ContextoAplicacion {

    private final ConfiguracionSistema configuracion;
    private final PersonaService personas;
    private final PuntoEcologicoService puntos;
    private final ReporteService reportes;
    private final RutaService rutas;
    private final CampaniaService campanias;
    private final EcoPuntosService ecoPuntos;
    private final ResumenService resumen;

    public ContextoAplicacion(ConfiguracionSistema configuracion) {
        this.configuracion = configuracion;
        RepositorioMemoria<Persona> repoPersonas = new RepositorioMemoria<>("una persona");
        RepositorioMemoria<PuntoEcologico> repoPuntos = new RepositorioMemoria<>("un punto ecologico");
        RepositorioMemoria<ReporteResiduo> repoReportes = new RepositorioMemoria<>("un reporte");
        RepositorioMemoria<RutaRecoleccion> repoRutas = new RepositorioMemoria<>("una ruta");
        RepositorioMemoria<Campania> repoCampanias = new RepositorioMemoria<>("una campania");

        ecoPuntos = new EcoPuntosService(configuracion);
        personas = new PersonaService(repoPersonas, new PersonaFactory());
        puntos = new PuntoEcologicoService(repoPuntos, repoReportes);
        reportes = new ReporteService(repoReportes, new ReporteFactory(), configuracion.getPublicador(), ecoPuntos);
        rutas = new RutaService(repoRutas, configuracion.getPublicador());
        campanias = new CampaniaService(repoCampanias, configuracion.getPublicador(), ecoPuntos);
        resumen = new ResumenService(personas, puntos, reportes, rutas, campanias, ecoPuntos);
    }

    public static ContextoAplicacion crear() {
        return new ContextoAplicacion(ConfiguracionSistema.getInstancia());
    }

    public ConfiguracionSistema getConfiguracion() {
        return configuracion;
    }

    public PersonaService getPersonas() {
        return personas;
    }

    public PuntoEcologicoService getPuntos() {
        return puntos;
    }

    public ReporteService getReportes() {
        return reportes;
    }

    public RutaService getRutas() {
        return rutas;
    }

    public CampaniaService getCampanias() {
        return campanias;
    }

    public EcoPuntosService getEcoPuntos() {
        return ecoPuntos;
    }

    public ResumenService getResumen() {
        return resumen;
    }
}
