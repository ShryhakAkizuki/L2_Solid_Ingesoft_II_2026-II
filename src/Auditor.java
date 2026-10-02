import java.time.LocalDateTime;

public class Auditor {
    public void generarLog(Cuenta origen, Cuenta destino, double monto, String tipo ) {
        System.out.println( "[AUDITORIA] " + LocalDateTime.now() + " " + tipo
                    + " " + origen.getNumero() + " -> " + destino.getNumero() + " $" + monto);
    }
}