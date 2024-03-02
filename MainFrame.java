import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MainFrame extends JFrame {

    private static JToggleButton addSensorButton = new JToggleButton();

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
        buttonPanel.add(Box.createVerticalStrut(10)); // Add some space between the buttons
    
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

        // Create the slider
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPreferredSize(new Dimension(300, 50)); // Set a preferred size for the slider

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    MainPanel.setMaxElementsToShow((MainPanel.getMaxElementsToShow() + source.getValue() > 100)  ? 100: MainPanel.getMaxElementsToShow() + source.getValue());
                    MainPanel.updateCharts();
                }
            }
        });

        // Create a JDialog to act as a pop-up panel for the slider
        JDialog dialog = new JDialog();
        dialog.setTitle("Sensor Range");
        dialog.setLayout(new BorderLayout());
        dialog.add(slider, BorderLayout.CENTER); // Add the slider to the dialog
        dialog.setPreferredSize(new Dimension(500,100));
        dialog.pack();

        //Setting the icon for the Clock
        String iconName = "resources/clock.png";
        ImageIcon icon = new ImageIcon(iconName);
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newImage);

        //Set the icon for multi
        String multiIconName = "resources/multi.png";
        ImageIcon multiIcon = new ImageIcon(multiIconName);
        Image multiImage = multiIcon.getImage();
        Image newMultiImage = multiImage.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        multiIcon = new ImageIcon(newMultiImage);
        addSensorButton.setIcon(multiIcon);

        // Create the button
        JButton sliderButton = new JButton(icon);
        sliderButton.addActionListener(e -> {
            dialog.setVisible(!dialog.isVisible()); // Toggle the visibility of the dialog
        });
        slider.setPreferredSize(buttonSize);

        // Add the button to the button panel
        buttonPanel.add(sliderButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Add some space between the buttons
        //Add the the multi button
        buttonPanel.add(addSensorButton);

        // Create a panel for the split pane and the button
        JPanel splitPanePanel = new JPanel(new BorderLayout());
        splitPanePanel.add(buttonPanel, BorderLayout.WEST);
        splitPanePanel.add(splitPane, BorderLayout.CENTER);
    
        // Set layout and add split pane panel to the frame
        setLayout(new BorderLayout());
        add(splitPanePanel, BorderLayout.CENTER);
    }

    public static boolean getMultiStatus(){
        if (addSensorButton.isSelected()){
            return true;
        }
        else{
            return false;
        }
    }
    
}

