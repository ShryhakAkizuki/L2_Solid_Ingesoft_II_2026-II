import java.time.LocalDate;

public class CDT extends Cuenta {
    private final LocalDate vencimiento;

    public CDT (String numero, String titular, double monto, LocalDate vencimiento) {
        super(numero, titular, monto);
        this.vencimiento = vencimiento;
    }

    @Override
    public boolean retirar (double monto) {
        retirable = !LocalDate.now().isBefore(vencimiento);
        return super.retirar(monto);
    }
}