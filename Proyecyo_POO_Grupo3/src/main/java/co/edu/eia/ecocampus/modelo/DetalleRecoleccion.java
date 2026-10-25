package co.edu.eia.ecocampus.modelo;

import java.util.Objects;

import co.edu.eia.ecocampus.util.Validador;

public class DetalleRecoleccion {

    private final TipoMaterial material;
    private final double cantidad;
    private final UnidadMedida unidad;

    public DetalleRecoleccion(TipoMaterial material, double cantidad, UnidadMedida unidad) {
        this.material = Objects.requireNonNull(material, "material");
        this.cantidad = Validador.validarPositivo(cantidad, "cantidad");
        this.unidad = Objects.requireNonNull(unidad, "unidad");
    }

    public TipoMaterial getMaterial() {
        return material;
    }

    public double getCantidad() {
        return cantidad;
    }

    public UnidadMedida getUnidad() {
        return unidad;
    }

    public double getPesoEnKg() {
        return unidad.aKg(cantidad);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f %s", material, cantidad, unidad);
    }
}
