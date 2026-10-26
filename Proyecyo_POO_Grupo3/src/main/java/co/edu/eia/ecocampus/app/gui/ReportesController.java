package co.edu.eia.ecocampus.app.gui;

import java.util.function.Function;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.excepcion.OperacionNoPermitidaException;
import co.edu.eia.ecocampus.excepcion.TransicionInvalidaException;
import co.edu.eia.ecocampus.modelo.DetalleRecoleccion;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.Prioridad;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.modelo.TipoReporte;
import co.edu.eia.ecocampus.modelo.UnidadMedida;
import co.edu.eia.ecocampus.servicio.PersonaService;
import co.edu.eia.ecocampus.servicio.PuntoEcologicoService;
import co.edu.eia.ecocampus.servicio.ReporteService;
import co.edu.eia.ecocampus.servicio.RutaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/** Controlador de Reportes.fxml: opciones 1 a 9 del submenu de reportes. */
public class ReportesController {

    /** Una accion sobre el reporte seleccionado que puede violar una regla de negocio. */
    @FunctionalInterface
    private interface AccionSobreReporte {
        void ejecutar(ReporteResiduo reporte) throws EcoCampusException;
    }

    // ------------------------------------------------ tabla e historial (centro)
    @FXML private TableView<ReporteResiduo> tablaReportes;
    @FXML private TableColumn<ReporteResiduo, String> colCodigo;
    @FXML private TableColumn<ReporteResiduo, String> colTipo;
    @FXML private TableColumn<ReporteResiduo, String> colPrioridad;
    @FXML private TableColumn<ReporteResiduo, String> colEstado;
    @FXML private TableColumn<ReporteResiduo, String> colPunto;
    @FXML private TableColumn<ReporteResiduo, String> colAutor;
    @FXML private TableColumn<ReporteResiduo, String> colOperador;
    @FXML private ListView<ReporteResiduo.RegistroCambio> listaHistorial;

    // ------------------------------------------------ pestana Crear
    @FXML private TextField txtCodigo;
    @FXML private ComboBox<Persona> cbAutor;
    @FXML private ComboBox<PuntoEcologico> cbPunto;
    @FXML private ComboBox<TipoReporte> cbTipo;
    @FXML private TextField txtDescripcion;

    // ------------------------------------------------ pestana Gestionar
    @FXML private Label lblSeleccionado;
    @FXML private ComboBox<Prioridad> cbPrioridad;
    @FXML private ComboBox<Operador> cbOperador;
    @FXML private ComboBox<RutaRecoleccion> cbRuta;
    @FXML private TextField txtMotivo;

    // ------------------------------------------------ pestana Cerrar
    @FXML private TextField txtIdRecoleccion;
    @FXML private TextField txtObservaciones;
    @FXML private ComboBox<TipoMaterial> cbMaterial;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<UnidadMedida> cbUnidad;
    @FXML private ListView<DetalleRecoleccion> listaDetalles;

    // ------------------------------------------------ servicios compartidos
    private final ReporteService reportes = ContextoGui.get().getReportes();
    private final PersonaService personas = ContextoGui.get().getPersonas();
    private final PuntoEcologicoService puntos = ContextoGui.get().getPuntos();
    private final RutaService rutas = ContextoGui.get().getRutas();

    @FXML
    private void initialize() {
        // Combos con enums: sus valores se muestran tal cual.
        cbTipo.getItems().setAll(TipoReporte.values());
        cbPrioridad.getItems().setAll(Prioridad.values());
        cbMaterial.getItems().setAll(TipoMaterial.values());
        cbUnidad.getItems().setAll(UnidadMedida.values());
        cbUnidad.setValue(UnidadMedida.KG);

        // Combos con objetos: decidimos que texto mostrar de cada uno.
        mostrarCon(cbAutor, p -> p.getId() + " - " + p.getNombre());
        mostrarCon(cbPunto, p -> p.getId() + " - " + p.getUbicacion() + " (" + p.getEstado() + ")");
        mostrarCon(cbOperador, o -> o.getId() + " - " + o.getNombre());
        mostrarCon(cbRuta, r -> r.getId() + " - " + r.getNombre() + " (" + r.getEstado() + ")");

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colPrioridad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrioridad().toString()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
        colPunto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPunto().getId()));
        colAutor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAutor().getNombre()));
        colOperador.setCellValueFactory(c -> {
            Operador operador = c.getValue().getOperadorAsignado();
            return new SimpleStringProperty(operador == null ? "-" : operador.getNombre());
        });

        // 8. Historial: se actualiza solo cada vez que cambia la fila seleccionada.
        tablaReportes.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, nuevo) -> mostrarSeleccion(nuevo));

        refrescar();
    }

    // ================================================ 1. Crear reporte
    @FXML
    private void crearReporte() {
        if (cbAutor.getValue() == null || cbPunto.getValue() == null || cbTipo.getValue() == null) {
            Alertas.error("Faltan datos", "Elija el autor, el punto y el tipo de reporte.");
            return;
        }
        try {
            ReporteResiduo nuevo = reportes.crearReporte(txtCodigo.getText(), cbAutor.getValue(), cbPunto.getValue(),
                    cbTipo.getValue(), txtDescripcion.getText());
            txtCodigo.clear();
            txtDescripcion.clear();
            refrescar();
            tablaReportes.getSelectionModel().select(nuevo);
            Alertas.info("Reporte creado", nuevo.toString());
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo crear el reporte", e.getMessage());
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        }
    }

    // ================================================ 2 a 5 y 7. Gestionar el seleccionado
    @FXML
    private void clasificarReporte() {
        Prioridad prioridad = cbPrioridad.getValue();
        if (prioridad == null) {
            Alertas.error("Falta la prioridad", "Elija una prioridad.");
            return;
        }
        ejecutarSobreSeleccionado(r -> reportes.clasificar(r.getId(), prioridad));
    }

    @FXML
    private void asignarOperador() {
        Operador operador = cbOperador.getValue();
        if (operador == null) {
            Alertas.error("Falta el operador", "Elija un operador disponible.");
            return;
        }
        ejecutarSobreSeleccionado(r -> reportes.asignarAOperador(r.getId(), operador));
    }

    @FXML
    private void asignarRuta() {
        RutaRecoleccion ruta = cbRuta.getValue();
        if (ruta == null) {
            Alertas.error("Falta la ruta", "Elija una ruta.");
            return;
        }
        ejecutarSobreSeleccionado(r -> reportes.asignarARuta(r.getId(), ruta));
    }

    @FXML
    private void iniciarAtencion() {
        ejecutarSobreSeleccionado(r -> reportes.iniciarAtencion(r.getId()));
    }

    @FXML
    private void reabrirReporte() {
        ejecutarSobreSeleccionado(r -> {
            reportes.reabrir(r.getId(), txtMotivo.getText());
            txtMotivo.clear();
        });
    }

    // ================================================ 6. Registrar recoleccion y cerrar
    @FXML
    private void agregarMaterial() {
        if (cbMaterial.getValue() == null || cbUnidad.getValue() == null) {
            Alertas.error("Faltan datos", "Elija el material y la unidad.");
            return;
        }
        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim().replace(',', '.'));
            listaDetalles.getItems().add(new DetalleRecoleccion(cbMaterial.getValue(), cantidad, cbUnidad.getValue()));
            txtCantidad.clear();
        } catch (NumberFormatException e) {
            Alertas.error("Dato invalido", "La cantidad debe ser un numero, por ejemplo 12.5");
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        }
    }

    @FXML
    private void cerrarReporte() {
        ejecutarSobreSeleccionado(r -> {
            Operador operador = r.getOperadorAsignado();
            if (operador == null) {
                throw new OperacionNoPermitidaException("El reporte " + r.getId() + " no tiene operador asignado");
            }
            Recoleccion recoleccion = new Recoleccion(txtIdRecoleccion.getText(), operador,
                    txtObservaciones.getText());
            for (DetalleRecoleccion detalle : listaDetalles.getItems()) {
                recoleccion.agregarDetalle(detalle.getMaterial(), detalle.getCantidad(), detalle.getUnidad());
            }
            int puntosGanados = reportes.cerrar(r.getId(), recoleccion);
            listaDetalles.getItems().clear();
            txtIdRecoleccion.clear();
            txtObservaciones.clear();
            Alertas.info("Reporte cerrado", r.getAutor().getNombre() + " recibio " + puntosGanados + " eco-puntos.");
        });
    }

    // ================================================ 0. Volver
    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }

    // ================================================ utilidades
    /**
     * Patron comun de las acciones 2-7: exige un reporte seleccionado, ejecuta la
     * accion y muestra cualquier regla violada. El finally refresca siempre.
     */
    private void ejecutarSobreSeleccionado(AccionSobreReporte accion) {
        ReporteResiduo seleccionado = tablaReportes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.error("Seleccione un reporte", "Primero haga clic en un reporte de la tabla.");
            return;
        }
        try {
            accion.ejecutar(seleccionado);
        } catch (TransicionInvalidaException e) {
            Alertas.error("Cambio de estado no permitido", e.getMessage());
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo completar la operacion", e.getMessage());
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        } finally {
            refrescar();
        }
    }

    /** Vuelve a leer todo de los servicios conservando la fila seleccionada. */
    private void refrescar() {
        ReporteResiduo seleccionado = tablaReportes.getSelectionModel().getSelectedItem();
        tablaReportes.getItems().setAll(reportes.listar());
        if (seleccionado != null) {
            tablaReportes.getSelectionModel().select(seleccionado);
        }
        cbAutor.getItems().setAll(personas.listar());
        cbPunto.getItems().setAll(puntos.listar());
        cbOperador.getItems().setAll(personas.operadoresDisponibles());
        cbRuta.getItems().setAll(rutas.listar());
        mostrarSeleccion(seleccionado);
    }

    private void mostrarSeleccion(ReporteResiduo reporte) {
        if (reporte == null) {
            lblSeleccionado.setText("ninguno");
            listaHistorial.getItems().clear();
        } else {
            lblSeleccionado.setText(reporte.getId() + " (" + reporte.getEstado() + ")");
            listaHistorial.getItems().setAll(reporte.getHistorial());
        }
    }

    /** Metodo generico: sirve para un ComboBox de cualquier tipo T. */
    private <T> void mostrarCon(ComboBox<T> combo, Function<T, String> texto) {
        combo.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T objeto) {
                return objeto == null ? "" : texto.apply(objeto);
            }

            @Override
            public T fromString(String cadena) {
                return null; // los combos no son editables
            }
        });
    }
}