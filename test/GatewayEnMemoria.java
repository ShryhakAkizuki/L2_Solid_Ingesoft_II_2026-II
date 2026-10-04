import java.util.ArrayList;
import java.util.List;

public class GatewayEnMemoria extends Gateway {

    public static class Mensaje {
        public final String destinatario;
        public final String mensaje;

        public Mensaje(String destinatario, String mensaje) {
            this.destinatario = destinatario;
            this.mensaje = mensaje;
        }
    }

    public final List<Mensaje> mensajes = new ArrayList<>();
    public int contadorEnviados = 0;

    @Override
    public void enviar(String destinatario, String mensaje) {
        contadorEnviados++;
        mensajes.add(new Mensaje(destinatario, mensaje));
    }
}
