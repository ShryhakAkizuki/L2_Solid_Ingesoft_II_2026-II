public class ServicioGuardado {
    // se pude generlizar a interfaz repositorio 
    private final OracleRepositorio repo;

    ServicioGuardado(OracleRepositorio repo) {
        this.repo = repo; 
    }

    public void guardartransaccion(Cuenta origen, Cuenta destino, double monto, double comision){
        repo.guardarTransaccion(origen.getNumero(), destino.getNumero(), monto, comision);
    }
}