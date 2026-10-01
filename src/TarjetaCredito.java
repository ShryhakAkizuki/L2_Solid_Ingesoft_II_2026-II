public class TarjetaCredito implements ProductoBancario {
    private double deuda;
    private final double cupo;

    public TarjetaCredito (double cupo){ this.cupo = cupo; }

    public void depositar (double monto) {} //noaplica
    public void retirar (double monto) { //avanceenefectivo
        if (deuda + monto > cupo) throw new IllegalStateException("Cupo insuficiente");
        deuda += monto;
    }
    public double calcularIntereses () { return deuda * 0.028; }
    public void pagarCuota (double monto) { deuda -= monto; }
    public String generarExtracto () { return "Tarjeta - deuda: $" + deuda; }
}