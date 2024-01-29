import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;

public class LeftPanel extends JPanel {

    public LeftPanel() {
        setLayout(new GridLayout(32, 1));

        // Read sensor data from the sensor.txt file
        try (BufferedReader reader = new BufferedReader(new FileReader("sensors.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JPanel miniElement = new JPanel();
                miniElement.setLayout(new BorderLayout());
                miniElement.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border
 
                //Fetch the name from the txt input
                String[] arr = line.split(",");
                JLabel sensorLabel = new JLabel(arr[0]);
                miniElement.add(sensorLabel, BorderLayout.CENTER);

                //TODO: Eventually grab the acceptable range for each sensor and have the indicator change color if the data is out of range

                //Status indicator to see if sensor is in the range
                JPanel statusIndicator = new JPanel();
                statusIndicator.setPreferredSize(new Dimension(25, 10)); // Set size

                // Set color based on status
                if (line.contains("green")) {
                    statusIndicator.setBackground(new Color(76, 175, 80)); // Subtle green
                } else if (line.contains("yellow")) {
                    statusIndicator.setBackground(new Color(255, 235, 59)); // Subtle yellow
                } else if (line.contains("red")) {
                    statusIndicator.setBackground(new Color(244, 67, 54)); // Subtle red
                }

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
}
