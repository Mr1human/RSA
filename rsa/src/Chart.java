import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class Chart extends JFrame {
    public void createGraph(String title, String nameX, String nameY, XYDataset xyDataset){
        JFreeChart chart = ChartFactory.createXYLineChart(title, nameX, nameY, xyDataset);
        XYPlot plot = (XYPlot) chart.getPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0,true);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public void createCombinedGraph(String title, String nameX, String nameY, XYDataset[] datasets, Color[] colors) {
        JFreeChart chart = ChartFactory.createXYLineChart(title, nameX, nameY, null);
        XYPlot plot = (XYPlot) chart.getPlot();

        for (int i = 0; i < datasets.length; i++) {
            plot.setDataset(i, datasets[i]);
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesStroke(0, new BasicStroke(2.5f));
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesPaint(0, colors[i]);
            plot.setRenderer(i, renderer);
        }

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis(); // Получаем вертикальную ось графика
        yAxis.setAutoRangeIncludesZero(false); // Устанавливаем автоматическое включение нуля в масштаб
        yAxis.setAutoRangeStickyZero(false);

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Combined Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }



}
