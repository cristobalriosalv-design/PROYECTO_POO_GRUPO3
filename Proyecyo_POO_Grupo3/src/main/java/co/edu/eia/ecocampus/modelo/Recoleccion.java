package co.edu.eia.ecocampus.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.util.Validador;

/** Evidencia de recoleccion: materiales, cantidades y operador responsable. */
public class Recoleccion {

    private final String id;
    private final LocalDateTime fecha;
    private final String observaciones;
    private final Operador operador;
    private final List<DetalleRecoleccion> detalles;

    public Recoleccion(String id, Operador operador, String observaciones) {
        this.id = Validador.validarTexto(id, "id");
        this.operador = Objects.requireNonNull(operador, "La recoleccion necesita un operador responsable");
        this.observaciones = observaciones == null ? "" : observaciones.trim();
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    /** Regla: los residuos especiales requieren un operador habilitado. */
    public void agregarDetalle(TipoMaterial material, double cantidad, UnidadMedida unidad)
            throws OperacionNoPermitidaException {
        if (material == TipoMaterial.ESPECIAL && !operador.isHabilitadoResiduosEspeciales()) {
            throw new OperacionNoPermitidaException(
                    "El operador " + operador.getNombre() + " no esta habilitado para recoger residuos especiales");
        }
        detalles.add(new DetalleRecoleccion(material, cantidad, unidad));
    }

    public void agregarDetalle(TipoMaterial material, double cantidadKg) throws OperacionNoPermitidaException {
        agregarDetalle(material, cantidadKg, UnidadMedida.KG);
    }

    public double getPesoTotalKg() {
        double total = 0;
        for (DetalleRecoleccion detalle : detalles) {
            total += detalle.getPesoEnKg();
        }
        return total;
    }

    public boolean esValidaParaCierre() {
        return !detalles.isEmpty() && getPesoTotalKg() > 0;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Operador getOperador() {
        return operador;
    }

    public List<DetalleRecoleccion> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    @Override
    public String toString() {
        return String.format("Recoleccion %s por %s: %.2f kg %s", id, operador.getNombre(), getPesoTotalKg(),
                detalles);
    }
}
