package co.edu.eia.ecocampus.patron;

import java.util.Objects;

/**
 * Singleton: configuracion central de la aplicacion. Garantiza que todos los
 * servicios usen la misma estrategia de puntos y el mismo publicador de eventos.
 */
public final class ConfiguracionSistema {

    private static ConfiguracionSistema instancia;

    private EstrategiaCalculoPuntos estrategiaPuntos;
    private final PublicadorEventos publicador;

    private ConfiguracionSistema() {
        estrategiaPuntos = new EstrategiaPorMaterial();
        publicador = new PublicadorEventos();
    }

    public static synchronized ConfiguracionSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionSistema();
        }
        return instancia;
    }

    public EstrategiaCalculoPuntos getEstrategiaPuntos() {
        return estrategiaPuntos;
    }

    public void setEstrategiaPuntos(EstrategiaCalculoPuntos estrategia) {
        this.estrategiaPuntos = Objects.requireNonNull(estrategia, "estrategia");
    }

    public PublicadorEventos getPublicador() {
        return publicador;
    }
}
