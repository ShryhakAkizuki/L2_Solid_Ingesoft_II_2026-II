public class ComisionInternacional extends Comision {
    public String getType() { return "INTERNACIONAL"; };
    public double calculoComision(double monto) { return monto * 0.03 + 25_000; };
}