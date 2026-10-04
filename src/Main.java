import java.time.LocalDate;
import java.util.List;

public class Main{
    public static void main (String[]args) {
        Cuenta ana = new CuentaAhorros("001-1","Ana",2_000_000);
        Cuenta luis = new CuentaAhorros("001-2","Luis",500_000);
        Cuenta cdtAna = new CDT("CDT-9","Ana",10_000_000, LocalDate.now().plusMonths(6));
        Cuenta cuentaInfantil = new CuentaInfantil("001-3","Carlos",500_000);
        
        Notificador notificador = new Notificador(
            List.of(
                new SmsGateway(), new AppGateway()
            )
        );
        
        ServicioGuardado serv = new ServicioGuardado(new PostgresRepositorio());
        Auditor auditor = new Auditor();
        Comprobante comprobante = new Comprobante();
        Antifraude antifraude = new Antifraude();
        TransaccionService servicio = new TransaccionService(serv, notificador, auditor, comprobante, antifraude);

        Comision comision = new ComisionOtroBanco();
        servicio.transferir(ana,luis,150_000,comision);

        Comision comisionLlave = new ComisionLlave();
        // aqui estaría el código de búsqueda de llaves
        servicio.transferir(ana, luis, 50_000, comisionLlave);

        servicio.transferir(cuentaInfantil, ana, 150_000, comision);
        try {
            servicio.transferir(cuentaInfantil, ana, 60_000, comision);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        new CobroCuotaManejo().cobrarMensual(List.of(ana, luis, cdtAna));

        List<GenerarExtracto>productos=
            List.of(new TarjetaCredito(3_000_000),new CreditoVivienda(120_000_000));
        for ( GenerarExtracto p : productos) System.out.println(p.generarExtracto());

        PagoServicios servicioPagos = new PagoServicios(
                serv, notificador, auditor, comprobante, antifraude
        );
        System.out.println("\n--- INICIO DE PAGO DE SERVICIO PUBLICO ---");
        servicioPagos.pagar(ana, "AGUA-12345", 184_300);
        System.out.println("Saldo de Ana posterior al pago: $" + ana.getSaldo());
    }
}