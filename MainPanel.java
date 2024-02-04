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
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainPanel extends JPanel {

    private ChartPanel chartPanel1;
    private ChartPanel chartPanel2;
    private ChartPanel chartPanel3;
    private ChartPanel chartPanel4;
    private static List<JFreeChart> charts = new ArrayList<>();
    private static List<JPanel> chartPanels = new ArrayList<>();
    
    private HashMap<String, Sensor> snameToObject = new HashMap<>();

    private HashMap<Sensor, XYSeriesCollection> hmap = new HashMap<>();
    private HashMap<Sensor, XYSeries> seriesmap = new HashMap<>();


    final int MAX_ELEMENTS_TO_SHOW = 10;
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
            hmap.put(s, xysc);
            seriesmap.put(s, ser);
        }

        // Create default charts
        JFreeChart lineChart1 = createChart(hmap.get(snameToObject.get("Test Sensor1")), "Test Sensor1");
        JFreeChart lineChart2 = createChart(hmap.get(snameToObject.get("Test Sensor2")), "Test Sensor2");
        JFreeChart lineChart3 = createChart(hmap.get(snameToObject.get("Test Sensor3")), "Test Sensor3");
        JFreeChart lineChart4 = createChart(hmap.get(snameToObject.get("Test Sensor4")), "Test Sensor4");
        charts = Arrays.asList(new JFreeChart[]{lineChart1, lineChart2, lineChart3, lineChart4});
        charts.stream().forEach((r)->{
                r.getXYPlot().getRendererForDataset(r.getXYPlot().getDataset()).setSeriesPaint(0, Color.WHITE);
                r.getXYPlot().getRenderer().setDefaultStroke(new BasicStroke(4.0f));
                ((AbstractRenderer) r.getXYPlot().getRenderer()).setAutoPopulateSeriesStroke(false);


                r.getXYPlot().getRangeAxis().setRange(0, 1);
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
        chartPanel1 = new ChartPanel(lineChart1);
        chartPanel2 = new ChartPanel(lineChart2);
        chartPanel3 = new ChartPanel(lineChart3);
        chartPanel4 = new ChartPanel(lineChart4);
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
        
                    // Update the chart with the dropped data
                    JFreeChart chart = droppedChartPanel.getChart();
                    chart.setTitle(droppedData);
                    chart.getXYPlot().setDataset(hmap.get(snameToObject.get(droppedData)));
                    ((NumberAxis) (chart.getXYPlot().getDomainAxis())).setNumberFormatOverride(NumberFormat.getNumberInstance());
                    
                    Sensor s = snameToObject.get(droppedData);
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
                for (Sensor s : hmap.keySet()){
                    XYSeries series = seriesmap.get(s);
                    series.add((double)(System.currentTimeMillis() - startTime)/1000, random.nextGaussian(0.5, 0.2));
                }
            }
        });

        darkenCharts(); // Set the initial color of the charts to dark mode

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            try {
                for (Sensor s : hmap.keySet()){
                    XYSeries series = seriesmap.get(s);
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
}