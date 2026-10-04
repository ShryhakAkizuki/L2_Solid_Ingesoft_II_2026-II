import java.time.LocalDate;

public class CuentaInfantil extends Cuenta {
    private static final double DAILY_LIMIT = 200_000;
    private double dailyLimitRemaining = DAILY_LIMIT;
    private LocalDate limitDate = LocalDate.now();

    public CuentaInfantil (String numero, String titular, double saldoInicial) {
        super(numero,titular,saldoInicial);
    }
    private void checkDailyLimit() {
        LocalDate today = LocalDate.now();
        if (!today.equals(limitDate)) {
            dailyLimitRemaining = DAILY_LIMIT;
            limitDate = today;
        }
    }
    @Override
    public boolean retirar (double monto) {
        if (retirable != true) return false;
        if (monto > saldo) throw new IllegalStateException("Saldo insuficiente");

        checkDailyLimit();
        if (monto > dailyLimitRemaining) {
            throw new IllegalStateException("Supera el tope diario");
        }

        saldo -= monto;
        dailyLimitRemaining -= monto;
        return true;
    }
}