import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {

        super("Telemetry Dashboard");

        // Left Panel with Scrollbar
        LeftPanel leftPanel = new LeftPanel();
        JScrollPane scrollPane = new JScrollPane(leftPanel);

        // Main Panel with Graph Components
        MainPanel mainPanel = new MainPanel();

        // Split Pane to combine Left and Main Panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, mainPanel);
        splitPane.setOneTouchExpandable(true);

        // Set layout and add split pane to the frame
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }
    
}

