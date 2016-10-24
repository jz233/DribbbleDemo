package zjj.com.dribbbledemoapp.events;


public class SimpleEvent<T> {
    public T message;

    public SimpleEvent(T message) {
        this.message = message;
    }
}
