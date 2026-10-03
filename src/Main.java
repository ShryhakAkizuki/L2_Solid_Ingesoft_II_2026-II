import java.time.LocalDate;
import java.util.List;

public class Main{
    public static void main (String[]args) {
        Cuenta ana = new CuentaAhorros("001-1","Ana",2_000_000);
        Cuenta luis = new CuentaAhorros("001-2","Luis",500_000);
        Cuenta cdtAna = new CDT("CDT-9","Ana",10_000_000, LocalDate.now().plusMonths(6));

        Notificador notificador = new Notificador(new SmsGateway());
        ServicioGuardado serv = new ServicioGuardado(new OracleRepositorio());
        Auditor auditor = new Auditor();
        Comprobante comprobante = new Comprobante();
        TransaccionService servicio = new TransaccionService(serv, notificador, auditor, comprobante);

        Comision comision = new ComisionOtroBanco();
        servicio.transferir(ana,luis,150_000,comision);

        new CobroCuotaManejo().cobrarMensual(List.of(ana, luis, cdtAna));

        List<GenerarExtracto>productos=
            List.of(new TarjetaCredito(3_000_000),new CreditoVivienda(120_000_000));
        for ( GenerarExtracto p : productos) System.out.println(p.generarExtracto());
    }
}