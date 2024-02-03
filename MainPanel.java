import javax.swing.*;
import javax.swing.border.LineBorder;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.HMSNumberFormat;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Random;

public class MainPanel extends JPanel {

    private DefaultCategoryDataset dataset1;
    private DefaultCategoryDataset dataset2;
    private DefaultCategoryDataset dataset3;
    private DefaultCategoryDataset dataset4;

    public HashMap<String, XYSeriesCollection> hmap = new HashMap<>();
    public HashMap<String, XYSeries> seriesmap = new HashMap<>();

    private ChartPanel chartPanel1;
    private ChartPanel chartPanel2;
    private ChartPanel chartPanel3;
    private ChartPanel chartPanel4;

    public long startTime = 0;

    public MainPanel(String[] sensors) {
        
        setLayout(new GridLayout(2, 2)); // Set layout to a 2x2 grid

        for (String s : sensors){
            hmap.put(s, new XYSeriesCollection());
            XYSeries ser = new XYSeries(s);
            ser.setMaximumItemCount(20);
            hmap.get(s).addSeries(ser);
            seriesmap.put(s, ser);
        }

        // Create default charts
        JFreeChart lineChart1 = createChart(hmap.get("Test Sensor1"), "Test Sensor1");
        JFreeChart lineChart2 = createChart(hmap.get("Test Sensor2"), "Test Sensor2");
        JFreeChart lineChart3 = createChart(hmap.get("Test Sensor3"), "Test Sensor3");
        JFreeChart lineChart4 = createChart(hmap.get("Test Sensor4"), "Test Sensor4");

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
                    chart.getXYPlot().setDataset(hmap.get(droppedData));
                    ((NumberAxis) (chart.getXYPlot().getDomainAxis())).setNumberFormatOverride(new HMSNumberFormat());
        
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
        Timer timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (String s : hmap.keySet()){
                    System.out.println("Updating data: " + s);
                    XYSeries series = seriesmap.get(s);
                    series.add((double)(System.currentTimeMillis() - startTime)/1000, random.nextGaussian(5, 2));
                }
            }
        });
        
        // Start the timer
        timer.start();
        startTime = System.currentTimeMillis();
    }

    //Chart Axis Labels and Frame
    private JFreeChart createChart(XYSeriesCollection dataset, String title) {
        return ChartFactory.createXYLineChart(
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
}