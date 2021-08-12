package guiComponents;


import OSMToGraph.ImportGraphFromRawData;
import World.*;
import entities.District;
import utils.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigurationPanel {

    private JFrame frame = new JFrame("City Police Simulation");

    private JPanel citySelectionPanel;
    private JPanel districtConfigurationPanel;
    private JPanel simulationConfigurationPanel;
    private JPanel buttonsPanel;

    private final int textInputColumns = 20;
    // TODO Change into dynamically generated
    private final HashMap<String, String[]> availablePlaces = new HashMap<>();

    private JComboBox<String> countrySelectionComboBox;
    private JComboBox<String> citySelectionComboBox;
    private final JTextField numberOfCityPatrolsTextField = new JTextField();
    private final JTextField timeRateTextField = new JTextField();
    private final JTextField simulationDurationDaysTextField = new JTextField();
    private final JTextField simulationDurationHoursTextField = new JTextField();
    private final JTextField simulationDurationMinutesTextField = new JTextField();
    private final JTextField simulationDurationSecondsTextField = new JTextField();
    private final JCheckBox drawDistrictsBoundariesCheckBox = new JCheckBox();
    private final JCheckBox drawFiringDetailsCheckBox = new JCheckBox();

    private final JTextField threatLevelMaxIncidentsTextField_SAFE = new JTextField();
    private final JTextField threatLevelMaxIncidentsTextField_RATHERSAFE = new JTextField();
    private final JTextField threatLevelMaxIncidentsTextField_NOTSAFE = new JTextField();

    private final JTextField threatLevelFiringChanceTextField_SAFE = new JTextField();
    private final JTextField threatLevelFiringChanceTextField_RATHERSAFE = new JTextField();
    private final JTextField threatLevelFiringChanceTextField_NOTSAFE = new JTextField();

    public ConfigurationPanel() {
        availablePlaces.put("Poland", new String[]{"Kraków", "Warszawa", "Rzeszów", "Katowice", "Gdańsk", "Łódź", "Szczecin", "Poznań", "Lublin", "Białystok", "Wrocław"});
    }

    private void setDurationInputs(long time) {
        var days = time / 86400;
        var hours = (time % 86400)/3600;
        var minutes = (time % 3600)/60;
        var seconds = time % 60;

        simulationDurationDaysTextField.setText(String.valueOf(days));
        simulationDurationHoursTextField.setText(String.valueOf(hours));
        simulationDurationMinutesTextField.setText(String.valueOf(minutes));
        simulationDurationSecondsTextField.setText(String.valueOf(seconds));
    }

    private long getDurationFromInputs() {
        var days = simulationDurationDaysTextField.getText().equals("") ? 0 : Long.parseLong(simulationDurationDaysTextField.getText());
        var hours = simulationDurationHoursTextField.getText().equals("") ? 0 : Long.parseLong(simulationDurationHoursTextField.getText());
        var minutes = simulationDurationMinutesTextField.getText().equals("") ? 0 : Long.parseLong(simulationDurationMinutesTextField.getText());
        var seconds = simulationDurationSecondsTextField.getText().equals("") ? 0 : Long.parseLong(simulationDurationSecondsTextField.getText());
        return seconds + minutes*60 + hours*3600 + days*86400;
    }

    private void setDefaultValues() {
        var worldConfig = World.getInstance().getConfig();
        numberOfCityPatrolsTextField.setText(Integer.toString(worldConfig.getNumberOfPolicePatrols()));
        timeRateTextField.setText(Integer.toString(worldConfig.getTimeRate()));
        setDurationInputs(worldConfig.getSimulationDuration());
    }

    // TODO Add validation for input data
    public void createWindow(){
        frame.setSize( 1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.setLayout(new GridLayout(1,4));

        citySelectionPanel = new JPanel();
        frame.add(citySelectionPanel);

        countrySelectionComboBox = new JComboBox<>(availablePlaces.keySet().toArray(new String[0]));
        countrySelectionComboBox.addActionListener(e -> {
            var selectedItem = countrySelectionComboBox.getSelectedItem().toString();
            var newModel = new DefaultComboBoxModel<>(availablePlaces.get(selectedItem));
            citySelectionComboBox.setModel(newModel);
        });
        citySelectionPanel.add(countrySelectionComboBox);
        citySelectionComboBox = new JComboBox<>(availablePlaces.get(availablePlaces.keySet().stream().findFirst().get()));
        citySelectionPanel.add(citySelectionComboBox);
        var citySelectionButton = new Button("Select");

        citySelectionButton.addActionListener(e -> citySelectionButtonClicked());

        citySelectionPanel.add(citySelectionButton);

        districtConfigurationPanel = new JPanel();
        frame.add(districtConfigurationPanel);

        var scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        var districtScrollPane = new JScrollPane(scrollContent);
        districtScrollPane.setPreferredSize(new Dimension(300, 500));
        districtScrollPane.setBounds(300,0,300,500);
        districtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        districtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        districtConfigurationPanel.add(districtScrollPane);

        simulationConfigurationPanel = new JPanel();
        frame.add(simulationConfigurationPanel);

        simulationConfigurationPanel.add(new JLabel("Simulation Time Rate"));
        timeRateTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(timeRateTextField);

        simulationConfigurationPanel.add(new JLabel("Simulation Duration"));
        var simulationDurationPanel = new JPanel();
        simulationDurationPanel.add(new JLabel("Days:"));
        simulationDurationDaysTextField.setColumns(3);
        simulationDurationPanel.add(simulationDurationDaysTextField);
        simulationDurationPanel.add(new JLabel("H:"));
        simulationDurationHoursTextField.setColumns(2);
        simulationDurationPanel.add(simulationDurationHoursTextField);
        simulationDurationPanel.add(new JLabel("M:"));
        simulationDurationMinutesTextField.setColumns(2);
        simulationDurationPanel.add(simulationDurationMinutesTextField);
        simulationDurationPanel.add(new JLabel("S:"));
        simulationDurationSecondsTextField.setColumns(2);
        simulationDurationPanel.add(simulationDurationSecondsTextField);
        simulationConfigurationPanel.add(simulationDurationPanel);

        simulationConfigurationPanel.add(new JLabel("Number of City Patrols"));
        numberOfCityPatrolsTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(numberOfCityPatrolsTextField);

        var drawDistrictsPanel = new JPanel();
        drawDistrictsPanel.add(new JLabel("Draw districts boundaries"));
        drawDistrictsPanel.add(drawDistrictsBoundariesCheckBox);
        simulationConfigurationPanel.add(drawDistrictsPanel);

        var drawFiringDetailsPanel = new JPanel();
        drawFiringDetailsPanel.add(new JLabel("Draw firing details"));
        drawFiringDetailsPanel.add(drawFiringDetailsCheckBox);
        simulationConfigurationPanel.add(drawFiringDetailsPanel);

        var threatLevelToMaxIncidentsConfigurationPanel =  new JPanel();
        threatLevelToMaxIncidentsConfigurationPanel.setBorder(new LineBorder(Color.BLACK, 1));
        threatLevelToMaxIncidentsConfigurationPanel.setLayout(new BoxLayout(threatLevelToMaxIncidentsConfigurationPanel, BoxLayout.Y_AXIS));
        threatLevelToMaxIncidentsConfigurationPanel.add(new JLabel("Set maximum number of incidents per hour"));

        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(District.ThreatLevelEnum.Safe.toString()));
        threatLevelMaxIncidentsTextField_SAFE.setColumns(textInputColumns);
        threatLevelMaxIncidentsTextField_SAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.Safe)));
        panel.add(threatLevelMaxIncidentsTextField_SAFE);
        threatLevelToMaxIncidentsConfigurationPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(District.ThreatLevelEnum.RatherSafe.toString()));
        threatLevelMaxIncidentsTextField_RATHERSAFE.setColumns(textInputColumns);
        threatLevelMaxIncidentsTextField_RATHERSAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.RatherSafe)));
        panel.add(threatLevelMaxIncidentsTextField_RATHERSAFE);
        threatLevelToMaxIncidentsConfigurationPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(District.ThreatLevelEnum.NotSafe.toString()));
        threatLevelMaxIncidentsTextField_NOTSAFE.setColumns(textInputColumns);
        threatLevelMaxIncidentsTextField_NOTSAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.NotSafe)));
        panel.add(threatLevelMaxIncidentsTextField_NOTSAFE);
        threatLevelToMaxIncidentsConfigurationPanel.add(panel);

        simulationConfigurationPanel.add(threatLevelToMaxIncidentsConfigurationPanel);

        var threatLevelToFiringChanceConfigurationPanel =  new JPanel();
        threatLevelToFiringChanceConfigurationPanel.setBorder(new LineBorder(Color.BLACK, 1));
        threatLevelToFiringChanceConfigurationPanel.setLayout(new BoxLayout(threatLevelToFiringChanceConfigurationPanel, BoxLayout.Y_AXIS));
        threatLevelToFiringChanceConfigurationPanel.add(new JLabel("Set chance for firing"));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(District.ThreatLevelEnum.Safe.toString()));
        threatLevelFiringChanceTextField_SAFE.setColumns(textInputColumns);
        threatLevelFiringChanceTextField_SAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.Safe)));
        panel.add(threatLevelFiringChanceTextField_SAFE);
        threatLevelToFiringChanceConfigurationPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(District.ThreatLevelEnum.RatherSafe.toString()));
        threatLevelFiringChanceTextField_RATHERSAFE.setColumns(textInputColumns);
        threatLevelFiringChanceTextField_RATHERSAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.RatherSafe)));
        panel.add(threatLevelFiringChanceTextField_RATHERSAFE);
        threatLevelToFiringChanceConfigurationPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(District.ThreatLevelEnum.NotSafe.toString()));
        threatLevelFiringChanceTextField_NOTSAFE.setColumns(textInputColumns);
        threatLevelFiringChanceTextField_NOTSAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.NotSafe)));
        panel.add(threatLevelFiringChanceTextField_NOTSAFE);
        threatLevelToFiringChanceConfigurationPanel.add(panel);

        simulationConfigurationPanel.add(threatLevelToFiringChanceConfigurationPanel);

        buttonsPanel = new JPanel();
        frame.add(buttonsPanel);
        var runSimulationButton = new Button("Run the simulation!");
        runSimulationButton.addActionListener(e -> runSimulationButtonClicked());
        buttonsPanel.add(runSimulationButton);

        // Disable further sections
        setComponentEnabledRecursively(districtConfigurationPanel, false);
        setComponentEnabledRecursively(simulationConfigurationPanel, false);
        setComponentEnabledRecursively(buttonsPanel, false);

        setDefaultValues();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setComponentEnabledRecursively(JComponent section, boolean isEnabled) {
        for (var component : section.getComponents()) {
            if (component instanceof JComponent) {
                setComponentEnabledRecursively((JComponent) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    private void citySelectionButtonClicked() {
        var cityName = citySelectionComboBox.getSelectedItem().toString();
        World.getInstance().getConfig().setCityName(cityName);
        if (loadMapIntoWorld(cityName)) {
            var scrollContent = (JPanel) ((JScrollPane)Arrays.stream(districtConfigurationPanel.getComponents()).filter(x -> x instanceof JScrollPane).findFirst().get()).getViewport().getView();
            scrollContent.removeAll();
            for (var district : World.getInstance().getMap().getDistricts()) {
                scrollContent.add(new DistrictConfigComponent(district));
            }
            scrollContent.revalidate();

            setComponentEnabledRecursively(districtConfigurationPanel, true);
            setComponentEnabledRecursively(simulationConfigurationPanel, true);
            setComponentEnabledRecursively(buttonsPanel, true);
        }
    }

    private void runSimulationButtonClicked() {
        var mapPanel = new MapPanel();
        mapPanel.createMapWindow();

        var config = World.getInstance().getConfig();
        config.setNumberOfPolicePatrols(numberOfCityPatrolsTextField.getText().equals("") ? 0 : Integer.parseInt(numberOfCityPatrolsTextField.getText()));
        config.setTimeRate(timeRateTextField.getText().equals("") ? 0 : Integer.parseInt(timeRateTextField.getText()));
        config.setSimulationDuration(getDurationFromInputs());
        config.setDrawDistrictsBorders(drawDistrictsBoundariesCheckBox.isSelected());
        config.setDrawFiringDetails(drawFiringDetailsCheckBox.isSelected());

        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.Safe, threatLevelMaxIncidentsTextField_SAFE.getText().equals("") ? 0 : Integer.parseInt(threatLevelMaxIncidentsTextField_SAFE.getText()));
        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.RatherSafe, threatLevelMaxIncidentsTextField_RATHERSAFE.getText().equals("") ? 0 : Integer.parseInt(threatLevelMaxIncidentsTextField_RATHERSAFE.getText()));
        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.NotSafe, threatLevelMaxIncidentsTextField_NOTSAFE.getText().equals("") ? 0 : Integer.parseInt(threatLevelMaxIncidentsTextField_NOTSAFE.getText()));

        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.Safe, threatLevelFiringChanceTextField_SAFE.getText().equals("") ? 0 : Double.parseDouble(threatLevelFiringChanceTextField_SAFE.getText()));
        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.RatherSafe, threatLevelFiringChanceTextField_RATHERSAFE.getText().equals("") ? 0 : Double.parseDouble(threatLevelFiringChanceTextField_RATHERSAFE.getText()));
        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.NotSafe, threatLevelFiringChanceTextField_NOTSAFE.getText().equals("") ? 0 : Double.parseDouble(threatLevelFiringChanceTextField_NOTSAFE.getText()));

        Logger.getInstance().logNewMessage("World config has been set.");

        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        mapPanel.selectHQLocation();
    }

    private boolean loadMapIntoWorld(String cityName) {
        try {
            var map = ImportGraphFromRawData.createMap(cityName);
            World.getInstance().setMap(map);
        } catch (Exception e) {
            // TODO Add logger
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
