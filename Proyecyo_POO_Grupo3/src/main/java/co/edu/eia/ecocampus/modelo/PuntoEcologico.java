package co.edu.eia.ecocampus.modelo;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import co.edu.eia.ecocampus.util.Validador;

public class PuntoEcologico implements Identificable {

    private final String codigo;
    private String ubicacion;
    private double capacidadKg;
    private EstadoPunto estado;
    private final Set<TipoMaterial> categoriasAceptadas;

    public PuntoEcologico(String codigo, String ubicacion, double capacidadKg) {
        this.codigo = Validador.validarTexto(codigo, "codigo");
        this.ubicacion = Validador.validarTexto(ubicacion, "ubicacion");
        this.capacidadKg = Validador.validarPositivo(capacidadKg, "capacidadKg");
        this.estado = EstadoPunto.ACTIVO;
        this.categoriasAceptadas = EnumSet.noneOf(TipoMaterial.class);
    }

    public PuntoEcologico(String codigo, String ubicacion, double capacidadKg, Set<TipoMaterial> categorias) {
        this(codigo, ubicacion, capacidadKg);
        categoriasAceptadas.addAll(categorias);
    }

    @Override
    public String getId() {
        return codigo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public double getCapacidadKg() {
        return capacidadKg;
    }

    public EstadoPunto getEstado() {
        return estado;
    }

    public boolean estaActivo() {
        return estado == EstadoPunto.ACTIVO;
    }

    public void activar() {
        estado = EstadoPunto.ACTIVO;
    }

    public void desactivar() {
        estado = EstadoPunto.INACTIVO;
    }

    public void ponerEnMantenimiento() {
        estado = EstadoPunto.EN_MANTENIMIENTO;
    }

    public boolean admite(TipoMaterial material) {
        return categoriasAceptadas.contains(material);
    }

    public void agregarCategoria(TipoMaterial material) {
        categoriasAceptadas.add(material);
    }

    public Set<TipoMaterial> getCategoriasAceptadas() {
        return Collections.unmodifiableSet(categoriasAceptadas);
    }

    @Override
    public String toString() {
        return String.format("Punto %s - %s (%.0f kg) %s %s", codigo, ubicacion, capacidadKg, estado,
                categoriasAceptadas);
    }
}
