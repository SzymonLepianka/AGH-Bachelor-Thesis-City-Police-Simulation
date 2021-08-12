package guiComponents;

import entities.District;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.Hashtable;

public class DistrictConfigComponent extends JPanel {

    private District district;

    private JSlider districtThreatLevelInput = new JSlider(District.ThreatLevelEnum.values()[0].value, District.ThreatLevelEnum.values()[District.ThreatLevelEnum.values().length - 1].value);

    private final int MARGIN = 10;

    public DistrictConfigComponent(District districtName) {
        this.district = districtName;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        this.setSize(new Dimension(290-(MARGIN*2), 80));

        var labels = new Hashtable<Integer, JLabel>();
        for (var label : District.ThreatLevelEnum.values()) {
            labels.put(label.value, new JLabel(label.toString()));
        }

        var sliderContainer = new JPanel();

        this.districtThreatLevelInput.addChangeListener(event -> inputChanged());
        this.districtThreatLevelInput.setLabelTable(labels);
        this.districtThreatLevelInput.setPaintLabels(true);

        sliderContainer.add(districtThreatLevelInput);

        this.add(new JLabel(this.district.getName()));
        this.add(sliderContainer);
    }

    private void inputChanged() {
        try {
            this.district.setThreatLevel(Arrays.stream(District.ThreatLevelEnum.values()).filter(x -> x.value == districtThreatLevelInput.getValue()).findFirst().get());
        } catch (Exception exception) {
            districtThreatLevelInput.setBorder(new LineBorder(Color.RED, MARGIN));
        }
    }

}
