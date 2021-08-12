package utils;

import World.World;
import guiComponents.LoggerPanel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

// TODO Add debug logger
public class Logger {

    private static volatile Logger instance;

    public static Logger getInstance() {
        // Result variable here may seem pointless, but it's needed for DCL (Double-checked locking).
        var result = instance;
        if (instance != null) {
            return  result;
        }
        synchronized (Logger.class) {
            if (instance == null) {
                instance = new Logger();
            }
            return instance;
        }
    }

    private final File logFile;
    private final File logsDirectory;
    private final String logsDirectoryPath = "logs";
    private final List<LoggerPanel> loggingPanels = new ArrayList<>();
    private final DateTimeFormatter dateFormat = new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy_HH-mm-ss").toFormatter();

    private Logger() {
        logsDirectory = new File(logsDirectoryPath);
        if (!(logsDirectory.exists() && logsDirectory.isDirectory())) {
            logsDirectory.mkdir();
        }

        logFile = new File(logsDirectoryPath + "/" + dateFormat.format(LocalDateTime.now()) + ".log");
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addLoggingPanel(LoggerPanel panel) {
        return loggingPanels.add(panel);
    }

    public boolean removeLoggingPanel(LoggerPanel panel) {
        return loggingPanels.remove(panel);
    }

    public void clearLoggingPanels() {
        loggingPanels.clear();
    }

    public void logNewMessage(String message) {
        var realDate = LocalDateTime.now();
        var simulationTime = World.getInstance().getSimulationTimeLong();

        var messageBuilder = new StringBuilder();
        messageBuilder.append(realDate.format(dateFormat));
        messageBuilder.append(" ");
        messageBuilder.append(simulationTime);
        messageBuilder.append(" ");
        messageBuilder.append(message);
        messageBuilder.append("\n");
        // TODO Change to FileWriter in the future
        try {
            Files.write(logFile.toPath(), messageBuilder.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(var loggerPanel : loggingPanels) {
            loggerPanel.showNewLogMessage(message, realDate, simulationTime);
        }
    }

}
