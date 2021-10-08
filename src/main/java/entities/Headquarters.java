package entities;

import CsvExport.ExportFiringDetails;
import World.World;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import utils.Logger;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Headquarters extends Entity implements IDrawable {

    private final double range;
    private final double durationOfTheShift;
    private List<Patrol> patrols = new ArrayList<>();
    private List<Incident> incidents = new ArrayList<>();
    private double endOfCurrentShift;

    public Headquarters(double latitude, double longitude) {
        super(latitude, longitude);
        this.durationOfTheShift = World.getInstance().getDurationOfTheShift();
        this.endOfCurrentShift = World.getInstance().getSimulationTime() + durationOfTheShift;
        this.range = World.getInstance().getConfig().getBasicSearchDistance();
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();
        g.setColor(Color.BLUE);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
        g.fill(mark);

        g.setColor(oldColor);
    }

    public void assignTasks() {
        checkIfTheShiftIsOver();
        updatePatrolsAndIncidents();
        var allInterventions = incidents.stream().filter(x -> x instanceof Intervention).sorted(Comparator.comparingLong(Incident::getStartTime)).collect(Collectors.toList());
        var allFirings = incidents.stream().filter(x -> x instanceof Firing).sorted(Comparator.comparingLong(Incident::getStartTime)).collect(Collectors.toList());

        for (var firing : allFirings) {
            int requiredPatrols = ((Firing) firing).getRequiredPatrols();
            List<Patrol> patrolsSolving = ((Firing) firing).getPatrolsSolving();
            List<Patrol> patrolsReaching = ((Firing) firing).getPatrolsReaching();
            if (requiredPatrols <= patrolsSolving.size()) {
                for (int i = 0; i < patrolsReaching.size(); i++) {
                    Logger.getInstance().logNewMessage(patrolsReaching.get(i) + " state set from " + patrolsReaching.get(i).getState() + " to PATROLLING");
                    patrolsReaching.get(i).setState(Patrol.State.PATROLLING);
                    ((Firing) firing).removeReachingPatrol(patrolsReaching.get(i));
                }
            }
            if (patrolsSolving.size() + patrolsReaching.size() < requiredPatrols) {
                for (int i = 1; i < 4; i++) {
                    List<Patrol> foundPatrols = World.getInstance().getEntitiesNear(firing, range * i)
                            .stream()
                            .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.PATROLLING)
                            .map(x -> (Patrol) x)
                            .collect(Collectors.toList());
                    giveOrdersToFoundPatrols(firing, foundPatrols);
                    if (foundPatrols.size() + patrolsSolving.size() + patrolsReaching.size() >= requiredPatrols) {
                        break;
                    }
                }
                if (patrolsSolving.size() + patrolsReaching.size() < requiredPatrols) {
                    List<Patrol> foundTransferringToInterventionPatrols = World.getInstance().getEntitiesNear(firing, range * 2)
                            .stream()
                            .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.TRANSFER_TO_INTERVENTION)
                            .map(x -> (Patrol) x)
                            .collect(Collectors.toList());
                    giveOrdersToFoundPatrols(firing, foundTransferringToInterventionPatrols);
                }
            }
        }

        for (Entity intervention : allInterventions) {
            if (((Intervention) intervention).getPatrolSolving() == null) {
                Patrol availablePatrol;
                int i = 0;
                while (true) {
                    availablePatrol = World.getInstance().getEntitiesNear(intervention, range * i)
                            .stream()
                            .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.PATROLLING)
                            .map(x -> (Patrol) x).findFirst()
                            .orElse(null);
                    if (availablePatrol != null || i > 10) {
                        break;
                    }
                    i++;
                }
                if (availablePatrol != null) {
                    Logger.getInstance().logNewMessage(availablePatrol + " took order from HQ. State set from " + availablePatrol.getState() + " to TRANSFER_TO_INTERVENTION; target: " + intervention);
                    availablePatrol.takeOrder(
                            availablePatrol.new Transfer(World.getInstance().getSimulationTimeLong(),
                                    intervention, Patrol.State.TRANSFER_TO_INTERVENTION));
                    ((Intervention) intervention).setPatrolSolving(availablePatrol);
                }
            }
        }
    }

    private void giveOrdersToFoundPatrols(Incident firing, List<Patrol> foundPatrols) {
        for (Patrol p : foundPatrols) {
            Logger.getInstance().logNewMessage(p + " took order from HQ. State set from " + p.getState() + " to TRANSFER_TO_FIRING; target: " + firing);
            p.takeOrder(p.new Transfer(World.getInstance().getSimulationTimeLong(), firing, Patrol.State.TRANSFER_TO_FIRING));
            ((Firing) firing).addReachingPatrol(p);
        }
        if (foundPatrols.size() > 0) {
            ExportFiringDetails.getInstance().writeToCsvFile((Firing) firing, foundPatrols);
        }
    }

    private void updatePatrolsAndIncidents() {
        var allEntities = World.getInstance().getAllEntities();
        patrols = allEntities.stream().filter(x -> x instanceof Patrol).map(x -> (Patrol) x).collect(Collectors.toList());
        incidents = allEntities.stream().filter(x -> x instanceof Incident).map(x -> (Incident) x).collect(Collectors.toList());
    }

    private void checkIfTheShiftIsOver() {
        var world = World.getInstance();
        if (world.getSimulationTime() > endOfCurrentShift) {
            for (int i = 0; i < world.getConfig().getNumberOfPolicePatrols(); i++) {
                var newPatrol = new Patrol(this.getPosition());
                newPatrol.setState(Patrol.State.PATROLLING);
                world.addEntity(newPatrol);
            }
            endOfCurrentShift += durationOfTheShift;
            Logger.getInstance().logNewMessage("New shift has started");
        }
    }
}
