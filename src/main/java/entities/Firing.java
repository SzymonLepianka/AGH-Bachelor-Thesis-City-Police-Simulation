package entities;

import World.World;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class Firing extends Incident implements IDrawable {

    private final int requiredPatrols;
    private double strength;
    private List<Patrol> patrolsSolving = new ArrayList<>();
    private List<Patrol> patrolsReaching = new ArrayList<>();
    private District district;

    public Firing(double latitude, double longitude) {
        super(latitude, longitude);

        // TODO Parameters random selection
        this.requiredPatrols = 2;
        this.strength = 500;
    }

    public Firing(double latitude, double longitude, int requiredPatrols, double initialStrength, District district) {
        super(latitude, longitude);
        this.requiredPatrols = requiredPatrols;
        this.strength = initialStrength;
        this.district = district;
    }

    public int getRequiredPatrols() {
        return requiredPatrols;
    }

    public List<Patrol> getPatrolsSolving() {
        return patrolsSolving;
    }

    public List<Patrol> getPatrolsReaching() {
        return patrolsReaching;
    }

    public void addReachingPatrol(Patrol patrol) {
        patrolsReaching.add(patrol);
    }

    public void removeReachingPatrol(Patrol patrol) {
        patrolsReaching.remove(patrol);
    }

    public void addSolvingPatrol(Patrol patrol) {
        patrolsSolving.add(patrol);
    }

    public void removeSolvingPatrol(Patrol patrol) {
        patrolsSolving.remove(patrol);
    }

    public double getStrength() {
        return strength;
    }

    public District getDistrict() {
        return district;
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();

        g.setColor(Color.BLACK);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
        g.fill(mark);

        if (World.getInstance().getConfig().isDrawFiringDetails()) {
            g.drawString(String.format("Stren.:%.1f", strength), (int) point.getX() + 5, (int) point.getY());
            g.drawString(String.format("Patr.Req.:%d", requiredPatrols), (int) point.getX() + 5, (int) point.getY() - 10);
            g.drawString(String.format("Patr.Reach.:%d", patrolsReaching.size()), (int) point.getX() + 5, (int) point.getY() - 20);
            g.drawString(String.format("Part.Solv.:%d", patrolsSolving.size()), (int) point.getX() + 5, (int) point.getY() - 30);
        }

        g.setColor(oldColor);
    }

    @Override
    public void updateState() {
//        super.updateState();
        // TODO improve the calculation of loss of strength
        this.strength -= patrolsSolving.size() * (World.getInstance().getSimulationTime() - timeOfLastUpdate);
        timeOfLastUpdate = World.getInstance().getSimulationTime();
        if (this.strength <= 0) {
            setActive(false);
            World.getInstance().removeEntity(this);
            for (var p : patrolsSolving) {
                p.setState(Patrol.State.PATROLLING);
            }
        }
    }
}
