
public class TransaccionService {
    private final OracleRepositorio repositorio = new OracleRepositorio();
    private final SmsGateway sms = new SmsGateway();

    private double calculoComision(double monto,String tipo) {
        double comision;
        switch(tipo) {
            case "MISMO_BANCO" -> comision = 0;
            case "OTRO_BANCO" -> comision = 7_500;
            case "INTERNACIONAL" -> comision = monto * 0.03 + 25_000;
            default -> throw new IllegalArgumentException("Tipo de transferencia desconocido");
        }
        return comision;
    }

    private void trasferirDinero(Cuenta origen, Cuenta destino, double monto, double comision) {
        origen.retirar(monto + comision);
        destino.depositar(monto);
    }

    public void transferir (Cuenta origen, Cuenta destino, double monto, String tipo) {
        //1. Validación
        if (monto <= 0)throw new IllegalArgumentException("Monto inválido");
        if (monto > 5_000_000)throw new IllegalArgumentException("Supera el tope diario");

        double comision = calculoComision(monto, tipo);

        trasferirDinero(origen, destino, monto, comision);

        // quiza sea mejor que solo reciba el obejeto
        ServicioGuardado servGuar = new ServicioGuardado(repositorio);
        servGuar.guardartransaccion(origen, destino, monto, comision);

        Comprobante comp = new Comprobante();
        comp.imprimirComprobante(monto, comision, origen, destino);

        // quiza sea mejor que solo reciba el objeto
        Notificador notificador = new Notificador(sms);
        notificador.enviarNotificacion(origen, destino, monto);

        Auditor auditorTransaccion = new Auditor();
        auditorTransaccion.generarLog(origen, destino, monto, tipo);
    }
}