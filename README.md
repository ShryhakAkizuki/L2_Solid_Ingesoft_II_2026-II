# L2_Solid_Ingesoft_II_2026-II
Repository for developing a practice exercise in refactoring using the S.O.L.I.D principles.

**Integrantes:**
- Oscar Leonardo Riveros Perez
- Juan Luis Arteaga Mantovani

# 1 - Diagnóstico

## 1.1 - Tabla de Hallazgos

| Clase / método                       | Letra                    | Evidencia en el código                                                                                                                                                                                                                                               | Consecuencia para el banco o el cliente                                                                                                                                                                                                                                                                                        |
| ------------------------------------ | ------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `CDT / retirar`                      | L: Liskov Substitution   | La clase `CDT` hereda de la clase `Cuenta` y sobrescribe el método `retirar`. Sin embargo, este método lanza una excepción de tipo `UnsupportedOperationException` cuando se intenta realizar un retiro antes de la fecha de vencimiento.                            | Al tratar un objeto `CDT` como una instancia de la clase base `Cuenta`, se puede asumir que la operación de retiro está disponible. Si se intenta realizar antes del vencimiento, se genera una excepción en tiempo de ejecución, lo que puede provocar fallos en los procesos que operen de forma genérica sobre las cuentas. |
| `TransaccionService / transferir`    | S: Single Responsibility | El método `transferir` concentra múltiples responsabilidades: realiza validaciones, calcula las comisiones, ejecuta la transferencia de dinero, registra la transacción en la base de datos, genera el comprobante, envía las notificaciones y realiza la auditoría. | Un cambio en cualquiera de las siete responsabilidades obliga a modificar directamente el método `transferir`. Esto incrementa su complejidad y lo hace más susceptible a errores y dificultades de mantenimiento.                                                                                                             |
| `TransaccionService`                 | D: Dependency Inversion  | La clase depende directamente de una base de datos Oracle y de un servicio de notificaciones mediante SMS, en lugar de utilizar abstracciones que permitan desacoplar estas dependencias.                                                                            | Ante una eventual migración de la base de datos o del servicio de notificaciones, sería necesario modificar `TransaccionService`. Esto dificulta la sustitución de componentes y aumenta el impacto de los cambios sobre la lógica de negocio.                                                                                 |
| `TransaccionService / transferir`    | O: Open/Closed           | La comisión se determina mediante una estructura `switch` con múltiples opciones que representan las diferentes alternativas de cálculo.                                                                                                                             | Si se requiere incorporar una nueva tasa o estrategia de comisión, es necesario modificar la lógica existente y agregar una nueva condición. A medida que aumenten las opciones, el método puede hacerse más extenso y difícil de mantener.                                                                                    |
| `ProductoBancario / CreditoVivienda` | I: Interface Segregation | La interfaz `ProductoBancario` obliga a `CreditoVivienda` a implementar múltiples métodos que no son soportados por esta clase, por lo que algunos permanecen vacíos o no implementados adecuadamente.                                                               | Al utilizar `CreditoVivienda` mediante polimorfismo a través de la interfaz, existe el riesgo de ejecutar operaciones no soportadas y obtener excepciones o comportamientos inesperados debido a métodos que no son aplicables al producto.   

## 1.2 - Dos Experimentos

1. Al incluir un `CDT` dentro del proceso de cobro de la cuota de manejo, el programa genera una excepción debido a que dicho proceso intenta realizar una operación de retiro que no es válida para este tipo de cuenta. Si este comportamiento se trasladara a producción y el proceso se ejecutara sobre un volumen elevado de cuentas, bastaría con encontrar un `CDT` para provocar el fallo del proceso, afectando su continuidad.

2. No es posible implementar adecuadamente una prueba unitaria del método `transferir` debido a que la base de datos Oracle y el servicio de notificaciones SMS se encuentran fuertemente acoplados a `TransaccionService`. Al no existir abstracciones que permitan sustituir estas dependencias, la prueba requiere interactuar directamente con servicios externos, lo que dificulta el aislamiento de la lógica que se desea evaluar.

## 1.3 - Medición "Antes"

| Métrica                                                                     | Antes |
| --------------------------------------------------------------------------- | ----: |
| Líneas del método `transferir`                                              |    36 |
| Número de razones distintas por las que `TransaccionService` podría cambiar |     7 |
| Clases concretas que `TransaccionService` instancia mediante `new`          |     2 |
| Métodos vacíos o que lanzan excepciones por "no aplica"                     |     3 |
| ¿Se puede probar `transferir` sin Oracle ni SMS?                            |    No |

## 1.4 - Diagrama de clases del código original

<img
    src=".\L2_Diagrama_Base.png"
    alt="L2_Diagrama_Base"
    align="left"
  />

# 2 - Refactorización

## Punto de control S

La clase `TransaccionService` gestiona el proceso de realización de una transacción, es decir, realiza la transferencia de dinero y crea un `Notificador` para que envíe mensajes por SMS, un `ServicioGuardado` para que se registre la realización de la transferencia, un `Auditor` para que genere logs y `Comprobante` para que imprima el comprobante de la transacción
En caso de que el área legal quiera cambiar el formato del comprobante ahora sólo es necesario modificar la clase `Comprobante`. 

## Punto de control O

Si llega un nuevo tipo de transferencia basta con crear una nueva clase que extienda la clase abstracta comisión, allí se define el tipo de comisión y la forma de calcularla. Concretamente, si fuera necesario una comisón particular, bastaría con crear una nueva clase que extienda `Comision` y modificar `Main` para que se cree un objeto de esa nueva clase. 

## Punto de control L
La solución implementada permite detectar el error mediante el sistema de tipos, ya que se modificó el valor de retorno del método. Cuando el retiro puede realizarse, se ejecuta la transacción y el método devuelve un valor booleano que confirma su correcta ejecución. En caso contrario, devuelve false.

Esto permite que el proceso de cobro de la cuota de manejo conozca si la transacción se realizó correctamente y, en función de ello, muestre el mensaje correspondiente, sin necesidad de generar excepciones.

Esta solución resulta más adecuada que el uso de un bloque try-catch, debido a que cada tipo de cuenta puede implementar su propia lógica para determinar si un retiro es válido. Además, se establece un comportamiento predeterminado en el que la operación está permitida. Por el contrario, utilizar try-catch como mecanismo de control puede ocultar el problema en lugar de solucionarlo explícitamente.

## Punto de control I
La implementación es correcta, ya que se aplicó una adecuada segregación de responsabilidades mediante múltiples interfaces. De esta forma, para que una clase pueda ser utilizada por el generador de extractos, únicamente debe implementar la interfaz correspondiente y cumplir con el contrato definido, sin verse obligada a proporcionar funcionalidades adicionales que no necesita.

## Punto de control D
Ahora `TransaccionService` solo recibe objetos de `Notificador`, `ServicioGuardado`, `Comprobante` y `Auditor`, de modo que no crea ninguna clase concreta. Si, por ejemplo, fuera necesario cambiar el repositorio o el método de notificacion, solo es necesario escribir una clase que extienda `Repositorio` y `Gateway` respectivamente y cambiar los argumentos en el constructor de `ServicioGuardado` y `Notificador` en `Main.java`.

# 3 - Pruebas unitarias

Las cinco pruebas unitarias diseñadas tardan aproximadamente 57 ms en ejecutarse. No fue necesario modificar ninguna línea de `TransaccionService`, ya que la clase depende únicamente de abstracciones, lo que permite sustituir sus dependencias por implementaciones simuladas durante las pruebas.

En contraste, si se hubieran intentado realizar estas mismas pruebas con la implementación del bloque 1, probablemente habría sido necesario modificar `TransaccionService` para poder reemplazar o aislar sus dependencias, dificultando las pruebas unitarias y aumentando el acoplamiento de la clase.

# Bloque 4 - Nuevos requerimientos

| Req. | Archivos a modificar en el código original (estimado) | Archivos existentes modificados (real) | Archivos nuevos | ¿Se rompió alguna prueba? |
|---|---|---|---|---|
| R1 | 2 archivos. Se estima modificar `TransaccionService.java` porque es el responsable de ejecutar las transferencias y se tendría que modificar el switch original. Además `Main.java`. | 1 archivo: `Main.java`. Se agrega el código correspondiente a la transferencia por medio de llaves. | 1 archivo. `ComisionLlave.java` para establecer una comisión de $0. Manteniendo la misma lógica de las comisiones anteriores | No. La lógica existente de `transferir()` y las comisiones actuales no se modifican, por lo que las pruebas de las funcionalidades existentes continuan funcionando. |
| R2 | 2 archivos. Se estima que hubiera sido necesario modificar el `Main.java` | 1 archivo. Sólo se modificó el `Main.java`. | 1 archivo. Se creó el archivo `CuentaInfantil.java` que extiende `Cuenta.java`. En este se modificó la lógica de retiro para que se pudiera verificar el límite diario | No, no se alteró el funcionamiento de las demás clases, en particular, no se hicieron cambios sobre `Cuenta.java` ni `TransaccionService.java` |
| R3 | 1 archivo. Hubiera sido necesario modificar el archivo `TransaccionService.java`, haciendo que ahora creara objetos para cada Gateway y modificando el metodo `transferir()` para que enviara los mensajes. | 2 archivos. Se modificó la clase `Notificador.java` para hacer posible que se enviaran múltiples objetos de tipo `Gateway.java` a la vez. Esto pudo prevenirse si se diseñaba `Notificador.java` desde un principio con la capacidad de recibir una lista de `Gateways`. También se modificó `Main.java` para crear los objetos de `SmsGateway.java` y `AppGateway.java` | 1 archivo. Se creó el archivo `AppGateway.java` como un nuevo tipo de `Gateway`. | No, se mantuvo el constructor de `Notificador.java` para un solo `Gateway.java` por lo tanto cualquier tipo de prueba que usara el constructor con un sólo `Gateway.java`. |
| R4 | 1 archivo. Hubiera sido necesario modificar la clase `TransaccionService`, en particular el método `transferir()`. | 2 archivos. Se modificaron los archivos `TransaccionService.java`, para recibir un objeto `Antifraude`, y el archivo `Main.java` para tomar esto en cuenta. | 1 archivo. Se creó una nueva clase `Antifraude.java` | Si. Una de las pruebas dejó de pasar porque crea un objeto `TransaccionService` sin enviar un objeto `Antifraude`. Si bien este problema también podría ser gestionado con varios constructores, no consideramos que sea posible, porque esa modificación es "por regulación", es decir, siempre tendrá por lo menos un `Auditor` (como política de la empresa) y un `Antifraude`. Por lo tanto, sería necesario modificar el archivo de prueba. |
| R5 | 1 archivo. Hubiera sido necesario modificar el archivo `TransaccionService.java`, haciendo que de acuerdo a un switch creara un objeto `OracleRepositorio` o `OraclePostgres`. | 1 archivo. Se modificó el `Main.java` para crear un objeto de tipo `OraclePostgres`. Esto fue muy sencillo, solo se cambió una línea. | 1 archivo. Se creó un archivo `OraclePostgres.java` que extiende la clase `Repositorio`. | No. No se cambia el funcionamiento de ninguna pieza de código porque todas las interacciones son partir de la abstracción `Repositorio`. |

# Bloque 5 - Revision Cruzada

| Criterio | Sí | No |
| :--- | :---: | :---: |
| Entendimos qué hace cada clase leyendo solo su nombre y sus métodos públicos. | **X** | |
| Pudimos reutilizar piezas existentes sin copiar y pegar código. | **X** | |
| Implementamos el requerimiento sin modificar la lógica de clases existentes. | **X** | |
| No encontramos métodos vacíos ni que lancen "no aplica". | **X** | |
| No encontramos if/switch por tipo que tuvimos que extender. | **X** | |
| Las pruebas existentes siguieron pasando después de nuestro cambio. | **X** | |
| No encontramos abstracciones innecesarias (interfaces que no aportan). | | **X** |

**Lo mejor del diseño de la otra pareja:**
El manejo de las validaciones del CDT. Al configurar la variable `retirable` internamente en la clase `CDT` con base en la fecha y dejar que el método `retirar` de la clase padre haga el trabajo, respetaron el principio de Liskov. No tuvimos que escribir ningún "if" para comprobar si el producto era un CDT al momento de pagar servicios; simplemente fallaba si debía fallar.

**Lo que nos costó extender (y su relación con SOLID):**
Tuvieron una violación fuerte del **Principio de Inversión de Dependencias (D)**. Clases transversales como `Notificador`, `Auditor` y `Comprobante` exigían recibir objetos concretos de tipo `Cuenta` como destino. Para poder pagar un servicio público (cuyo destino es un String), tuvimos que inyectar una "Cuenta Falsa" (Dummy object) que llevaba la referencia de la factura en el atributo número.

Además, su clase `Notificador` tiene el mensaje de texto hardcodeado en su interior, lo que provocó que al pagar un recibo, el cliente reciba un mensaje ilógico que dice *"Transferiste $184300 a la cuenta AGUA-12345"*. El diseño habría sido verdaderamente reutilizable si esas herramientas hubiesen dependido de abstracciones (ej. recibir un simple `String destinoId` en lugar de una `Cuenta`) y si permitieran que el mensaje a enviar se pasara como parámetro desde el servicio.