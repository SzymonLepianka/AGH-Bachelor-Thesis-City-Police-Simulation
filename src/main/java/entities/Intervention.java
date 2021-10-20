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
            drawString(g,(int) point.getX() + 5, (int) point.getY(),"Duration:" + duration / 60 + " [minutes]");
            drawString(g,(int) point.getX() + 5, (int) point.getY() - 15,"Will change into firing: " + willChangeIntoFiring);
            if (this.patrolSolving != null) {
                if (patrolSolving.getAction() instanceof Patrol.IncidentParticipation){
                    var timeLeft = duration - (World.getInstance().getSimulationTime() - patrolSolving.getAction().startTime);

                    drawString(g,(int) point.getX() + 5, (int) point.getY() - 30, String.format("Time left: %.2f [minutes]", timeLeft / 60));
                } else{
                    drawString(g,(int) point.getX() + 5, (int) point.getY() - 30, "Patrol is on its way to the intervention.");
                }
            }
        }
    }

    private void drawString(Graphics2D g, int x, int y, String str1){

        var oldColor = g.getColor();

        Color bgColor = new Color(1,1,1,0.6f );
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(str1, g);
        g.setColor(bgColor);
        g.fillRect(x,
                y - fm.getAscent() + 2,
                (int) rect.getWidth(),
                (int) rect.getHeight());
        g.setColor(oldColor);
        g.drawString(str1, x, y);
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
