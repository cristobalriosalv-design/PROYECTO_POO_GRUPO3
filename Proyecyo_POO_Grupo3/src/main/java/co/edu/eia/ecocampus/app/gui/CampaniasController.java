package co.edu.eia.ecocampus.app.gui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Actividad;
import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.servicio.CampaniaService;
import co.edu.eia.ecocampus.servicio.PersonaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controlador de CampaniasAmbientales.fxml: opciones 1 a 8 del submenu de
 * campanias. Cada pestana muestra el resultado en su area "Confirmacion".
 */
public class CampaniasController {

    /** Operacion que devuelve el texto de confirmacion o lanza una regla violada. */
    @FunctionalInterface
    private interface Operacion {
        String ejecutar() throws EcoCampusException;
    }

    // Crear campania
    @FXML private TextField txtCodigoCampania;
    @FXML private TextField txtNombreCampania;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtFechaInicio;
    @FXML private TextField txtFechaFin;
    @FXML private TextField txtCupo;
    @FXML private TextField txtPuntos;
    @FXML private TextArea txtConfCrear;
    // Agregar actividad
    @FXML private TextField txtCampaniaActividad;
    @FXML private TextField txtIdActividad;
    @FXML private TextField txtNombreActividad;
    @FXML private TextField txtFechaActividad;
    @FXML private TextField txtCupoActividad;
    @FXML private TextArea txtConfActividad;
    // Activar, inscribir, retirar participante, cerrar, retirar campania
    @FXML private TextField txtCampaniaActivar;
    @FXML private TextArea txtConfActivar;
    @FXML private TextField txtCampaniaInscribir;
    @FXML private TextField txtPersonaInscribir;
    @FXML private TextArea txtConfInscribir;
    @FXML private TextField txtCampaniaRetirar;
    @FXML private TextField txtPersonaRetirar;
    @FXML private TextArea txtConfRetirar;
    @FXML private TextField txtCampaniaCerrar;
    @FXML private TextArea txtConfCerrar;
    @FXML private TextField txtCampaniaEliminar;
    @FXML private TextArea txtConfEliminar;
    // Listar
    @FXML private TextArea txtListaCampanias;

    private final CampaniaService campanias = ContextoGui.get().getCampanias();
    private final PersonaService personas = ContextoGui.get().getPersonas();

    @FXML
    private void initialize() {
        listarCampanias();
    }

    // 1. Crear campania
    @FXML
    private void crearCampania() {
        ejecutar(txtConfCrear, () -> {
            Campania campania = new Campania(txtCodigoCampania.getText(), txtNombreCampania.getText(),
                    txtDescripcion.getText(), LocalDate.parse(txtFechaInicio.getText().trim()),
                    LocalDate.parse(txtFechaFin.getText().trim()), Integer.parseInt(txtCupo.getText().trim()),
                    Integer.parseInt(txtPuntos.getText().trim()));
            campanias.crearCampania(campania);
            return "Creada: " + campania;
        });
    }

    // 2. Agregar actividad (la campania la crea: composicion)
    @FXML
    private void agregarActividad() {
        ejecutar(txtConfActividad, () -> {
            String codigo = txtCampaniaActividad.getText();
            campanias.agregarActividad(codigo, txtIdActividad.getText(), txtNombreActividad.getText(),
                    LocalDate.parse(txtFechaActividad.getText().trim()).atTime(9, 0),
                    Integer.parseInt(txtCupoActividad.getText().trim()));
            return "Actividad agregada.\n" + describir(campanias.buscar(codigo));
        });
    }

    // 3. Activar campania
    @FXML
    private void activarCampania() {
        ejecutar(txtConfActivar, () -> {
            campanias.activar(txtCampaniaActivar.getText());
            return "Activada: " + campanias.buscar(txtCampaniaActivar.getText());
        });
    }

    // 4. Inscribir persona (si se completa el cupo se publica un evento: Observer)
    @FXML
    private void inscribirPersona() {
        ejecutar(txtConfInscribir, () -> {
            String codigo = txtCampaniaInscribir.getText();
            Persona persona = personas.buscar(txtPersonaInscribir.getText());
            campanias.inscribir(codigo, persona);
            Campania campania = campanias.buscar(codigo);
            String mensaje = persona.getNombre() + " inscrito en " + campania.getNombre() + "\n" + campania;
            return campania.alcanzoCupo() ? mensaje + "\nLa campania completo su cupo." : mensaje;
        });
    }

    // 5. Retirar participante
    @FXML
    private void retirarParticipante() {
        ejecutar(txtConfRetirar, () -> {
            String codigo = txtCampaniaRetirar.getText();
            Persona persona = personas.buscar(txtPersonaRetirar.getText());
            campanias.retirarParticipante(codigo, persona);
            return persona.getNombre() + " fue retirado.\n" + campanias.buscar(codigo);
        });
    }

    // 6. Cerrar campania (otorga los puntos de participacion)
    @FXML
    private void cerrarCampania() {
        ejecutar(txtConfCerrar, () -> {
            int puntos = campanias.cerrar(txtCampaniaCerrar.getText());
            return "Campania cerrada. Puntos otorgados en total: " + puntos;
        });
    }

    // 7. Retirar campania (solo si no tiene participantes)
    @FXML
    private void retirarCampania() {
        ejecutar(txtConfEliminar, () -> {
            campanias.retirarCampania(txtCampaniaEliminar.getText());
            return "Campania " + txtCampaniaEliminar.getText() + " retirada.";
        });
    }

    // 0. Volver
    @FXML
    private void volverMenu(ActionEvent event) {
        Navegador.irA(event, "MenuPrincipal.fxml");
    }

    // 8. Listar campanias con actividades
    private void listarCampanias() {
        List<Campania> lista = campanias.listar();
        if (lista.isEmpty()) {
            txtListaCampanias.setText("No hay campanias registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Campania campania : lista) {
            sb.append(describir(campania)).append("\n\n");
        }
        txtListaCampanias.setText(sb.toString());
    }

    private String describir(Campania campania) {
        StringBuilder sb = new StringBuilder(campania.toString());
        for (Actividad actividad : campania.getActividades()) {
            sb.append("\n   ").append(actividad);
        }
        if (!campania.getParticipantes().isEmpty()) {
            sb.append("\n   Participantes:");
            campania.getParticipantes().forEach(p -> sb.append(' ').append(p.getNombre()).append(';'));
        }
        return sb.toString();
    }

    /** Muestra la confirmacion en el area de la pestana; los errores tambien en una alerta. */
    private void ejecutar(TextArea salida, Operacion operacion) {
        try {
            salida.setText(operacion.ejecutar());
        } catch (EcoCampusException e) {
            mostrarError(salida, e.getMessage());
        } catch (DateTimeParseException e) {
            mostrarError(salida, "Fecha invalida, use el formato aaaa-mm-dd (por ejemplo 2026-11-15).");
        } catch (NumberFormatException e) {
            mostrarError(salida, "El cupo y los puntos deben ser numeros enteros.");
        } catch (IllegalArgumentException e) {
            mostrarError(salida, e.getMessage());
        } finally {
            listarCampanias();
        }
    }

    private void mostrarError(TextArea salida, String mensaje) {
        salida.setText("ERROR: " + mensaje);
        Alertas.error("No se pudo completar la operacion", mensaje);
    }
}
