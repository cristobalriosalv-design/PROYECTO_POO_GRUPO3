# EcoCampus Circular — Grupo 3

Sistema de consola para gestionar residuos, rutas de recolección, campañas ambientales y eco-puntos en la Universidad EIA (POO en Java, 2026-2, Entrega 2).

## Requisitos

- JDK 26
- Eclipse con soporte Maven (m2e, incluido en Eclipse IDE for Java Developers)
- Maven descarga JUnit 5.14.4 y JavaFX 27.0.1. No hace falta instalar nada más.

## Importar en Eclipse

1. `File > Import > Maven > Existing Maven Projects` y selecciona la carpeta `Proyecyo_POO_Grupo3`.
   Si el proyecto ya estaba en el workspace: clic derecho > `Maven > Update Project...` (Alt+F5).
2. Espera a que Maven descargue las dependencias.

## Ejecutar

| Qué | Cómo |
|---|---|
| Aplicación de consola (la evaluada) | Clic derecho en `co.edu.eia.ecocampus.app.Main` > `Run As > Java Application` |
| Interfaz gráfica opcional (JavaFX) | Clic derecho en `co.edu.eia.ecocampus.app.gui.LanzadorGui` > `Run As > Java Application` |
| Pruebas JUnit | Clic derecho en `src/test/java` > `Run As > JUnit Test` |

Con Maven en la terminal: `mvn test`, `mvn compile exec:java` (consola) y `mvn javafx:run` (GUI).

## Datos de demostración

Al iniciar, `DatosDemo.cargar` crea:

- **Personas:** E001 Ana y E002 Luis (estudiantes), C001 Marta (colaboradora), O001 Carlos (operador habilitado para residuos especiales), O002 Pedro (operador no habilitado) y R001 Laura (responsable de sostenibilidad).
- **Puntos ecológicos:** P01 Bloque A, P02 Cafetería, P03 Laboratorios y P04 Parqueadero (inactivo).
- **Ruta RUT-01:** paradas en P01 y P02, operada por Carlos y ya cerrada.
- **Reportes:**
  - REP-001: recorre todo el flujo hasta CERRADO; Ana recibe 140 eco-puntos.
  - REP-002, REP-003 y REP-004: quedan pendientes con distintas prioridades.
- **Campañas:**
  - CAM-01: activa, con 2 actividades y el cupo de 3 completo.
  - CAM-02: planificada.

En el menú, la opción **8** ejecuta 11 casos inválidos y muestra la excepción con que se rechaza cada uno. La opción **7** muestra el resumen general.

## Paquetes

| Paquete | Responsabilidad |
|---|---|
| `modelo` | Entidades, jerarquía `Persona`, interfaces `Identificable` y `Notificable`, enums de estado |
| `servicio` | Casos de uso y reglas que involucran varias entidades |
| `repositorio` | `RepositorioMemoria<T extends Identificable>` genérico sobre un `Map` |
| `patron` | Singleton, Factory, Strategy y Observer |
| `excepcion` | Excepciones verificadas del dominio |
| `util` | Validaciones y métodos genéricos de colecciones |
| `app` | `Main`, menú de consola, composición (`ContextoAplicacion`) y datos de demostración |
| `app.gui` | Interfaz JavaFX opcional (no reemplaza la consola) |

## Dónde está cada concepto

| Concepto | Ubicación |
|---|---|
| Herencia, `super`, sobrescritura, polimorfismo | `Persona` (abstracta) con `Estudiante`, `Colaborador`, `Operador` y `ResponsableSostenibilidad`: `getFactorBonificacion()` y `puedeParticiparEnCampanias()` |
| Interfaces con 2 o más implementaciones | `Identificable` (6 clases), `EstrategiaCalculoPuntos` (3), `Notificable` (`Persona` + anónima) |
| Composición | `Campania` → `Actividad`, `RutaRecoleccion` → `Parada` (constructores de paquete), `Recoleccion` → `DetalleRecoleccion` |
| Agregación | `Campania` ◇→ `Persona`, `PublicadorEventos` ◇→ `Notificable` |
| Sobrecarga | Constructores de `Estudiante`, `ReporteResiduo`, `Campania`, etc.; `Recoleccion.agregarDetalle`, `PublicadorEventos.publicar` |
| List / Set / Map | List para historial, paradas y movimientos (orden); `LinkedHashSet<Persona>` para participantes y `EnumSet` para categorías (evitan duplicados); `LinkedHashMap` en el repositorio (búsqueda por id en O(1)) y `EnumMap` para acumular materiales |
| Clase genérica + tipo acotado | `RepositorioMemoria<T extends Identificable>` |
| Métodos genéricos | `ColeccionesUtil.buscarPorId`, `ColeccionesUtil.ordenarPorValorDesc`, `PersonaService.registrar(P)`, `MenuConsola.leerEnum` |
| `? extends` (producer) | `EcoPuntosService.sumarPuntos`, `ranking`, `RepositorioMemoria.agregarTodos`, `EstrategiaCalculoPuntos.calcular` |
| `? super` (consumer) | `EcoPuntosService.copiarDetalles`, `RepositorioMemoria.filtrar(Predicate<? super T>)` |
| Singleton | `ConfiguracionSistema` (estrategia activa + publicador de eventos) |
| Factory | `ReporteFactory` (asigna la prioridad inicial según el tipo), `PersonaFactory` |
| Strategy | `EstrategiaPorMaterial`, `EstrategiaPorPesoTotal`, `EstrategiaBonificacionCampania` (regla de campaña) |
| Observer | `PublicadorEventos` notifica cambios de estado de reportes, rutas cerradas y cupos completos |
| Excepciones | `IdentificadorDuplicadoException`, `TransicionInvalidaException`, `OperacionNoPermitidaException`, `RecursoNoEncontradoException`; `try/catch/finally` en `MenuConsola.ejecutar` y `Main` |
| Clase interna | `ReporteResiduo.RegistroCambio` (usa `ReporteResiduo.this.codigo`) |
| Implementación anónima | Bitácora `Notificable` en `Main.registrarBitacora` |

## Reglas de negocio implementadas

| Regla | Dónde se valida |
|---|---|
| Identificadores únicos | `RepositorioMemoria.guardar` |
| Punto inactivo sin nuevos reportes ni rutas | Constructor de `ReporteResiduo`, `RutaRecoleccion.agregarParada` |
| Transiciones de estado válidas (CERRADO → ASIGNADO solo tras reabrir) | `EstadoReporte.puedeCambiarA` |
| Cierre solo con recolección de peso mayor que 0 | `ReporteResiduo.cerrar` |
| Sin inscripciones duplicadas, sin cupo excedido, campaña cerrada bloqueada | `Campania.inscribir` y `Campania.agregarActividad` |
| Eco-puntos nunca negativos; movimientos con concepto y fecha | `Persona.agregarMovimiento`, `MovimientoEcoPuntos` |
| Residuos especiales: operador habilitado y disponible | `Operador.puedeAtender`, `Recoleccion.agregarDetalle` |
| Eliminaciones consistentes | `PuntoEcologicoService.retirar`, `CampaniaService.retirarCampania` |
| Búsquedas sin resultados informadas | `RecursoNoEncontradoException` y "No se encontraron resultados." en el menú |

**Regla propia:** el responsable de sostenibilidad organiza las campañas, por lo que no puede inscribirse como participante.

## Pruebas

Hay 26 pruebas JUnit 5 en 5 clases, con casos normales, de frontera y de excepción:

- `ReporteResiduoTest`
- `CampaniaTest`
- `EstrategiaCalculoPuntosTest`
- `RepositorioMemoriaTest`
- `ServiciosIntegracionTest`

## UML

Los diagramas están en `docs/uml`:

- `EcoCampus_Diagrama_Clases_Final`: diagrama completo (es el entregable).
- `Vista_1_Modelo` y `Vista_2_Arquitectura`: vistas legibles por partes.

El contenido de los tres diagramas está en `ecocampus_cuerpo.iuml`. Para regenerarlos:

```
java -jar plantuml.jar -Playout=smetana -DPLANTUML_LIMIT_SIZE=16384 -tsvg docs/uml/*.puml
```

`-Playout=smetana` usa el motor de diagramación del propio jar, así que no hace falta instalar Graphviz.
