package especificacion;

public interface Especificacion<T> {
    boolean isSatisfiedBy(T item);
}
