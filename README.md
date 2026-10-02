# L2_Solid_Ingesoft_II_2026-II
Repository for developing a practice exercise in refactoring using the S.O.L.I.D principles.

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