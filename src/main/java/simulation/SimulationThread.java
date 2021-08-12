package simulation;

import World.World;
import entities.Entity;
import entities.Headquarters;
import entities.IAgent;
import entities.Patrol;

import java.util.stream.Collectors;

public class SimulationThread extends Thread {

    @Override
    public void run() {
        var world = World.getInstance();
        World.getInstance().simulationStart();

        for (int i = 0; i < world.getConfig().getNumberOfPolicePatrols(); i++) {
            var HQ = world.getAllEntities().stream().filter(x -> x instanceof Headquarters).findFirst().orElse(null);
            if (HQ != null) {
                var newPatrol = new Patrol(HQ.getPosition());
                newPatrol.setState(Patrol.State.PATROLLING);
                world.addEntity(newPatrol);
            } else {
                try {
                    throw new Exception("HQ location is not defined");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        while (!world.hasSimulationDurationElapsed()) {
            if (!world.isSimulationPaused()) {
                try {
                    HQAssignTasks();
                    updateStatesOfAgents();
                    performAgentsActions();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            try {
                sleep(40);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                e.getCause();
            }
        }
    }

    private void HQAssignTasks() {
        var allHQs = World.getInstance().getAllEntities().stream().filter(x -> x instanceof Headquarters).map(x -> (Headquarters) x).collect(Collectors.toList());
        for (var hqs : allHQs) {
            hqs.assignTasks();
        }
    }

    private void updateStatesOfAgents() throws Exception {
        var allAgents = World.getInstance().getAllEntities().stream().filter(x -> x instanceof IAgent).collect(Collectors.toList());
        for (Entity agents : allAgents) {
            ((IAgent) agents).updateStateSelf();
        }
    }

    private void performAgentsActions() throws Exception {
        var allAgents = World.getInstance().getAllEntities().stream().filter(x -> x instanceof IAgent).collect(Collectors.toList());
        for (Entity agents : allAgents) {
            ((IAgent) agents).performAction();
        }
    }
}
