package com.mycompany.coord_sat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author Rogerio
 */
public class RinexParser {
      
    static final int PROCESS_GPS = 0;
    static final int PROCESS_GALILEO = 1;
    static final int PROCESS_BEIDOU = 2;
    
    static final int INCREMENT_MINUTES = 0;
    static final int INCREMENT_SECONDS = 1;
    
    static int flag_min_seconds = INCREMENT_MINUTES; // 0 == minutes; 1 == seconds
    static int flag_gnss = PROCESS_GPS; // 0 == GPS, 1 = Galileo, 2 - Beidou
    static final int LAGRANGE_DEGREE = 6;
    
    static StringBuilder builder;
    
    public static double convert_HMS_TO_HOURS(double hour, double minutes, double seconds) {
        return ( hour + minutes*(1d/60d) + seconds*(1d/3600d));
    }
    
    public static void print_file(String fileName) {
        BufferedWriter bufferedWriter = null;
        try {
            String strContent = builder.toString();
            File myFile = new File(fileName);
            // check if file exist, otherwise create the file before writing
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            Writer writer = new FileWriter(myFile);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(strContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(bufferedWriter != null) bufferedWriter.close();
            } catch(Exception ex){
                 
            }
        }
    }
    
    public static void main (String[] args) throws IOException {

        System.out.println("==============================================\n");
        System.out.println("    Calculo de Coordenadas de Satelites\n");
        System.out.println("==============================================\n\n");
        
        String fileName    = "C:\\Users\\Rogerio\\Desktop\\coord\\processamento\\recorte.19p";
        
        readRINEX_Navigation_3(fileName);
        
        System.out.println("Arquivo: " + fileName + "\n\n");
        
        String fileNameSP3 = "C:\\Users\\Rogerio\\Desktop\\coord\\processamento\\COM20646.EPH";
        String contentSPE = read_sp3_cut(fileNameSP3);
               
        //calcCoordSat();
        
                
        int fit_interval = 24; // 0 == 24
        int incremento = 5; // 0 == 5
        if (flag_min_seconds == INCREMENT_SECONDS) { // seconds
            fit_interval = 20;
            incremento = 15;           
        }               
                
        //fit_interval = 6; // Numero de epocas
        calcCoordSat_Interval(flag_gnss, -incremento, fit_interval);
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        calcCoordSat_Interval(flag_gnss,  incremento, fit_interval);
        //interpolateCoordSat_Interval(flag_gnss,incremento, fit_interval); 
        
        print_file("C:\\Users\\Rogerio\\Desktop\\coord_pos.txt");
        builder = new StringBuilder(contentSPE);
        //builder.append(contentSPE);
        print_file("C:\\Users\\Rogerio\\Desktop\\coord_precisa.txt");
    }
    
    public static String read_sp3_cut(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        StringBuilder sb = new StringBuilder();
        
        // PULANDO O CABEÇALHO
        String mLine = reader.readLine();
        int cont = 0;
        while( cont < 23) {
            mLine = reader.readLine();
            cont++;
        }
              
        String sub = "";

        cont = 1;
        mLine = reader.readLine();
//        while ( (mLine = reader.readLine()) != null ){
        while ( !mLine.equals("EOF") ){
//            mLine = reader.readLine();

//            sb.append(mLine);
//            sb.append("\n");

            if (mLine.isEmpty()) break; // Last line of the file
                        
//first line - epoch of satellite clock (toc)
//======================================================================================================            
            try {
                int    year    = Integer.valueOf(mLine.substring( 3,  7).trim());
                int    month   = Integer.valueOf(mLine.substring( 8, 10).trim());
                int    day     = Integer.valueOf(mLine.substring(11, 13).trim());
                int    hour    = Integer.valueOf(mLine.substring(14, 16).trim());
                int    minutes = Integer.valueOf(mLine.substring(17, 19).trim());
                double seconds = Double .valueOf(mLine.substring(20, 31).trim());

                GNSSDate data = new GNSSDate(year, month, day, hour, minutes, seconds);
//              efemeride.setGNSSDate(data);

//                mLine = reader.readLine();
                while ( (mLine = reader.readLine()).startsWith("P") ) {
//                    mLine = reader.readLine();

                    // Jump SBAS or Glonas messages
                    if (mLine.contains("S") || mLine.contains("R")) {
                        //reader.readLine();
                        continue;
                    }
                    // Jump IRNSS (NAVIC) or QZSS messages
                    if (mLine.contains("I") || mLine.contains("J")) {
//                        reader.readLine();
                        continue;
                    }

                    String PRN = mLine.substring(1, 4);
                    
                    //dataAtual = data; // TO DO
                    double X = Double.valueOf(mLine.substring(5, 18).trim());
                    double Y = Double.valueOf(mLine.substring(19, 32).trim());
                    double Z = Double.valueOf(mLine.substring(33, 46).trim());
                    double dts = Double.valueOf(mLine.substring(47, 60).trim());

                    CoordenadaGNSS novaCoord = new CoordenadaGNSS(PRN, X, Y, Z, dts);

                    if (minutes == 0 || minutes == 15 || minutes == 30 || minutes == 45) {
                        listaCoordPrecisasLidas.add(novaCoord);

//                int epch = i + 1;
                        System.out.println("Epoca nº: " + cont + " " + data.toString() + "\n" + novaCoord.toString());
                       
                        if (PRN.equals("G02") || PRN.equals("E02") || PRN.equals("C12")) {
                            sb.append(PRN).append(" ");
                            sb.append(data.toString()).append(" ").append(X).
                                                      append(" ").append(Y).
                                                      append(" ").append(Z).
                                                      append(" ").append(dts);
                            sb.append("\n");
                        }

                    }

                }
                if (minutes == 0 || minutes == 15 || minutes == 30 || minutes == 45) {
                    cont++;
                    System.out.println("\n=======================================");
                    
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
//            listaEfemeridesAtual.add(efemeride);
        }

        
        
        reader.close();
        return sb.toString();
    }
    
    public static ArrayList<GNSSNavMsg> listaEfemeridesOriginal = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesOriginal = new ArrayList<>();

    public static ArrayList<GNSSNavMsg> listaEfemeridesAtual = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesAtual = new ArrayList<>();
    public static ArrayList<CoordenadaGNSS> listaCoordAtual = new ArrayList<>();
    
    public static ArrayList<CoordenadaGNSS> listaCoordInterpoladas  = new ArrayList<>();
    public static ArrayList<CoordenadaGNSS> listaCoordPrecisasLidas = new ArrayList<>();
    
    public static ArrayList<Integer> listaPRNsAtual = new ArrayList<>();
    public static ArrayList<EpocaGNSS> listaEpocas = new ArrayList<>();
        
    public static EpocaGNSS epocaAtual;
    public static GNSSDate  dataAtual;
        
    public static String readRINEX_Navigation_3(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        StringBuilder sb = new StringBuilder();
        
        // PULANDO O CABEÇALHO
        String mLine = reader.readLine();        
        while( !mLine.contains("END OF HEADER") ) {
            mLine = reader.readLine();
        }
              
        String sub = "";

        while ( (mLine = reader.readLine()) != null ){
            GNSSNavMsg efemeride = new GNSSNavMsg();
//            mLine = reader.readLine();

            if (mLine.isEmpty()) break; // Last line of the file

            // Jump SBAS or Glonas messages
            if (mLine.contains("S") || mLine.contains("R")) {
                reader.readLine();
                reader.readLine();
                reader.readLine();
                continue;
            }
            
            // Jump IRNSS (NAVIC) or QZSS messages
            if (mLine.contains("I") || mLine.contains("J")) {
                reader.readLine();
                reader.readLine();
                reader.readLine();
                reader.readLine();
                reader.readLine();
                reader.readLine();
                reader.readLine();
                continue;
            }
            
//first line - epoch of satellite clock (toc)
//======================================================================================================
            sub = mLine.substring(0, 4).trim();
            efemeride.setPRN(sub);  // FIXME

            try {
                int    year    = Integer.valueOf(mLine.substring(4 ,  8));
                int    month   = Integer.valueOf(mLine.substring(9 , 11));
                int    day     = Integer.valueOf(mLine.substring(12, 14));
                int    hour    = Integer.valueOf(mLine.substring(15, 17));
                int    minutes = Integer.valueOf(mLine.substring(18, 20));
                double seconds = Double .valueOf(mLine.substring(21, 23));

                GNSSDate data = new GNSSDate(year, month, day, hour, minutes, seconds);
                efemeride.setGNSSDate(data);
                
                dataAtual = data; // TO DO

            }catch (Exception err){
                efemeride.setToc(0);
                err.printStackTrace();
            }

            double af0 = Double.valueOf(mLine.substring(23,42).replace('D','e').trim());
            double af1 = Double.valueOf(mLine.substring(42,61).replace('D','e').trim());
            double af2 = Double.valueOf(mLine.substring(61,80).replace('D','e').trim());

            efemeride.setAf0(af0);
            efemeride.setAf1(af1);
            efemeride.setAf2(af2);
  
//second line - broadcast orbit
//==============================================================================================================
            mLine = reader.readLine();
           
            efemeride.setIODE   (Double.parseDouble(mLine.substring( 4, 23).replace('D', 'e').trim()));
            efemeride.setCrs    (Double.parseDouble(mLine.substring(23, 42).replace('D', 'e').trim()));
            efemeride.setDelta_n(Double.parseDouble(mLine.substring(42, 61).replace('D', 'e').trim()));                   
            efemeride.setM0     (Double.parseDouble(mLine.substring(61, 80).replace('D', 'e').trim()));
   
//third line - broadcast orbit (2)
//==============================================================================================================
            mLine = reader.readLine();

            efemeride.setCuc  (Double.parseDouble(mLine.substring( 4, 23).replace('D', 'e').trim()));
            efemeride.setE    (Double.parseDouble(mLine.substring(23, 42).replace('D', 'e').trim()));
            efemeride.setCus  (Double.parseDouble(mLine.substring(42, 61).replace('D', 'e').trim()));
            efemeride.setAsqrt(Double.parseDouble(mLine.substring(61, 80).replace('D', 'e').trim()));

//fourth line
//==============================================================================================================
            mLine = reader.readLine();

            efemeride.setToe    (Double.parseDouble(mLine.substring( 4, 23).replace('D', 'e').trim()));
            efemeride.setCic    (Double.parseDouble(mLine.substring(23, 42).replace('D', 'e').trim()));
            efemeride.setOmega_0(Double.parseDouble(mLine.substring(42, 61).replace('D', 'e').trim()));
            efemeride.setCis    (Double.parseDouble(mLine.substring(61, 80).replace('D', 'e').trim()));

//fifth line
//==============================================================================================================
            mLine = reader.readLine();

            efemeride.setI0     (Double.parseDouble(mLine.substring( 4, 23).replace('D', 'e').trim()));           
            efemeride.setCrc    (Double.parseDouble(mLine.substring(23, 42).replace('D', 'e').trim()));
            efemeride.setW      (Double.parseDouble(mLine.substring(42, 61).replace('D', 'e').trim()));
            efemeride.setOmega_v(Double.parseDouble(mLine.substring(61, 80).replace('D', 'e').trim()));

//sixth line
//==============================================================================================================
            mLine = reader.readLine();

            efemeride.setIDOT    (      Double.parseDouble(mLine.substring( 4, 23).replace('D', 'e').trim()));          
            efemeride.setCodeL2  (      Double.parseDouble(mLine.substring(23, 42).replace('D', 'e').trim()));            
            efemeride.setGPS_Week((int) Double.parseDouble(mLine.substring(42, 61).replace('D', 'e').trim()));
                        
            sub = mLine.substring(61, 80).replace('D', 'e');
            
            if (!sub.isEmpty() && !sub.equals("                   ")) {
                double L2Flag = Double.parseDouble(sub.trim());
                efemeride.setL2PdataFlag((int) L2Flag);
            }           

//seventh line
//==============================================================================================================
            mLine = reader.readLine();

            efemeride.setAccuracy((int) Double.parseDouble(mLine.substring( 4, 23).replace('D', 'e').trim()));
            efemeride.setHealth  ((int) Double.parseDouble(mLine.substring(23, 42).replace('D', 'e').trim()));
            efemeride.setTGD     (      Double.parseDouble(mLine.substring(42, 61).replace('D', 'e').trim()));
            efemeride.setIODC    ((int) Double.parseDouble(mLine.substring(61, 80).replace('D', 'e').trim()));

//eigth line
//==============================================================================================================
            mLine = reader.readLine();

            int len = mLine.length();

            efemeride.setTtx(Double.parseDouble(mLine.substring(4, 23).replace('D', 'e').trim()));

            if (len > 24) {
                try {
                    efemeride.setFit_interval(Double.parseDouble(mLine.substring(23, 42).replace('D', 'e').trim()));
                } catch (NumberFormatException er) {
                    efemeride.setFit_interval(0);
                }
            } else {
                efemeride.setFit_interval(0);
            }

            listaEfemeridesAtual.add(efemeride);
        }

        reader.close();
        return sb.toString();
    }

    public static double calc_Toc(GNSSDate dataGNSS) { // TODO: Calc day of week
        return (  (6 * 24 + dataGNSS.getHour()) * 3600 + dataGNSS.getMin() * 60 + dataGNSS.getSec() );
    }
 
    public static Double Interpolation_Lagrange(double x, ArrayList<Double> arrayx, ArrayList<Double> arrayy) {        
        if (arrayx.size() != arrayy.size()) return Double.NaN;
        
        int n = arrayx.size();
        int count, count2;

//               x = 0;
        double y = 0; //The corresponding value, f(x)=y
        double numerator;
        double denominator;
        
        for (count = 0; count < n; count++) {
            numerator = 1;
            denominator = 1;

            for (count2 = 0; count2 < n; count2++) {
                if (count2 != count) {
                    numerator = numerator * (x - arrayx.get(count2));
                    denominator = denominator * (arrayx.get(count) - arrayx.get(count2));
                }
            }
            y = y + (numerator / denominator) * arrayy.get(count);
        }
        return (y);
    }
    
    private static void interpolateCoordSat_Interval(int pos_inicial, int incremento, int fit_interval) {
       
        System.out.println("Interpolandooooo");
        
        GNSSDate dataObservacao = listaEfemeridesAtual.get(pos_inicial).getData();
        dataObservacao.setHour(12);
        dataObservacao.setMin(0);
        dataObservacao.setSec(0);        
        
        // Array X: TOCs das coordenadas interpoladas
        ArrayList<Double> desired_Xs_Tocs = new ArrayList<>();
        
        for (int i = 0; i < fit_interval; i++) {
            desired_Xs_Tocs.add(calc_Toc(dataObservacao));
            dataObservacao.addSeconds(incremento); // + 15s
        }
        
//        // 1
//        desired_Xs_Tocs.add(calc_Toc(dataObservacao));
//        dataObservacao.addSeconds(incremento); // + 15s
//        // 2
//        desired_Xs_Tocs.add(calc_Toc(dataObservacao));
//        dataObservacao.addSeconds(incremento); // + 15s
//        // 3
//        desired_Xs_Tocs.add(calc_Toc(dataObservacao));
//        dataObservacao.addSeconds(incremento); // + 15s
//        // 4
//        desired_Xs_Tocs.add(calc_Toc(dataObservacao));
//        dataObservacao.addSeconds(incremento); // + 15s
//        // 5
//        desired_Xs_Tocs.add(calc_Toc(dataObservacao));
//        dataObservacao.addSeconds(incremento); // + 15s
//        // 6
//        desired_Xs_Tocs.add(calc_Toc(dataObservacao));
//        dataObservacao.addSeconds(incremento); // + 15s
        
        // ==================================================== //
        
        // Array Y: X Coordinates
        int dia_semana = 6;
        ArrayList<Double> arrayx = new ArrayList<>();
        
//        for (int i = 0; i < LAGRANGE_DEGREE; i++) {
//            
//        }                
        arrayx.add((dia_semana * 86400d + convert_HMS_TO_HOURS(11,50, 00) * 3600d));
        arrayx.add((dia_semana * 86400d + convert_HMS_TO_HOURS(11,55, 00) * 3600d));
        arrayx.add((dia_semana * 86400d + convert_HMS_TO_HOURS(12,00, 00) * 3600d));
        arrayx.add((dia_semana * 86400d + convert_HMS_TO_HOURS(12,05, 00) * 3600d));
        arrayx.add((dia_semana * 86400d + convert_HMS_TO_HOURS(12,10, 00) * 3600d));
        arrayx.add((dia_semana * 86400d + convert_HMS_TO_HOURS(12,15, 00) * 3600d));
        
        // Array Y: X Coordinates
        ArrayList<Double> arrayy_X = new ArrayList<>();
        // Array Y: Y Coordinates
        ArrayList<Double> arrayy_Y = new ArrayList<>();
        // Array Z: Z Coordinates
        ArrayList<Double> arrayy_Z = new ArrayList<>();
       
        if (flag_gnss == PROCESS_GPS) { // Processing G02 Satellite
            arrayy_X.add(-17715.316069);
            arrayy_X.add(-17448.582641);
            arrayy_X.add(-17184.938287);
            arrayy_X.add(-16925.694775);
            arrayy_X.add(-16672.106105);
            arrayy_X.add(-16425.362538);

            arrayy_Y.add(7443.418342);
            arrayy_Y.add(6769.741861);
            arrayy_Y.add(6074.034482);
            arrayy_Y.add(5357.520386);
            arrayy_Y.add(4621.515721);
            arrayy_Y.add(3867.423578);

            arrayy_Z.add(-17668.023889);
            arrayy_Z.add(-18183.683663);
            arrayy_Z.add(-18662.635467);
            arrayy_Z.add(-19103.878165);
            arrayy_Z.add(-19506.489236);
            arrayy_Z.add(-19869.627178);
        }
        
        
        if (flag_gnss == PROCESS_GALILEO) { // Processing E02 Satellite
            arrayy_X.add(-17083.811556);
            arrayy_X.add(-16442.437204);
            arrayy_X.add(-15795.535845);
            arrayy_X.add(-15144.534153);
            arrayy_X.add(-14490.861126);
            arrayy_X.add(-13835.943305);

            arrayy_Y.add(-9705.759959);
            arrayy_Y.add(-9909.594633);
            arrayy_Y.add(-10132.656156);
            arrayy_Y.add(-10374.953574);
            arrayy_Y.add(-10636.416049);
            arrayy_Y.add(-10916.892927);

            arrayy_Z.add(22141.506878);
            arrayy_Z.add(22533.344685);
            arrayy_Z.add(22894.023626);
            arrayy_Z.add(23223.045218);
            arrayy_Z.add(23519.954737);
            arrayy_Z.add(23784.341841);
        }
        
        if (flag_gnss == PROCESS_BEIDOU) { // Processing C12 Satellite
            arrayy_X.add(13517.998236);
            arrayy_X.add(13684.308117);
            arrayy_X.add(13868.060748);
            arrayy_X.add(14069.084789);
            arrayy_X.add(14287.116731);
            arrayy_X.add(14521.801699);

            arrayy_Y.add(-8963.582936);
            arrayy_Y.add(-8202.517467);
            arrayy_Y.add(-7439.578969);
            arrayy_Y.add(-6676.411543);
            arrayy_Y.add(-5914.650354);
            arrayy_Y.add(-5155.915752);

            arrayy_Z.add(22778.533211);
            arrayy_Z.add(22963.402531);
            arrayy_Z.add(23110.596762);
            arrayy_Z.add(23219.868055);
            arrayy_Z.add(23291.030325);
            arrayy_Z.add(23323.959593);
        }
                
//        dataObservacao.setMin(0);
        for (int i = 0; i < fit_interval; i++) {
            String PRN = listaEfemeridesAtual.get(pos_inicial).getPRN_FULL();

            dataObservacao.setHour(12);
            dataObservacao.setMin(0);
            dataObservacao.setSec(0);
            //dataObservacao.addSeconds(incremento);

            double X = Interpolation_Lagrange(desired_Xs_Tocs.get(i), arrayx, arrayy_X);
            double Y = Interpolation_Lagrange(desired_Xs_Tocs.get(i), arrayx, arrayy_Y);
            double Z = Interpolation_Lagrange(desired_Xs_Tocs.get(i), arrayx, arrayy_Z);

            CoordenadaGNSS novaCoord = new CoordenadaGNSS(PRN, X, Y, Z, Double.NaN);
            listaCoordInterpoladas.add(novaCoord);
            
            int epch = i + 1;
            System.out.println("Epoca nº: " + epch + " " + desired_Xs_Tocs.get(i) + "\n" + novaCoord.toString());
        }
        
    }
    
    private static void calcCoordSat_Interval(int pos_inicial, int incremento, int nn) {
//        GNSSDate dataObservacao = epocaAtual.getDateUTC();
//        GNSSDate dataObservacao = listaEfemeridesAtual.get(0).getData();
        
        GNSSDate dataObservacao = listaEfemeridesAtual.get(pos_inicial).getData();
        dataObservacao.setHour(12);
        dataObservacao.setMin(0);
        dataObservacao.setSec(0);
        
        builder = new StringBuilder();
        
        for (int i = 0; i < nn; i++ ){                        
            double a0 = listaEfemeridesAtual.get(pos_inicial).getAf0();                       
            double a1 = listaEfemeridesAtual.get(pos_inicial).getAf1();                    
            double a2 = listaEfemeridesAtual.get(pos_inicial).getAf2();
            
            double Crs = listaEfemeridesAtual.get(pos_inicial).getCrs();
            double delta_n = listaEfemeridesAtual.get(pos_inicial).getDelta_n();
            double m0 = listaEfemeridesAtual.get(pos_inicial).getM0();

            double Cuc = listaEfemeridesAtual.get(pos_inicial).getCuc();
            double e = listaEfemeridesAtual.get(pos_inicial).getE();
            double Cus = listaEfemeridesAtual.get(pos_inicial).getCus();
            double a = listaEfemeridesAtual.get(pos_inicial).getAsqrt() * listaEfemeridesAtual.get(pos_inicial).getAsqrt();

            double toe = listaEfemeridesAtual.get(pos_inicial).getToe();         
            double trs = listaEfemeridesAtual.get(pos_inicial).getTtx();           
            
            double Cic = listaEfemeridesAtual.get(pos_inicial).getCic();
            double omega_0 = listaEfemeridesAtual.get(pos_inicial).get0mega_0();
            double Cis = listaEfemeridesAtual.get(pos_inicial).getCis();

            double io = listaEfemeridesAtual.get(pos_inicial).getI0();
            double Crc = listaEfemeridesAtual.get(pos_inicial).getCrc();
            double w = listaEfemeridesAtual.get(pos_inicial).getW();
            double omega_v = listaEfemeridesAtual.get(pos_inicial).getOmega_v();
            double idot = listaEfemeridesAtual.get(pos_inicial).getIDOT();

            /* Tempo de transmisao do sinal */
            double toc = calc_Toc(dataObservacao);
            
            if (listaEfemeridesAtual.get(pos_inicial).getConstellation().equals(GNSSConstants.BEIDOU_LETTER)){
                toc -= 14.0d;
//                toe += 14.0d;
            }                        
            
            /* Erro do relogio no sistema de tempo do satelite */
            double dts = a0 + a1 * (trs - toc) + a2 * (Math.pow(trs - toc, 2.0));
            
           double Tsat = toc - dts;
           double delta_tk = Tsat - toe; // Tempo de propagacao do sinal

            /* Considerando possível mudanca de semana */
            if (delta_tk > 302400)
                delta_tk = delta_tk - 604800;
            else if (delta_tk < -302400)
                delta_tk = delta_tk + 604800;

            /*(4.9)*/
            double GM;
            if (listaEfemeridesAtual.get(pos_inicial).getConstellation().equals(GNSSConstants.BEIDOU_LETTER)){
                GM = GNSSConstants.GM_BEIDOU;
            } else {
                GM = GNSSConstants.GM_GPS;
            }
            
            double no = Math.sqrt(GM / (a*a*a)); // terceira lei de kepler

            /*(4.10)*/
            double n = no + delta_n; // movimento medio corrigido
            double mk = m0 + n * delta_tk; // anomalia media

            /* iteracao - anomalia excentrica */
            /*(4.11)*/
            double ek = mk;
            for (int k = 0; k < 7; k++){
                ek = mk + e * Math.sin(ek);
            }

            // Anomalia verdadeira
            /*(4.12)*/
            double sen_vk = ( (Math.sqrt(1 - (e * e)) ) * Math.sin(ek) )  / ( 1 - (e * Math.cos(ek)) );
            double cos_vk = (Math.cos(ek) - e) / (1 - e * Math.cos(ek) );

            /* Teste de Quadrante */
            double vk = 0d;
            if (((sen_vk >= 0) && (cos_vk >= 0)) || (sen_vk < 0) && (cos_vk >= 0)) { // I ou III quadrante
                vk = Math.atan(sen_vk / cos_vk);
            } else if (((sen_vk >= 0) && (cos_vk < 0)) || ((sen_vk < 0 ) && (cos_vk) < 0)) { //  II ou IV quadrante
                vk = Math.atan(sen_vk / cos_vk) + 3.1415926535898; // FIXME Math.pi();
            } else{
//                Log.e("VK","Erro no ajuste do quadrante!");
            }

            //coordenadas planas do satelite
            /*(4.13)*/
            double fik = vk + w; // argumento da latitude
            double delta_uk = Cuc * Math.cos(2 * fik) + Cus * Math.sin(2 * fik); // correcao do argumento da latitude
            // latitude
            double uk = fik + delta_uk; //argumento da latitude corrigido
            /*(4.14)*/
            double delta_rk = Crc * Math.cos(2 * fik) + Crs * Math.sin(2 * fik); //correcao do raio
            double rk = a * (1 - e * Math.cos(ek)) + delta_rk; //raio corrigido

            double delta_ik = Cic * Math.cos(2 * fik) + Cis * Math.sin(2 * fik); //correcao da inclinacao
            double ik = io + idot * delta_tk + delta_ik; //inclinacao corrigida
            /*(4.15)*/
            // Coordenadas do satélite no plano orbital
            double xk = rk * Math.cos(uk); //posicao x no plano orbital
            double yk = rk * Math.sin(uk); //posicao y no plano orbital

            // Coordenadas do satélite em 3D (WGS 84)
            double WE;
            if (listaEfemeridesAtual.get(pos_inicial).getConstellation().equals(GNSSConstants.BEIDOU_LETTER)){
                WE = GNSSConstants.WE_BEIDOU;
            }else {
                WE = GNSSConstants.WE_GPS;
            }
            
            double Omegak = omega_0 + omega_v * delta_tk - WE * Tsat;

            // Coordenadas originais do satelites - Saida em Km para comparacao com efemerides precisas
            double X = ((xk * Math.cos(Omegak)) - (yk * Math.sin(Omegak) * Math.cos(ik))) / 1000;
            double Y = ((xk * Math.sin(Omegak)) + (yk * Math.cos(Omegak) * Math.cos(ik))) / 1000;
            double Z = (yk * Math.sin(ik)) / 1000;

            dts = dts / 1E6; // Segundos para microsegundos
            
            String PRN = listaEfemeridesAtual.get(pos_inicial).getPRN_FULL();
            CoordenadaGNSS novaCoord = new CoordenadaGNSS(PRN,X,Y,Z,dts);
            listaCoordAtual.add(novaCoord);
            
            int epch = i + 1;
            System.out.println("Epoca nº: " + epch + " " + dataObservacao.toString() + "\n" + novaCoord.toString());
            
            
            builder.append(dataObservacao.toString()).append(" ").append(X).
                                                      append(" ").append(Y).
                                                      append(" ").append(Z).
                                                      append(" ").append(dts);
            builder.append("\n");
            
            //Next iteration
            if (flag_min_seconds == 0) {
                dataObservacao.addMinutes(incremento);
            }else{ // flag_min_seconds == 1
                dataObservacao.addSeconds(incremento);
            }
            
        }
    }

    
    private static void calcCoordSat() {
//        GNSSDate dataObservacao = epocaAtual.getDateUTC();
//        GNSSDate dataObservacao = listaEfemeridesAtual.get(0).getData();
        

        
        for (int i = 0; i < listaEfemeridesAtual.size(); i++ ){// FIXME
            GNSSDate dataObservacao = listaEfemeridesAtual.get(i).getData();
            
            double a0 = listaEfemeridesAtual.get(i).getAf0();                       
            double a1 = listaEfemeridesAtual.get(i).getAf1();                    
            double a2 = listaEfemeridesAtual.get(i).getAf2();
            
            double Crs = listaEfemeridesAtual.get(i).getCrs();
            double delta_n = listaEfemeridesAtual.get(i).getDelta_n();
            double m0 = listaEfemeridesAtual.get(i).getM0();

            double Cuc = listaEfemeridesAtual.get(i).getCuc();
            double e = listaEfemeridesAtual.get(i).getE();
            double Cus = listaEfemeridesAtual.get(i).getCus();
            double a = listaEfemeridesAtual.get(i).getAsqrt() * listaEfemeridesAtual.get(i).getAsqrt();

            double toe = listaEfemeridesAtual.get(i).getToe();         
            double trs = listaEfemeridesAtual.get(i).getTtx();           
            
            double Cic = listaEfemeridesAtual.get(i).getCic();
            double omega_0 = listaEfemeridesAtual.get(i).get0mega_0();
            double Cis = listaEfemeridesAtual.get(i).getCis();

            double io = listaEfemeridesAtual.get(i).getI0();
            double Crc = listaEfemeridesAtual.get(i).getCrc();
            double w = listaEfemeridesAtual.get(i).getW();
            double omega_v = listaEfemeridesAtual.get(i).getOmega_v();
            double idot = listaEfemeridesAtual.get(i).getIDOT();

            /* Tempo de transmisao do sinal */
            double toc = calc_Toc(dataObservacao);
            
            if (listaEfemeridesAtual.get(i).getConstellation().equals(GNSSConstants.BEIDOU_LETTER)){
                toc -= 14.0d;
//                toe += 14.0d;
            }                        
            
            /* Erro do relogio no sistema de tempo do satelite */
            double dts = a0 + a1 * (trs - toc) + a2 * (Math.pow(trs - toc, 2.0));
            
           double Tsat = toc - dts;
           double delta_tk = Tsat - toe; // Tempo de propagacao do sinal

            /* Considerando possível mudanca de semana */
            if (delta_tk > 302400)
                delta_tk = delta_tk - 604800;
            else if (delta_tk < -302400)
                delta_tk = delta_tk + 604800;

            /*(4.9)*/
            double GM;
            if (listaEfemeridesAtual.get(i).getConstellation().equals(GNSSConstants.BEIDOU_LETTER)){
                GM = GNSSConstants.GM_BEIDOU;
            } else {
                GM = GNSSConstants.GM_GPS;
            }
            
            double no = Math.sqrt(GM / (a*a*a)); // terceira lei de kepler

            /*(4.10)*/
            double n = no + delta_n; // movimento medio corrigido
            double mk = m0 + n * delta_tk; // anomalia media

            /* iteracao - anomalia excentrica */
            /*(4.11)*/
            double ek = mk;
            for (int k = 0; k < 7; k++){
                ek = mk + e * Math.sin(ek);
            }

            // Anomalia verdadeira
            /*(4.12)*/
            double sen_vk = ( (Math.sqrt(1 - (e * e)) ) * Math.sin(ek) )  / ( 1 - (e * Math.cos(ek)) );
            double cos_vk = (Math.cos(ek) - e) / (1 - e * Math.cos(ek) );

            /* Teste de Quadrante */
            double vk = 0d;
            if (((sen_vk >= 0) && (cos_vk >= 0)) || (sen_vk < 0) && (cos_vk >= 0)) { // I ou III quadrante
                vk = Math.atan(sen_vk / cos_vk);
            } else if (((sen_vk >= 0) && (cos_vk < 0)) || ((sen_vk < 0 ) && (cos_vk) < 0)) { //  II ou IV quadrante
                vk = Math.atan(sen_vk / cos_vk) + 3.1415926535898; // FIXME Math.pi();
            } else{
//                Log.e("VK","Erro no ajuste do quadrante!");
            }

            //coordenadas planas do satelite
            /*(4.13)*/
            double fik = vk + w; // argumento da latitude
            double delta_uk = Cuc * Math.cos(2 * fik) + Cus * Math.sin(2 * fik); // correcao do argumento da latitude
            // latitude
            double uk = fik + delta_uk; //argumento da latitude corrigido
            /*(4.14)*/
            double delta_rk = Crc * Math.cos(2 * fik) + Crs * Math.sin(2 * fik); //correcao do raio
            double rk = a * (1 - e * Math.cos(ek)) + delta_rk; //raio corrigido

            double delta_ik = Cic * Math.cos(2 * fik) + Cis * Math.sin(2 * fik); //correcao da inclinacao
            double ik = io + idot * delta_tk + delta_ik; //inclinacao corrigida
            /*(4.15)*/
            // Coordenadas do satélite no plano orbital
            double xk = rk * Math.cos(uk); //posicao x no plano orbital
            double yk = rk * Math.sin(uk); //posicao y no plano orbital

            // Coordenadas do satélite em 3D (WGS 84)
            double WE;
            if (listaEfemeridesAtual.get(i).getConstellation().equals(GNSSConstants.BEIDOU_LETTER)){
                WE = GNSSConstants.WE_BEIDOU;
            }else {
                WE = GNSSConstants.WE_GPS;
            }
            
            double Omegak = omega_0 + omega_v * delta_tk - WE * Tsat;

            // Coordenadas originais do satelites - Saida em Km para comparacao com efemerides precisas
            double X = ((xk * Math.cos(Omegak)) - (yk * Math.sin(Omegak) * Math.cos(ik))) / 1000;
            double Y = ((xk * Math.sin(Omegak)) + (yk * Math.cos(Omegak) * Math.cos(ik))) / 1000;
            double Z = (yk * Math.sin(ik)) / 1000;

            dts = dts / 1E6; // Segundos para microsegundos
            
            String PRN = listaEfemeridesAtual.get(i).getPRN_FULL();
            CoordenadaGNSS novaCoord = new CoordenadaGNSS(PRN,X,Y,Z,dts);
            listaCoordAtual.add(novaCoord);
            
            int epch = i + 1;
            System.out.println("Epoca nº: " + epch + " " + dataObservacao.toString() + "\n" + novaCoord.toString());
        }
    }

    private static void interpolarCoordSP3() {
        System.out.println("INTERPOLANDO!!!");
        
//        int x = 18000;
        int x   = 18900;        
        int dia_semana = 0;

        ArrayList<Double> arrayx = new ArrayList<>();
        arrayx.add((dia_semana * 86400 + 4.50 * 3600));
        arrayx.add((dia_semana * 86400 + 4.75 * 3600));
        arrayx.add((dia_semana * 86400 + 5.00 * 3600));
        arrayx.add((dia_semana * 86400 + 5.25 * 3600));
        arrayx.add((dia_semana * 86400 + 5.50 * 3600));
        arrayx.add((dia_semana * 86400 + 5.75 * 3600));
        
        // =====================================================================================
        //                              Interpolando para X:
        // =====================================================================================
               
        ArrayList<Double> arrayy = new ArrayList<>();   
        arrayy.add(-10896.1927925292);
        arrayy.add(-11846.8306779090);
        arrayy.add(-12945.9817781000);
        arrayy.add(-14179.0219006000);
        arrayy.add(-15525.9906839000);
        arrayy.add(-16962.1775668000);
        
        System.out.println("\nResultado em X: " + Interpolation_Lagrange(x, arrayx, arrayy));
        
        // =====================================================================================
        //                              Interpolando para Y:
        // =====================================================================================
              
        arrayy = new ArrayList<>();   
        arrayy.add(15184.5056355764);
        arrayy.add(13306.1986291445);
        arrayy.add(11454.0395703000);
        arrayy.add(9661.69595340000);
        arrayy.add(7960.07674900000);
        arrayy.add(6376.41668740000);
        
        System.out.println("\nResultado em Y: " + Interpolation_Lagrange(x, arrayx, arrayy));
        
        // =====================================================================================
        //                              Interpolando para Z:
        // =====================================================================================
        
        arrayy = new ArrayList<>();   
        arrayy.add(-22940.7616400);
        arrayy.add(-23624.9681300);
        arrayy.add(-24015.0531744);
        arrayy.add(-24106.1500698);
        arrayy.add(-23897.1167937);
        arrayy.add(-23390.5506176);
        
        System.out.println("\nResultado em Z: " + Interpolation_Lagrange(x, arrayx, arrayy));
    }
    
}
