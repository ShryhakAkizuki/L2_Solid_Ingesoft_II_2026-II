public class Notificador {
    private final Gateway gate;

    Notificador (Gateway gate) { this.gate = gate; }
    public void enviarNotificacion(Cuenta origen, Cuenta destino, double monto) {
        gate.enviar(origen.getTitular(), "Transferiste $" + monto + " a la cuenta " + destino.getNumero());
    }
    
}