package CsvExport;

import World.World;
import com.opencsv.CSVWriter;
import entities.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.stream.Collectors;

public class ExportToCSV extends Thread {

    private final World world = World.getInstance();
    private final File simulationDetailsCsvFile;
    private final File districtsDetailsCsvFile;
    private final String csvDirectoryPath = "results";
    private final String[] simulationDetailsHeader = new String[]{
            "simulationTime",
            "amountOfPatrols",
            "amountOfPatrollingPatrols",
            "amountOfCalculatingPathPatrols",
            "amountOfTransferToInterventionPatrols",
            "amountOfTransferToFiringPatrols",
            "amountOfInterventionPatrols",
            "amountOfFiringPatrols",
            "amountOfNeutralizedPatrols",
            "amountOfReturningToHqPatrols",
            "amountOfIncidents",
            "amountOfInterventions",
            "amountOfInterventionsBeingSolved",
            "amountOfFirings",
            "amountOfFiringBeingSolved"
    };
    private final String[] districtsDetailsHeader = new String[]{
            "simulationTime",
            "districtName",
            "districtSafetyLevel",
            "amountOfPatrols",
            "amountOfPatrollingPatrols",
            "amountOfCalculatingPathPatrols",
            "amountOfTransferToInterventionPatrols",
            "amountOfTransferToFiringPatrols",
            "amountOfInterventionPatrols",
            "amountOfFiringPatrols",
            "amountOfReturningToHqPatrols",
            "amountOfIncidents"
    };

    private final DateTimeFormatter dateFormat = new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy_HH-mm-ss").toFormatter();
    private int exportCounter = 1;

    public ExportToCSV() {
        File csvDirectory = new File(csvDirectoryPath);
        if (!(csvDirectory.exists() && csvDirectory.isDirectory())) {
            csvDirectory.mkdir();
        }

        simulationDetailsCsvFile = new File(csvDirectoryPath + "/" + dateFormat.format(LocalDateTime.now()) + "--Simulation Details.csv");
        districtsDetailsCsvFile = new File(csvDirectoryPath + "/" + dateFormat.format(LocalDateTime.now()) + "--Districts Details.csv");
        try {
            simulationDetailsCsvFile.createNewFile();
            CSVWriter csvWriter1 = new CSVWriter(new FileWriter(simulationDetailsCsvFile));
            csvWriter1.writeNext(simulationDetailsHeader);
            csvWriter1.close();

            districtsDetailsCsvFile.createNewFile();
            CSVWriter csvWriter2 = new CSVWriter(new FileWriter(districtsDetailsCsvFile));
            csvWriter2.writeNext(districtsDetailsHeader);
            csvWriter2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!world.hasSimulationDurationElapsed()) {
            if (!world.isSimulationPaused() && exportCounter <= (world.getSimulationTimeLong() / 600)) {
                exportCounter++;
                var allEntities = world.getAllEntities();
                var allPatrols = allEntities.stream()
                        .filter(x -> x instanceof Patrol)
                        .map(x -> (Patrol) x)
                        .collect(Collectors.toList());
                var allIncidents = allEntities.stream()
                        .filter(x -> x instanceof Incident && ((Incident) x).isActive())
                        .map(x -> (Incident) x)
                        .collect(Collectors.toList());
                var simulationTimeLong = world.getSimulationTimeLong();

                try {
                    writeToSimulationDetailsCsvFile(simulationTimeLong, allPatrols, allIncidents);
                    writeToDistrictsDetailsCsvFile(simulationTimeLong, allPatrols, allIncidents);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // sleep for next 10 minutes in simulation time
                var sleepTime = ((600 - (world.getSimulationTime() % 600)) * 1000) / world.getConfig().getTimeRate();
                try {
                    sleep((long) sleepTime, (int) ((sleepTime - (long) sleepTime) * 1000000));
                } catch (InterruptedException e) {
                    // Ignore
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeToSimulationDetailsCsvFile(long simulationTimeLong, List<Patrol> allPatrols, List<Incident> allIncidents) throws IOException {
        var csvWriter = new CSVWriter(new FileWriter(simulationDetailsCsvFile, true));
        csvWriter.writeNext(new String[]{
                String.valueOf(simulationTimeLong),
                String.valueOf(allPatrols.size()),
                String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.PATROLLING).count()),
                String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.CALCULATING_PATH).count()),
                String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.TRANSFER_TO_INTERVENTION).count()),
                String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.TRANSFER_TO_FIRING).count()),
                String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.INTERVENTION).count()),
                String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.FIRING).count()),
                String.valueOf(world.getNeutralizedPatrolsTotal() + allPatrols.stream().filter(x -> x.getState() == Patrol.State.NEUTRALIZED).count()),
                String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.RETURNING_TO_HQ).count()),
                String.valueOf(allIncidents.size()),
                String.valueOf(allIncidents.stream().filter(x -> x instanceof Intervention).count()),
                String.valueOf(allIncidents.stream().filter(x -> x instanceof Intervention && ((Intervention) x).getPatrolSolving() != null).count()),
                String.valueOf(allIncidents.stream().filter(x -> x instanceof Firing).count()),
                String.valueOf(allIncidents.stream().filter(x -> x instanceof Firing && ((Firing) x).getPatrolsSolving().size() > 0).count())
        }, false);
        csvWriter.close();
    }

    private void writeToDistrictsDetailsCsvFile(long simulationTimeLong, List<Patrol> allPatrols, List<Incident> allIncidents) throws IOException {
        var districts = world.getDistricts();
        var csvWriter = new CSVWriter(new FileWriter(districtsDetailsCsvFile, true));
        for (District d : districts) {
            var allPatrolsInDistrict = allPatrols.stream().filter(x -> d.contains(x.getPosition())).collect(Collectors.toList());
            csvWriter.writeNext(new String[]{
                    String.valueOf(simulationTimeLong),
                    d.getName(),
                    String.valueOf(d.getThreatLevel()),
                    String.valueOf(allPatrolsInDistrict.size()),
                    String.valueOf(allPatrolsInDistrict.stream().filter(x -> x.getState() == Patrol.State.PATROLLING).count()),
                    String.valueOf(allPatrolsInDistrict.stream().filter(x -> x.getState() == Patrol.State.CALCULATING_PATH).count()),
                    String.valueOf(allPatrolsInDistrict.stream().filter(x -> x.getState() == Patrol.State.TRANSFER_TO_INTERVENTION).count()),
                    String.valueOf(allPatrolsInDistrict.stream().filter(x -> x.getState() == Patrol.State.TRANSFER_TO_FIRING).count()),
                    String.valueOf(allPatrolsInDistrict.stream().filter(x -> x.getState() == Patrol.State.INTERVENTION).count()),
                    String.valueOf(allPatrolsInDistrict.stream().filter(x -> x.getState() == Patrol.State.FIRING).count()),
                    String.valueOf(allPatrolsInDistrict.stream().filter(x -> x.getState() == Patrol.State.RETURNING_TO_HQ).count()),
                    String.valueOf(allIncidents.stream().filter(x -> d.contains(x.getPosition())).count())
            }, false);
        }
        csvWriter.close();
    }
}
