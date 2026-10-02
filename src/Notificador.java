public class Notificador {
    // se podria generalizar a una interfaz gateway
    private final SmsGateway sms;

    Notificador (SmsGateway sms) {
        this.sms = sms; 
    } 
    public void enviarNotificacion(Cuenta origen, Cuenta destino, double monto) {
        sms.enviar(origen.getTitular(), "Transferiste $" + monto + " a la cuenta " + destino.getNumero());
    }
    
}