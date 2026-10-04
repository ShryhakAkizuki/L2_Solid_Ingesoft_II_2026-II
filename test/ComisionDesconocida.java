public class ComisionDesconocida extends Comision {

    @Override
    public String getType() {
        return "DESECONOCIDO";
    }

    @Override
    public double calculoComision(double monto) {
        throw new IllegalStateException("Tipo de comision desconocido: " + getType());
    }
}
