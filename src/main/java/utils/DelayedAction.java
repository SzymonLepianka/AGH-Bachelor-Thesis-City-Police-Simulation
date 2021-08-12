package utils;

import java.lang.reflect.Method;
import java.util.function.Function;

public class DelayedAction extends Thread {

    public interface Thunk { void apply(); }

    private long delayMillis = 0;
    private int delayNanos = 0;
    private Thunk function;

    public DelayedAction(long delayMillis, int delayNanos, Thunk function) {
        this.delayMillis = delayMillis;
        this.delayNanos = delayNanos;
        this.function = function;
    }

    public DelayedAction(long delayMillis, Thunk function) {
        this(delayMillis, 0, function);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delayMillis, delayNanos);
        } catch (InterruptedException e) {
            // Ignore
            e.printStackTrace();
        }
        function.apply();
    }

}
