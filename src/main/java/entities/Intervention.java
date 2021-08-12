package entities;

import World.World;
import entities.factories.IncidentFactory;
import org.jxmapviewer.JXMapViewer;

import java.awt.*;

public class Intervention extends Incident implements IDrawable {

    private final long duration;
    private final boolean willChangeIntoFiring;
    private final long timeToChange;
    private District district;

    private Patrol patrolSolving;

    public Intervention(double latitude, double longitude) {
        super(latitude, longitude);

        // TODO Random parameters selection
        this.duration = 500;
        this.willChangeIntoFiring = false;
        this.timeToChange = -1;
    }

    public Intervention(double latitude, double longitude, long duration, District district) {
        super(latitude, longitude);
        this.duration = duration;
        this.district = district;

        this.willChangeIntoFiring = false;
        this.timeToChange = -1;
    }

    public Intervention(double latitude, double longitude, long duration, boolean willChangeIntoFiring, long timeToChange, District district) {
        super(latitude, longitude);
        this.duration = duration;
        this.willChangeIntoFiring = willChangeIntoFiring;
        this.district = district;
        if (timeToChange < 0) {
            throw new IllegalArgumentException("timeToChange must be greater than or equal to zero");
        }
        this.timeToChange = timeToChange;
    }

    @Override
    public void updateState() {
        super.updateState();
        if (this.patrolSolving != null) {
            if (willChangeIntoFiring && patrolSolving.getAction() instanceof Patrol.IncidentParticipation && patrolSolving.getAction().startTime + this.timeToChange < World.getInstance().getSimulationTime()) {
                var firing = IncidentFactory.createRandomFiringFromIntervention(this);
                this.patrolSolving.getAction().setTarget(firing);
                World.getInstance().removeEntity(this);
                World.getInstance().addEntity(firing);
            }
            else if (patrolSolving.getAction() instanceof Patrol.IncidentParticipation && patrolSolving.getAction().startTime + this.getDuration() < World.getInstance().getSimulationTime()) {
                setActive(false);
                World.getInstance().removeEntity(this);
            }
        }
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        super.drawSelf(g, mapViewer);
        /*var oldColor = g.getColor();

        g.setColor(Color.ORANGE);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
        g.fill(mark);

        g.setColor(oldColor);*/
    }

    public Patrol getPatrolSolving() {
        return patrolSolving;
    }

    public void setPatrolSolving(Patrol patrolSolving) {
        this.patrolSolving = patrolSolving;
    }

    public long getDuration() {
        return duration;
    }

    public District getDistrict() {
        return district;
    }
}
