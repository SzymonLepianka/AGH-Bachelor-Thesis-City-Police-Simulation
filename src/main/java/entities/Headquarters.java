package entities;

import csv_export.ExportFiringDetails;
import world.World;
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

    private final double searchRange;
    private final double durationOfTheShift;
    private List<Incident> incidents = new ArrayList<>();
    private double endOfCurrentShift;

    public Headquarters(double latitude, double longitude) {
        super(latitude, longitude);
        this.durationOfTheShift = World.getInstance().getDurationOfTheShift();
        this.endOfCurrentShift = World.getInstance().getSimulationTime() + durationOfTheShift;
        this.searchRange = World.getInstance().getConfig().getBasicSearchDistance();
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();
        g.setColor(Color.BLUE);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2.0), (int) (point.getY() - size / 2.0), size, size);
        g.fill(mark);

        g.setColor(oldColor);
    }

    public void assignTasks() {
        checkIfTheShiftIsOver();

        updateListOfIncidents();
        var allInterventions = incidents.stream().filter(Intervention.class::isInstance).sorted(Comparator.comparingLong(Incident::getStartTime)).collect(Collectors.toList());
        var allFirings = incidents.stream().filter(Firing.class::isInstance).sorted(Comparator.comparingLong(Incident::getStartTime)).collect(Collectors.toList());

        checkAllFirings(allFirings);
        checkAllInterventions(allInterventions);
    }

    private void checkAllFirings(List<Incident> allFirings){
        for (var firing : allFirings) {
            var requiredPatrols = ((Firing) firing).getRequiredPatrols();
            var patrolsSolving = ((Firing) firing).getPatrolsSolving();
            var patrolsReaching = ((Firing) firing).getPatrolsReaching();
            if (requiredPatrols <= patrolsSolving.size()) {
                for (int i = 0; i < patrolsReaching.size(); i++) {
                    Logger.getInstance().logNewMessage(patrolsReaching.get(i) + " state set from " + patrolsReaching.get(i).getState() + " to PATROLLING");
                    patrolsReaching.get(i).setState(Patrol.State.PATROLLING);
                    ((Firing) firing).removeReachingPatrol(patrolsReaching.get(i));
                }
            }
            summonSupportForFiring((Firing) firing, patrolsSolving, patrolsReaching, requiredPatrols);
        }
    }

    private void summonSupportForFiring(Firing firing, List <Patrol> patrolsSolving, List<Patrol> patrolsReaching, int requiredPatrols){
        if (patrolsSolving.size() + patrolsReaching.size() < requiredPatrols) {
            for (int i = 1; i < 4; i++) {
                var foundPatrols = World.getInstance().getEntitiesNear(firing, searchRange * i)
                        .stream()
                        .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.PATROLLING)
                        .map(Patrol.class::cast)
                        .collect(Collectors.toList());
                giveOrdersToFoundPatrols(firing, foundPatrols);
                if (foundPatrols.size() + patrolsSolving.size() + patrolsReaching.size() >= requiredPatrols) {
                    break;
                }
            }
            if (patrolsSolving.size() + patrolsReaching.size() < requiredPatrols) {
                var foundTransferringToInterventionPatrols = World.getInstance().getEntitiesNear(firing, searchRange * 2)
                        .stream()
                        .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.TRANSFER_TO_INTERVENTION)
                        .map(Patrol.class::cast)
                        .collect(Collectors.toList());
                giveOrdersToFoundPatrols(firing, foundTransferringToInterventionPatrols);
            }
        }
    }

    private void checkAllInterventions(List<Incident> allInterventions){
        for (var intervention : allInterventions) {
            if (((Intervention) intervention).getPatrolSolving() == null) {
                Patrol availablePatrol;
                int i = 0;
                while (true) {
                    availablePatrol = World.getInstance().getEntitiesNear(intervention, searchRange * i)
                            .stream()
                            .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.PATROLLING)
                            .map(Patrol.class::cast).findFirst()
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
        if (!foundPatrols.isEmpty()) {
            ExportFiringDetails.getInstance().writeToCsvFile((Firing) firing, foundPatrols);
        }
    }

    private void updateListOfIncidents() {
        var allEntities = World.getInstance().getAllEntities();
        incidents = allEntities.stream().filter(Incident.class::isInstance).map(Incident.class::cast).collect(Collectors.toList());
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
