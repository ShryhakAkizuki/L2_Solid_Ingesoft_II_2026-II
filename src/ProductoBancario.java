interface Depositar {
    void depositar(double monto);
}

interface Retirar {
    void retirar(double monto);
}

interface CalcularIntereses {
    double calcularIntereses();
}

interface PagarCuota {
    void pagarCuota(double monto);
}

interface GenerarExtracto {
    String generarExtracto();
}

