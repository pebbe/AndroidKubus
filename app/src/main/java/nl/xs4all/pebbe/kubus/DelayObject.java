package nl.xs4all.pebbe.kubus;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayObject implements Delayed {

    public float dh;
    public float dv;
    private long startTime;

    public DelayObject(float dh, float dv, long delay) {
        this.dh = dh;
        this.dv = dv;
        this.startTime = System.currentTimeMillis() + delay;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long left = startTime - System.currentTimeMillis();
        return unit.convert(left, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.startTime < ((DelayObject) o).startTime) {
            return -1;
        }
        if (this.startTime > ((DelayObject) o).startTime) {
            return 1;
        }
        return 0;
    }
}
