import java.time.LocalDateTime;

public class Antifraude {
    public void generarLog(Cuenta origen, Cuenta destino, double monto, Comision comision ) {
        System.out.println( "[ANTIFRAUDE] " + LocalDateTime.now() + " " + comision.getType()
                    + " " + origen.getNumero() + " -> " + destino.getNumero() + " $" + monto);
    }
}