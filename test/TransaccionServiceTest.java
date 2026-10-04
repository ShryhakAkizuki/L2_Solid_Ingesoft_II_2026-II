import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransaccionServiceTest {

    private Cuenta origen;
    private Cuenta destino;
    private RepositorioEnMemoria repositorio;
    private GatewayEnMemoria gateway;
    private TransaccionService servicio;

    @BeforeEach
    void configurarDobles() {
        origen = new CuentaAhorros("001-1", "Ana", 2_000_000);
        destino = new CuentaAhorros("001-2", "Luis", 500_000);
        repositorio = new RepositorioEnMemoria();
        gateway = new GatewayEnMemoria();
        ServicioGuardado guardado = new ServicioGuardado(repositorio);
        Notificador notificador = new Notificador(gateway);
        servicio = new TransaccionService(guardado, notificador, new Auditor(), new Comprobante());
    }

    @Test
    @DisplayName("1. Mismo banco: no cobra comision y mueve exactamente el monto")
    void Test_1() {
        double saldoOrigen = origen.getSaldo();
        double saldoDestino = destino.getSaldo();
        double monto = 150_000;

        servicio.transferir(origen, destino, monto, new ComisionMismoBanco());

        assertEquals(saldoOrigen - monto, origen.getSaldo(), 0.0001,
                "El origen debe descontar exactamente el monto (comision 0)");
        assertEquals(saldoDestino + monto, destino.getSaldo(), 0.0001,
                "El destino debe recibir exactamente el monto");
        assertEquals(1, repositorio.contadorGuardado, "Debe guardarse 1 transaccion");
        assertEquals(0, repositorio.registros.get(0).comision, 0.0001,
                "La comision guardada debe ser 0 para el mismo banco");
        assertEquals(1, gateway.contadorEnviados, "Debe notificar 1 vez");
    }

    @Test
    @DisplayName("2. Otro banco: cobra $7.500 y descuenta monto + comision del origen")
    void Test_2() {
        double saldoOrigen = origen.getSaldo();
        double saldoDestino = destino.getSaldo();
        double monto = 150_000;

        servicio.transferir(origen, destino, monto, new ComisionOtroBanco());

        assertEquals(saldoOrigen - monto, origen.getSaldo(), 7_500.0001,
                "El origen debe descontar monto + comision de $7.500");
        assertEquals(saldoDestino + monto, destino.getSaldo(), 0.0001,
                "El destino recibe el monto, no el monto + comision");
        assertEquals(1, repositorio.contadorGuardado, "Debe guardarse 1 transaccion");
        assertEquals(7_500, repositorio.registros.get(0).comision, 0.0001,
                "La comision guardada debe ser $7.500");
        assertEquals(1, gateway.contadorEnviados, "Debe notificar 1 vez");
    }

    @Test
    @DisplayName("3. Saldo insuficiente: se rechaza, no se guarda ni se notifica")
    void Test_3() {
        double saldoOrigen = origen.getSaldo();
        double saldoDestino = destino.getSaldo();

        assertThrows(IllegalStateException.class,
                () -> servicio.transferir(origen, destino, 3_000_000, new ComisionMismoBanco()),
                "El retiro debe rechazarse cuando el saldo no alcanza");

        assertEquals(saldoOrigen, origen.getSaldo(), 0.0001, "El saldo del origen no cambia");
        assertEquals(saldoDestino, destino.getSaldo(), 0.0001, "El saldo del destino no cambia");
        assertEquals(0, repositorio.contadorGuardado, "No debe guardarse ninguna transaccion");
        assertEquals(0, gateway.contadorEnviados, "No debe enviarse ninguna notificacion");
    }

    @Test
    @DisplayName("4. Transferencia exitosa: se guarda una sola vez y una sola notificacion")
    void Test_4() {
        double monto = 100_000;

        servicio.transferir(origen, destino, monto, new ComisionOtroBanco());

        assertEquals(1, repositorio.contadorGuardado, "Debe guardarse exactamente una vez");
        assertEquals(1, gateway.contadorEnviados, "Debe generarse exactamente una notificacion");

        RepositorioEnMemoria.RegistroTransaccion r = repositorio.registros.get(0);
        assertEquals(origen.getNumero(), r.origen);
        assertEquals(destino.getNumero(), r.destino);
        assertEquals(monto, r.monto, 0.0001);
        assertEquals(7_500, r.comision, 0.0001);

        GatewayEnMemoria.Mensaje m = gateway.mensajes.get(0);
        assertEquals(origen.getTitular(), m.destinatario, "La notificacion va al titular del origen");
        assertTrue(m.mensaje.contains(destino.getNumero()), "El mensaje menciona la cuenta destino");
    }

    @Test
    @DisplayName("5. Tipo de comision desconocido: se rechaza y el saldo no cambia")
    void Test_5() {
        double saldoOrigen = origen.getSaldo();
        double saldoDestino = destino.getSaldo();

        assertThrows(IllegalStateException.class,
                () -> servicio.transferir(origen, destino, 100_000, new ComisionDesconocida()),
                "Un tipo de comision que no puede calcularse debe rechazarse");

        assertEquals(saldoOrigen, origen.getSaldo(), 0.0001, "El saldo del origen no cambia");
        assertEquals(saldoDestino, destino.getSaldo(), 0.0001, "El saldo del destino no cambia");
        assertEquals(0, repositorio.contadorGuardado, "No debe guardarse ninguna transaccion");
        assertEquals(0, gateway.contadorEnviados, "No debe enviarse ninguna notificacion");
    }
}
