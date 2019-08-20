package com.mycompany.coord_sat;

import static com.mycompany.coord_sat.GNSSConstants.LIGHTSPEED;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static com.mycompany.coord_sat.GNSSConstants.GM_GPS;
import static com.mycompany.coord_sat.GNSSConstants.WE_GPS;

/**
 *
 * @author Rogerio
 */
public class RinexParser {
    
    public static void main (String[] args) throws IOException {
        
        System.out.println("==============================================\n");
        System.out.println("    Calculo de Coordenadas de Satelites\n");
        System.out.println("==============================================\n\n");
        
        String fileName = "C:\\Users\\Rogerio\\Desktop\\TesteBeidou_MEO.txt";
        
        readRINEX_RawAssets(fileName);       
        
        System.out.println("Arquivo: " + fileName + "\n\n");
        
        calcCoordSat();        
    }
    
    public static ArrayList<GNSSNavMsg> listaEfemeridesOriginal = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesOriginal = new ArrayList<>();

    public static ArrayList<GNSSNavMsg> listaEfemeridesAtual = new ArrayList<>();
    public static ArrayList<GNSSMeasurement> listaMedicoesAtual = new ArrayList<>();
    public static ArrayList<CoordenadaGPS> listaCoordAtual = new ArrayList<>();
    public static ArrayList<Integer> listaPRNsAtual = new ArrayList<>();
    public static ArrayList<EpocaGPS> listaEpocas = new ArrayList<>();
    
    
    public static EpocaGPS epocaAtual;
    public static GNSSDate dataAtual;
    
    private static int qntSatEpchAtual;
      
    public static String readRINEX_RawAssets(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        StringBuilder sb = new StringBuilder();

//        listaEfemeridesAtual = new ArrayList()<>;
        
        //PULANDO O CABEÇALHO DE 8 LINHAS
        String mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();
        mLine = reader.readLine();

        String sub = "";
        int numEfemerides = 1; // TODO REVISAR

        for (int i = 0; i < numEfemerides; i++){
            GNSSNavMsg efemeride = new GNSSNavMsg();
            mLine = reader.readLine();

//first line - epoch of satellite clock (toc)
//==================================================================================================
            sub = mLine.substring(0, 2).replaceAll("\\s", "");
            efemeride.setPRN(Integer.valueOf(sub));  // FIXME

            try { // FIXME REVER
                int year = Integer.valueOf(mLine.substring(3, 6).replaceAll("\\s", ""));
                int month = Integer.valueOf(mLine.substring(6, 8).replaceAll("\\s", ""));
                int day = Integer.valueOf(mLine.substring(9, 11).replaceAll("\\s", ""));
                int hour = Integer.valueOf(mLine.substring(12, 14).replaceAll("\\s", ""));
                int minute = Integer.valueOf(mLine.substring(15, 17).replaceAll("\\s", ""));
                double seconds = Double.valueOf(mLine.substring(18, 22).replaceAll("\\s", ""));

                GNSSDate data = new GNSSDate(year, month, day, hour, minute, seconds);
                efemeride.setGNSSDate(data);
                dataAtual = data;

            }catch (Exception err){
                efemeride.setToc(0);
//                Log.e("TOC-ERR","Erro: " + err.getMessage());
            }

            double af0 = Double.valueOf(mLine.substring(22,41).replace('D','e')
                    .replaceAll("\\s",""));

            double af1 = Double.valueOf(mLine.substring(41,60).replace('D','e')
                    .replaceAll("\\s",""));

            double af2 = Double.valueOf(mLine.substring(60,79).replace('D','e')
                    .replaceAll("\\s",""));

            efemeride.setAf0(af0);
            efemeride.setAf1(af1);
            efemeride.setAf2(af2);
//second line - broadcast orbit
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(3, 22).replace('D', 'e');
            double iode = Double.parseDouble(sub.trim());
            efemeride.setIODE(iode);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrs(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setDelta_n(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setM0(Double.parseDouble(sub.trim()));
//third line - broadcast orbit (2)
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double Cuc = Double.parseDouble(sub.trim());
            efemeride.setCuc(Cuc);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setE(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setCus(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setAsqrt(Double.parseDouble(sub.trim()));
//fourth line
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double toe = Double.parseDouble(sub.trim());
            efemeride.setToe(toe);

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCic(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setOmega_0(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setCis(Double.parseDouble(sub.trim()));
//fifth line
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setI0(Double.parseDouble(sub.trim()));

            sub = mLine.substring(22, 41).replace('D', 'e');
            efemeride.setCrc(Double.parseDouble(sub.trim()));

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setW(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            efemeride.setOmega_v(Double.parseDouble(sub.trim()));
//sixth line
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setIDOT(Double.parseDouble(sub.trim()));

            sub = mLine.substring(22, 41).replace('D', 'e');
            double L2Code = Double.parseDouble(sub.trim());
            efemeride.setCodeL2(L2Code);

            sub = mLine.substring(41, 60).replace('D', 'e');
            double week = Double.parseDouble(sub.trim());
            efemeride.setGPS_Week((int) week);
                        
            sub = mLine.substring(60, 79).replace('D', 'e');
            
            if (!sub.isEmpty() && !sub.equals("                   ")) {
                double L2Flag = Double.parseDouble(sub.trim());
                efemeride.setL2PdataFlag((int) L2Flag);
            }           
//seventh line
//==================================================================================================
            mLine = reader.readLine();

            sub = mLine.substring(0, 22).replace('D', 'e');
            double svAccur = Double.parseDouble(sub.trim());
            efemeride.setAccuracy((int) svAccur);

            sub = mLine.substring(22, 41).replace('D', 'e');
            double svHealth = Double.parseDouble(sub.trim());
            efemeride.setHealth((int) svHealth);

            sub = mLine.substring(41, 60).replace('D', 'e');
            efemeride.setTGD(Double.parseDouble(sub.trim()));

            sub = mLine.substring(60, 79).replace('D', 'e');
            double iodc = Double.parseDouble(sub.trim());
            efemeride.setIODC((int) iodc);
//eigth line
//==================================================================================================
            mLine = reader.readLine();

            int len = mLine.length();

            sub = mLine.substring(0, 22).replace('D', 'e');
            efemeride.setTtx(Double.parseDouble(sub.trim()));

            if (len > 24) {
                sub = mLine.substring(22, 41).replace('D', 'e');
                efemeride.setFit_interval(Double.parseDouble(sub.trim()));

            } else {
                efemeride.setFit_interval(0);
            }

            listaEfemeridesAtual.add(efemeride);
        }

        reader.close();
        return sb.toString();
    }

    public static double calc_Toc(GNSSDate dataGNSS) {
        return (  (6 * 24 + dataGNSS.getHour()) * 3600 + dataGNSS.getMin() * 60 + dataGNSS.getSec() );
    }
 
    private static void calcCoordSat() {
//        GNSSDate dataObservacao = epocaAtual.getDateUTC();

        GNSSDate dataObservacao = listaEfemeridesAtual.get(0).getData();

        qntSatEpchAtual = 1;
        
        for (int i = 0; i < qntSatEpchAtual; i++ ){// FIXME
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
            /* Erro do relogio no sistema de tempo do satelite */
            double dts = a0 + a1 * (trs - toc) + a2 * (Math.pow(trs - toc,2.0));            
            double tgps = toc - dts;

            double delta_tk = tgps - toe;

            /*
              Considerando possível mudança de semana
              Autor: Bruno Vani
             */
            if (delta_tk > 302400)
                delta_tk = delta_tk - 604800;
            else if (delta_tk < -302400)
                delta_tk = delta_tk + 604800;

            /*(4.9)*/
            double no = Math.sqrt(GNSSConstants.GM_BEIDOU / (a*a*a)); // terceira lei de kepler

            /*(4.10)*/
            double n = no + delta_n; // movimento medio corrigido
            double mk = m0 + n * delta_tk; // anomalia media

            /*
              iteracao - anomalia excentrica
             */
            /*(4.11)*/
            double ek = mk;
            for (int k = 0; k < 7; k++){
                ek = mk + e * Math.sin(ek);
            }

            // Anomalia verdadeira
            /*(4.12)*/
            double sen_vk = ( (Math.sqrt(1 - (e * e)) ) * Math.sin(ek) )  / ( 1 - (e * Math.cos(ek)) );
            double cos_vk = (Math.cos(ek) - e) / (1 - e * Math.cos(ek) );

            /*
              Teste do quadrante
              autor: Bruno Vani
             */
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
            // Coordenadas do satélite no plano
            double xk = rk * Math.cos(uk); //posicao x no plano orbital
            double yk = rk * Math.sin(uk); //posicao y no plano orbital

            // Coordenadas do satélite em 3D (WGS 84)
            double Omegak = omega_0 + omega_v * delta_tk - GNSSConstants.WE_BEIDOU * tgps;

            // Coordenadas originais do satelites - Saida em Km para comparacao com efemerides precisas
            double X = ((xk * Math.cos(Omegak)) - (yk * Math.sin(Omegak) * Math.cos(ik))) / 1000;
            double Y = ((xk * Math.sin(Omegak)) + (yk * Math.cos(Omegak) * Math.cos(ik))) / 1000;
            double Z = (yk * Math.sin(ik)) / 1000;

            dts = dts / 1000000; // Segundos para microsegundos
            
            int PRN = listaEfemeridesAtual.get(i).getPRN();
            CoordenadaGPS novaCoord = new CoordenadaGPS(PRN,X,Y,Z,dts);
            listaCoordAtual.add(novaCoord);
            
            System.out.println("Satelite: 1\n" + novaCoord.toString());
        }
    }
    
    private void calcCoordSat_pseudo() {
        GNSSDate dataObservacao = epocaAtual.getDateUTC();

        for (int i = 0; i < qntSatEpchAtual; i++ ){// FIXME
            //------------------------------------------
            //Dados de entrada
            //------------------------------------------
            //Tempo de recepcao do sinal ->  Hora da observacao
//            double tr = (3*24+0)*3600 + 0*60 + 0.00; // FIXME CORRIGIR O TEMPO
//            double tr = calc_Toc(GNSSConstants.DAY_QUA,)
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
            double Cic = listaEfemeridesAtual.get(i).getCic();
            double omega_0 = listaEfemeridesAtual.get(i).get0mega_0();
            double Cis = listaEfemeridesAtual.get(i).getCis();

            double io = listaEfemeridesAtual.get(i).getI0();
            double Crc = listaEfemeridesAtual.get(i).getCrc();
            double w = listaEfemeridesAtual.get(i).getW();
            double omega_v = listaEfemeridesAtual.get(i).getOmega_v();
            double idot = listaEfemeridesAtual.get(i).getIDOT();

            /*Tempo de transmisao do sinal*/
            double dtr = 0d; // ERRO DO RELÓGIO DO RECEPTOR
            double tr = calc_Toc(dataObservacao);
            double tgps = tr - (listaMedicoesAtual.get(i).getPseudorangeMeters() / LIGHTSPEED);

            double dts = a0 + a1 * (tgps - toe) + a2 * (Math.pow(tgps - toe,2.0)); // ERRO DO SATÉLITE fixme É O TOC
            double tpropag = listaMedicoesAtual.get(i).getPseudorangeMeters() / LIGHTSPEED - dtr + dts;

            tgps = tr - dtr- tpropag + dts; // melhoria no tempo de transmissao
            double delta_tk = tgps - toe;

            /*
              Considerando possível mudança de semana
              Autor: Bruno Vani
             */
            if (delta_tk > 302400)
                delta_tk = delta_tk - 604800;
            else if (delta_tk < -302400)
                delta_tk = delta_tk + 604800;

            /*(4.9)*/
            double no = Math.sqrt(GM_GPS / (a*a*a)); // terceira lei de kepler

            /*(4.10)*/
            double n = no + delta_n; // movimento medio corrigido
            double mk = m0 + n * delta_tk; // anomalia media

            /*
              iteracao - anomalia excentrica
             */
            /*(4.11)*/
            double ek = mk;
            for (int k = 0; k < 7; k++){
                ek = mk + e * Math.sin(ek);
            }

            // Anomalia verdadeira
            /*(4.12)*/
            double sen_vk = ( (Math.sqrt(1 - (e * e)) ) * Math.sin(ek) )  / ( 1 - (e * Math.cos(ek)) );
            double cos_vk = (Math.cos(ek) - e) / (1 - e * Math.cos(ek) );

            /*
              Teste do quadrante
              autor: Bruno Vani
             */
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
            // Coordenadas do satélite no plano
            double xk = rk * Math.cos(uk); //posicao x no plano orbital
            double yk = rk * Math.sin(uk); //posicao y no plano orbital

            // Coordenadas do satélite em 3D (WGS 84)
            double Omegak = omega_0 + omega_v * delta_tk - WE_GPS * tgps;

            // Coordenadas originais do satelites
            double X = ((xk * Math.cos(Omegak)) - (yk * Math.sin(Omegak) * Math.cos(ik)));
            double Y = ((xk * Math.sin(Omegak)) + (yk * Math.cos(Omegak) * Math.cos(ik)));
            double Z = (yk * Math.sin(ik));

            // Coordenadas do satelites corrigidas do erro de rotacao da Terra
            double alpha = WE_GPS * tpropag;
            double Xc = X + alpha * Y;
            double Yc = -alpha * X + Y;
            double Zc = Z;

            int PRN = listaEfemeridesAtual.get(i).getPRN();
            CoordenadaGPS novaCoord = new CoordenadaGPS(PRN,Xc,Yc,Zc,dts);
            listaCoordAtual.add(novaCoord);
        }
    }
}
