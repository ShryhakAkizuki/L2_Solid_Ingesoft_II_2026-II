public class ServicioGuardado {
    private final Repositorio repo;

    ServicioGuardado(Repositorio repo) { this.repo = repo; }
    public void guardartransaccion(Cuenta origen, Cuenta destino, double monto, double comision){
        repo.guardarTransaccion(origen.getNumero(), destino.getNumero(), monto, comision);
    }
}