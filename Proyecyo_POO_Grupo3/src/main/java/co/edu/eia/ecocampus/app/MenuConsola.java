package co.edu.eia.ecocampus.app;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import co.edu.eia.ecocampus.excepcion.EcoCampusException;
import co.edu.eia.ecocampus.modelo.Campania;
import co.edu.eia.ecocampus.modelo.Operador;
import co.edu.eia.ecocampus.modelo.Persona;
import co.edu.eia.ecocampus.modelo.Prioridad;
import co.edu.eia.ecocampus.modelo.PuntoEcologico;
import co.edu.eia.ecocampus.modelo.Recoleccion;
import co.edu.eia.ecocampus.modelo.ReporteResiduo;
import co.edu.eia.ecocampus.modelo.RutaRecoleccion;
import co.edu.eia.ecocampus.modelo.TipoMaterial;
import co.edu.eia.ecocampus.modelo.TipoPersona;
import co.edu.eia.ecocampus.modelo.TipoReporte;
import co.edu.eia.ecocampus.modelo.UnidadMedida;
import co.edu.eia.ecocampus.patron.EstrategiaBonificacionCampania;
import co.edu.eia.ecocampus.patron.EstrategiaPorMaterial;
import co.edu.eia.ecocampus.patron.EstrategiaPorPesoTotal;
import co.edu.eia.ecocampus.util.ColeccionesUtil;

/** Menu de consola: permite ejecutar todos los flujos sin modificar el codigo. */
public class MenuConsola {

    private final ContextoAplicacion ctx;
    private final Scanner scanner;

    public MenuConsola(ContextoAplicacion ctx, Scanner scanner) {
        this.ctx = ctx;
        this.scanner = scanner;
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("""

                    ========= ECOCAMPUS CIRCULAR =========
                    1. Personas y puntos ecologicos
                    2. Reportes de residuos
                    3. Rutas de recoleccion
                    4. Campanias ambientales
                    5. Consultas
                    6. Estrategia de eco-puntos
                    7. Resumen general
                    8. Demostrar casos invalidos
                    0. Salir""");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1 -> menuPersonasYPuntos();
                case 2 -> menuReportes();
                case 3 -> menuRutas();
                case 4 -> menuCampanias();
                case 5 -> menuConsultas();
                case 6 -> menuEstrategia();
                case 7 -> System.out.println(ctx.getResumen().generarResumen());
                case 8 -> DatosDemo.demostrarCasosInvalidos(ctx).forEach(System.out::println);
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    // ================================================== submenus

    private void menuPersonasYPuntos() {
        System.out.println("""
                -- Personas y puntos --
                1. Registrar persona
                2. Registrar punto ecologico
                3. Activar / desactivar punto
                4. Retirar punto
                5. Cambiar disponibilidad de operador
                6. Listar personas
                7. Listar puntos
                0. Volver""");
        switch (leerEntero("Opcion: ")) {
            case 1 -> ejecutar(this::registrarPersona);
            case 2 -> ejecutar(this::registrarPunto);
            case 3 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo del punto: ");
                if (leerSiNo("Activar? (s = activar, n = desactivar): ")) {
                    ctx.getPuntos().activar(codigo);
                } else {
                    ctx.getPuntos().desactivar(codigo);
                }
                System.out.println(ctx.getPuntos().buscar(codigo));
            });
            case 4 -> ejecutar(() -> {
                ctx.getPuntos().retirar(leerTexto("Codigo del punto: "));
                System.out.println("Punto retirado.");
            });
            case 5 -> ejecutar(() -> {
                Operador operador = ctx.getPersonas().buscarOperador(leerTexto("Id del operador: "));
                operador.cambiarDisponibilidad(leerSiNo("Disponible? (s/n): "));
                System.out.println(operador);
            });
            case 6 -> imprimirLista("Personas", ctx.getPersonas().listar());
            case 7 -> imprimirLista("Puntos ecologicos", ctx.getPuntos().listar());
            default -> { }
        }
    }

    private void menuReportes() {
        System.out.println("""
                -- Reportes --
                1. Crear reporte
                2. Clasificar reporte
                3. Asignar a operador
                4. Asignar a ruta
                5. Iniciar atencion
                6. Registrar recoleccion y cerrar
                7. Reabrir reporte
                8. Ver historial de estados
                9. Listar reportes
                0. Volver""");
        switch (leerEntero("Opcion: ")) {
            case 1 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo del reporte: ");
                Persona autor = ctx.getPersonas().buscar(leerTexto("Id del autor: "));
                PuntoEcologico punto = ctx.getPuntos().buscar(leerTexto("Codigo del punto: "));
                TipoReporte tipo = leerEnum("Tipo de reporte", TipoReporte.class);
                String descripcion = leerTexto("Descripcion: ");
                System.out.println("Creado: " + ctx.getReportes().crearReporte(codigo, autor, punto, tipo, descripcion));
            });
            case 2 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo del reporte: ");
                ctx.getReportes().clasificar(codigo, leerEnum("Prioridad", Prioridad.class));
                System.out.println(ctx.getReportes().buscar(codigo));
            });
            case 3 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo del reporte: ");
                imprimirLista("Operadores disponibles", ctx.getPersonas().operadoresDisponibles());
                Operador operador = ctx.getPersonas().buscarOperador(leerTexto("Id del operador: "));
                ctx.getReportes().asignarAOperador(codigo, operador);
                System.out.println(ctx.getReportes().buscar(codigo));
            });
            case 4 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo del reporte: ");
                RutaRecoleccion ruta = ctx.getRutas().buscar(leerTexto("Codigo de la ruta: "));
                ctx.getReportes().asignarARuta(codigo, ruta);
                System.out.println(ctx.getReportes().buscar(codigo));
            });
            case 5 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo del reporte: ");
                ctx.getReportes().iniciarAtencion(codigo);
                System.out.println(ctx.getReportes().buscar(codigo));
            });
            case 6 -> ejecutar(this::registrarRecoleccionYCerrar);
            case 7 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo del reporte: ");
                ctx.getReportes().reabrir(codigo, leerTexto("Motivo de la reapertura: "));
                System.out.println(ctx.getReportes().buscar(codigo));
            });
            case 8 -> ejecutar(() -> {
                ReporteResiduo reporte = ctx.getReportes().buscar(leerTexto("Codigo del reporte: "));
                imprimirLista("Historial de " + reporte.getId(), reporte.getHistorial());
            });
            case 9 -> imprimirLista("Reportes", ctx.getReportes().listar());
            default -> { }
        }
    }

    private void menuRutas() {
        System.out.println("""
                -- Rutas --
                1. Crear ruta
                2. Agregar parada
                3. Quitar parada
                4. Asignar operador
                5. Iniciar ruta
                6. Cerrar ruta
                7. Listar rutas con paradas
                0. Volver""");
        switch (leerEntero("Opcion: ")) {
            case 1 -> ejecutar(() -> System.out.println("Creada: "
                    + ctx.getRutas().crearRuta(leerTexto("Codigo de la ruta: "), leerTexto("Nombre: "))));
            case 2 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la ruta: ");
                PuntoEcologico punto = ctx.getPuntos().buscar(leerTexto("Codigo del punto: "));
                ctx.getRutas().agregarParada(codigo, punto, leerTexto("Accion esperada: "));
                imprimirLista("Paradas", ctx.getRutas().buscar(codigo).getParadas());
            });
            case 3 -> ejecutar(() -> {
                RutaRecoleccion ruta = ctx.getRutas().buscar(leerTexto("Codigo de la ruta: "));
                ruta.quitarParada(leerEntero("Orden de la parada a quitar: "));
                imprimirLista("Paradas", ruta.getParadas());
            });
            case 4 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la ruta: ");
                ctx.getRutas().asignarOperador(codigo,
                        ctx.getPersonas().buscarOperador(leerTexto("Id del operador: ")));
                System.out.println(ctx.getRutas().buscar(codigo));
            });
            case 5 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la ruta: ");
                ctx.getRutas().iniciarRuta(codigo);
                System.out.println(ctx.getRutas().buscar(codigo));
            });
            case 6 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la ruta: ");
                ctx.getRutas().cerrarRuta(codigo);
                System.out.println(ctx.getRutas().buscar(codigo));
            });
            case 7 -> {
                List<RutaRecoleccion> rutas = ctx.getRutas().listar();
                imprimirLista("Rutas", rutas);
                rutas.forEach(r -> r.getParadas().forEach(p -> System.out.println("     " + r.getId() + " " + p)));
            }
            default -> { }
        }
    }

    private void menuCampanias() {
        System.out.println("""
                -- Campanias --
                1. Crear campania
                2. Agregar actividad
                3. Activar campania
                4. Inscribir persona
                5. Retirar participante
                6. Cerrar campania (otorga puntos)
                7. Retirar campania
                8. Listar campanias con actividades
                0. Volver""");
        switch (leerEntero("Opcion: ")) {
            case 1 -> ejecutar(() -> {
                Campania campania = new Campania(leerTexto("Codigo: "), leerTexto("Nombre: "),
                        leerTexto("Descripcion: "), leerFecha("Fecha de inicio (aaaa-mm-dd): "),
                        leerFecha("Fecha de fin (aaaa-mm-dd): "), leerEntero("Cupo: "),
                        leerEntero("Puntos por participacion: "));
                ctx.getCampanias().crearCampania(campania);
                System.out.println("Creada: " + campania);
            });
            case 2 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la campania: ");
                ctx.getCampanias().agregarActividad(codigo, leerTexto("Id de la actividad: "), leerTexto("Nombre: "),
                        leerFecha("Fecha (aaaa-mm-dd): ").atTime(9, 0), leerEntero("Cupo: "));
                imprimirLista("Actividades", ctx.getCampanias().buscar(codigo).getActividades());
            });
            case 3 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la campania: ");
                ctx.getCampanias().activar(codigo);
                System.out.println(ctx.getCampanias().buscar(codigo));
            });
            case 4 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la campania: ");
                ctx.getCampanias().inscribir(codigo, ctx.getPersonas().buscar(leerTexto("Id de la persona: ")));
                System.out.println(ctx.getCampanias().buscar(codigo));
            });
            case 5 -> ejecutar(() -> {
                String codigo = leerTexto("Codigo de la campania: ");
                ctx.getCampanias().retirarParticipante(codigo,
                        ctx.getPersonas().buscar(leerTexto("Id de la persona: ")));
                System.out.println(ctx.getCampanias().buscar(codigo));
            });
            case 6 -> ejecutar(() -> {
                int puntos = ctx.getCampanias().cerrar(leerTexto("Codigo de la campania: "));
                System.out.println("Campania cerrada. Puntos otorgados: " + puntos);
            });
            case 7 -> ejecutar(() -> {
                ctx.getCampanias().retirarCampania(leerTexto("Codigo de la campania: "));
                System.out.println("Campania retirada.");
            });
            case 8 -> {
                List<Campania> campanias = ctx.getCampanias().listar();
                imprimirLista("Campanias", campanias);
                campanias.forEach(c -> c.getActividades().forEach(a -> System.out.println("     " + c.getId() + " " + a)));
            }
            default -> { }
        }
    }

    private void menuConsultas() {
        System.out.println("""
                -- Consultas --
                1. Reportes pendientes por prioridad
                2. Reportes pendientes de un punto
                3. Historial de una persona
                4. Materiales recolectados
                5. Ranking de eco-puntos
                0. Volver""");
        switch (leerEntero("Opcion: ")) {
            case 1 -> imprimirLista("Pendientes por prioridad", ctx.getReportes().pendientesPorPrioridad());
            case 2 -> {
                String codigo = leerTexto("Codigo del punto: ");
                imprimirLista("Pendientes del punto " + codigo, ctx.getReportes().pendientesPorPunto(codigo));
            }
            case 3 -> ejecutar(() -> {
                Persona persona = ctx.getPersonas().buscar(leerTexto("Id de la persona: "));
                System.out.println(persona);
                imprimirLista("Movimientos de eco-puntos", persona.getHistorial());
                System.out.println("  Total segun movimientos: "
                        + ctx.getEcoPuntos().sumarPuntos(persona.getHistorial()) + " pts");
                imprimirLista("Reportes creados", ctx.getReportes().reportesDePersona(persona.getId()));
                imprimirLista("Notificaciones", persona.getNotificaciones());
            });
            case 4 -> {
                List<Map.Entry<TipoMaterial, Double>> materiales = ColeccionesUtil
                        .ordenarPorValorDesc(ctx.getEcoPuntos().materialesRecolectados());
                imprimirLista("Materiales recolectados", materiales.stream()
                        .map(e -> String.format("%-11s %8.2f kg", e.getKey(), e.getValue())).toList());
            }
            case 5 -> {
                List<Persona> ranking = ctx.getEcoPuntos().ranking(ctx.getPersonas().listar());
                imprimirLista("Ranking de eco-puntos", ranking.stream()
                        .map(p -> String.format("%-25s %5d pts", p.getNombre(), p.getSaldoPuntos())).toList());
            }
            default -> { }
        }
    }

    private void menuEstrategia() {
        System.out.println("Estrategia actual: " + ctx.getEcoPuntos().getEstrategiaActual().getNombre());
        System.out.println("""
                1. Por tipo de material
                2. Por peso total
                3. Bonificacion por campania
                0. Volver""");
        switch (leerEntero("Opcion: ")) {
            case 1 -> ctx.getEcoPuntos().cambiarEstrategia(new EstrategiaPorMaterial());
            case 2 -> ejecutar(() -> ctx.getEcoPuntos()
                    .cambiarEstrategia(new EstrategiaPorPesoTotal(leerEntero("Puntos por kg: "))));
            case 3 -> ejecutar(() -> {
                Campania campania = ctx.getCampanias().buscar(leerTexto("Codigo de la campania: "));
                double multiplicador = leerDecimal("Multiplicador (>= 1): ");
                ctx.getEcoPuntos().cambiarEstrategia(
                        new EstrategiaBonificacionCampania(new EstrategiaPorMaterial(), campania, multiplicador));
            });
            default -> { }
        }
        System.out.println("Estrategia vigente: " + ctx.getEcoPuntos().getEstrategiaActual().getNombre());
    }

    // ================================================== acciones compuestas

    private void registrarPersona() throws EcoCampusException {
        TipoPersona tipo = leerEnum("Tipo de persona", TipoPersona.class);
        String id = leerTexto("Id: ");
        String nombre = leerTexto("Nombre: ");
        String correo = leerTexto("Correo: ");
        Persona persona;
        if (tipo == TipoPersona.OPERADOR) {
            boolean habilitado = leerSiNo("Habilitado para residuos especiales? (s/n): ");
            persona = ctx.getPersonas().registrar(new Operador(id, nombre, correo, habilitado));
        } else {
            persona = ctx.getPersonas().registrar(tipo, id, nombre, correo);
        }
        System.out.println("Registrado: " + persona);
    }

    private void registrarPunto() throws EcoCampusException {
        PuntoEcologico punto = new PuntoEcologico(leerTexto("Codigo: "), leerTexto("Ubicacion: "),
                leerDecimal("Capacidad (kg): "));
        do {
            punto.agregarCategoria(leerEnum("Categoria aceptada", TipoMaterial.class));
        } while (leerSiNo("Agregar otra categoria? (s/n): "));
        ctx.getPuntos().registrar(punto);
        System.out.println("Registrado: " + punto);
    }

    private void registrarRecoleccionYCerrar() throws EcoCampusException {
        ReporteResiduo reporte = ctx.getReportes().buscar(leerTexto("Codigo del reporte: "));
        Operador operador = reporte.getOperadorAsignado();
        if (operador == null) {
            operador = ctx.getPersonas().buscarOperador(leerTexto("Id del operador que recolecto: "));
        }
        Recoleccion recoleccion = new Recoleccion(leerTexto("Id de la recoleccion: "), operador,
                leerTexto("Observaciones: "));
        do {
            recoleccion.agregarDetalle(leerEnum("Material", TipoMaterial.class), leerDecimal("Cantidad: "),
                    leerEnum("Unidad", UnidadMedida.class));
        } while (leerSiNo("Agregar otro material? (s/n): "));
        int puntos = ctx.getReportes().cerrar(reporte.getId(), recoleccion);
        System.out.println(recoleccion);
        System.out.println("Reporte cerrado. " + reporte.getAutor().getNombre() + " recibio " + puntos + " eco-puntos.");
    }

    // ================================================== utilidades de entrada/salida

    /** Ejecuta una accion mostrando de forma uniforme los errores de negocio y de datos. */
    private void ejecutar(AccionConsola accion) {
        try {
            accion.ejecutar();
        } catch (EcoCampusException e) {
            System.out.println("No se pudo completar: " + e.getMessage());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Dato invalido: " + e.getMessage());
        } finally {
            System.out.println("--------------------------------------");
        }
    }

    /** Las busquedas informan claramente cuando no hay resultados. */
    private void imprimirLista(String titulo, Collection<?> elementos) {
        System.out.println("== " + titulo + " (" + elementos.size() + ") ==");
        if (elementos.isEmpty()) {
            System.out.println("  No se encontraron resultados.");
            return;
        }
        for (Object elemento : elementos) {
            System.out.println("  " + elemento);
        }
    }

    /** Metodo generico acotado: sirve para cualquier enum del dominio. */
    private <E extends Enum<E>> E leerEnum(String mensaje, Class<E> tipo) {
        E[] valores = tipo.getEnumConstants();
        System.out.println(mensaje + ":");
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + valores[i]);
        }
        while (true) {
            int opcion = leerEntero("Seleccione: ");
            if (opcion >= 1 && opcion <= valores.length) {
                return valores[opcion - 1];
            }
            System.out.println("Seleccione un numero entre 1 y " + valores.length);
        }
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("El valor no puede estar vacio.");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero.");
            }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero (use punto decimal).");
            }
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(leerTexto(mensaje));
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha invalido, use aaaa-mm-dd.");
            }
        }
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje).toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("n")) {
                return respuesta.equals("s");
            }
            System.out.println("Responda s o n.");
        }
    }
}
