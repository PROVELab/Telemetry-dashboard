import javax.swing.*;
import javax.swing.border.LineBorder;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;

public class MainPanel extends JPanel {

    private DefaultCategoryDataset dataset1;
    private DefaultCategoryDataset dataset2;
    private DefaultCategoryDataset dataset3;
    private DefaultCategoryDataset dataset4;
    private ChartPanel chartPanel1;
    private ChartPanel chartPanel2;
    private ChartPanel chartPanel3;
    private ChartPanel chartPanel4;

    public MainPanel() {
        setLayout(new GridLayout(2, 2)); // Set layout to a 2x2 grid

        // Initialize datasets
        dataset1 = new DefaultCategoryDataset();
        dataset2 = new DefaultCategoryDataset();
        dataset3 = new DefaultCategoryDataset();
        dataset4 = new DefaultCategoryDataset();

        // Create charts
        JFreeChart lineChart1 = createChart(dataset1, "Sensor Data 1");
        JFreeChart lineChart2 = createChart(dataset2, "Sensor Data 2");
        JFreeChart lineChart3 = createChart(dataset3, "Sensor Data 3");
        JFreeChart lineChart4 = createChart(dataset4, "Sensor Data 4");

        // Create chart panels
        chartPanel1 = new ChartPanel(lineChart1);
        chartPanel2 = new ChartPanel(lineChart2);
        chartPanel3 = new ChartPanel(lineChart3);
        chartPanel4 = new ChartPanel(lineChart4);

        //Set border for the chart panels
        chartPanel1.setBorder(new LineBorder(Color.BLACK));
        chartPanel2.setBorder(new LineBorder(Color.BLACK));
        chartPanel3.setBorder(new LineBorder(Color.BLACK));
        chartPanel4.setBorder(new LineBorder(Color.BLACK));

        // Add chart panels to the main panel
        add(chartPanel1);
        add(chartPanel2);
        add(chartPanel3);
        add(chartPanel4);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset, String title) {
        return ChartFactory.createLineChart(
            title,
            "Time",
            "Value",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false);
    }

    public void addData(String seriesName, double value, String category, int chartNumber) {
        switch (chartNumber) {
            case 1:
                dataset1.addValue(value, seriesName, category);
                break;
            case 2:
                dataset2.addValue(value, seriesName, category);
                break;
            case 3:
                dataset3.addValue(value, seriesName, category);
                break;
            case 4:
                dataset4.addValue(value, seriesName, category);
                break;
        }
    }
}