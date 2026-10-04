import java.util.ArrayList;
import java.util.List;

public class RepositorioEnMemoria extends Repositorio {

    public static class RegistroTransaccion {
        public final String origen;
        public final String destino;
        public final double monto;
        public final double comision;

        public RegistroTransaccion(String origen, String destino, double monto, double comision) {
            this.origen = origen;
            this.destino = destino;
            this.monto = monto;
            this.comision = comision;
        }
    }

    public final List<RegistroTransaccion> registros = new ArrayList<>();
    public int contadorGuardado = 0;

    @Override
    public void guardarTransaccion(String origen, String destino, double monto, double comision) {
        contadorGuardado++;
        registros.add(new RegistroTransaccion(origen, destino, monto, comision));
    }
}
