package guiComponents;

import OSMToGraph.ImportGraphFromRawData;
import World.World;
import entities.District;
import utils.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigurationPanel {

    private final int textInputColumns = 20;
    // TODO Change into dynamically generated
    private final HashMap<String, String[]> availablePlaces = new HashMap<>();
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
    private final JTextField basicSearchDistanceTextField = new JTextField();
    private final JFrame mainFrame = new JFrame("City Police Simulation");
    private JPanel citySelectionPanel;
    private JPanel districtConfigurationPanel;
    private JPanel simulationConfigurationPanel;
    private JPanel buttonsPanel;
    private JComboBox<String> countrySelectionComboBox;
    private JComboBox<String> citySelectionComboBox;

    public ConfigurationPanel() {
        availablePlaces.put("Poland", new String[]{"Kraków", "Warszawa", "Rzeszów", "Katowice", "Gdańsk", "Łódź", "Szczecin", "Poznań", "Lublin", "Białystok", "Wrocław"});
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

    // TODO Add validation for input data
    public void createWindow() {
        mainFrame.setSize(1200, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(1, 4));

        citySelectionPanel = new JPanel();
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
        jSeparator.setPreferredSize(new Dimension(300, 20));
        citySelectionPanel.add(jSeparator);

        // drop-down list with country selection
        countrySelectionComboBox = new JComboBox<>(availablePlaces.keySet().toArray(new String[0]));
        countrySelectionComboBox.addActionListener(e -> {
            var selectedItem = countrySelectionComboBox.getSelectedItem().toString();
            var newModel = new DefaultComboBoxModel<>(availablePlaces.get(selectedItem));
            citySelectionComboBox.setModel(newModel);
        });

        //drop-down list with city selection
        citySelectionPanel.add(countrySelectionComboBox);
        citySelectionComboBox = new JComboBox<>(availablePlaces.get(availablePlaces.keySet().stream().findFirst().get()));
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
        districtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        districtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

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
        drawDistrictsPanel.add(drawDistrictsBoundariesCheckBox);
        simulationConfigurationPanel.add(drawDistrictsPanel);

        var drawFiringDetailsPanel = new JPanel();
        drawFiringDetailsPanel.add(new JLabel("Draw firing details"));
        drawFiringDetailsPanel.add(drawFiringDetailsCheckBox);
        simulationConfigurationPanel.add(drawFiringDetailsPanel);

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
        panel.add(new JLabel(District.ThreatLevelEnum.Safe + ": "));
        threatLevelMaxIncidentsTextField_SAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.Safe)));
        addRestrictionOfEnteringOnlyIntegers(threatLevelMaxIncidentsTextField_SAFE);
        threatLevelMaxIncidentsTextField_SAFE.setInputVerifier(new NonNegativeIntegerInputVerifier());
        panel.add(threatLevelMaxIncidentsTextField_SAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.RatherSafe + ": "));
        threatLevelMaxIncidentsTextField_RATHERSAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.RatherSafe)));
        addRestrictionOfEnteringOnlyIntegers(threatLevelMaxIncidentsTextField_RATHERSAFE);
        threatLevelMaxIncidentsTextField_RATHERSAFE.setInputVerifier(new NonNegativeIntegerInputVerifier());
        panel.add(threatLevelMaxIncidentsTextField_RATHERSAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.NotSafe + ": "));
        threatLevelMaxIncidentsTextField_NOTSAFE.setColumns(11);
        threatLevelMaxIncidentsTextField_NOTSAFE.setText(String.valueOf(World.getInstance().getConfig().getMaxIncidentForThreatLevel(District.ThreatLevelEnum.NotSafe)));
        addRestrictionOfEnteringOnlyIntegers(threatLevelMaxIncidentsTextField_NOTSAFE);
        threatLevelMaxIncidentsTextField_NOTSAFE.setInputVerifier(new NonNegativeIntegerInputVerifier());
        panel.add(threatLevelMaxIncidentsTextField_NOTSAFE);
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
        panel.add(new JLabel(District.ThreatLevelEnum.Safe + ": "));
        threatLevelFiringChanceTextField_SAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.Safe)));
        addRestrictionOfEnteringOnlyFloats(threatLevelFiringChanceTextField_SAFE);
        threatLevelFiringChanceTextField_SAFE.setInputVerifier(new NonNegativeIntegerInputVerifier());
        panel.add(threatLevelFiringChanceTextField_SAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.RatherSafe + ": "));
        threatLevelFiringChanceTextField_RATHERSAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.RatherSafe)));
        addRestrictionOfEnteringOnlyFloats(threatLevelFiringChanceTextField_RATHERSAFE);
        threatLevelFiringChanceTextField_RATHERSAFE.setInputVerifier(new NonNegativeIntegerInputVerifier());
        panel.add(threatLevelFiringChanceTextField_RATHERSAFE);
        panel.add(new JLabel(District.ThreatLevelEnum.NotSafe + ": "));
        threatLevelFiringChanceTextField_NOTSAFE.setColumns(11);
        threatLevelFiringChanceTextField_NOTSAFE.setText(String.valueOf(World.getInstance().getConfig().getFiringChanceForThreatLevel(District.ThreatLevelEnum.NotSafe)));
        addRestrictionOfEnteringOnlyFloats(threatLevelFiringChanceTextField_NOTSAFE);
        threatLevelFiringChanceTextField_NOTSAFE.setInputVerifier(new NonNegativeIntegerInputVerifier());
        panel.add(threatLevelFiringChanceTextField_NOTSAFE);
        threatLevelToFiringChanceConfigurationPanel.add(panel);

        simulationConfigurationPanel.add(threatLevelToFiringChanceConfigurationPanel);

//----------------------------------------------------

        buttonsPanel = new JPanel();
        mainFrame.add(buttonsPanel);
        var runSimulationButton = new Button("Run the simulation!");
        runSimulationButton.addActionListener(e -> runSimulationButtonClicked());
        buttonsPanel.add(runSimulationButton);

        // Disable further sections
        setComponentEnabledRecursively(districtConfigurationPanel, false);
        setComponentEnabledRecursively(simulationConfigurationPanel, false);
        setComponentEnabledRecursively(buttonsPanel, false);

        setDefaultValues();

        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private void addRestrictionOfEnteringOnlyIntegers(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            // only numbers can be entered
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    textField.setEditable(true);
                } else {
                    textField.setEditable(false);
                }
            }
        });
    }

    private void addRestrictionOfEnteringOnlyFloats(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            // only numbers can be entered
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == '.' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    textField.setEditable(true);
                } else {
                    textField.setEditable(false);
                }
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
            var scrollContent = (JPanel) ((JScrollPane) Arrays.stream(districtConfigurationPanel.getComponents()).filter(x -> x instanceof JScrollPane).findFirst().get()).getViewport().getView();
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

        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.Safe, threatLevelMaxIncidentsTextField_SAFE.getText().equals("") ? 0 : convertInputToInteger(threatLevelMaxIncidentsTextField_SAFE, 0));
        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.RatherSafe, threatLevelMaxIncidentsTextField_RATHERSAFE.getText().equals("") ? 0 : convertInputToInteger(threatLevelMaxIncidentsTextField_RATHERSAFE, 0));
        config.setMaxIncidentsForThreatLevel(District.ThreatLevelEnum.NotSafe, threatLevelMaxIncidentsTextField_NOTSAFE.getText().equals("") ? 0 : convertInputToInteger(threatLevelMaxIncidentsTextField_NOTSAFE, 0));

        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.Safe, threatLevelFiringChanceTextField_SAFE.getText().equals("") ? 0.0 : convertInputToDouble(threatLevelFiringChanceTextField_SAFE, 0.0));
        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.RatherSafe, threatLevelFiringChanceTextField_RATHERSAFE.getText().equals("") ? 0.0 : convertInputToDouble(threatLevelFiringChanceTextField_RATHERSAFE, 0.0));
        config.setFiringChanceForThreatLevel(District.ThreatLevelEnum.NotSafe, threatLevelFiringChanceTextField_NOTSAFE.getText().equals("") ? 0.0 : convertInputToDouble(threatLevelFiringChanceTextField_NOTSAFE, 0.0));

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
            var map = ImportGraphFromRawData.createMap(cityName);
            World.getInstance().setMap(map);
        } catch (Exception e) {
            // TODO Add logger
            e.printStackTrace();
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
}
