package world;

import entities.District;
import utils.Logger;

import java.util.EnumMap;
import java.util.HashMap;


public class WorldConfiguration {

    private final java.util.Map<District, Integer> districtsDangerLevels = new HashMap<>();
    private final EnumMap<District.ThreatLevelEnum, Integer> threatLevelToMaxIncidentsPerHour = new EnumMap<>(District.ThreatLevelEnum.class);
    private final EnumMap<District.ThreatLevelEnum, Double> threatLevelToFiringChanceMap = new EnumMap<>(District.ThreatLevelEnum.class);
    private String cityName;
    private int timeRate = 300;
    private long simulationDuration = 86400;
    private int numberOfPolicePatrols = 25;
    private double basicSearchDistance = 1200.0;
    private boolean drawDistrictsBorders = false;
    private boolean drawFiringDetails = false;
    private boolean drawLegend = false;
    private boolean drawInterventionDetails = false;
    private int minimumInterventionDuration = 10; // minutes
    private int maximumInterventionDuration = 30; // minutes
    private int minimumFiringStrength = 30;
    private int maximumFiringStrength = 90;
    private int basePatrollingSpeed = 40;
    private int baseTransferSpeed = 60;
    private int basePrivilegedSpeed = 80;

    WorldConfiguration() {
        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.SAFE, 1);
        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.RATHER_SAFE, 2);
        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.NOT_SAFE, 4);
        threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.SAFE, 0.01);
        threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.RATHER_SAFE, 0.1);
        threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.NOT_SAFE, 0.4);
    }

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

    public java.util.Map<District, Integer> getDistrictsDangerLevels() {
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

    public boolean isDrawLegend() {
        return drawLegend;
    }

    public void setDrawLegend(boolean drawLegend) {
        this.drawLegend = drawLegend;
    }

    public boolean isDrawInterventionDetails() {
        return drawInterventionDetails;
    }

    public void setDrawInterventionDetails(boolean drawInterventionDetails) {
        this.drawInterventionDetails = drawInterventionDetails;
    }

    public int getMinimumInterventionDuration() {
        return minimumInterventionDuration;
    }

    public void setMinimumInterventionDuration(int minimumInterventionDuration) {
        this.minimumInterventionDuration = minimumInterventionDuration;
    }

    public int getMaximumInterventionDuration() {
        return maximumInterventionDuration;
    }

    public void setMaximumInterventionDuration(int maximumInterventionDuration) {
        this.maximumInterventionDuration = maximumInterventionDuration;
    }

    public int getMinimumFiringStrength() {
        return minimumFiringStrength;
    }

    public void setMinimumFiringStrength(int minimumFiringStrength) {
        this.minimumFiringStrength = minimumFiringStrength;
    }

    public int getMaximumFiringStrength() {
        return maximumFiringStrength;
    }

    public void setMaximumFiringStrength(int maximumFiringStrength) {
        this.maximumFiringStrength = maximumFiringStrength;
    }

    public int getBasePatrollingSpeed() {
        return basePatrollingSpeed;
    }

    public void setBasePatrollingSpeed(int basePatrollingSpeed) {
        this.basePatrollingSpeed = basePatrollingSpeed;
    }

    public int getBaseTransferSpeed() {
        return baseTransferSpeed;
    }

    public void setBaseTransferSpeed(int baseTransferSpeed) {
        this.baseTransferSpeed = baseTransferSpeed;
    }

    public int getBasePrivilegedSpeed() {
        return basePrivilegedSpeed;
    }

    public void setBasePrivilegedSpeed(int basePrivilegedSpeed) {
        this.basePrivilegedSpeed = basePrivilegedSpeed;
    }

    public void resetMaxIncidentsPerHourForThreatLevel() {
        this.threatLevelToMaxIncidentsPerHour.clear();

        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.SAFE, 2);
        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.RATHER_SAFE, 4);
        threatLevelToMaxIncidentsPerHour.put(District.ThreatLevelEnum.NOT_SAFE, 7);

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

        this.threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.SAFE, 0.01);
        this.threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.RATHER_SAFE, 0.1);
        this.threatLevelToFiringChanceMap.put(District.ThreatLevelEnum.NOT_SAFE, 0.4);

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
