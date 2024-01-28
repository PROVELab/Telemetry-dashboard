import javax.swing.*;
import java.awt.*;

public class LeftPanel extends JPanel {

    public LeftPanel() {
        setLayout(new GridLayout(32, 1));

        // Add 32 mini-elements
        for (int i = 0; i < 32; i++) {
            JPanel miniElement = new JPanel();
            miniElement.setLayout(new BorderLayout());
            miniElement.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border

            JLabel label = new JLabel("Sensor Number " + (i + 1));
            miniElement.add(label, BorderLayout.CENTER);

            JPanel statusIndicator = new JPanel();
            statusIndicator.setPreferredSize(new Dimension(25, 10)); // Set size

            // Set color based on status
            if (i % 3 == 0) {
                statusIndicator.setBackground(new Color(76, 175, 80)); // Subtle green
            } else if (i % 3 == 1) {
                statusIndicator.setBackground(new Color(255, 235, 59)); // Subtle yellow
            } else {
                statusIndicator.setBackground(new Color(244, 67, 54)); // Subtle red
            }

            miniElement.add(statusIndicator, BorderLayout.EAST);

            add(miniElement);
        }
    }
}
