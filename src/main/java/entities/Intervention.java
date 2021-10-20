package entities;

import World.World;
import entities.factories.IncidentFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Intervention extends Incident implements IDrawable {

    private final long duration;
    private final boolean willChangeIntoFiring;
    private final long timeToChange;
    private District district;

    private Patrol patrolSolving;

    public Intervention(double latitude, double longitude) {
        super(latitude, longitude);

        // the default intervention duration is the middle of the values entered by the user in the configuration
        var config = World.getInstance().getConfig();
        this.duration = config.getMinimumInterventionDuration()+(config.getMaximumInterventionDuration() - config.getMinimumInterventionDuration()) / 2;
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
            } else if (patrolSolving.getAction() instanceof Patrol.IncidentParticipation && patrolSolving.getAction().startTime + this.getDuration() < World.getInstance().getSimulationTime()) {
                setActive(false);
                World.getInstance().removeEntity(this);
            }
        }
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        super.drawSelf(g, mapViewer);
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));
        if (World.getInstance().isSimulationPaused()) {
            g.drawString(String.format("Duration:" + duration), (int) point.getX() + 5, (int) point.getY());
            g.drawString(String.format("WCIF:" + willChangeIntoFiring), (int) point.getX() + 5, (int) point.getY() - 10);
//            g.drawString(String.format("Patr.Reach.:%d", patrolsReaching.size()), (int) point.getX() + 5, (int) point.getY() - 20);
//            g.drawString(String.format("Part.Solv.:%d", patrolsSolving.size()), (int) point.getX() + 5, (int) point.getY() - 30);
        }


//        final var size = 10;
//
//        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
//        g.fill(mark);
//
//        g.setColor(oldColor);*/
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
