import javax.swing.*;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.dnd.*;
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


    private ArrayList<Sensor> sensorList = new ArrayList<>();
    public Sensor[] getSensors(){
        return sensorList.toArray(new Sensor[]{});
    }
    private HashMap<String, JPanel> sensorStatus = new HashMap<>();

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
    public void setStatusIndicator(Sensor sensor, Double data) {
        if(data < Double.parseDouble(sensor.range1().split("-")[0]) || data > Double.parseDouble(sensor.range1().split("-")[1])){
            sensorStatus.get(sensor.name()).setBackground(new Color(255, 235, 59)); // Subtle yellow
        }
        if(data < Double.parseDouble(sensor.range2().split("-")[0]) || data > Double.parseDouble(sensor.range2().split("-")[1])) {
            sensorStatus.get(sensor.name()).setBackground(new Color(244, 67, 54)); // Subtle red
        }
        if(data > Double.parseDouble(sensor.range1().split("-")[0]) || data < Double.parseDouble(sensor.range1().split("-")[1])){
            sensorStatus.get(sensor.name()).setBackground(new Color(76, 175, 80)); // Subtle green
        }
    }

}
