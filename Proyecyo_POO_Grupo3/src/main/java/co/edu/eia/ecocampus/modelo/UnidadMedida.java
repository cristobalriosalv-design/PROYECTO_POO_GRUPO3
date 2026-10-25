package co.edu.eia.ecocampus.modelo;

/** Unidad en que se registra una cantidad recolectada y su factor a kilogramos. */
public enum UnidadMedida {
    KG(1.0),
    GRAMO(0.001),
    LIBRA(0.453592);

    private final double factorAKg;

    UnidadMedida(double factorAKg) {
        this.factorAKg = factorAKg;
    }

    public double aKg(double cantidad) {
        return cantidad * factorAKg;
    }

    public double getFactorAKg() {
        return factorAKg;
    }
}
