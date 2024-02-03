import javax.swing.*;
import javax.swing.border.LineBorder;

import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainPanel extends JPanel {

    private DefaultCategoryDataset dataset1;
    private DefaultCategoryDataset dataset2;
    private DefaultCategoryDataset dataset3;
    private DefaultCategoryDataset dataset4;
    private ChartPanel chartPanel1;
    private ChartPanel chartPanel2;
    private ChartPanel chartPanel3;
    private ChartPanel chartPanel4;
    private static List<JFreeChart> charts = new ArrayList<>();


    public MainPanel() {
        
        setLayout(new GridLayout(2, 2)); // Set layout to a 2x2 grid

        // Initialize datasets
        dataset1 = new DefaultCategoryDataset();
        dataset2 = new DefaultCategoryDataset();
        dataset3 = new DefaultCategoryDataset();
        dataset4 = new DefaultCategoryDataset();

        // Create default charts
        JFreeChart lineChart1 = createChart(dataset1, "Sensor Data 1");
        JFreeChart lineChart2 = createChart(dataset2, "Sensor Data 2");
        JFreeChart lineChart3 = createChart(dataset3, "Sensor Data 3");
        JFreeChart lineChart4 = createChart(dataset4, "Sensor Data 4");
        charts.add(lineChart1);
        charts.add(lineChart2);
        charts.add(lineChart3);
        charts.add(lineChart4);

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

        // Create a DropTarget and DropTargetListener
        DropTargetListener dtl = new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    // Get the dropped data (currently fetching name of sensor)
                    String droppedData = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
        
                    // Get the chart panel where the data was dropped
                    ChartPanel droppedChartPanel = (ChartPanel) dtde.getDropTargetContext().getComponent();
        
                    // Update the chart with the dropped data
                    JFreeChart chart = droppedChartPanel.getChart();
                    chart.setTitle(droppedData);
        
                    // Repaint the chart to apply the changes
                    droppedChartPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        // Create a DropTarget for each chart panel and set the DropTarget of each chart panel
        chartPanel1.setDropTarget(new DropTarget(chartPanel1, dtl));
        chartPanel2.setDropTarget(new DropTarget(chartPanel2, dtl));
        chartPanel3.setDropTarget(new DropTarget(chartPanel3, dtl));
        chartPanel4.setDropTarget(new DropTarget(chartPanel4, dtl));

        //Add sample data to the charts
        Random random = new Random();
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Generate random data for each sensor
                double sensorData1 = random.nextDouble();
                double sensorData2 = random.nextDouble();
                double sensorData3 = random.nextDouble();
                double sensorData4 = random.nextDouble();
        
                // Get the current time as a string
                String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        
                // Add the new data using the addData method
                addData("Sensor 1", sensorData1, currentTime, 1);
                addData("Sensor 2", sensorData2, currentTime, 2);
                addData("Sensor 3", sensorData3, currentTime, 3);
                addData("Sensor 4", sensorData4, currentTime, 4);
            }
        });

        darkenCharts(); // Set the initial color of the charts to dark mode
        
        // Start the timer
        timer.start();
    }

    //Chart Axis Labels and Frame
    private JFreeChart createChart(DefaultCategoryDataset dataset, String title) {
        return ChartFactory.createLineChart(
            title,
            "Time",
            "Value",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false);
    }

    //Method to add data to the datasets
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

    public static void lightenCharts() {
        for (JFreeChart chart : charts) {
            chart.getTitle().setPaint(Color.BLACK);
            chart.setBackgroundPaint(Color.WHITE);
            chart.getPlot().setBackgroundPaint(Color.WHITE);
            chart.getLegend().setBackgroundPaint(Color.WHITE);
            chart.getLegend().setItemPaint(Color.DARK_GRAY);
            CategoryPlot plot = chart.getCategoryPlot();

            // Change the color of the tick labels on the X axis
            CategoryAxis xAxis = plot.getDomainAxis();
            xAxis.setTickLabelPaint(Color.BLACK);
            xAxis.setLabelPaint(Color.BLACK);

            // Change the color of the tick labels on the Y axis
            ValueAxis yAxis = plot.getRangeAxis();
            yAxis.setTickLabelPaint(Color.BLACK);
            yAxis.setLabelPaint(Color.BLACK);
        }
    }

    public static void darkenCharts() {
        for (JFreeChart chart : charts) {
            chart.getTitle().setPaint(Color.WHITE);
            chart.setBackgroundPaint(Color.DARK_GRAY);
            chart.getPlot().setBackgroundPaint(Color.DARK_GRAY);
            chart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
            chart.getLegend().setItemPaint(Color.WHITE);
            CategoryPlot plot = chart.getCategoryPlot();

            // Change the color of the tick labels on the X axis
            CategoryAxis xAxis = plot.getDomainAxis();
            xAxis.setTickLabelPaint(Color.WHITE);
            xAxis.setLabelPaint(Color.WHITE);


            // Change the color of the tick labels on the Y axis
            ValueAxis yAxis = plot.getRangeAxis();
            yAxis.setTickLabelPaint(Color.WHITE);
            yAxis.setLabelPaint(Color.WHITE);
        }
    }
}