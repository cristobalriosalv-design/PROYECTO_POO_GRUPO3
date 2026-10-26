package co.edu.eia.ecocampus.app.gui;

import java.util.List;

import co.edu.eia.ecocampus.app.AccionConsola;
import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Parada;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.servicio.PersonaService;
import co.edu.eia.ecocampus.servicio.PuntoEcologicoService;
import co.edu.eia.ecocampus.servicio.RutaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Controlador de RutasDeRecoleccion.fxml: opciones 1 a 7 del submenu de rutas. */
public class RutasController {

    @FXML private TextField txtCodigoRuta;
    @FXML private TextField txtNombreRuta;
    @FXML private TextField txtRutaParada;
    @FXML private TextField txtPuntoParada;
    @FXML private TextField txtAccionParada;
    @FXML private TextField txtRutaQuitar;
    @FXML private TextField txtOrdenQuitar;
    @FXML private TextField txtRutaOperador;
    @FXML private TextField txtIdOperador;
    @FXML private TextField txtRutaIniciar;
    @FXML private TextField txtRutaCerrar;
    @FXML private TextArea txtListaRutas;

    private final RutaService rutas = ContextoGui.get().getRutas();
    private final PuntoEcologicoService puntos = ContextoGui.get().getPuntos();
    private final PersonaService personas = ContextoGui.get().getPersonas();

    @FXML
    private void initialize() {
        listarRutas();
    }

    // 1. Crear ruta
    @FXML
    private void crearRuta() {
        ejecutar(() -> {
            RutaRecoleccion ruta = rutas.crearRuta(txtCodigoRuta.getText(), txtNombreRuta.getText());
            txtCodigoRuta.clear();
            txtNombreRuta.clear();
            Alertas.info("Ruta creada", ruta.toString());
        });
    }

    // 2. Agregar parada
    @FXML
    private void agregarParada() {
        ejecutar(() -> {
            String codigo = txtRutaParada.getText();
            rutas.agregarParada(codigo, puntos.buscar(txtPuntoParada.getText()), txtAccionParada.getText());
            txtPuntoParada.clear();
            txtAccionParada.clear();
            Alertas.info("Parada agregada", describirParadas(rutas.buscar(codigo)));
        });
    }

    // 3. Quitar parada
    @FXML
    private void quitarParada() {
        ejecutar(() -> {
            RutaRecoleccion ruta = rutas.buscar(txtRutaQuitar.getText());
            ruta.quitarParada(Integer.parseInt(txtOrdenQuitar.getText().trim()));
            txtOrdenQuitar.clear();
            Alertas.info("Parada quitada", describirParadas(ruta));
        });
    }

    // 4. Asignar operador
    @FXML
    private void asignarOperador() {
        ejecutar(() -> {
            String codigo = txtRutaOperador.getText();
            rutas.asignarOperador(codigo, personas.buscarOperador(txtIdOperador.getText()));
            Alertas.info("Operador asignado", rutas.buscar(codigo).toString());
        });
    }

    // 5. Iniciar ruta
    @FXML
    private void iniciarRuta() {
        ejecutar(() -> {
            rutas.iniciarRuta(txtRutaIniciar.getText());
            Alertas.info("Ruta iniciada", rutas.buscar(txtRutaIniciar.getText()).toString());
        });
    }

    // 6. Cerrar ruta (publica el evento RUTA_CERRADA: Observer)
    @FXML
    private void cerrarRuta() {
        ejecutar(() -> {
            rutas.cerrarRuta(txtRutaCerrar.getText());
            Alertas.info("Ruta cerrada", rutas.buscar(txtRutaCerrar.getText()).toString());
        });
    }

    // 0. Volver
    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }

    // 7. Listar rutas con paradas (se actualiza despues de cada accion)
    private void listarRutas() {
        List<RutaRecoleccion> lista = rutas.listar();
        if (lista.isEmpty()) {
            txtListaRutas.setText("No hay rutas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (RutaRecoleccion ruta : lista) {
            sb.append(describirParadas(ruta)).append("\n\n");
        }
        txtListaRutas.setText(sb.toString());
    }

    private String describirParadas(RutaRecoleccion ruta) {
        StringBuilder sb = new StringBuilder(ruta.toString());
        if (ruta.getParadas().isEmpty()) {
            sb.append("\n   (sin paradas)");
        }
        for (Parada parada : ruta.getParadas()) {
            sb.append("\n   ").append(parada);
        }
        return sb.toString();
    }

    /** Reutiliza AccionConsola del menu de consola: misma idea, otra vista. */
    private void ejecutar(AccionConsola accion) {
        try {
            accion.ejecutar();
        } catch (EcoCampusException e) {
            Alertas.error("No se pudo completar la operacion", e.getMessage());
        } catch (NumberFormatException e) {
            Alertas.error("Dato invalido", "El orden de la parada debe ser un numero entero.");
        } catch (IllegalArgumentException e) {
            Alertas.error("Dato invalido", e.getMessage());
        } finally {
            listarRutas();
        }
    }
}
