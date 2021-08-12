package entities.factories;

import World.World;
import entities.District;
import entities.Firing;
import entities.Intervention;

import java.util.concurrent.ThreadLocalRandom;

public class IncidentFactory {

    private static final int MIN_EVENT_DURATION = 5;
    private static final int MAX_EVENT_DURATION = 60;
    private static final int MIN_FIRING_STRENGTH = 2000;
    private static final int MAX_FIRING_STRENGTH = 5000;
    private static final World world = World.getInstance();

    public static Intervention createRandomInterventionForDistrict(District district) {
        var randomNode = district.getAllNodesInDistrict().get(ThreadLocalRandom.current().nextInt(0, district.getAllNodesInDistrict().size()));
        var latitude = randomNode.getPosition().getLatitude();
        var longitude = randomNode.getPosition().getLongitude();

        // Will change into firing
        if (ThreadLocalRandom.current().nextDouble() < ThreatLevelToFiringChance(district.getThreatLevel())) {
            var duration = ThreadLocalRandom.current().nextInt(MIN_EVENT_DURATION, MAX_EVENT_DURATION);
            var timeToChange = ThreadLocalRandom.current().nextInt(0, duration);

            return new Intervention(latitude, longitude, duration, true, timeToChange, district);
        } else {
            return new Intervention(latitude, longitude, ThreadLocalRandom.current().nextInt(MIN_EVENT_DURATION, MAX_EVENT_DURATION + 1), district);
        }
    }

    public static Firing createRandomFiringFromIntervention(Intervention intervention) {
        var strength = ThreadLocalRandom.current().nextInt(MIN_FIRING_STRENGTH, MAX_FIRING_STRENGTH + 1);
        var ceil = (int) Math.ceil(strength / 500.);
        var numberOfRequiredPatrols = ThreadLocalRandom.current().nextInt(ceil > 3 ? ceil - 2 : 1, ceil + 1);
        return new Firing(intervention.getLatitude(), intervention.getLongitude(), numberOfRequiredPatrols, strength, intervention.getDistrict());
    }

    private static double ThreatLevelToFiringChance(District.ThreatLevelEnum threatLevel) {
        return world.getConfig().getFiringChanceForThreatLevel(threatLevel);
    }

}
