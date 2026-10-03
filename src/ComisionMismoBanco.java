public class ComisionMismoBanco extends Comision {
    public String getType() { return "MISMO_BANCO"; };
    public double calculoComision(double monto) { return 0; };
}