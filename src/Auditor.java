import java.time.LocalDateTime;

public class Auditor {
    public void generarLog(Cuenta origen, Cuenta destino, double monto, Comision comision ) {
        System.out.println( "[AUDITORIA] " + LocalDateTime.now() + " " + comision.getType()
                    + " " + origen.getNumero() + " -> " + destino.getNumero() + " $" + monto);
    }
}