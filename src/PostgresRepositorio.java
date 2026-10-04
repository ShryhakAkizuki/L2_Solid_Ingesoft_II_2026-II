public class PostgresRepositorio extends Repositorio {
    public void guardarTransaccion(String origen, String destino, double monto, double comision) {
        System.out.println("[POSTGRES] Conectando a jdbc:postgresql:thin:@prod-db:1523/BANCO...");
        System.out.println( "[POSTGRES] INSERT INTO transacciones VALUES (’"
                            + origen + "’, ’" + destino + "’, " + monto + ", " + comision + ")");
    }
}