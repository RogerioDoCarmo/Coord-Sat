/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.TECPROG.View;

import com.mycompany.TECPROG.Model.CoordenadaGNSS;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//import org.jfree.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Rogerio
 */
public class UI_Graph_Multiline extends JFrame { // the class extends the JFrame class

    String chartTitle;
    String xAxisLabel;
    String yAxisLabel;
    ArrayList<CoordenadaGNSS> listaCoord;
    
    public UI_Graph_Multiline(String chartTitle, String xAxisLabel, String yAxisLabel, String PRN,
                              ArrayList<CoordenadaGNSS> lista) {
        super("Trabalho Técnicas de Programação - Rogério do Carmo : PPGC - FCT/UNESP");

        this.chartTitle = chartTitle + " " + PRN;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.listaCoord = lista;
        
        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);
        
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza o grafico na tela
    }

    private JPanel createChartPanel() {

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                                            xAxisLabel, yAxisLabel, dataset,
                                            PlotOrientation.VERTICAL,
                                            true,true,false);

        customizeChart(chart);

        // saves the chart as an image files
        File imageFile = new File("C:\\Users\\Rogerio\\Desktop\\TEC\\LineChart.png");
        int width  = 640;
        int height = 480;

        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
        } catch (IOException ex) {
            System.err.println(ex);
        }

        return new ChartPanel(chart);
    }

    public  XYDataset createDataset() {    // this method creates the data as time seris 
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        XYSeries seriesX = new XYSeries("X [Km]");
        XYSeries seriesY = new XYSeries("Y [Km]");
        XYSeries seriesZ = new XYSeries("Z [Km]");

        float hour = 0.0f;
        
        for (int i = 0; i < listaCoord.size(); i++) {
            seriesX.add(hour,listaCoord.get(i).getX());
            seriesY.add(hour,listaCoord.get(i).getY());
            seriesZ.add(hour,listaCoord.get(i).getZ());
            
            hour += 0.25; // + 15 minutos
        }
        
//        seriesX.add
//        seriesX.add(1.0, 2.0);
//        seriesX.add(2.0, 3.0);
//        seriesX.add(3.0, 2.5);
//        seriesX.add(3.5, 2.8);
//        seriesX.add(4.2, 6.0);
//
//        seriesY.add(2.0, 1.0);
//        seriesY.add(2.5, 2.4);
//        seriesY.add(3.2, 1.2);
//        seriesY.add(3.9, 2.8);
//        seriesY.add(4.6, 3.0);
//
//        seriesZ.add(1.2, 4.0);
//        seriesZ.add(2.5, 4.4);
//        seriesZ.add(3.8, 4.2);
//        seriesZ.add(4.3, 3.8);
//        seriesZ.add(4.5, 4.0);

        dataset.addSeries(seriesX);
        dataset.addSeries(seriesY);
        dataset.addSeries(seriesZ);

        return dataset;
    }

    private void customizeChart(JFreeChart chart) {   // here we make some customization
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);

        // sets thickness for series (using strokes)
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));

        // sets paint color for plot outlines
        plot.setOutlinePaint(Color.BLUE);
        plot.setOutlineStroke(new BasicStroke(2.0f));

        // sets renderer for lines
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

    }

    public static void main(String[] args, ArrayList<CoordenadaGNSS> lista) {
        //SwingUtilities.invokeLater(new Runnable() {
          //  @Override
            //public void run() {
            UI_Graph_Multiline grafico =  new UI_Graph_Multiline(args[0], args[1], args[2], args[3], lista);
            grafico.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            grafico.setVisible(true);
            //}
        //});
    }

}
