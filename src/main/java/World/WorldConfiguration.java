package World;

import entities.District;
import utils.Logger;

import java.util.HashMap;

public class WorldConfiguration {

    private final HashMap<District, Integer> districtsDangerLevels = new HashMap<>();
    private final HashMap<District.ThreatLevelEnum, Integer> threatLevelToMaxIncidentsPerHour = new HashMap<>() {{
        this.put(District.ThreatLevelEnum.Safe, 2);
        this.put(District.ThreatLevelEnum.RatherSafe, 4);
        this.put(District.ThreatLevelEnum.NotSafe, 7);
    }};
    private final HashMap<District.ThreatLevelEnum, Double> threatLevelToFiringChanceMap = new HashMap<>() {{
        this.put(District.ThreatLevelEnum.Safe, 0.01);
        this.put(District.ThreatLevelEnum.RatherSafe, 0.1);
        this.put(District.ThreatLevelEnum.NotSafe, 0.4);
    }};
    private String cityName;
    private int timeRate = 300;
    private long simulationDuration = 86400;
    private int numberOfPolicePatrols = 10;
    private double basicSearchDistance = 1200.0;
    private boolean drawDistrictsBorders = false;
    private boolean drawFiringDetails = false;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
        Logger.getInstance().logNewMessage("City has been set.");
    }

    public int getTimeRate() {
        return timeRate;
    }

    public void setTimeRate(int timeRate) {
        if (timeRate <= 0) {
            throw new IllegalArgumentException("Time rate must be of positive value.");
        }
        this.timeRate = timeRate;
    }

    public long getSimulationDuration() {
        return simulationDuration;
    }

    public void setSimulationDuration(long simulationDuration) {
        this.simulationDuration = simulationDuration;
    }

    public int getNumberOfPolicePatrols() {
        return numberOfPolicePatrols;
    }

    public void setNumberOfPolicePatrols(int numberOfPolicePatrols) {
        this.numberOfPolicePatrols = numberOfPolicePatrols;
    }

    public double getBasicSearchDistance() {
        return basicSearchDistance;
    }

    public void setBasicSearchDistance(double basicSearchDistance) {
        this.basicSearchDistance = basicSearchDistance;
    }

    public HashMap<District, Integer> getDistrictsDangerLevels() {
        return districtsDangerLevels;
    }

    public boolean isDrawDistrictsBorders() {
        return drawDistrictsBorders;
    }

    public void setDrawDistrictsBorders(boolean drawDistrictsBorders) {
        this.drawDistrictsBorders = drawDistrictsBorders;
    }

    public boolean isDrawFiringDetails() {
        return drawFiringDetails;
    }

    public void setDrawFiringDetails(boolean drawFiringDetails) {
        this.drawFiringDetails = drawFiringDetails;
    }

    public void resetMaxIncidentsPerHourForThreatLevel() {
        this.threatLevelToMaxIncidentsPerHour.clear();

        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.Safe, 2);
        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.RatherSafe, 4);
        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.NotSafe, 7);

        Logger.getInstance().logNewMessage("Settings for maximum number of incidents per hour for all threat levels have been reset to default values.");
    }

    public void setMaxIncidentsForThreatLevel(District.ThreatLevelEnum threatLevel, int maxIncidents) {
        if (this.threatLevelToMaxIncidentsPerHour.get(threatLevel) != maxIncidents) {
            this.threatLevelToMaxIncidentsPerHour.put(threatLevel, maxIncidents);
            Logger.getInstance().logNewMessage(String.format("Chance for firing for %s was changed to %d.", threatLevel.toString(), maxIncidents));
        }
    }

    public int getMaxIncidentForThreatLevel(District.ThreatLevelEnum threatLevel) {
        return this.threatLevelToMaxIncidentsPerHour.get(threatLevel);
    }

    public void resetFiringChanceForThreatLevel() {
        this.threatLevelToFiringChanceMap.clear();

        this.threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.Safe, 0.01);
        this.threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.RatherSafe, 0.1);
        this.threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.NotSafe, 0.4);

        Logger.getInstance().logNewMessage("Chances for firing have been reset to default values.");
    }

    public void setFiringChanceForThreatLevel(District.ThreatLevelEnum threatLevel, double chance) {
        if (this.threatLevelToFiringChanceMap.get(threatLevel) != chance) {
            this.threatLevelToFiringChanceMap.put(threatLevel, chance);
            Logger.getInstance().logNewMessage(String.format("Chance for firing for %s was changed to %f.", threatLevel.toString(), chance));
        }
    }

    public double getFiringChanceForThreatLevel(District.ThreatLevelEnum threatLevel) {
        return this.threatLevelToFiringChanceMap.get(threatLevel);
    }
}
