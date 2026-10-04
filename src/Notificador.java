import java.util.List;

public class Notificador {
    private final List<Gateway> gateways;

    public Notificador(Gateway gate) {
        this.gateways = List.of(gate);
    }
    public Notificador(List<Gateway> gateways) {
        this.gateways = gateways;
    }

    public void enviarNotificacion(Cuenta origen, Cuenta destino, double monto) {
        String mensaje =
            "Transferiste $" + monto +
            " a la cuenta " + destino.getNumero();

        for (Gateway gateway : gateways) {
            gateway.enviar(origen.getTitular(), mensaje);
        }
    }
}
