package simulation;

import World.World;
import entities.Incident;

public class EventUpdater extends Thread {

    private final World world = World.getInstance();

    public EventUpdater() {
    }

    @Override
    public void run() {
        while (!world.hasSimulationDurationElapsed()) {
            if (!world.isSimulationPaused()) {
                var activeEvents = world.getEvents();
                for (var intervention : activeEvents) {
                    if (intervention.isActive()){
                        intervention.updateState();
                    }
                    else {
                        world.removeEntity((Incident)intervention);
                    }
                }
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e);
                System.out.println(e.getMessage());
                e.printStackTrace();
                e.getCause();
            }
        }
    }
}