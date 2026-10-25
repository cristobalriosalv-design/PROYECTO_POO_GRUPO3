package co.edu.eia.ecocampus.servicio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.MovimientoEcoPuntos;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.patron.ConfiguracionSistema;
import co.edu.eia.ecocampus.patron.EstrategiaCalculoPuntos;

/**
 * Otorga y consulta eco-puntos. Delega el calculo en la estrategia activa de
 * ConfiguracionSistema, por lo que cambiar la politica no toca esta clase.
 */
public class EcoPuntosService {

    private final ConfiguracionSistema configuracion;
    /** EnumMap: acumulado de kg por material (Map para acumulacion). */
    private final Map<TipoMaterial, Double> materialesAcumulados;
    private int consecutivoMovimientos;

    public EcoPuntosService(ConfiguracionSistema configuracion) {
        this.configuracion = configuracion;
        this.materialesAcumulados = new EnumMap<>(TipoMaterial.class);
    }

    public int otorgarPorRecoleccion(Persona persona, Recoleccion recoleccion) throws OperacionNoPermitidaException {
        EstrategiaCalculoPuntos estrategia = configuracion.getEstrategiaPuntos();
        int puntos = estrategia.calcular(persona, recoleccion.getDetalles());
        acumularMateriales(recoleccion.getDetalles());
        if (puntos > 0) {
            persona.agregarMovimiento(new MovimientoEcoPuntos(siguienteId(),
                    "Recoleccion " + recoleccion.getId() + " - " + estrategia.getNombre(), puntos));
        }
        return puntos;
    }

    public int otorgarPorCampania(Persona persona, Campania campania) throws OperacionNoPermitidaException {
        if (!campania.esParticipante(persona)) {
            throw new OperacionNoPermitidaException(
                    persona.getNombre() + " no participa en la campania " + campania.getId());
        }
        int puntos = (int) Math.round(campania.getPuntosPorParticipacion() * persona.getFactorBonificacion());
        persona.agregarMovimiento(new MovimientoEcoPuntos(siguienteId(),
                "Participacion en campania " + campania.getNombre(), puntos));
        return puntos;
    }

    /** Movimiento negativo; Persona impide que el saldo quede por debajo de cero. */
    public void canjear(Persona persona, int puntos, String concepto) throws OperacionNoPermitidaException {
        if (puntos <= 0) {
            throw new OperacionNoPermitidaException("La cantidad a canjear debe ser mayor que cero");
        }
        persona.agregarMovimiento(new MovimientoEcoPuntos(siguienteId(), "Canje: " + concepto, -puntos));
    }

    /** PECS - Producer Extends: solo se leen los movimientos. */
    public int sumarPuntos(Collection<? extends MovimientoEcoPuntos> movimientos) {
        int total = 0;
        for (MovimientoEcoPuntos movimiento : movimientos) {
            total += movimiento.getPuntos();
        }
        return total;
    }

    /** PECS - Producer Extends (origen) / Consumer Super (destino). */
    public void copiarDetalles(List<? extends DetalleRecoleccion> origen, List<? super DetalleRecoleccion> destino) {
        for (DetalleRecoleccion detalle : origen) {
            destino.add(detalle);
        }
    }

    public void acumularMateriales(List<? extends DetalleRecoleccion> detalles) {
        for (DetalleRecoleccion detalle : detalles) {
            materialesAcumulados.merge(detalle.getMaterial(), detalle.getPesoEnKg(), Double::sum);
        }
    }

    public Map<TipoMaterial, Double> materialesRecolectados() {
        return Collections.unmodifiableMap(materialesAcumulados);
    }

    /** Ranking de mayor a menor saldo; acepta cualquier coleccion de subtipos de Persona. */
    public List<Persona> ranking(Collection<? extends Persona> personas) {
        List<Persona> ordenadas = new ArrayList<>(personas);
        ordenadas.sort(Comparator.comparingInt(Persona::getSaldoPuntos).reversed()
                .thenComparing(Persona::getNombre));
        return ordenadas;
    }

    public EstrategiaCalculoPuntos getEstrategiaActual() {
        return configuracion.getEstrategiaPuntos();
    }

    public void cambiarEstrategia(EstrategiaCalculoPuntos estrategia) {
        configuracion.setEstrategiaPuntos(estrategia);
    }

    private String siguienteId() {
        consecutivoMovimientos++;
        return String.format("MOV-%04d", consecutivoMovimientos);
    }
}
