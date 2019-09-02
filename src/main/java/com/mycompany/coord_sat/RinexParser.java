package com.mycompany.coord_sat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 *
 * @author Rogerio
 */
public class RinexParser {
    
   
    static int flag_min_seconds = 0; // 0 == minutes; 1 == seconds
    static int flag_gnss = 2; // 0 == GPS, 1 = Galileo, 2 - Beidou
    
    public static void main (String[] args) throws IOException {
        
        System.out.println("==============================================\n");
        System.out.println("    Calculo de Coordenadas de Satelites\n");
        System.out.println("==============================================\n\n");
        
        String fileName = "C:\\Users\\Rogerio\\Desktop\\coord\\processamento\\recorte.19p";
        
        readRINEX_Navigation_3(fileName);
        
        System.out.println("Arquivo: " + fileName + "\n\n");
        
        //calcCoordSat();
        
        int fit_interval = 24; // 0 == 24
        int incremento = 5; // 0 == 24
        if (flag_min_seconds == 1) { // seconds
            fit_interval = 20;
            incremento = 15;           
        }
                        
        calcCoordSat_Interval(flag_gnss,  incremento, fit_interval);
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        calcCoordSat_Interval(flag_gnss, -incremento, fit_interval);
        //interpolarCoordSP3(); 
    }
    
    public static ArrayList<GNSSNavMsg> listaEfemeridesOriginal = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesOriginal = new ArrayList<>();

    public static ArrayList<GNSSNavMsg> listaEfemeridesAtual = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesAtual = new ArrayList<>();
    public static ArrayList<CoordenadaGNSS> listaCoordAtual = new ArrayList<>();
    public static ArrayList<Integer> listaPRNsAtual = new ArrayList<>();
    public static ArrayList<EpocaGNSS> listaEpocas = new ArrayList<>();
        
    public static EpocaGNSS epocaAtual;
    public static GNSSDate dataAtual;
        
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
 
    public static Double Interpolation_Lagrange(int x, ArrayList<Double> arrayx, ArrayList<Double> arrayy) {
        
        if (arrayx.size() != arrayy.size()) return Double.NaN;
        
        int n = arrayx.size();
        int count, count2;

//               x = 0;
        double y = 0; //The corresponding value, f(x)=y
        double numerator;
        double denominator;  //The denominator

        



        //first Loop for the polynomial calculation
        for (count = 0; count < n; count++) {
            //Initialisation of variable
            numerator = 1;
            denominator = 1;

            //second Loop for the polynomial calculation
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
    
    private static void calcCoordSat_Interval(int pos_inicial, int incremento, int nn) {
//        GNSSDate dataObservacao = epocaAtual.getDateUTC();
//        GNSSDate dataObservacao = listaEfemeridesAtual.get(0).getData();
        

        GNSSDate dataObservacao = listaEfemeridesAtual.get(pos_inicial).getData();
        dataObservacao.setHour(12);
        dataObservacao.setMin(0);
        dataObservacao.setSec(0);
        
        for (int i = 0; i < nn; i++ ){// FIXME                        
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
            System.out.println("Epoca nº:" + epch + " " + dataObservacao.toString() + "\n" + novaCoord.toString());
            
            //Next iteration
            if (flag_min_seconds == 0) {
                dataObservacao.addMinutes(incremento);
            }else{
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
            System.out.println("Epoca: nº " + epch + " " + dataObservacao.toString() + "\n" + novaCoord.toString());
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
