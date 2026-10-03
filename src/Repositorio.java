public abstract class Repositorio {
    public abstract void guardarTransaccion(String origen, String destino, double monto, double comision);
}