/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.TECPROG.Controller;

import static com.mycompany.TECPROG.Controller.Main.lastFileDownloaded;
import com.mycompany.TECPROG.Model.CoordenadaGNSS;
import com.mycompany.TECPROG.Model.GNSSDate;
import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

/**
 *
 * @author Rogerio
 */
public class LocalFiles_Handler {
    
    private static ArrayList<CoordenadaGNSS> listaCoordPrecisasLidas;
    private static StringBuilder sb;     
    
    public static String read_textFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        sb = new StringBuilder();

        String mLine;

        while ((mLine = reader.readLine()) != null) {
            sb.append(mLine);
            sb.append("\n");
        }
                    
        reader.close();
        return sb.toString();
    }
    
    public static String read_SP3(String fileName, String aPRN) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        sb = new StringBuilder();
        listaCoordPrecisasLidas = new ArrayList<>();
        
        // PULANDO O CABEÇALHO
        String mLine = reader.readLine();
        int cont = 0;
        while( cont < 21) {
            mLine = reader.readLine();
            cont++;
        }

        cont = 0;
        mLine = reader.readLine();

        while ( !mLine.equals("EOF") ){

            if (mLine.isEmpty()) break; // Last line of the file                       
           
            try {
                int    year    = Integer.valueOf(mLine.substring( 3,  7).trim());
                int    month   = Integer.valueOf(mLine.substring( 8, 10).trim());
                int    day     = Integer.valueOf(mLine.substring(11, 13).trim());
                int    hour    = Integer.valueOf(mLine.substring(14, 16).trim());
                int    minutes = Integer.valueOf(mLine.substring(17, 19).trim());
                double seconds = Double .valueOf(mLine.substring(20, 31).trim());

                GNSSDate data = new GNSSDate(year, month, day, hour, minutes, seconds);
//              efemeride.setGNSSDate(data);

                while ( (mLine = reader.readLine()).startsWith("P") ) {

                    String PRN = mLine.substring(1, 4);

                    double  X  = Double.valueOf(mLine.substring( 5, 18).trim());
                    double  Y  = Double.valueOf(mLine.substring(19, 32).trim());
                    double  Z  = Double.valueOf(mLine.substring(33, 46).trim());
                    double dts = Double.valueOf(mLine.substring(47, 60).trim());

                    CoordenadaGNSS novaCoord = new CoordenadaGNSS(PRN, X, Y, Z, dts);                   
                      
                        if (PRN.equals(aPRN)) {
                            getListaCoordPrecisasLidas().add(novaCoord);
                            
                            getSb().append(PRN).append(" ");
                            getSb().append(data.toString()).append(" ").append(X).
                                                       append(" ").append(Y).
                                                       append(" ").append(Z).
                                                       append(" ").append(dts);
                            getSb().append("\n");
                            
                            cont++;
                            System.out.println("Epoca nº: " + cont + " " + data.toString() + "\n" + novaCoord.toString());
                        }
                }
            } catch (Exception err) {
                err.printStackTrace();                
            }
        }
                
        reader.close();
        return getSb().toString();
    }

    /**
     * @return the listaCoordPrecisasLidas
     */
    public static ArrayList<CoordenadaGNSS> getListaCoordPrecisasLidas() {
        return listaCoordPrecisasLidas;
    }

    /**
     * @return the sb
     */
    public static StringBuilder getSb() {
        return sb;
    }    
    
}
