package utils;

import world.World;

public class DelayedActionWithTargetSimulationTime extends Thread {

    public interface Thunk { void apply(); }

    private long targetSimulationTime;
    private World world = World.getInstance();
    private Thunk function;

    public DelayedActionWithTargetSimulationTime(long targetSimulationTime, Thunk function) {
        this.targetSimulationTime = targetSimulationTime;
        this.function = function;
    }

    public DelayedActionWithTargetSimulationTime(int delay, Thunk function) {
        this(World.getInstance().getSimulationTimeLong() + delay, function);
    }

    @Override
    public void run() {
        try {
            while (world.getSimulationTimeLong() < targetSimulationTime) {
                var sleepTime = ((targetSimulationTime - world.getSimulationTimeLong()) * 1000) / world.getConfig().getTimeRate();
                Thread.sleep(sleepTime, 0);
            }
        } catch (InterruptedException e) {
            // Ignore
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        function.apply();
    }
}
