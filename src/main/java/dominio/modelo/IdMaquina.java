package dominio.modelo;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una Máquina.
 * Garantiza inmutabilidad y validaciones sobre cómo se identifica un dispositivo en el sistema.
 */
public final class IdMaquina {
    private final String valor;

    public IdMaquina(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la máquina no puede ser nulo o vacío.");
        }
        this.valor = valor;
    }

    public static IdMaquina generar() {
        return new IdMaquina(UUID.randomUUID().toString());
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdMaquina that = (IdMaquina) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return "IdMaquina{" +
                "valor='" + valor + '\'' +
                '}';
    }
}
