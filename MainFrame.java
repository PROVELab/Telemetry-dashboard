import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        //Title for App
        super("Telemetry Dashboard");
    
        // Left Panel with Scrollbar
        LeftPanel leftPanel = new LeftPanel();
        Sensor[] sensors = leftPanel.getSensors();
        leftPanel.setPreferredSize(new Dimension(210, 100)); // Set preferred width to 400
        int leftPanelPreferredWidth = 210;
        JScrollPane scrollPane = new JScrollPane(leftPanel);
        scrollPane.setMaximumSize(new Dimension(400, Integer.MAX_VALUE)); // Set maximum width to 400
    
        // Main Panel with Graph Components
        MainPanel mainPanel = new MainPanel(sensors);
    
        // Create expand/collapse button for the Left Panel
        JButton expandButton = new JButton("<");
        Dimension buttonSize = new Dimension(37, 37); // Set button size
        expandButton.setPreferredSize(buttonSize);
        expandButton.setMinimumSize(buttonSize);
        expandButton.setMaximumSize(buttonSize);

        // Create a button to toggle night mode
        JButton nightModeButton = new JButton(MainApp.getNightModeIcon());
        nightModeButton.addActionListener(e -> {
            MainApp.toggleNightMode();
            nightModeButton.setIcon(MainApp.getNightModeIcon()); // Update the button icon
        });

        // Add the button to the frame
        add(nightModeButton, BorderLayout.NORTH);
    
        // Create a panel for the button with a flow layout aligned to the right
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        //Add the buttons to the button panel
        buttonPanel.add(expandButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Add some space between the buttons
        buttonPanel.add(nightModeButton);
    
        // Split Pane to combine Left and Main Panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, mainPanel);

        splitPane.setDividerLocation(leftPanelPreferredWidth);

        //Logic for Expand/Collapse button
        expandButton.addActionListener(e -> {
            if (splitPane.getDividerLocation() < 50) {
                splitPane.setDividerLocation(leftPanelPreferredWidth); // Expand
                expandButton.setText("<"); // Change button text to "<"
            } else {
                splitPane.setDividerLocation(0); // Collapse
                expandButton.setText(">"); // Change button text to ">"
            }
        });
    
        // Create a panel for the split pane and the button
        JPanel splitPanePanel = new JPanel(new BorderLayout());
        splitPanePanel.add(buttonPanel, BorderLayout.WEST);
        splitPanePanel.add(splitPane, BorderLayout.CENTER);
    
        // Set layout and add split pane panel to the frame
        setLayout(new BorderLayout());
        add(splitPanePanel, BorderLayout.CENTER);
    }
    
}

