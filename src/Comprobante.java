public class Comprobante {
    public void imprimirComprobante(double monto, double comision, Cuenta origen, Cuenta destino) {
        System.out.println("===== BANCO ANDINO - COMPROBANTE =====");
        System.out.println("Origen: " + origen.getNumero());
        System.out.println("Destino: " + destino.getNumero());
        System.out.println("Monto: $" + monto);
        System.out.println("Comisión: $" + comision);
        System.out.println("======================================");
    }
}