public class Cuenta {
    protected final String numero;
    protected final String titular;
    protected double saldo;
    protected boolean retirable;

    public Cuenta (String numero, String titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
        this.retirable = true;
    }

    public String getNumero  () { return numero; }
    public String getTitular () { return titular; }
    public double getSaldo   () { return saldo; }

    public void depositar (double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");
        saldo += monto;
    }

    public boolean retirar (double monto) {
        if (retirable != true) return false;
        if (monto > saldo) throw new IllegalStateException("Saldo insuficiente");

        saldo -= monto;
        return true;
    }
}