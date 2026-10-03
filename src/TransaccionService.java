
public class TransaccionService {
    private final ServicioGuardado serv;
    private final Notificador notificador;
    private final Auditor auditor;
    private final Comprobante comprobante;

    TransaccionService(ServicioGuardado serv, Notificador notificador, Auditor auditor, Comprobante comprobante){
        this.serv = serv;
        this.notificador = notificador;
        this.auditor = auditor;
        this.comprobante = comprobante;
    }
    private void trasferirDinero(Cuenta origen, Cuenta destino, double monto, double comision) {
        origen.retirar(monto + comision);
        destino.depositar(monto);
    }
    public void transferir (Cuenta origen, Cuenta destino, double monto, Comision calcComision) {
        if (monto <= 0)throw new IllegalArgumentException("Monto inválido");
        if (monto > 5_000_000)throw new IllegalArgumentException("Supera el tope diario");

        double comision = calcComision.calculoComision(monto);
        trasferirDinero(origen, destino, monto, comision);
        serv.guardartransaccion(origen, destino, monto, comision);
        comprobante.imprimirComprobante(monto, comision, origen, destino);
        notificador.enviarNotificacion(origen, destino, monto);
        auditor.generarLog(origen, destino, monto, calcComision);
    }
}