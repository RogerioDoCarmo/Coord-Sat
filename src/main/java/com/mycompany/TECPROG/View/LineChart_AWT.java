/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.TECPROG.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

public class LineChart_AWT extends ApplicationFrame {

   public LineChart_AWT( String applicationTitle , String chartTitle ) {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         "Years","Number of Schools",
         createDataset(),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }

   private DefaultCategoryDataset createDataset( ) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      dataset.addValue( 15 , "schools" , "1970" );
      dataset.addValue( 30 , "schools" , "1980" );
      dataset.addValue( 60 , "schools" ,  "1990" );
      dataset.addValue( 120 , "schools" , "2000" );
      dataset.addValue( 240 , "schools" , "2010" );
      dataset.addValue( 300 , "schools" , "2014" );
      return dataset;
   }
   
   private DefaultCategoryDataset createDataset_example( ) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      dataset.addValue( 15 , "schools" , "1970" );
      dataset.addValue( 30 , "schools" , "1980" );
      dataset.addValue( 60 , "schools" ,  "1990" );
      dataset.addValue( 120 , "schools" , "2000" );
      dataset.addValue( 240 , "schools" , "2010" );
      dataset.addValue( 300 , "schools" , "2014" );
      return dataset;
   }
   
   public static void main( String[ ] args ) throws IOException {
      LineChart_AWT chart = new LineChart_AWT(
         "School Vs Years" ,
         "Numer of Schools vs years");

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
      
      String fileName     = "C:\\Users\\Rogerio\\Desktop\\TEC\\Ephemeris_Yesterday.Z";
      File downloadedFile = new File(fileName);
      
      LocalDate now =  LocalDate.now();
      final int TODAY_DAY_OF_YEAR = now.getDayOfYear();
      final int YESTERDAY_DAY_OF_YEAR = TODAY_DAY_OF_YEAR - 1;
                
       String server_name = "cddis.gsfc.nasa.gov";
       int port_number = 21;
       String user = "anonymous";
       String senha = "";
//       String fileName_toDownload = "gps/data/daily/2018/304/18n/brdc3040.19n.Z";
       String fileName_toDownload = "gps/data/daily/2019/" + YESTERDAY_DAY_OF_YEAR + 
                                       "/19n/brdc" + YESTERDAY_DAY_OF_YEAR + "0.19n.Z";

       FTP_Handler ftp = new FTP_Handler(server_name, port_number, user, senha,
                                         fileName_toDownload, downloadedFile);
       ftp.downloadAndSaveFile();
       retorno = ftp.getmNewFile();
       
       decompressZfile();

   }
   
   static File retorno;
   static private void decompressZfile() {
           
        String fileName     = "C:\\Users\\Rogerio\\Desktop\\TEC\\brdc_yesterday.19n";
        File extractedFile = new File(fileName);
           
        try {
            FileInputStream fin = new FileInputStream(retorno);
            BufferedInputStream in = new BufferedInputStream(fin);

            FileOutputStream out = new FileOutputStream(extractedFile);
            ZCompressorInputStream zIn = new ZCompressorInputStream(in);

            final byte[] buffer = new byte[(int) retorno.length() / Byte.SIZE];

            int n = 0;
            while (-1 != (n = zIn.read(buffer))) {
                out.write(buffer, 0, n);
            }

            out.close();
            zIn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
   
}