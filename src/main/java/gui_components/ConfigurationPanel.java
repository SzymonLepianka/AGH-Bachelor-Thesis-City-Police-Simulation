package gui_components;

import osm_to_graph.ImportGraphFromRawData;
import world.World;
import entities.District;
import utils.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class ConfigurationPanel {

    private final static int textInputColumns = 20;
    private final HashMap<String, String[]> availablePlaces = new HashMap<>();
    private final HashMap<String, Integer> cityAdminLevelForAvailablePlaces = new HashMap<>();
    private final HashMap<String, Integer> districtAdminLevelForAvailablePlaces = new HashMap<>();
    private final JTextField numberOfCityPatrolsTextField = new JTextField();
    private final JTextField timeRateTextField = new JTextField();
    private final JTextField simulationDurationDaysTextField = new JTextField();
    private final JTextField simulationDurationHoursTextField = new JTextField();
    private final JTextField simulationDurationMinutesTextField = new JTextField();
    private final JTextField simulationDurationSecondsTextField = new JTextField();
    private final JCheckBox drawDistrictsBoundariesCheckBox = new JCheckBox();
    private final JCheckBox drawFiringDetailsCheckBox = new JCheckBox();
    private final JCheckBox drawLegendCheckBox = new JCheckBox();
    private final JCheckBox drawInterventionDetailsCheckBox = new JCheckBox();
    private final JTextField threatLevelMaxIncidentsTextFieldSAFE = new JTextField();
    private final JTextField threatLevelMaxIncidentsTextFieldRATHERSAFE = new JTextField();
    private final JTextField threatLevelMaxIncidentsTextFieldNOTSAFE = new JTextField();
    private final JTextField threatLevelFiringChanceTextFieldSAFE = new JTextField();
    private final JTextField threatLevelFiringChanceTextFieldRATHERSAFE = new JTextField();
    private final JTextField threatLevelFiringChanceTextFieldNOTSAFE = new JTextField();
    private final JTextField basicSearchDistanceTextField = new JTextField();
    private final JTextField minimumInterventionDurationTextField = new JTextField();
    private final JTextField maximumInterventionDurationTextField = new JTextField();
    private final JTextField minimumFiringStrength = new JTextField();
    private final JTextField maximumFiringStrength = new JTextField();
    private final JFrame mainFrame = new JFrame("City Police Simulation");
    private JPanel districtConfigurationPanel;
    private JPanel simulationConfigurationPanel;
    private JPanel buttonsPanel;
    private JComboBox<String> countrySelectionComboBox;
    private JComboBox<String> citySelectionComboBox;

    public ConfigurationPanel() {
        final String POLAND = "Poland";
        final String[] POLAND_CITIES = new String[]{"Kraków", "Warszawa", "Rzeszów", "Katowice", "Gdańsk", "Łódź", "Szczecin", "Poznań", "Lublin", "Białystok", "Wrocław"};
        final String GERMANY = "Germany";
        final String[] GERMANY_CITIES = new String[]{"Berlin"};

        availablePlaces.put(POLAND, POLAND_CITIES);
        cityAdminLevelForAvailablePlaces.put(POLAND, 6);
        districtAdminLevelForAvailablePlaces.put(POLAND, 9);

        availablePlaces.put(GERMANY, GERMANY_CITIES);
        cityAdminLevelForAvailablePlaces.put(GERMANY, 4);
        districtAdminLevelForAvailablePlaces.put(GERMANY, 9);
    }

    private void setDurationInputs(long time) {
        var days = time / 86400;
        var hours = (time % 86400) / 3600;
        var minutes = (time % 3600) / 60;
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
        return seconds + minutes * 60 + hours * 3600 + days * 86400;
    }

    private void setDefaultValues() {
        var worldConfig = World.getInstance().getConfig();
        numberOfCityPatrolsTextField.setText(Integer.toString(worldConfig.getNumberOfPolicePatrols()));
        basicSearchDistanceTextField.setText(Double.toString(worldConfig.getBasicSearchDistance()));
        timeRateTextField.setText(Integer.toString(worldConfig.getTimeRate()));
        setDurationInputs(worldConfig.getSimulationDuration());
    }

    public void createWindow() {
        mainFrame.setSize(1200, 600);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(1, 4));

        JPanel citySelectionPanel = new JPanel();
        mainFrame.add(citySelectionPanel);

        // simulation title
        var titlePane = new JTextPane();
        titlePane.setText("City Police\nSimulation");
        titlePane.setEditable(false);
        titlePane.setOpaque(false);
        titlePane.setFont(titlePane.getFont().deriveFont(30f));
        citySelectionPanel.add(titlePane);

        // simulation description
        var descriptionPane = new JTextPane();
        descriptionPane.setText("""
                The purpose of the application is to \s
                simulate the work of police units\s
                in any chosen city. It is possible\s
                to select additional simulation\s
                parameters to bring the logic and\s
                operation of the police in each city\s
                as close as possible.""");
        descriptionPane.setEditable(false);
        descriptionPane.setOpaque(false);
        descriptionPane.setFont(descriptionPane.getFont().deriveFont(14f));
        citySelectionPanel.add(descriptionPane);

        // line separating the components
        var jSeparator = new JSeparator();
        jSeparator.setOrientation(SwingConstants.HORIZONTAL);
        jSeparator.setPreferredSize(new Dimension(300, 10));
        citySelectionPanel.add(jSeparator);

        citySelectionPanel.add(new JLabel("      Select an area for the simulation:      "));

        // drop-down list with country selection
        countrySelectionComboBox = new JComboBox<>(availablePlaces.keySet().toArray(new String[0]));
        countrySelectionComboBox.addActionListener(e -> {
            var selectedItem = countrySelectionComboBox.getSelectedItem().toString();
            var newModel = new DefaultComboBoxModel<>(availablePlaces.get(selectedItem));
            citySelectionComboBox.setModel(newModel);
        });

        //drop-down list with city selection
        citySelectionPanel.add(countrySelectionComboBox);
        citySelectionComboBox = new JComboBox<>(availablePlaces.get(availablePlaces.keySet().stream().findFirst().orElseThrow()));
        citySelectionPanel.add(citySelectionComboBox);

        // city select button
        var citySelectionButton = new Button("Select");
        citySelectionButton.addActionListener(e -> citySelectionButtonClicked());

        citySelectionPanel.add(citySelectionButton);

//----------------------------------------------------
        districtConfigurationPanel = new JPanel();
        mainFrame.add(districtConfigurationPanel);

        var scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        var districtScrollPane = new JScrollPane(scrollContent);
        districtScrollPane.setPreferredSize(new Dimension(300, 500));
        districtScrollPane.setBounds(300, 0, 300, 500);
        districtScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        districtScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        districtConfigurationPanel.add(districtScrollPane);

//----------------------------------------------------
        simulationConfigurationPanel = new JPanel();
        mainFrame.add(simulationConfigurationPanel);

        simulationConfigurationPanel.add(new JLabel("Simulation Time Rate"));
        addRestrictionOfEnteringOnlyIntegers(timeRateTextField);
        timeRateTextField.setInputVerifier(new PositiveIntegerInputVerifier());
        timeRateTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(timeRateTextField);

        simulationConfigurationPanel.add(new JLabel("Simulation Duration"));
        var simulationDurationPanel = new JPanel();
        simulationDurationPanel.add(new JLabel("Days:"));
        addRestrictionOfEnteringOnlyIntegers(simulationDurationDaysTextField);
        simulationDurationDaysTextField.setInputVerifier(new NonNegativeIntegerInputVerifier());
        simulationDurationDaysTextField.setColumns(3);
        simulationDurationPanel.add(simulationDurationDaysTextField);
        simulationDurationPanel.add(new JLabel("H:"));
        addRestrictionOfEnteringOnlyIntegers(simulationDurationHoursTextField);
        simulationDurationHoursTextField.setInputVerifier(new NonNegativeIntegerInputVerifier());
        simulationDurationHoursTextField.setColumns(2);
        simulationDurationPanel.add(simulationDurationHoursTextField);
        simulationDurationPanel.add(new JLabel("M:"));
        addRestrictionOfEnteringOnlyIntegers(simulationDurationMinutesTextField);
        simulationDurationMinutesTextField.setInputVerifier(new NonNegativeIntegerInputVerifier());
        simulationDurationMinutesTextField.setColumns(2);
        simulationDurationPanel.add(simulationDurationMinutesTextField);
        simulationDurationPanel.add(new JLabel("S:"));
        addRestrictionOfEnteringOnlyIntegers(simulationDurationSecondsTextField);
        simulationDurationSecondsTextField.setInputVerifier(new NonNegativeIntegerInputVerifier());
        simulationDurationSecondsTextField.setColumns(2);
        simulationDurationPanel.add(simulationDurationSecondsTextField);
        simulationConfigurationPanel.add(simulationDurationPanel);

        simulationConfigurationPanel.add(new JLabel("Number of City Patrols"));
        addRestrictionOfEnteringOnlyIntegers(numberOfCityPatrolsTextField);
        numberOfCityPatrolsTextField.setInputVerifier(new PositiveIntegerInputVerifier());
        numberOfCityPatrolsTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(numberOfCityPatrolsTextField);

        simulationConfigurationPanel.add(new JLabel("Basic search range for police support [meters]"));
        addRestrictionOfEnteringOnlyFloats(basicSearchDistanceTextField);
        basicSearchDistanceTextField.setInputVerifier(new FloatInputVerifier());
        basicSearchDistanceTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(basicSearchDistanceTextField);

        var drawDistrictsPanel = new JPanel();
        drawDistrictsPanel.add(new JLabel("Draw districts boundaries"));
        drawDistrictsBoundariesCheckBox.setSelected(true);
        drawDistrictsPanel.add(drawDistrictsBoundariesCheckBox);
        simulationConfigurationPanel.add(drawDistrictsPanel);

        var drawFiringDetailsPanel = new JPanel();
        drawFiringDetailsPanel.add(new JLabel("Draw firing details"));
        drawFiringDetailsCheckBox.setSelected(true);
        drawFiringDetailsPanel.add(drawFiringDetailsCheckBox);
        simulationConfigurationPanel.add(drawFiringDetailsPanel);

        var drawLegendPanel = new JPanel();
        drawLegendPanel.add(new JLabel("Draw legend"));
        drawLegendCheckBox.setSelected(true);
        drawLegendPanel.add(drawLegendCheckBox);
        simulationConfigurationPanel.add(drawLegendPanel);

        var drawInterventionDetailsPanel = new JPanel();
        drawInterventionDetailsPanel.add(new JLabel("Draw intervention details while paused"));
        drawInterventionDetailsCheckBox.setSelected(true);
        drawInterventionDetailsPanel.add(drawInterventionDetailsCheckBox);
        simulationConfigurationPanel.add(drawInterventionDetailsPanel);

//----------------------------------------------------

        var threatLevelToMaxIncidentsConfigurationPanel = new JPanel();
        threatLevelToMaxIncidentsConfigurationPanel.setLayout(new BoxLayout(threatLevelToMaxIncidentsConfigurationPanel, BoxLayout.Y_AXIS));
        threatLevelToMaxIncidentsConfigurationPanel.setBorder(new LineBorder(Color.BLACK, 1));

        JLabel descriptionLabel = new JLabel("Set the maximum number of incidents per");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        threatLevelToMaxIncidentsConfigurationPanel.add(descriptionLabel);
        descriptionLabel = new JLabel("hour for a given district security level:");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        threatLevelToMaxIncidentsConfigurationPanel.add(descriptionLabel);

        var panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel(District.ThreatLevelEnum.NotSafe + ": "));
        threatLevelMaxIncidentsTextFieldNOTSAFE.setColumns(11);
        threatLevelMaxIncidentsTextFieldNOTSAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.NotSafe)));
        addRestrictionOfEnteringOnlyIntegers(threatLevelMaxIncidentsTextFieldNOTSAFE);
        threatLevelMaxIncidentsTextFieldNOTSAFE.setInputVerifier(new MaxNumberOfIncidentsInputVerifier(District.ThreatLevelEnum.NotSafe));
        panel.add(threatLevelMaxIncidentsTextFieldNOTSAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.RatherSafe + ": "));
        threatLevelMaxIncidentsTextFieldRATHERSAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.RatherSafe)));
        addRestrictionOfEnteringOnlyIntegers(threatLevelMaxIncidentsTextFieldRATHERSAFE);
        threatLevelMaxIncidentsTextFieldRATHERSAFE.setInputVerifier(new MaxNumberOfIncidentsInputVerifier(District.ThreatLevelEnum.RatherSafe));
        panel.add(threatLevelMaxIncidentsTextFieldRATHERSAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.Safe + ": "));
        threatLevelMaxIncidentsTextFieldSAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.Safe)));
        addRestrictionOfEnteringOnlyIntegers(threatLevelMaxIncidentsTextFieldSAFE);
        threatLevelMaxIncidentsTextFieldSAFE.setInputVerifier(new MaxNumberOfIncidentsInputVerifier(District.ThreatLevelEnum.Safe));
        panel.add(threatLevelMaxIncidentsTextFieldSAFE);
        threatLevelToMaxIncidentsConfigurationPanel.add(panel);

        simulationConfigurationPanel.add(threatLevelToMaxIncidentsConfigurationPanel);

//----------------------------------------------------

        var threatLevelToFiringChanceConfigurationPanel = new JPanel();
        threatLevelToFiringChanceConfigurationPanel.setLayout(new BoxLayout(threatLevelToFiringChanceConfigurationPanel, BoxLayout.Y_AXIS));
        threatLevelToFiringChanceConfigurationPanel.setBorder(new LineBorder(Color.BLACK, 1));

        descriptionLabel = new JLabel("Set the chance for the intervention to");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        threatLevelToFiringChanceConfigurationPanel.add(descriptionLabel);
        descriptionLabel = new JLabel("turn into a firing for the given");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        threatLevelToFiringChanceConfigurationPanel.add(descriptionLabel);
        descriptionLabel = new JLabel("district security level:");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        threatLevelToFiringChanceConfigurationPanel.add(descriptionLabel);

        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel(District.ThreatLevelEnum.NotSafe + ": "));
        threatLevelFiringChanceTextFieldNOTSAFE.setColumns(11);
        threatLevelFiringChanceTextFieldNOTSAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.NotSafe)));
        addRestrictionOfEnteringOnlyFloats(threatLevelFiringChanceTextFieldNOTSAFE);
        threatLevelFiringChanceTextFieldNOTSAFE.setInputVerifier(new ProbabilityInputVerifier(District.ThreatLevelEnum.NotSafe));
        panel.add(threatLevelFiringChanceTextFieldNOTSAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.RatherSafe + ": "));
        threatLevelFiringChanceTextFieldRATHERSAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.RatherSafe)));
        addRestrictionOfEnteringOnlyFloats(threatLevelFiringChanceTextFieldRATHERSAFE);
        threatLevelFiringChanceTextFieldRATHERSAFE.setInputVerifier(new ProbabilityInputVerifier(District.ThreatLevelEnum.RatherSafe));
        panel.add(threatLevelFiringChanceTextFieldRATHERSAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.Safe + ": "));
        threatLevelFiringChanceTextFieldSAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.Safe)));
        addRestrictionOfEnteringOnlyFloats(threatLevelFiringChanceTextFieldSAFE);
        threatLevelFiringChanceTextFieldSAFE.setInputVerifier(new ProbabilityInputVerifier(District.ThreatLevelEnum.Safe));
        panel.add(threatLevelFiringChanceTextFieldSAFE);
        threatLevelToFiringChanceConfigurationPanel.add(panel);

        simulationConfigurationPanel.add(threatLevelToFiringChanceConfigurationPanel);

//----------------------------------------------------

        buttonsPanel = new JPanel();
        mainFrame.add(buttonsPanel);

//----------------------------------------------------

        var interventionDurationConfigurationPanel = new JPanel();
        interventionDurationConfigurationPanel.setLayout(new BoxLayout(interventionDurationConfigurationPanel, BoxLayout.Y_AXIS));
        interventionDurationConfigurationPanel.setBorder(new LineBorder(Color.BLACK, 1));

        descriptionLabel = new JLabel("Set the time range for the duration");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        interventionDurationConfigurationPanel.add(descriptionLabel);
        descriptionLabel = new JLabel("of the intervention [minutes]:");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        interventionDurationConfigurationPanel.add(descriptionLabel);

        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(new JLabel("MIN: "));
        minimumInterventionDurationTextField.setText(String.valueOf(World.getInstance().getConfig().getMinimumInterventionDuration()));
        addRestrictionOfEnteringOnlyIntegers(minimumInterventionDurationTextField);
        minimumInterventionDurationTextField.setInputVerifier(new MinDurationInputVerifier());
        panel.add(minimumInterventionDurationTextField);
        panel.add(new JLabel("MAX: "));
        maximumInterventionDurationTextField.setText(String.valueOf(World.getInstance().getConfig().getMaximumInterventionDuration()));
        addRestrictionOfEnteringOnlyIntegers(maximumInterventionDurationTextField);
        maximumInterventionDurationTextField.setColumns(11);
        maximumInterventionDurationTextField.setInputVerifier(new MaxDurationInputVerifier());
        panel.add(maximumInterventionDurationTextField);

        interventionDurationConfigurationPanel.add(panel);

        buttonsPanel.add(interventionDurationConfigurationPanel);

//----------------------------------------------------

        var firingStrengthConfigurationPanel = new JPanel();
        firingStrengthConfigurationPanel.setLayout(new BoxLayout(firingStrengthConfigurationPanel, BoxLayout.Y_AXIS));
        firingStrengthConfigurationPanel.setBorder(new LineBorder(Color.BLACK, 1));

        descriptionLabel = new JLabel("Set the time range for the duration");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        firingStrengthConfigurationPanel.add(descriptionLabel);
        descriptionLabel = new JLabel("of the firing [minutes]:");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        firingStrengthConfigurationPanel.add(descriptionLabel);

        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(new JLabel("MIN: "));
        minimumFiringStrength.setText(String.valueOf(World.getInstance().getConfig().getMinimumFiringStrength()));
        addRestrictionOfEnteringOnlyIntegers(minimumFiringStrength);
        minimumFiringStrength.setInputVerifier(new MinStrengthInputVerifier());
        panel.add(minimumFiringStrength);
        panel.add(new JLabel("MAX: "));
        maximumFiringStrength.setText(String.valueOf(World.getInstance().getConfig().getMaximumFiringStrength()));
        addRestrictionOfEnteringOnlyIntegers(maximumFiringStrength);
        maximumFiringStrength.setColumns(11);
        maximumFiringStrength.setInputVerifier(new MaxStrengthInputVerifier());
        panel.add(maximumFiringStrength);

        firingStrengthConfigurationPanel.add(panel);

        buttonsPanel.add(firingStrengthConfigurationPanel);

//----------------------------------------------------

        // line separating the components
        jSeparator = new JSeparator();
        jSeparator.setOrientation(SwingConstants.HORIZONTAL);
        jSeparator.setPreferredSize(new Dimension(300, 20));
        buttonsPanel.add(jSeparator);

//----------------------------------------------------

        var runSimulationButton = new Button("Run the simulation!");
        runSimulationButton.addActionListener(e -> runSimulationButtonClicked());
        buttonsPanel.add(runSimulationButton);

        // Disable further sections
        setComponentEnabledRecursively(districtConfigurationPanel, false);
        setComponentEnabledRecursively(simulationConfigurationPanel, false);
        setComponentEnabledRecursively(buttonsPanel, false);

        setDefaultValues();

        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private void addRestrictionOfEnteringOnlyIntegers(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            // only numbers can be entered
            @Override
            public void keyPressed(KeyEvent e) {
                textField.setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE);
            }
        });
    }

    private void addRestrictionOfEnteringOnlyFloats(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            // only numbers can be entered
            @Override
            public void keyPressed(KeyEvent e) {
                textField.setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '.' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE);
            }
        });
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
            var scrollContent = (JPanel) ((JScrollPane) Arrays.stream(districtConfigurationPanel.getComponents()).filter(JScrollPane.class::isInstance).findFirst().orElseThrow()).getViewport().getView();
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
        config.setNumberOfPolicePatrols(numberOfCityPatrolsTextField.getText().equals("") ? 1 : convertInputToInteger(numberOfCityPatrolsTextField, 1));
        config.setBasicSearchDistance(basicSearchDistanceTextField.getText().equals("") ? 1.0 : convertInputToDouble(basicSearchDistanceTextField, 1.0));
        config.setTimeRate(timeRateTextField.getText().equals("") ? 1 : convertInputToInteger(timeRateTextField, 1));
        config.setSimulationDuration(getDurationFromInputs());
        config.setDrawDistrictsBorders(drawDistrictsBoundariesCheckBox.isSelected());
        config.setDrawFiringDetails(drawFiringDetailsCheckBox.isSelected());
        config.setDrawLegend(drawLegendCheckBox.isSelected());
        config.setDrawInterventionDetails(drawInterventionDetailsCheckBox.isSelected());

        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.Safe, threatLevelMaxIncidentsTextFieldSAFE.getText().equals("") ? 0 : convertInputToInteger(threatLevelMaxIncidentsTextFieldSAFE, 0));
        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.RatherSafe, threatLevelMaxIncidentsTextFieldRATHERSAFE.getText().equals("") ? 0 : convertInputToInteger(threatLevelMaxIncidentsTextFieldRATHERSAFE, 0));
        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.NotSafe, threatLevelMaxIncidentsTextFieldNOTSAFE.getText().equals("") ? 0 : convertInputToInteger(threatLevelMaxIncidentsTextFieldNOTSAFE, 0));

        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.Safe, threatLevelFiringChanceTextFieldSAFE.getText().equals("") ? 0.0 : convertInputToDouble(threatLevelFiringChanceTextFieldSAFE, 0.0));
        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.RatherSafe, threatLevelFiringChanceTextFieldRATHERSAFE.getText().equals("") ? 0.0 : convertInputToDouble(threatLevelFiringChanceTextFieldRATHERSAFE, 0.0));
        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.NotSafe, threatLevelFiringChanceTextFieldNOTSAFE.getText().equals("") ? 0.0 : convertInputToDouble(threatLevelFiringChanceTextFieldNOTSAFE, 0.0));

        config.setMinimumInterventionDuration(minimumInterventionDurationTextField.getText().equals("") ? 1 : convertInputToInteger(minimumInterventionDurationTextField, Integer.parseInt(maximumInterventionDurationTextField.getText()) - 1));
        config.setMaximumInterventionDuration(maximumInterventionDurationTextField.getText().equals("") ? 1 : convertInputToInteger(maximumInterventionDurationTextField, Integer.parseInt(minimumInterventionDurationTextField.getText()) + 1));
        config.setMinimumFiringStrength(minimumFiringStrength.getText().equals("") ? 1 : convertInputToInteger(minimumFiringStrength, Integer.parseInt(maximumFiringStrength.getText()) - 1));
        config.setMaximumFiringStrength(maximumFiringStrength.getText().equals("") ? 1 : convertInputToInteger(maximumFiringStrength, Integer.parseInt(minimumFiringStrength.getText()) + 1));

        Logger.getInstance().logNewMessage("World config has been set.");

        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));

        mapPanel.selectHQLocation();
    }

    private Double convertInputToDouble(JTextField textField, Double basicValue) {
        try {
            return Double.parseDouble(textField.getText());
        } catch (Exception e) {
            return basicValue;
        }
    }

    private Integer convertInputToInteger(JTextField textField, Integer basicValue) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (Exception e) {
            return basicValue;
        }
    }

    private boolean loadMapIntoWorld(String cityName) {
        try {
            var countryName = countrySelectionComboBox.getSelectedItem().toString();
            var cityAdminLevel = cityAdminLevelForAvailablePlaces.get(countryName);
            var districtAdminLevel = districtAdminLevelForAvailablePlaces.get(countryName);
            var map = ImportGraphFromRawData.createMap(cityName, cityAdminLevel, districtAdminLevel);
            World.getInstance().setMap(map);
        } catch (InterruptedException | IOException e) {
            Logger.getInstance().logNewMessage("Unable to load map into world" + e.getLocalizedMessage());
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    public static class PositiveIntegerInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                var value = Integer.parseInt(((JTextField) input).getText());
                if (value <= 0) {
                    ((JTextField) input).setText("1");
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1");
                return false;
            }
        }
    }

    public static class NonNegativeIntegerInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                var value = Integer.parseInt(((JTextField) input).getText());
                if (value < 0) {
                    ((JTextField) input).setText("1");
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1");
                return false;
            }
        }
    }

    public static class FloatInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                var value = Double.parseDouble(((JTextField) input).getText());
                if (value <= 0.0) {
                    ((JTextField) input).setText("1.0");
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1.0");
                return false;
            }
        }
    }

    public class MaxNumberOfIncidentsInputVerifier extends InputVerifier {

        private District.ThreatLevelEnum safetyLevel;

        public MaxNumberOfIncidentsInputVerifier(District.ThreatLevelEnum safetyLevel) {
            this.safetyLevel = safetyLevel;
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                var value = Integer.parseInt(((JTextField) input).getText());
                if (value < 0) {
                    ((JTextField) input).setText("1");
                } else {
                    var safetyLevelSafe = Integer.parseInt(threatLevelMaxIncidentsTextFieldSAFE.getText());
                    var safetyLevelRatherSafe = Integer.parseInt(threatLevelMaxIncidentsTextFieldRATHERSAFE.getText());
                    var safetyLevelNotSafe = Integer.parseInt(threatLevelMaxIncidentsTextFieldNOTSAFE.getText());

                    if (safetyLevel == District.ThreatLevelEnum.Safe) {
                        if (value >= safetyLevelRatherSafe) {
                            ((JTextField) input).setText(String.valueOf(safetyLevelRatherSafe - 1));
                        }
                    } else if (safetyLevel == District.ThreatLevelEnum.RatherSafe) {
                        if (value <= safetyLevelSafe || value >= safetyLevelNotSafe) {
                            ((JTextField) input).setText(String.valueOf(safetyLevelSafe + 1));
                        }
                    } else {
                        if (value <= safetyLevelRatherSafe) {
                            ((JTextField) input).setText(String.valueOf(safetyLevelRatherSafe + 1));
                        }
                    }
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1");
                return false;
            }
        }
    }

    public class ProbabilityInputVerifier extends InputVerifier {
        private District.ThreatLevelEnum safetyLevel;

        public ProbabilityInputVerifier(District.ThreatLevelEnum safetyLevel) {
            this.safetyLevel = safetyLevel;
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                var value = Double.parseDouble(((JTextField) input).getText());
                if (value < 0.0 || value > 1.0) {
                    ((JTextField) input).setText("0.5");
                } else {
                    var safetyLevelSafe = Double.parseDouble(threatLevelFiringChanceTextFieldSAFE.getText());
                    var safetyLevelRatherSafe = Double.parseDouble(threatLevelFiringChanceTextFieldRATHERSAFE.getText());
                    var safetyLevelNotSafe = Double.parseDouble(threatLevelFiringChanceTextFieldNOTSAFE.getText());

                    if (safetyLevel == District.ThreatLevelEnum.Safe) {
                        if (value >= safetyLevelRatherSafe) {
                            ((JTextField) input).setText(String.format(Locale.US, "%.2f", safetyLevelRatherSafe - 0.01));
                        }
                    } else if (safetyLevel == District.ThreatLevelEnum.RatherSafe) {
                        if (value <= safetyLevelSafe || value >= safetyLevelNotSafe) {
                            ((JTextField) input).setText(String.format(Locale.US, "%.2f", safetyLevelSafe + 0.01));
                        }
                    } else {
                        if (value <= safetyLevelRatherSafe) {
                            ((JTextField) input).setText(String.format(Locale.US, "%.2f", safetyLevelRatherSafe + 0.01));
                        }
                    }
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("0.5");
                return false;
            }
        }
    }

    public class MinDurationInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                var minDurValue = Integer.parseInt(((JTextField) input).getText());
                var maxDurValue = Integer.parseInt(maximumInterventionDurationTextField.getText());

                if (minDurValue >= maxDurValue) {
                    ((JTextField) input).setText(String.valueOf(maxDurValue - 1));
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1");
                return false;
            }
        }
    }

    public class MaxDurationInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                var maxDurValue = Integer.parseInt(((JTextField) input).getText());
                var minDurValue = Integer.parseInt(minimumInterventionDurationTextField.getText());
                if (maxDurValue <= minDurValue) {
                    ((JTextField) input).setText(String.valueOf(minDurValue + 1));
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1");
                return false;
            }
        }
    }

    public class MinStrengthInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                var minStrengthValue = Integer.parseInt(((JTextField) input).getText());
                var maxStrengthValue = Integer.parseInt(maximumFiringStrength.getText());
                if (minStrengthValue >= maxStrengthValue) {
                    ((JTextField) input).setText(String.valueOf(maxStrengthValue - 1));
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1");
                return false;
            }
        }
    }

    public class MaxStrengthInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            try {
                var maxStrengthValue = Integer.parseInt(((JTextField) input).getText());
                var minStrengthValue = Integer.parseInt(minimumFiringStrength.getText());
                if (maxStrengthValue <= minStrengthValue) {
                    ((JTextField) input).setText(String.valueOf(minStrengthValue + 1));
                }
                return true;
            } catch (NumberFormatException e) {
                ((JTextField) input).setText("1");
                return false;
            }
        }
    }
}