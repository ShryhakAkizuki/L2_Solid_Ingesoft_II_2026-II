public class AppGateway extends Gateway {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[PUSH] Conectando al proveedor de notificaciones PUSH ...");
        System.out.println("[PUSH] Para " + destinatario + ": " + mensaje);
    }
}