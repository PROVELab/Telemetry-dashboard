import javax.swing.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

record Sensor(String name, String range1, String range2) {
    Double[] getRange1(){
        Double d[]  = new Double[2];
        int i = 0;
        for (String s : range1.strip().split("-")){
            d[i++] = Double.parseDouble(s);
        }
        return d;
    }
    Double[] getRange2(){
        Double d[]  = new Double[2];
        int i = 0;
        for (String s : range2.strip().split("-")){
            d[i++] = Double.parseDouble(s);
        }
        return d;
    }
}
public class LeftPanel extends JPanel {
    boolean hovered = false;

    private ArrayList<Sensor> sensorList = new ArrayList<>();
    public Sensor[] getSensors(){
        return sensorList.toArray(new Sensor[]{});
    }
    private static HashMap<String, JPanel> sensorStatus = new HashMap<>();

    public LeftPanel() {
        setLayout(new GridLayout(32, 1));

        // Read sensor data from the sensor.txt file
        try (BufferedReader reader = new BufferedReader(new FileReader("sensors.txt"))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                JPanel miniElement = new JPanel();
                miniElement.setLayout(new BorderLayout());
                miniElement.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border
 
                //Fetch the name from the txt input
                String[] arr = line.split(",");
                
                
                JLabel sensorLabel = new JLabel(arr[0]);
                String range1 = arr[1];
                String range2 = arr[2];

                Sensor s = new Sensor(sensorLabel.getText(), range1, range2);
                sensorList.add(s);
                miniElement.add(sensorLabel, BorderLayout.CENTER);

                //Status indicator to see if sensor is in the range
                JPanel statusIndicator = new JPanel();
                statusIndicator.setPreferredSize(new Dimension(25, 10)); // Set size
                statusIndicator.setBackground(new Color(76, 175, 80)); // Set default color to green
                sensorStatus.put(sensorLabel.getText(), statusIndicator);

                miniElement.add(statusIndicator, BorderLayout.EAST);
                add(miniElement);
                
                JPopupMenu jpm = new JPopupMenu(){
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(300, 300);
                    }
                };

                miniElement.addMouseListener(new MouseListener() {
                    public int x = 0;
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getClickCount()==2){
                            ChartPanel cp = reconstructChart(miniElement);
                            MainPanel.chartPanels.get(x).removeAll();
                            ((ChartPanel)MainPanel.chartPanels.get(x)).setChart(cp.getChart());
                            MainPanel.chartPanels.get(x).repaint();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        // TODO Auto-generated method stub
                        jpm.setVisible(false);
                        jpm.removeAll();
                        jpm.setMaximumSize(new Dimension(100, 100));
                        jpm.setMinimumSize(new Dimension(100, 100));

                        ChartPanel cp = reconstructChart(miniElement);
                        if (cp != null){
                            jpm.add(cp);
                            jpm.setVisible(true);
                            hovered=true;
                        }                 
                    }

                    public static ChartPanel reconstructChart(JPanel miniElement) {
                        for (Component c : miniElement.getComponents()){
                            if (c instanceof JLabel){
                                Sensor s = MainPanel.snameToObject.get(((JLabel) c).getText());
                                JFreeChart chart = MainPanel.temp_chart;

                                chart.setTitle(s.name());
                                chart.getXYPlot().setDataset(MainPanel.hmap.get(s));
                                ((NumberAxis) (chart.getXYPlot().getDomainAxis())).setNumberFormatOverride(NumberFormat.getNumberInstance());
                                
                                chart.getXYPlot().clearRangeMarkers();
                                // add yellow ranges
                                for (Double d : s.getRange1()){
                                    ValueMarker marker = new ValueMarker(d);
                                    marker.setPaint(Color.YELLOW);
                                    chart.getXYPlot().addRangeMarker(marker);
                                }
                    
                                for (Double d : s.getRange2()){
                                    ValueMarker marker = new ValueMarker(d);
                                    marker.setPaint(Color.RED);
                                    chart.getXYPlot().addRangeMarker(marker);
                                }
                                return new ChartPanel(chart);
                            }
                        }
                        return null;
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        jpm.setVisible(false);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                this.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseDragged(MouseEvent arg0) {}
                    @Override
                    public void mouseMoved(MouseEvent arg0) {
                        hovered = false;
                        jpm.setVisible(false);
                    }
                });

                //Make the sensors a drag source so someone can grab the sensor id/label from it
                DragSource ds = new DragSource();
                DragGestureListener dgl = new DragGestureListener() {
                    public void dragGestureRecognized(DragGestureEvent dge) {
                        try {
                            // Create an image of the panel
                            BufferedImage image = new BufferedImage(miniElement.getWidth(), miniElement.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            Graphics g = image.getGraphics();
                            miniElement.paint(g);
                    
                            // Use the image as the drag image
                            Image dragImage = Toolkit.getDefaultToolkit().createImage(image.getSource());
                            Point dragOffset = new Point(0, 0);
                    
                            // Start the drag
                            ds.startDrag(dge, DragSource.DefaultCopyDrop, dragImage, dragOffset, new StringSelection(sensorLabel.getText()), new DragSourceAdapter() {});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ds.createDefaultDragGestureRecognizer(sensorLabel, DnDConstants.ACTION_COPY, dgl);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Setter method to change the status indicator of the sensors
    public static void setStatusIndicator(Sensor sensor, Double data) {
        double lowerLimit1 = sensor.getRange1()[0];
        double upperLimit1 = sensor.getRange1()[1];
        double lowerLimit2 = sensor.getRange2()[0];
        double upperLimit2 = sensor.getRange2()[1];
        
        if (data >= lowerLimit1 && data <= upperLimit1) {
            sensorStatus.get(sensor.name()).setBackground(new Color(76, 175, 80)); // Subtle green
        } else if (data >= lowerLimit2 && data <= upperLimit2) {
            sensorStatus.get(sensor.name()).setBackground(new Color(255, 235, 59)); // Subtle yellow
        } else {
            sensorStatus.get(sensor.name()).setBackground(new Color(244, 67, 54)); // Subtle red
        }
    }

}
