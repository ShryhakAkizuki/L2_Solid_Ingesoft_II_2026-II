public class ComisionServiciosPublicos extends Comision {
    @Override
    public String getType() {
        return "PAGO_SERVICIOS";
    }

    @Override
    public double calculoComision(double monto) {
        return 1500.0;
    }
}