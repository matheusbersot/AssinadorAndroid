package br.uff.assinador.asyncTask;

/**
 * Created by matheus on 08/09/15.
 */
public class Resultado<T> {
    private T t;
    private Exception e;

    public Resultado()
    {
        t = null;
        e = null;
    }

    public void setException(Exception e) { this.e = e; }
    public Exception getException() { return e; }

    public void set(T t) { this.t = t; }
    public T get() { return t; }
}
