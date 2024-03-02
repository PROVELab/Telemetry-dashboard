import javax.swing.*;
import javax.swing.border.LineBorder;
import org.jfree.chart.*;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Add the ability to dynimically change range of every graph
 */

public class MainPanel extends JPanel {

    private ChartPanel chartPanel1;
    private ChartPanel chartPanel2;
    private ChartPanel chartPanel3;
    private ChartPanel chartPanel4;
    private static List<JFreeChart> charts = new ArrayList<>();
    private static List<JPanel> chartPanels = new ArrayList<>();
    private static List<ChartPanel> chartPanelList = new ArrayList<>();
    
    private HashMap<String, Sensor> snameToObject = new HashMap<>();

    private HashMap<Sensor, SeriesData> seriesmap = new HashMap<>();
    private static final double currTime = System.currentTimeMillis();


    private static int MAX_ELEMENTS_TO_SHOW = 10;
    public long startTime = 0;

    public MainPanel(Sensor[] sensors) {
        setLayout(new GridLayout(2, 2)); // Set layout to a 2x2 grid
        
        for (Sensor s : sensors){
            snameToObject.put(s.name(), s);
            XYSeries ser = new XYSeries(s.name()){{
                setMaximumItemCount(MAX_ELEMENTS_TO_SHOW);
            }};
            XYSeriesCollection xysc = new XYSeriesCollection(){{
                addSeries(ser);
            }};
            SeriesData sd = new SeriesData(xysc, ser);
            seriesmap.put(s, sd);
        }

        // Create default charts
        var keys = snameToObject.keySet().toArray(new String[]{});
        int key_ind = 0;
        if (keys.length < 4){
            throw new IllegalArgumentException("Too few sensors");
        }
        JFreeChart lineChart1 = createChart(seriesmap.get(snameToObject.get(keys[key_ind])).getXYSeriesCollection(), keys[key_ind++]);
        JFreeChart lineChart2 = createChart(seriesmap.get(snameToObject.get(keys[key_ind])).getXYSeriesCollection(), keys[key_ind++]);
        JFreeChart lineChart3 = createChart(seriesmap.get(snameToObject.get(keys[key_ind])).getXYSeriesCollection(), keys[key_ind++]);
        JFreeChart lineChart4 = createChart(seriesmap.get(snameToObject.get(keys[key_ind])).getXYSeriesCollection(), keys[key_ind++]);

        charts = Arrays.asList(new JFreeChart[]{lineChart1, lineChart2, lineChart3, lineChart4});
        charts.stream().forEach((r)->{
                r.getXYPlot().getRendererForDataset(r.getXYPlot().getDataset()).setSeriesPaint(0, Color.WHITE);
                r.getXYPlot().getRenderer().setDefaultStroke(new BasicStroke(4.0f));
                ((AbstractRenderer) r.getXYPlot().getRenderer()).setAutoPopulateSeriesStroke(false);

                //Y axis label
                r.getXYPlot().getRangeAxis().setLabel(snameToObject.get(r.getTitle().getText()).yLabel());

                //X axis label
                r.getXYPlot().getDomainAxis().setLabel("Time (s)");

                //Auto adjust value scalling to where values are
                ((NumberAxis) r.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);

                for (Double d : snameToObject.get(r.getTitle().getText()).getRange1()){
                    ValueMarker marker = new ValueMarker(d);
                    marker.setPaint(Color.YELLOW);
                    r.getXYPlot().addRangeMarker(marker);
                }
    
                for (Double d : snameToObject.get(r.getTitle().getText()).getRange2()){
                    ValueMarker marker = new ValueMarker(d);
                    marker.setPaint(Color.RED);
                    r.getXYPlot().addRangeMarker(marker);
                }
            }
        );
        
        // Create chart panels
        chartPanel1 = new CustomChartPanel(lineChart1);
        chartPanel2 = new CustomChartPanel(lineChart2);
        chartPanel3 = new CustomChartPanel(lineChart3);
        chartPanel4 = new CustomChartPanel(lineChart4);
        chartPanelList = Arrays.asList(new ChartPanel[]{chartPanel1, chartPanel2, chartPanel3, chartPanel4});
        
        //Stop some bad stuff in graphs
        for (ChartPanel cp : chartPanelList){
            cp.setDomainZoomable(false); 
            cp.setRangeZoomable(false); 
            cp.setHorizontalAxisTrace(false);
            cp.setVerticalAxisTrace(false);
        }

        chartPanels = Arrays.asList(new JPanel[]{chartPanel1, chartPanel2, chartPanel3, chartPanel4});

        chartPanels.stream().forEach((s)->s.setBorder(new LineBorder(Color.BLACK)));

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
        
                     // Get the chart and the sensor
                    JFreeChart chart = droppedChartPanel.getChart();
                    Sensor s = snameToObject.get(droppedData);

                    // Check the state of the toggle button
                    if (MainFrame.getMultiStatus()) {
                        // Add the sensor data to the existing graph
                        XYSeriesCollection dataset = (XYSeriesCollection) chart.getXYPlot().getDataset();
                        dataset.addSeries(seriesmap.get(s).getXYSeries());
                        chart.setTitle("Multiple Sensors"); 
                    } else {
                        // Replace the existing graph with the new sensor data
                        XYSeriesCollection dataset = new XYSeriesCollection();
                        dataset.addSeries(seriesmap.get(s).getXYSeries());
                        chart.getXYPlot().setDataset(dataset);
                        chart.setTitle(droppedData);
                    }

                    // Update the chart title and format

                    ((NumberAxis) (chart.getXYPlot().getDomainAxis())).setNumberFormatOverride(NumberFormat.getNumberInstance());
                    ((NumberAxis) (chart.getXYPlot().getRangeAxis())).setNumberFormatOverride(NumberFormat.getNumberInstance());

                    chart.getXYPlot().clearRangeMarkers();
                    // add yellow ranges
                    for (Double d : s.getRange1()){
                        ValueMarker marker = new ValueMarker(d);
                        marker.setPaint(Color.YELLOW);
                        chart.getXYPlot().addRangeMarker(marker);
                    }
        
                    for (Double d : s.getRange2()){
                        ValueMarker marker = new ValueMarker(d);
                        marker.setPaint(Color.RED);
                        chart.getXYPlot().addRangeMarker(marker);
                    }

                    //Update y axis label
                    chart.getXYPlot().getRangeAxis().setLabel(s.yLabel());

                    //X axis label
                    chart.getXYPlot().getDomainAxis().setLabel("Time (s)");

                    // Repaint the chart to apply the changes
                    droppedChartPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        // Create a DropTarget for each chart panel and set the DropTarget of each chart panel
        chartPanels.stream().forEach((s)->s.setDropTarget(new DropTarget(s, dtl)));

        long startTime = System.currentTimeMillis();
        //Add sample data to the charts
        Random random = new Random();
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Sensor s : seriesmap.keySet()){
                    XYSeries series = seriesmap.get(s).getXYSeries();
                    double data = random.nextGaussian(10, 0.5);
                    series.add((double)(System.currentTimeMillis() - startTime)/1000, data);
                    //Send data to update the CSV
                    updateCSV(data, s, currTime);
                }
            }
        });

        darkenCharts(); // Set the initial color of the charts to dark mode

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            try {
                for (Sensor s : seriesmap.keySet()){
                    XYSeries series = seriesmap.get(s).getXYSeries();
                    if (series != null && series.getItemCount() > 0) {
                        LeftPanel.setStatusIndicator(s, series.getY(series.getItemCount()-1).doubleValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        
        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        
        // Start the timer
        timer.start();
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

    public static void lightenCharts() {
        for (JFreeChart chart : charts) {
            chart.getTitle().setPaint(Color.BLACK);
            chart.setBackgroundPaint(Color.WHITE);
            chart.getPlot().setBackgroundPaint(Color.WHITE);
            chart.getLegend().setBackgroundPaint(Color.WHITE);
            chart.getLegend().setItemPaint(Color.DARK_GRAY);
            XYPlot plot = chart.getXYPlot();

            // Change the color of the tick labels on the X axis
            ValueAxis xAxis = plot.getDomainAxis();
            xAxis.setTickLabelPaint(Color.BLACK);
            xAxis.setLabelPaint(Color.BLACK);

            // Change the color of the tick labels on the Y axis
            ValueAxis yAxis = plot.getRangeAxis();
            yAxis.setTickLabelPaint(Color.BLACK);
            yAxis.setLabelPaint(Color.BLACK);

            //Change the color of the line
            chart.getXYPlot().getRendererForDataset(chart.getXYPlot().getDataset()).setSeriesPaint(0, Color.DARK_GRAY);
        }
    }
    public static void darkenCharts() {
        for (JFreeChart chart : charts) {
            chart.getTitle().setPaint(Color.WHITE);
            chart.setBackgroundPaint(Color.DARK_GRAY);
            chart.getPlot().setBackgroundPaint(Color.DARK_GRAY);
            chart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
            chart.getLegend().setItemPaint(Color.WHITE);
            XYPlot plot = chart.getXYPlot();

            // Change the color of the tick labels on the X axis
            ValueAxis xAxis = plot.getDomainAxis();
            xAxis.setTickLabelPaint(Color.WHITE);
            xAxis.setLabelPaint(Color.WHITE);


            // Change the color of the tick labels on the Y axis
            ValueAxis yAxis = plot.getRangeAxis();
            yAxis.setTickLabelPaint(Color.WHITE);
            yAxis.setLabelPaint(Color.WHITE);

            //Change the color of the line
            chart.getXYPlot().getRendererForDataset(chart.getXYPlot().getDataset()).setSeriesPaint(0, Color.WHITE);
        }
    }

    public static void setMaxElementsToShow(int maxElementsToShow) {
        MAX_ELEMENTS_TO_SHOW = maxElementsToShow;
    }

    public static int getMaxElementsToShow() {
        return MAX_ELEMENTS_TO_SHOW;
    }

    public static void updateCharts() {
        for (JFreeChart chart : charts) {
            for (int i = 0; i < chart.getXYPlot().getDataset().getSeriesCount(); i++) {
                XYSeries series = ((XYSeriesCollection) chart.getXYPlot().getDataset()).getSeries(i);
                series.setMaximumItemCount(MAX_ELEMENTS_TO_SHOW);
            }
        }
    }

    public void updateCSV(double data, Sensor s, double currTime){
        try {
            File z = new File("data/");

            if (!z.exists()) {
                z.mkdir();
            }

            File f = new File("data/" + s.name() + ".csv");
            if (!f.exists()){
                f.createNewFile();
            }
            FileWriter writer = new FileWriter(f, true);
            writer.append(String.valueOf(Math.floorDiv( (long) (System.currentTimeMillis() - MainPanel.currTime), 1000)));
            writer.append(",");
            writer.append(String.valueOf(data));
            writer.append(",\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}