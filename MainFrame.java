import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Telemetry Dashboard");
    
        // Left Panel with Scrollbar
        LeftPanel leftPanel = new LeftPanel();
        leftPanel.setPreferredSize(new Dimension(210, 100)); // Set preferred width to 400
        int leftPanelPreferredWidth = 210;
        JScrollPane scrollPane = new JScrollPane(leftPanel);
        scrollPane.setMaximumSize(new Dimension(400, Integer.MAX_VALUE)); // Set maximum width to 400
    
        // Main Panel with Graph Components
        MainPanel mainPanel = new MainPanel();
    
        // Create expand/collapse button
        JButton expandButton = new JButton("<");
        expandButton.setPreferredSize(new Dimension(40, 40)); // Set button size
    
        // Create a panel for the button with a flow layout aligned to the right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(expandButton);
    
        // Split Pane to combine Left and Main Panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, mainPanel);

        splitPane.setDividerLocation(leftPanelPreferredWidth);

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

