package co.edu.eia.ecocampus.app.gui;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.modelo.TipoPersona;
import co.edu.eia.ecocampus.servicio.PersonaService;
import co.edu.eia.ecocampus.servicio.PuntoEcologicoService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** Controlador de PersonasPuntos.fxml: opciones 1 a 7 del submenu de consola. */
public class PersonasPuntosController {

    // ------------------------------------------------ pestana Personas
    @FXML private ComboBox<TipoPersona> cbTipoPersona;
    @FXML private TextField txtIdPersona;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private CheckBox chkHabilitado;
    @FXML private TableView<Persona> tablaPersonas;
    @FXML private TableColumn<Persona, String> colIdPersona;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colTipo;
    @FXML private TableColumn<Persona, String> colCorreo;
    @FXML private TableColumn<Persona, Integer> colPuntos;

    // ------------------------------------------------ pestana Puntos ecologicos
    @FXML private TextField txtCodigoPunto;
    @FXML private TextField txtUbicacion;
    @FXML private TextField txtCapacidad;
    @FXML private CheckBox chkOrganico;
    @FXML private CheckBox chkReciclable;
    @FXML private CheckBox chkEspecial;
    @FXML private CheckBox chkOtro;
    @FXML private TableView<PuntoEcologico> tablaPuntos;
    @FXML private TableColumn<PuntoEcologico, String> colCodigo;
    @FXML private TableColumn<PuntoEcologico, String> colUbicacion;
    @FXML private TableColumn<PuntoEcologico, Double> colCapacidad;
    @FXML private TableColumn<PuntoEcologico, String> colEstado;
    @FXML private TableColumn<PuntoEcologico, String> colCategorias;

    // ------------------------------------------------ servicios (los mismos de la consola)
    private final PersonaService personas = ContextoGui.get().getPersonas();
    private final PuntoEcologicoService puntos = ContextoGui.get().getPuntos();

    /** JavaFX lo llama solo, despues de conectar todos los @FXML. */
    @FXML
    private void initialize() {
        cbTipoPersona.getItems().setAll(TipoPersona.values());
        cbTipoPersona.getSelectionModel().selectFirst();
        // La casilla solo tiene sentido para operadores.
        chkHabilitado.disableProperty().bind(cbTipoPersona.valueProperty().isNotEqualTo(TipoPersona.OPERADOR));

        colIdPersona.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(describirTipo(c.getValue())));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colPuntos.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSaldoPuntos()).asObject());

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colUbicacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUbicacion()));
        colCapacidad.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCapacidadKg()).asObject());
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
        colCategorias.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getCategoriasAceptadas().toString()));

        refrescarPersonas();
        refrescarPuntos();
    }

    // ================================================ 1. Registrar persona
    @FXML
    private void registrarPersona() {
        try {
            TipoPersona tipo = cbTipoPersona.getValue();
            String id = txtIdPersona.getText();
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            Persona persona;
            if (tipo == TipoPersona.OPERADOR) {
                persona = personas.registrar(new Operador(id, nombre, correo, chkHabilitado.isSelected()));
            } else {
                persona = personas.registrar(tipo, id, nombre, correo);
            }
            refrescarPersonas();
            txtIdPersona.clear();
            txtNombre.clear();
            txtCorreo.clear();
            chkHabilitado.setSelected(false);
            Alertas.info("Persona registrada", persona.toString());
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo registrar la persona", e.getMessage());
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        }
    }

    // ================================================ 5. Cambiar disponibilidad
    @FXML
    private void cambiarDisponibilidad() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (!(seleccionada instanceof Operador operador)) {
            Alertas.error("Seleccione un operador", "Elija en la tabla una persona de tipo OPERADOR.");
            return;
        }
        operador.cambiarDisponibilidad(!operador.isDisponible());
        tablaPersonas.refresh();
        Alertas.info("Disponibilidad actualizada", operador.getNombre()
                + (operador.isDisponible() ? " ahora esta disponible." : " ya no esta disponible."));
    }

    // ================================================ 2. Registrar punto
    @FXML
    private void registrarPunto() {
        try {
            double capacidad = Double.parseDouble(txtCapacidad.getText().trim().replace(',', '.'));
            PuntoEcologico punto = new PuntoEcologico(txtCodigoPunto.getText(), txtUbicacion.getText(), capacidad);
            if (chkOrganico.isSelected()) {
                punto.agregarCategoria(TipoMaterial.ORGANICO);
            }
            if (chkReciclable.isSelected()) {
                punto.agregarCategoria(TipoMaterial.RECICLABLE);
            }
            if (chkEspecial.isSelected()) {
                punto.agregarCategoria(TipoMaterial.ESPECIAL);
            }
            if (chkOtro.isSelected()) {
                punto.agregarCategoria(TipoMaterial.OTRO);
            }
            puntos.registrar(punto);
            refrescarPuntos();
            txtCodigoPunto.clear();
            txtUbicacion.clear();
            txtCapacidad.clear();
            Alertas.info("Punto registrado", punto.toString());
        } catch (NumberFormatException e) {
            Alertas.error("Dato invalido", "La capacidad debe ser un numero, por ejemplo 120 o 85.5");
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo registrar el punto", e.getMessage());
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        }
    }

    // ================================================ 3. Activar / desactivar punto
    @FXML
    private void activarPunto() {
        PuntoEcologico punto = puntoSeleccionado();
        if (punto == null) {
            return;
        }
        try {
            puntos.activar(punto.getId());
            tablaPuntos.refresh();
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo activar", e.getMessage());
        }
    }

    @FXML
    private void desactivarPunto() {
        PuntoEcologico punto = puntoSeleccionado();
        if (punto == null) {
            return;
        }
        try {
            puntos.desactivar(punto.getId());
            tablaPuntos.refresh();
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo desactivar", e.getMessage());
        }
    }

    // ================================================ 4. Retirar punto
    @FXML
    private void retirarPunto() {
        PuntoEcologico punto = puntoSeleccionado();
        if (punto == null) {
            return;
        }
        if (!Alertas.confirmar("Retirar punto ecologico",
                "Se retirara el punto " + punto.getId() + ". Desea continuar?")) {
            return;
        }
        try {
            puntos.retirar(punto.getId());
            refrescarPuntos();
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo retirar el punto", e.getMessage());
        }
    }

    // ================================================ 0. Volver
    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }

    // ================================================ 6 y 7. Listar (tablas) y utilidades
    private void refrescarPersonas() {
        tablaPersonas.getItems().setAll(personas.listar());
    }

    private void refrescarPuntos() {
        tablaPuntos.getItems().setAll(puntos.listar());
    }

    private PuntoEcologico puntoSeleccionado() {
        PuntoEcologico punto = tablaPuntos.getSelectionModel().getSelectedItem();
        if (punto == null) {
            Alertas.error("Seleccione un punto", "Primero haga clic en un punto de la tabla.");
        }
        return punto;
    }

    private String describirTipo(Persona persona) {
        if (persona instanceof Operador operador) {
            return "OPERADOR (" + (operador.isDisponible() ? "disponible" : "no disponible") + ")";
        }
        return persona.getTipo().toString();
    }
}