public class CreditoVivienda implements ProductoBancario {
    private double saldoPendiente;

    public CreditoVivienda (double valorPrestamo) { this.saldoPendiente = valorPrestamo; }

    public void depositar (double monto) {} //noaplica
    public void retirar (double monto) {} //noaplica
    public double calcularIntereses () { return saldoPendiente * 0.011; }
    public void pagarCuota (double monto) { saldoPendiente -= monto; }
    public String generarExtracto () { return "Crédito vivienda - pendiente: $" + saldoPendiente; }
}