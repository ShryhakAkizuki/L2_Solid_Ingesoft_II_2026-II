public class ComisionOtroBanco extends Comision {
    public String getType() { return "OTRO_BANCO"; };
    public double calculoComision(double monto) { return 7_500; };
}