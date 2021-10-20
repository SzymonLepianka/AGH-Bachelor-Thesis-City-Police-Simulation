package guiComponents;

import World.World;
import entities.Headquarters;
import entities.IDrawable;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import simulation.SimulationThread;
import simulation.StatisticsCounter;
import utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

public class MapPanel {

    class MapPainter implements Painter<JXMapViewer> {

        @Override
        public void paint(Graphics2D g, JXMapViewer mapViewer, int width, int height) {
            World.getInstance().getAllEntities().stream().filter(x -> x instanceof IDrawable).forEach(x -> ((IDrawable) x).drawSelf(g, mapViewer));
            if (World.getInstance().getConfig().isDrawDistrictsBorders()) {
                World.getInstance().getMap().getDistricts().forEach(x -> x.drawSelf(g, mapViewer));
            }

            drawSimulationClock(g);
        }

        private void drawSimulationClock(Graphics2D g) {
            var time = World.getInstance().getSimulationTimeLong();

            var days = (int)(time / 86400);
            var hours = (int)((time % 86400)/3600);
            var minutes = (int)((time % 3600)/60);
            var seconds = (int)(time % 60);

            // Draw background
            var oldColor = g.getColor();
            g.setColor(new Color(244, 226, 198, 175));
            g.fillRect(5,5,150, 20);
            g.setColor(oldColor);

            // Draw date
            var oldFont = g.getFont();
            g.setFont(new Font("TimesRoman", Font.BOLD, 15));
            g.drawString(String.format("Day: %03d, %02d:%02d:%02d", days, hours, minutes, seconds), 10, 20);
            g.setFont(oldFont);
        }
    }

    private final JFrame frame = new JFrame();
    private final JXMapViewer mapViewer = new JXMapViewer();

    private final JButton simulationPauseButton = new JButton("Pause");

    public MapPanel() {
        var info = new OSMTileFactoryInfo();
        var tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addMouseMotionListener(new PanMouseInputListener(mapViewer));

        mapViewer.setOverlayPainter(new MapPainter());
    }

    public void createMapWindow() {
        frame.setSize( 1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        var position = World.getInstance().getPosition();

        mapViewer.setAddressLocation(new GeoPosition(position.getLatitude(), position.getLongitude()));

        // TODO Add automatic zoom calculation
        /*mapViewer.calculateZoomFrom(new HashSet<>() {
            {
                add(minCoordinates);
                add(maxCoordinates);
            }
        });*/
        //mapViewer.setZoom(mapViewer.getZoom() / 2);
        //mapViewer.setZoom(mapViewer.getZoom());
        mapViewer.setZoom(7);

        simulationPauseButton.setMaximumSize(new Dimension(50, 50));

        simulationPauseButton.addActionListener(new ActionListener() {

            private boolean showingPause = !World.getInstance().isSimulationPaused();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (showingPause) {
                    World.getInstance().pauseSimulation();
                    JButton button = (JButton) e.getSource();
                    button.setText("Resume");
                    showingPause = false;
                } else {
                    World.getInstance().resumeSimulation();
                    JButton button = (JButton) e.getSource();
                    button.setText("Pause");
                    showingPause = true;
                }
            }

        });
        mapViewer.add(simulationPauseButton);

        frame.getContentPane().add(mapViewer);
        frame.setVisible(true);
    }

    public void selectHQLocation() {
        mapViewer.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var position = mapViewer.convertPointToGeoPosition(e.getPoint());
                Logger.getInstance().logNewMessage("HQ position has been selected.");

                var HQ = new Headquarters(position.getLatitude(), position.getLongitude());
                World.getInstance().addEntity(HQ);

                // GUI Drawing thread
                new Thread(() -> {
                    while (!World.getInstance().hasSimulationDurationElapsed()) {
                        mapViewer.repaint();
                        try {
                            Thread.sleep(1000/30);
                        } catch (Exception exception) {
                            // Ignore
                            exception.printStackTrace();
                        }
                    }

                    showSummary();
                }).start();

                // Simulation thread
                new SimulationThread().start();

                mapViewer.removeMouseListener(this);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        JOptionPane.showMessageDialog(frame, "Please select HQ location.");
    }

    private void showSummary() {
        var simulationSummaryMessage = new StringBuilder();

        simulationSummaryMessage.append("Simulation has finished.\n\n");

        simulationSummaryMessage.append("Simulated Patrols: ").append(StatisticsCounter.getInstance().getNumberOfPatrols()).append("\n");
        simulationSummaryMessage.append("Simulated Interventions: ").append(StatisticsCounter.getInstance().getNumberOfInterventions()).append("\n");
        simulationSummaryMessage.append("Simulated Firings: ").append(StatisticsCounter.getInstance().getNumberOfFirings()).append("\n");
        simulationSummaryMessage.append("Neutralized Patrols: ").append(StatisticsCounter.getInstance().getNumberOfNeutralizedPatrols()).append("\n");
        simulationSummaryMessage.append("Solved Interventions: ").append(StatisticsCounter.getInstance().getNumberOfSolvedInterventions()).append("\n");
        simulationSummaryMessage.append("Solved Firings: ").append(StatisticsCounter.getInstance().getNumberOfSolvedFirings()).append("\n");

        JOptionPane.showMessageDialog(frame, simulationSummaryMessage.toString());

        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

}
