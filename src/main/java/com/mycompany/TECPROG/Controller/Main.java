package com.mycompany.TECPROG.Controller;

import com.mycompany.TECPROG.Model.CoordenadaGNSS;
import com.mycompany.TECPROG.View.UI_Graph_Multiline;
import com.mycompany.TECPROG.View.UI_Main_Form;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

/**
 *
 * @author Rogerio
 */
public class Main {
    
    static File lastFileDownloaded;
    public static ArrayList<CoordenadaGNSS> listaCoordPrecisasLidas = new ArrayList<>();
    private static String fileName_SP3;
    private static String fileName_Broadcast;
 
    static public void download_BRDCephemFromFTPofYesterday() throws IOException {
        String fileName = "C:\\Users\\Rogerio\\Desktop\\TEC\\Ephemeris_BRDC_Yesterday.Z";
        File downloadedFile = new File(fileName);

        LocalDate now = LocalDate.now();
        final int TODAY_DAY_OF_YEAR = now.getDayOfYear();
        final int YESTERDAY_DAY_OF_YEAR = TODAY_DAY_OF_YEAR - 1;

        String server_name = "cddis.gsfc.nasa.gov";
        int port_number = 21;
        String user = "anonymous";
        String senha = "";

        String fileName_toDownload = "gps/data/daily/2019/" + YESTERDAY_DAY_OF_YEAR
                + "/19n/brdc" + YESTERDAY_DAY_OF_YEAR + "0.19n.Z";

        FTP_Handler ftp = new FTP_Handler(server_name, port_number, user, senha,
                fileName_toDownload, downloadedFile);
        ftp.downloadAndSaveFile();
        lastFileDownloaded = ftp.getmNewFile();

        fileName_Broadcast = "C:\\Users\\Rogerio\\Desktop\\TEC\\Ephemeris_BRDC_Yesterday.19n";
        decompressZfile(getFileName_Broadcast());
    }

    static public void download_PreciseEphemFromFTPofLastWeek() throws IOException {
        String fileName = "C:\\Users\\Rogerio\\Desktop\\TEC\\Ephemeris_SP3_LastWeek.Z";
        File downloadedFile = new File(fileName);

        String server_name = "cddis.gsfc.nasa.gov";
        int port_number = 21;
        String user = "anonymous";
        String senha = "";

        String fileName_toDownload = "gnss/products/2070/igr20704.sp3.Z";

        FTP_Handler ftp = new FTP_Handler(server_name, port_number, user, senha,
                fileName_toDownload, downloadedFile);
        ftp.downloadAndSaveFile();
        lastFileDownloaded = ftp.getmNewFile();

        fileName_SP3 = "C:\\Users\\Rogerio\\Desktop\\TEC\\Ephemeris_SP3_LastWeek.19n";
        decompressZfile(getFileName_SP3());
    }

    static private void decompressZfile(String fileName) {
        File extractedFile = new File(fileName);

        try {
            FileInputStream fin = new FileInputStream(lastFileDownloaded);
            BufferedInputStream in = new BufferedInputStream(fin);

            FileOutputStream out = new FileOutputStream(extractedFile);
            ZCompressorInputStream zIn = new ZCompressorInputStream(in);

            final byte[] buffer = new byte[(int) lastFileDownloaded.length() / Byte.SIZE];

            int n = 0;
            while (-1 != (n = zIn.read(buffer))) {
                out.write(buffer, 0, n);
            }

            out.close();
            zIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
    
    public static String getNtripSourceTable() throws IOException {
        return Ntrip_Handler.getSourceTable();
    }
    
    public static void show_CoordGraph(String PRN) throws IOException {
        LocalFiles_Handler.read_SP3(getFileName_SP3(), PRN);
        listaCoordPrecisasLidas = LocalFiles_Handler.getListaCoordPrecisasLidas();
        
        String[] argumentos = new String[4];
        argumentos[0] = "Gráficos de Coordenadas do satélite";
        argumentos[1] = "Tempo (h)";
        argumentos[2] = "Coordenadas (Km)";
        argumentos[3] = PRN;
        
        
        UI_Graph_Multiline.main(argumentos, listaCoordPrecisasLidas);
    }
    
    public static void main(String[] args) throws IOException {
        fileName_Broadcast = "C:\\Users\\Rogerio\\Desktop\\TEC\\Ephemeris_BRDC_Yesterday.19n";
        fileName_SP3       = "C:\\Users\\Rogerio\\Desktop\\TEC\\Ephemeris_SP3_LastWeek.19n";
        
        //download_BRDCephemFromFTPofYesterday();
        
        UI_Main_Form main_form = new UI_Main_Form();
        main_form.setVisible(true);
    }

    /**
     * @return the fileName_SP3
     */
    public static String getFileName_SP3() {
        return fileName_SP3;
    }

    /**
     * @return the fileName_Broadcast
     */
    public static String getFileName_Broadcast() {
        return fileName_Broadcast;
    }
    
}
