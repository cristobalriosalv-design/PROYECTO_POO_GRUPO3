package co.edu.eia.ecocampus.modelo;

public enum TipoReporte {
    DESBORDAMIENTO,
    CONTAMINACION,
    MATERIAL_ESPECIAL,
    OTRO;

    /** Los residuos especiales solo los atiende un operador habilitado. */
    public boolean requiereOperadorEspecial() {
        return this == MATERIAL_ESPECIAL;
    }
}
