package CsvExport;

import World.World;
import com.opencsv.CSVWriter;
import entities.Firing;
import entities.Patrol;
import utils.Haversine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

public class ExportFiringDetails {

    private static volatile ExportFiringDetails instance;
    private final World world = World.getInstance();
    private final File firingsDetailsCsvFile;
    private final String csvDirectoryPath = "results";
    private final String[] firingDetailsHeader = new String[]{
            "simulationTime",
            "firingID",
            "districtName",
            "districtSafetyLevel",
            "requiredPatrols",
            "solvingPatrols",
            "reachingPatrols(including 'called')",
            "calledPatrols",
            "totalDistanceOfCalledPatrols"
    };
    private final DateTimeFormatter dateFormat = new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy_HH-mm-ss").toFormatter();

    private ExportFiringDetails() {
        File csvDirectory = new File(csvDirectoryPath);
        if (!(csvDirectory.exists() && csvDirectory.isDirectory())) {
            csvDirectory.mkdir();
        }

        firingsDetailsCsvFile = new File(csvDirectoryPath + "/" + dateFormat.format(LocalDateTime.now()) + "--Firings Details.csv");
        try {
            firingsDetailsCsvFile.createNewFile();
            var csvWriter1 = new CSVWriter(new FileWriter(firingsDetailsCsvFile));
            csvWriter1.writeNext(firingDetailsHeader);
            csvWriter1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExportFiringDetails getInstance() {
        // Result variable here may seem pointless, but it's needed for DCL (Double-checked locking).
        var result = instance;
        if (instance != null) {
            return result;
        }
        synchronized (ExportFiringDetails.class) {
            if (instance == null) {
                instance = new ExportFiringDetails();
            }
            return instance;
        }
    }

    public void writeToCsvFile(Firing firing, List<Patrol> calledPatrols) {
        var simulationTimeLong = world.getSimulationTimeLong();
        try {
            writeToFiringsDetailsCsvFile(simulationTimeLong, firing, calledPatrols);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFiringsDetailsCsvFile(long simulationTimeLong, Firing firing, List<Patrol> calledPatrols) throws IOException {
        var csvWriter = new CSVWriter(new FileWriter(firingsDetailsCsvFile, true));
        csvWriter.writeNext(new String[]{
                String.valueOf(simulationTimeLong),
                String.valueOf(firing.getUniqueID()),
                firing.getDistrict().getName(),
                String.valueOf(firing.getDistrict().getThreatLevel()),
                String.valueOf(firing.getRequiredPatrols()),
                String.valueOf(firing.getPatrolsSolving().size()),
                String.valueOf(firing.getPatrolsReaching().size()),
                String.valueOf(calledPatrols.size()),
                String.valueOf(totalDistanceOfPatrols(firing, calledPatrols))
        }, false);
        csvWriter.close();
    }

    private double totalDistanceOfPatrols(Firing firing, List<Patrol> calledPatrols) {
        double distance = 0;
        for (var patrol : calledPatrols
        ) {
            distance += Haversine.distance(firing.getLatitude(), firing.getLongitude(), patrol.getLatitude(), patrol.getLongitude());
        }
        return distance;
    }
}

