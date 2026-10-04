public class PagoServicios {

    private final ServicioGuardado serv;
    private final Notificador notificador;
    private final Auditor auditor;
    private final Antifraude antifraude;
    private final Comprobante comprobante;


    public PagoServicios(ServicioGuardado serv, Notificador notificador,
                                Auditor auditor, Comprobante comprobante,
                                Antifraude antifraude) {
        this.serv = serv;
        this.notificador = notificador;
        this.auditor = auditor;
        this.comprobante = comprobante;
        this.antifraude = antifraude;
    }

    public void pagar(Cuenta origen, String referenciaFactura, double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        if (monto > 5_000_000) throw new IllegalArgumentException("Supera el tope diario");

        Comision comision = new ComisionServiciosPublicos();
        double valorComision = comision.calculoComision(monto);


        boolean retiroExitoso = origen.retirar(monto + valorComision);
        if (!retiroExitoso) {
            throw new IllegalStateException("La cuenta no permite retiros actualmente");
        }

        Cuenta destinoFactura = new Cuenta(referenciaFactura, "Servicios Publicos", 0);


        serv.guardartransaccion(origen, destinoFactura, monto, valorComision);
        comprobante.imprimirComprobante(monto, valorComision, origen, destinoFactura);
        auditor.generarLog(origen, destinoFactura, monto, comision);
        antifraude.generarLog(origen, destinoFactura, monto, comision);
        notificador.enviarNotificacion(origen, destinoFactura, monto);
    }
}