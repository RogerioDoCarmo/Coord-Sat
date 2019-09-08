/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.NTRIP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rogerio
 */
public class Ntrip_Main extends Thread{
//    public static final String     SERVER          = "products.igs-ip.net";
    public static final String     SERVER          = "170.84.40.52";
//    public static final String     SERVER          = "200.145.185.200";
    
    public static final Integer      PORT          = 2101;
//    public static final String MOUNTPOINT          = "RTCM3EPH";
//    public static final String MOUNTPOINT          = "SIRGAS200002";
    public static final String   MOUNTPOINT        = "POLI1";
    //public static final String MOUNTPOINT          = "IGS01";
//    public static final String MOUNTPOINT          = "PPTE1";
    public static final String       USER          = "";
    public static final String   PASSWORD          = "";
//    public static final String   USR_PASS_ENCODED_64 = "cm9nZXJpdTo3MTg5MA=="; // MINHA
    public static final String   USR_PASS_ENCODED_64 = "dmVydG9uOnZlcnRvbjE1"; // wev
//    public static final String   USR_PASS_ENCODED_64 = "dGVzdGUwMTplbmdjYXI="; // unesp
    
    
    private static boolean done = false;
    
    //static RTCM3ParserData handle;
    
    static Socket s;
    static String sourceTable;  
    
    
        
        
    static char[] dado;
    static char[] msg = new char[2048]; //buffer que contém somente os dados da mensagem
    //static char[] dado;
    static int controle = 0; static int tam=0; static int posi = 0;
    static int tam1=0;
    
     //---------------------------------------------------------------------------------------------
    // variáveis definidas no define
    static long numbits;
    static long bitfield;
    static int size;
    static int m=0; //variável de iteração para percorrer a mensagem no loadbits
    //----------------------------------------------------------------------------------------------

     //------------------------------funçoes #DEFINE em C----------------------------------------------------

   private static final void SKIPBITS( int b) { loadBITS(b); numbits -= (b); }

   private static final void loadBITS(int a){
       System.out.println("a:"+a);
       while(a > numbits)
      {   // System.out.println("dado[m]:"+dado[m]);
          // System.out.println("bitfield :"+bitfield );
          if((size--)==0) break; //if(!size--)
          bitfield = (bitfield<<8)|(dado[m++]);//carregando os bits para a variável
          //  System.out.println("dado[m]:"+dado[m]);
         //  System.out.println("bitfield :"+bitfield );
          numbits += 8; //qntde de bits carregados nessa leitura
      }
          //System.out.println("-------------------------------------------------------------------------------");
   }

    private static final long getBITS(long b,int a) {
     loadBITS(a) ;
   //  System.out.println("bitfield :"+bitfield );
   //  System.out.println("numbits :"+numbits );
     b = (long)((bitfield<<(64-numbits))>>>(64-(a))); //realiza deslocamento sem levar em conta bit mais significativo
     numbits -= a; //qntde de bits já lidos(para considerar no próximo carregamento)
     if(b < 0){ b = ~b+1;} //realizando o complemento de 2
  //    System.out.println("b:"+b );
     return(b);

    }
    
  private static final long getBITSSIGN(long b,int a){
    loadBITS(a);
    b = ((long)(bitfield<<(64-numbits)))>>(64-(a));
    numbits -= (a);
    return(b);
  }

 private static final double GETFLOATSIGNM(double b,int a,double c){
  long l;
  loadBITS(a) ;
  l = (bitfield<<(64-numbits))>>>(64-1);//deslocamento sem sinal(em C é td positivo)
  System.out.println("l:"+l);
  b = ((double)(((bitfield<<(64-(numbits-1))))>>(64-(a-1))))*(c);
  numbits -= (a);
  System.out.println("b:"+b);
  if(l != 0) b *= -1.0;  //if(l) b *= -1.0;
  System.out.println("b:"+b);
  return(b);
}

 private static final double GETFLOAT(double b,int a,double c) {
  loadBITS(a);
  b = ((double)((bitfield<<(64-numbits))>>>(64-(a))))*(c); //deslocamento sem sinal
  numbits -= (a);
    System.out.println("b:"+b );
  return(b);
}

private static final double GETFLOATSIGN(double b,int a,double c) {
    long l =0;
  loadBITS(a) ;
  b = (double)((((bitfield<<(64-numbits)))>>(64-(a))))*(c);
  numbits -= (a);
  return(b);
}


/* extrai os caracteres que formam a string
   b = variable to store size, s = variavel que armazena a string */
private static final char[] getSTRING(int b,char[] s) {
  s = new char[2048];
  b = dado[m++];
  System.out.println("b:"+b);
  System.arraycopy(dado, 4, s, 0,size); //copiando a string somente, a partir da 4 posição no array, como estudado
//  for(int v=0;v<size;v++) System.out.println("\ns: " +s[v]);
  dado = new char[2048];
  System.arraycopy(s, b, dado, 0,size-b); //  dado = dado+b;
 // for(int v=0;v<size;v++) System.out.println("\ndado: " +dado[v]);
  size -= b+1;
  return(s);
}


//-----------------------------fim #DEFINE-------------------------------------------------------------
    
    static RTCM3ParserData handle;
    
    public static void getSourceTable() throws IOException {
         s = new Socket(SERVER, PORT);

        String wtr = "";

        wtr += "GET / HTTP/1.1\r\n"
             + "Host: " + SERVER + "\r\n"
             + "Ntrip-Version: Ntrip/2.0\r\n"
             + "User-Agent: NTRIP Coord_Sat/1.0\r\n"
             + "\r\n";

        s.getOutputStream().write(wtr.getBytes());        
        s.getOutputStream().flush();
        
        Logger.getLogger(Ntrip_Main.class.getName()).log(Level.INFO, "Request SOURCETABLE sent\n");
        
        //Creates a BufferedReader that contains the SERVER response
        BufferedReader bufRead = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String outStr;

        StringBuilder builder = new StringBuilder();
      
        while((outStr = bufRead.readLine()) != null){
            builder.append(outStr+"\n");
        }

        Logger.getLogger(Ntrip_Main.class.getName()).log(Level.INFO, "Request SOURCETABLE received\n");
        
        sourceTable = builder.toString();        
        System.out.println(sourceTable);

        if (s != null) {
            s.close();
        }
        bufRead.close();
    }
    
    public static void getFromMountPoint() throws IOException {
         s = new Socket(SERVER, PORT);        
//        msg= new char[5000];
        String wtr = "";

        wtr += "GET /"  + MOUNTPOINT + " HTTP/1.1\r\n"
             + "Host: " + SERVER     + "\r\n"
             + "Ntrip-Version: Ntrip/2.0\r\n"
             + "User-Agent: NTRIP Coord_Sat/1.0\r\n"
             + "Authorization: Basic " + USR_PASS_ENCODED_64 + "\r\n"
             + "Connection: close\r\n"
             + "Accept-Encoding: gzip\r\n"
             + "Accept-Language: pt-BR,en,*\r\n"
             + "\r\n";

        s.getOutputStream().write(wtr.getBytes());        
        s.getOutputStream().flush();
        
        Logger.getLogger(Ntrip_Main.class.getName()).log(Level.INFO, "Request MOUNTPOINT sent\n");
        
        //Creates a BufferedReader that contains the SERVER response
        BufferedReader bufRead = new BufferedReader(new InputStreamReader(s.getInputStream()));
        int dadosEntrada;

        StringBuilder builder = new StringBuilder();
      
        builder.append("\n");
        while((dadosEntrada = bufRead.read()) != -1 && !done){
            builder.append((char)dadosEntrada);
//            builder.append("\n");
//            System.out.print((char)outStr);
            getMessage((char)dadosEntrada);
        }

        Logger.getLogger(Ntrip_Main.class.getName()).log(Level.INFO, "Request MOUNTPOINT received\n");
        
//        sourceTable = builder.toString();        
//        System.out.println(sourceTable);

        if (s != null){
            s.close();
        }
       
           
        bufRead.close();
    }
    static int contt = 0;
    public static void getMessage(char c ) {
        /* msg[0] - preambulo
       msg[1] - bits reservados
       msg[2] - tamanho da mensagem
       msg[3] - mensagem
         *
         *
         *
       msg[n] - CRC
       msg[n+1] - CRC
       msg[n+2] - CRC
     */

	int tam2;
        char preambulo = 0xD3;
        System.out.print(c);
	if(c == preambulo && controle == 0) { //se for o preambulo
                msg[posi]=c; //recebendo preambulo
                controle=1; //passou pelo pré ambulo
		posi=posi+1;
	}
	else if(controle==1&&(c>=(char)0x00 && c<=(char)0x03)) { // bits reservados
		msg[posi]=c; //recebendo bits reservados
		controle=2; //passou pelos bits reservados
		posi=posi+1;
	}
	else if(controle==1&&(c>=(char)0x00 && c<=(char)0x03)) {
		controle=0; //volta ao inicio para proxima mensagem
                posi=0; //indice volta
	}
	else if(controle==2) { //tamanho da mensagem
		msg[posi]=c;
              //  System.out.println("msg[2]:"+(int)msg[posi]);
		tam1= (msg[1]&0x03)<<8 ; //tamanho da mensagem
               // System.out.println("tam1:"+tam1);
                tam = tam1 | msg[2];
                System.out.println("tam:"+tam);
                tam2= (msg[1]&0x03)<<8 | msg[2] ;
                System.out.println("tam:"+tam2);
		posi=posi+1;
		controle=3; //passou pelo tamanho
	}
	else if(controle==3&&posi<tam+6) { //roda toda a mensagem (+3 p o posi ir p o fim da mensagem + 3 casas do CRC)
		msg[posi]=c;
		posi=posi+1;
	}
	else if(controle==3&&posi>=tam+6) { //terminou de ler toda a mensagem
            System.out.println("\nTerminou a mensagem!");
            
                if(((msg[3+tam]<<16)|(msg[3+tam+1]<<8)|(msg[3+tam+2])) == CRC24(tam+3, msg)) {
                    tam2=tam;
                    decodifica(tam2);
                }
//decodifica(tam);
		controle=1; tam=0; posi=0;
		msg[posi]=c;
		posi=posi+1;
	}
}
   
    public static int CRC24(long size, char[] buf) {
        int crc = 0;
        int i, j = 0;

        while (size != 0) {
            crc ^= ((buf[j++]) << (16));
            for (i = 0; i < 8; i++) {
                crc <<= 1;
                if ((crc & 0x1000000) != 0) {
                    crc ^= 0x01864cfb;
                }
            }
            size--;
        }
        return crc;
    }
  
    public static void decodifica(int cmsg) {
      System.out.printf("\n\nEntrou no decodifica!");
      
      System.out.printf("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      
      
    int  syncf = 0,old = 0,ret=0;
    size=cmsg;
    long nmsg = 0,refs = 0, data=0;
    int i= 0,cont=0;
    dado= new char[2048];
    m=0;
    double dbdata;
     System.out.println(msg);
    while(i<tam){ dado[i]= msg[i+3];cont++;i++;} //a mensagem começa a partir do quarto
    System.out.println("tamanho de dado : "+cont);
    numbits = 0;
    bitfield = 0;

    nmsg=getBITS(nmsg,12); // os 12 primeiros identificam o numero da mensagem
    int type = (int)nmsg;
    System.out.println("Numero da mensagem : "+type);

    switch(type) {            
       case 1019: {
            //int data=0;
            handle.ephemerisGPS = new gpsephemeris();

    	  //  System.out.println("\n\nMessage Number:" +nmsg+ "- Estacao:"+mp);

            int sv = 0;

            sv = (int)getBITS(sv, 6);
            handle.ephemerisGPS.satellite=(sv < 40 ? sv : sv+80);
            System.out.println("GPS Satellite ID: "+handle.ephemerisGPS.satellite);

            handle.ephemerisGPS.GPSweek=(int)getBITS(data, 10);
            handle.ephemerisGPS.GPSweek += 1024 ;
            System.out.println("GPS GPSWeek: "+ handle.ephemerisGPS.GPSweek);

            handle.ephemerisGPS.URAindex=(int)getBITS(data, 4);
            System.out.println("GPS URAindex: "+handle.ephemerisGPS.URAindex);

            sv =(int)getBITS(sv, 2);
            System.out.println("GPS SV ACCURACY  : "+sv);
            if((sv & 1)!=0)handle.ephemerisGPS.flags |= Constantes.GPSEPHF_L2PCODE;
            if((sv & 2)!=0)handle.ephemerisGPS.flags |= Constantes.GPSEPHF_L2CACODE;
            System.out.println("GPS  CODE ON L2  : "+handle.ephemerisGPS.flags);

            handle.ephemerisGPS.IDOT = GETFLOATSIGN(data, 14, Constantes.R2R_PI/(double)(1<<30)/(double)(1<<13));
            System.out.println("GPS IDOT: "+handle.ephemerisGPS.IDOT);

            handle.ephemerisGPS.IODE =(int)getBITS(data, 8);
            System.out.println("GPS IODE: "+handle.ephemerisGPS.IODE);

            handle.ephemerisGPS.TOC =(int)getBITS(data, 16);
            handle.ephemerisGPS.TOC <<= 4;
            System.out.println("GPS TOC: "+handle.ephemerisGPS.TOC);

            handle.ephemerisGPS.clock_driftrate = GETFLOATSIGN(data, 8, 1.0/(double)(1<<30)/(double)(1<<25));
            System.out.println("GPS clock_driftrate: "+handle.ephemerisGPS.clock_driftrate);

            handle.ephemerisGPS.clock_drift=GETFLOATSIGN(data, 16, 1.0/(double)(1<<30)/(double)(1<<13));
            System.out.println("GPS clock_drift: "+handle.ephemerisGPS.clock_drift);

            handle.ephemerisGPS.clock_bias=GETFLOATSIGN(data, 22, 1.0/(double)(1<<30)/(double)(1<<1));
            System.out.println("GPS clock_bias:  "+handle.ephemerisGPS.clock_bias);

            handle.ephemerisGPS.IODC=(int)getBITS(data, 10);
            System.out.println("GPS IODC: "+handle.ephemerisGPS.IODC);

            handle.ephemerisGPS.Crs=GETFLOATSIGN( data, 16, 1.0/(double)(1<<5));
            System.out.println("GPS Crs: "+ handle.ephemerisGPS.Crc);

            handle.ephemerisGPS.Delta_n=GETFLOATSIGN(data, 16, Constantes.R2R_PI/(double)(1<<30)/(double)(1<<13));
            System.out.println("GPS Delta_n: "+handle.ephemerisGPS.Delta_n);

            handle.ephemerisGPS.M0=GETFLOATSIGN(data, 32, Constantes.R2R_PI/(double)(1<<30)/(double)(1<<1));
            System.out.println("GPS M0: "+handle.ephemerisGPS.M0);

            handle.ephemerisGPS.Cuc=GETFLOATSIGN(data, 16, 1.0/(double)(1<<29));
            System.out.println("GPS Cuc: "+handle.ephemerisGPS.Cuc);

            handle.ephemerisGPS.e=GETFLOAT(data, 32, 1.0/(double)(1<<30)/(double)(1<<3));
            System.out.println("GPS Eccentricity (e) : "+handle.ephemerisGPS.e);

            handle.ephemerisGPS.Cus=GETFLOATSIGN(data, 16, 1.0/(double)(1<<29));
            System.out.println("GPS Cus: "+handle.ephemerisGPS.Cus);

            handle.ephemerisGPS.sqrt_A=GETFLOAT(data, 32, 1.0/(double)(1<<19));
            System.out.println("GPS sqrt_A: "+ handle.ephemerisGPS.sqrt_A);

            handle.ephemerisGPS.TOE=(int)getBITS(data,16);
            handle.ephemerisGPS.TOE <<= 4;
            System.out.println("GPS TOE: "+handle.ephemerisGPS.TOE);

            handle.ephemerisGPS.Cic=GETFLOATSIGN(data, 16, 1.0/(1<<29));
            System.out.println("GPS Cic: "+handle.ephemerisGPS.Cic);

            handle.ephemerisGPS.OMEGA0=GETFLOATSIGN(data, 32, Constantes.R2R_PI/(double)(1<<30)/(double)(1<<1));
            System.out.println("GPS OMEGA0: "+handle.ephemerisGPS.OMEGA0);

            handle.ephemerisGPS.Cis=GETFLOATSIGN(data, 16, 1.0/(double)(1<<29));
            System.out.println("GPS Cis: "+handle.ephemerisGPS.Cis);

            handle.ephemerisGPS.i0=GETFLOATSIGN(data, 32, Constantes.R2R_PI/(double)(1<<30)/(double)(1<<1));
            System.out.println("GPS i0: "+handle.ephemerisGPS.i0);

            handle.ephemerisGPS.Crc=GETFLOATSIGN(data, 16, 1.0/(double)(1<<5));
            System.out.println("GPS Crc: "+handle.ephemerisGPS.Crc);

            handle.ephemerisGPS.omega=GETFLOATSIGN(data, 32, Constantes.R2R_PI/(double)(1<<30)/(double)(1<<1));
            System.out.println("GPS ω (Argument of Perigee) : "+handle.ephemerisGPS.omega);

            handle.ephemerisGPS.OMEGADOT=GETFLOATSIGN(data, 24, Constantes.R2R_PI/(double)(1<<30)/(double)(1<<13));
            System.out.println("GPS OMEGADOT: "+handle.ephemerisGPS.OMEGADOT);

            handle.ephemerisGPS.TGD=GETFLOATSIGN(data, 8, 1.0/(double)(1<<30)/(double)(1<<1));
            System.out.println("GPS TGD: "+handle.ephemerisGPS.TGD);

            handle.ephemerisGPS.SVhealth=(int)getBITS(data, 6);
            System.out.println("GPS SVhealth: "+handle.ephemerisGPS.SVhealth);

            sv =(int)getBITS(sv, 1);
            System.out.println("GPS sv: "+sv);

            if(sv !=0) handle.ephemerisGPS.flags |= Constantes.GPSEPHF_L2PCODEDATA;
            System.out.println("GPS flags: "+handle.ephemerisGPS.flags);
            ret = 1019;
        }
        break;
        
    }
        try {
            HandleByte( ret); // grava arquivo
        } catch (IOException ex) {
             System.out.println("Não foi possível a chamada");
            ex.printStackTrace();
        }
   }
    
    public static void HandleByte(int r) throws IOException {
      System.out.println("ret1 : "+r);
       if(r!=0){
       byte data[];
            
            if (r == 1019) {
                System.out.printf("ROGERIO! VAI PROCESSAR UMA MENSAGEM 19");
                 StringBuffer str = new StringBuffer("");
                System.out.println("ret2 : "+r);
                // abre arquivo GPS para rinex 3.0
                //if (handle.rinex3 != 0 && (file = handle.gpsfile) == null)
                 {

                        String n;
                         if(handle.gpsephemeris.length()>0 && handle.gpsephemeris != null) {n = handle.gpsephemeris;}
                         else {n = handle.glonassephemeris;}

                              if (n != null && n.length()>0) {

//                              handle.gpsfile = (FileConnection)Connector.open(n, Connector.READ_WRITE);
//                              if (!handle.gpsfile.exists()) {
//                                   handle.gpsfile.create();
//                              }
                              System.out.println("HAHA");
//                              dops = handle.gpsfile.openDataOutputStream();
                              System.out.println("ABRIU O DOPS");
                              String s = "     3.00            N: GNSS NAV DATA    M: Mixed            RINEX VERSION / TYPE\n";
                              str.append(s);

                              BufferAux buffer = new BufferAux(new String(new char[100]));
//                              HandleRunBy(buffer, buffer.bufferSize, null, handle.rinex3);

                              s = ""+buffer.string+"\n                                                  " +
                                    "          END OF HEADER\n";
                              str.append(s);

                              handle.gpsephemeris = "";
                              handle.glonassephemeris = "";

//                              file = handle.gpsfile;

                        }
                } //if rinex3
                                       
                        // rinex 2.1
                        if (r == 1019) {
                             System.out.println("ESTÁ NA MENSAGEM 1019: ");

                            if (handle.gpsephemeris != null && handle.gpsephemeris.length()>0) {

//                                  handle.gpsfile = (FileConnection)Connector.open("file:///root1/GPS.txt", Connector.READ_WRITE);

//                                 if (!handle.gpsfile.exists()) {
//                                    handle.gpsfile.create();
//                                  }
//                                  dops = handle.gpsfile.openDataOutputStream();

                                  BufferAux buffer = new BufferAux(new String(new char[100]));

                                  String s = "     2.10           N: GPS NAV DATA                         RINEX VERSION / TYPE\n";
                                  str.append(s);

//                                  HandleRunBy(buffer, buffer.bufferSize, null, handle.rinex3);
                                  s = ""+buffer.string+"\n                                                  " +
                                    "          END OF HEADER\n";
                                  str.append(s);

                                  handle.gpsephemeris = "";
                             }
//                         file = handle.gpsfile;
                          System.out.println("RECEBEU O ARQUIVO HANDLE 1020");
                       }
                }// rinex 2.11

                System.out.println("É UMA MSG 1019!!!");
            
               // tratamento do file
//               if (file != null) 
                {
                     System.out.println("file não é null!!!");
                     
                         System.out.println("ENTROU NA MSG 1019!!!");
                        double d;                 /* temporary variable */
                        int i;       /* temporary variable */

                        converttime cti = new converttime();
                        StringBuilder str = new StringBuilder();
                        String temp;
//                        temp = "\nMensagem 1019";
//                        str.append(temp);
                        Tempo.convertTime(cti, handle.ephemerisGPS.GPSweek, handle.ephemerisGPS.TOC);

                        if (handle.rinex3 != 0) {
                            temp = "G"+Defines.formata("02",String.valueOf(handle.ephemerisGPS.satellite), "int")+" "+Defines.formata("04",String.valueOf(cti.year), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.month), "int")+" "+Defines.formata("02",String.valueOf(cti.day), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.hour), "int")+" "+Defines.formata("02",String.valueOf(cti.minute), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.second), "int")+Defines.formata("19,12",String.valueOf( handle.ephemerisGPS.clock_bias), "double")+
                                    Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.clock_drift), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.clock_driftrate), "double")+"\n";
                             temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                             str.append(temp);/*INSERIR temp NO ARQUIVO*/
                        } else {
                             int j= cti.year % 100;
                             double k = (double) cti.second;
                            temp = Defines.formata("02",String.valueOf(handle.ephemerisGPS.satellite), "int")+" "+Defines.formata("02",String.valueOf(j), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.month), "int")+" "+Defines.formata("02",String.valueOf(cti.day),"int")+" "+Defines.formata("02",String.valueOf(cti.hour), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.minute), "int")+Defines.formata("5,1",String.valueOf(k), "float")+
                                    Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.clock_bias), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.clock_drift), "double")+
                                    Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.clock_driftrate), "double")+"\n";
                            temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                            str.append(temp);
                            /*INSERIR temp NO ARQUIVO*/

                        }
                        double aux =  (double) handle.ephemerisGPS.IODE;
                        temp = "   "+Defines.formata("19,12",String.valueOf(aux), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.Crs), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.Delta_n), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.M0), "double")+"\n";
                         temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                         str.append(temp);/*INSERIR temp NO ARQUIVO*/

                        temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.Cuc), "double")+Defines.formata("19,12",String.valueOf( handle.ephemerisGPS.e), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.Cus), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.sqrt_A), "double")+"\n";
                         temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                         str.append(temp);/*INSERIR temp NO ARQUIVO*/

                         aux = (double) handle.ephemerisGPS.TOE;
                        temp ="   "+Defines.formata("19,12",String.valueOf(aux), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.Cic), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.OMEGA0), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.Cis), "double")+"\n";
                         temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                         str.append(temp);/*INSERIR temp NO ARQUIVO*/

                        temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.i0), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.Crc), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.omega), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.OMEGADOT), "double")+"\n";
                         temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                         str.append(temp);/*INSERIR temp NO ARQUIVO*/

                        d = 0;
                        i = handle.ephemerisGPS.flags;
                        if ((i & Constantes.GPSEPHF_L2CACODE) != 0) {
                            d += 2.0;
                        }
                        if ((i & Constantes.GPSEPHF_L2PCODE) != 0) {
                            d += 1.0;
                        }
                        double week = handle.ephemerisGPS.GPSweek;
                        if((i & Constantes.GPSEPHF_L2PCODEDATA) != 0 ){
                               temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.IDOT), "double")+Defines.formata("19,12",String.valueOf(week), "double")+
                                Defines.formata("19,12",String.valueOf(1.0), "double")+"\n";
                        }
                        else{
                               temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.IDOT), "double")+Defines.formata("19,12",String.valueOf(week), "double")+
                                 Defines.formata("19,12",String.valueOf(0.0), "double")+"\n";
                        }
                        temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                        str.append(temp); /*INSERIR temp NO ARQUIVO*/

                        if (handle.ephemerisGPS.URAindex <= 6) /* URA index */ {
                            d = Math.ceil(10.0 * Mathm.pow(2.0, 1.0 + ((double) handle.ephemerisGPS.URAindex) / 2.0)) / 10.0;
                        } else {
                            d = Math.ceil(10.0 * Mathm.pow(2.0, ((double) handle.ephemerisGPS.URAindex) / 2.0)) / 10.0;
                        }
                        /* 15 indicates not to use satellite. We can't handle this special
                        case, so we create a high "non"-accuracy value. */
                        double sh = (double) handle.ephemerisGPS.SVhealth;
                        double iodc = (double) handle.ephemerisGPS.IODC;
                        temp = "   "+Defines.formata("19,12",String.valueOf(sh), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGPS.TGD), "double")+
                                Defines.formata("19,12",String.valueOf(iodc), "double")+"\n";
                        temp = Defines.replace(temp, 'E','D');System.out.println("\ntemp: "+temp);
                        str.append(temp); /*INSERIR temp NO ARQUIVO*/

                        double tow = (double) handle.ephemerisGPS.TOW;
                        temp = "   "+Defines.formata("19,12",String.valueOf(tow), "double")+Defines.formata("19,12",String.valueOf(0.0), "double")+"\n";
                        temp = Defines.replace(temp, 'E','D'); System.out.println("\ntemp: "+temp);
                        str.append(temp);
                        temp = str.toString();
                        data = temp.getBytes();
//                        dops.write(data);/*INSERIR temp NO ARQUIVO*/

            /* TOW */
                    }//if r == 1019
//                }//if file !=null
//            } //fim arq 1020 e 1019
//            else 
            {
            //System.out.println("iria entrar no arquivo");
            if(handle.observdata != null && handle.observdata.length()>0){
           // System.out.println("entrou no else!");
                int i, j, o;
//                process = true;
              
                StringBuffer strbuff = new StringBuffer("");
                String line;
                converttime cti = new converttime(); // struct converttimeinfo cti;

                if (handle.init < Constantes.NUMSTARTSKIP) {/* skip first epochs to detect correct data types */
                    ++handle.init;
                    System.out.println("incrementou init: " +handle.init);
                    if (handle.init == Constantes.NUMSTARTSKIP) {
                        System.out.println("entrou pra chamar handleheader: " +handle.init);
                       //strbuff = HandleHeader();
                        System.out.println("chamou handle!");
                    }
                    else {
                        for (i = 0; i < handle.Data.numsats; ++i) {
                            handle.startflags |= handle.Data.dataflags[i];
                        }
                        System.out.println("vai setar com false!");
                        //process = false; // continue;
                    }//else
                 }//fim if nunstartskip
                //if(process)
                  {

                    if (r == 2 && !(handle.validwarning != 0)) {
                        line = "No valid RINEX! All values are modulo 299792.458!"
                                + "           COMMENT\n";
                        strbuff.append(line); /*INSERIR LINE NO ARQUIVO*/
                        handle.validwarning = 1;
                    }
                   System.out.println("handle.Data.week!"+handle.Data.week);
                    System.out.println("handle.Data.timeofweek!"+handle.Data.timeofweek);
                     System.out.println("floor!"+(int)Math.floor(handle.Data.timeofweek / 1000.0));
                    Tempo.convertTime(cti, (int)handle.Data.week,
                            (int) Math.floor(handle.Data.timeofweek / 1000.0));
                    if (handle.rinex3 != 0) {
                        double z = (handle.Data.timeofweek / 1000.0)/1.0 ;
                        double f =  (z - (int) z)* 1.0;
                        f = cti.second + f;
                        line ="> "+Defines.formata("04",String.valueOf(cti.year), "int")+" "+Defines.formata("02",String.valueOf(cti.month), "int")+" "
                                +Defines.formata("02",String.valueOf(cti.day), "int")+" "+Defines.formata("02",String.valueOf(cti.hour), "int")
                                +" "+Defines.formata("02",String.valueOf(cti.minute), "int")+Defines.formata("11,7",String.valueOf(f), "float")
                                +" "+"0 "+Defines.formata("03",String.valueOf(handle.Data.numsats), "int")+"\n";

                        strbuff.append(line); /*INSERIR LINE NO ARQUIVO*/

                       int sat;
                        for (i = 0; i < handle.Data.numsats; ++i) {

                            int glo = 0;
                            if (handle.Data.satellites[i] <= Constantes.PRN_GPS_END) {
                                sat = handle.Data.satellites[i];
                                line ="G"+Defines.formata("02",String.valueOf(sat), "int");//RTCM3Text("G%02d", handle.Data.satellites[i]);

                                strbuff.append(line);/*INSERIR LINE NO ARQUIVO*/

                            }
                            else if (handle.Data.satellites[i] >= Constantes.PRN_GLONASS_START
                                    && handle.Data.satellites[i] <= Constantes.PRN_GLONASS_END) {
                                sat = handle.Data.satellites[i] - (Constantes.PRN_GLONASS_START - 1);
                                line ="R"+Defines.formata("02",String.valueOf(sat), "int");//RTCM3Text("R%02d", handle.Data.satellites[i] - (Constantes.PRN_GLONASS_START - 1));

                                strbuff.append(line);/*INSERIR LINE NO ARQUIVO*/
                                glo = 1;
                            }
                            else if (handle.Data.satellites[i] >= Constantes.PRN_WAAS_START
                                    && handle.Data.satellites[i] <= Constantes.PRN_WAAS_END) {
                                 sat = handle.Data.satellites[i] - Constantes.PRN_WAAS_START + 20;
                                 line ="S"+Defines.formata("02",String.valueOf(sat), "int");// RTCM3Text("S%02d", handle.Data.satellites[i] - Constantes.PRN_WAAS_START + 20);

                                 strbuff.append(line);/*INSERIR LINE NO ARQUIVO*/
                            }
                            else {
                                 sat =handle.Data.satellites[i];
                                 line = Defines.formata("03",String.valueOf(sat), "int"); //RTCM3Text("%3d", handle.Data.satellites[i]);
                                 strbuff.append(line);
                            }
                            double dat;
                            if (glo != 0) {
                                for (j = 0; j < handle.numdatatypesGLO; ++j) {
                                    int df = handle.dataflagGLO[j];
                                    int pos = handle.dataposGLO[j];
                                    if ((handle.Data.dataflags[i] & df) != 0
                                            && !Double.isNaN(handle.Data.measdata[i][pos])
                                            && !Double.isInfinite(handle.Data.measdata[i][pos])) {
                                        char lli = ' ';
                                        char snr = ' ';
                                        if (df != 0 & (Constantes.GNSSDF_L1CDATA | Constantes.GNSSDF_L1PDATA) != 0) {
                                            if ((handle.Data.dataflags[i] & Constantes.GNSSDF_LOCKLOSSL1) != 0) {
                                                lli = '1';
                                            }
                                            snr = (char) ('0' + handle.Data.snrL1[i]);
                                        }
                                        if (df != 0 & (Constantes.GNSSDF_L2CDATA | Constantes.GNSSDF_L2PDATA) != 0) {
                                            if ((handle.Data.dataflags[i] & Constantes.GNSSDF_LOCKLOSSL2) != 0) {
                                                lli = '1';
                                            }
                                            snr = (char) ('0' + handle.Data.snrL2[i]);
                                        }
                                       dat = handle.Data.measdata[i][pos];
                                       line = Defines.formata("14,3",String.valueOf(dat), "floatd")+lli+snr;//RTCM3Text("%14.3f%c%c",handle.Data.measdata[i][pos], lli, snr);
                                       strbuff.append(line); /*INSERIR LINE NO ARQUIVO*/
                                    }
                                    else { /* no or illegal data */
                                        line ="                ";//RTCM3Text("                ");
                                        strbuff.append(line);/*INSERIR LINE NO ARQUIVO*/
                                    }
                                }
                            } //if(glo)
                            else {
                                for (j = 0; j < handle.numdatatypesGPS; ++j) {
                                    int df = handle.dataflagGPS[j];
                                    int pos = handle.dataposGPS[j];
                                    if ((handle.Data.dataflags[i] & df) != 0
                                            && !Double.isNaN(handle.Data.measdata[i][pos])
                                            && !Double.isInfinite(handle.Data.measdata[i][pos])) {
                                        char lli = ' ';
                                        char snr = ' ';
                                        if ((df & (Constantes.GNSSDF_L1CDATA | Constantes.GNSSDF_L1PDATA)) != 0) {
                                            if ((handle.Data.dataflags[i] & Constantes.GNSSDF_LOCKLOSSL1) != 0) {
                                                lli = '1';
                                            }
                                            snr = (char) ('0' + handle.Data.snrL1[i]);
                                        }
                                        if ((df & (Constantes.GNSSDF_L2CDATA | Constantes.GNSSDF_L2PDATA)) != 0) {
                                            if ((handle.Data.dataflags[i] & Constantes.GNSSDF_LOCKLOSSL2) != 0) {
                                                lli = '1';
                                            }
                                            snr = (char) ('0' + handle.Data.snrL2[i]);
                                        }
                                        dat = handle.Data.measdata[i][pos];
                                        line = Defines.formata("14,3",String.valueOf(dat), "floatd")+lli+snr;//RTCM3Text("%14.3f%c%c",handle.Data.measdata[i][pos], lli, snr);
                                        strbuff.append(line);/*INSERIR LINE NO ARQUIVO*/
                                    } else { /* no or illegal data */
                                         line ="                ";//RTCM3Text("                ");
                                         strbuff.append(line);  /*INSERIR LINE NO ARQUIVO*/
                                    }
                                }
                            }
                             line ="\n";//RTCM3Text("\n");
                             strbuff.append(line);
                             /*INSERIR LINE NO ARQUIVO*/

                        }//for
                      } //fim rinex 3
                      else {
                         double z = (handle.Data.timeofweek / 1000.0)/1.0 ;
                        double f =  (z - (int) z)* 1.0;
                        f = cti.second + f;
                         line = " "+Defines.formata("02",String.valueOf((cti.year)%100), "int")+" "+Defines.formata("02",String.valueOf(cti.month), "int")+" "
                                +Defines.formata("02",String.valueOf(cti.day), "int")+" "+Defines.formata("02",String.valueOf(cti.hour), "int")
                                +" "+Defines.formata("02",String.valueOf(cti.minute), "int")+" "+
                                Defines.formata("10,7",String.valueOf(f), "float")+" "+"0 "+Defines.formata("03",String.valueOf(handle.Data.numsats), "int");
                         strbuff.append(line);
                      
                         int sat;
                         for (i = 0; i < 12 && i < handle.Data.numsats; ++i) {
                            sat = handle.Data.satellites[i];
                            if (handle.Data.satellites[i] <= Constantes.PRN_GPS_END) {
                                line="G"+Defines.formata("02",String.valueOf(sat), "int");//RTCM3Text("G%02d", handle.Data.satellites[i]);
                                strbuff.append(line);
                            } else if (handle.Data.satellites[i] >= Constantes.PRN_GLONASS_START
                                    && handle.Data.satellites[i] <= Constantes.PRN_GLONASS_END) {
                                sat = handle.Data.satellites[i] - (Constantes.PRN_GLONASS_START - 1);
                                line="R"+Defines.formata("02",String.valueOf(sat), "int");//RTCM3Text("R%02d", handle.Data.satellites[i] - (Constantes.PRN_GLONASS_START - 1));
                                strbuff.append(line);

                            } else if (handle.Data.satellites[i] >= Constantes.PRN_WAAS_START
                                    && handle.Data.satellites[i] <= Constantes.PRN_WAAS_END) {
                                 sat = handle.Data.satellites[i] - Constantes.PRN_WAAS_START + 20;
                                line="S"+Defines.formata("02",String.valueOf(sat), "int");//RTCM3Text("S%02d", handle.Data.satellites[i] - Constantes.PRN_WAAS_START + 20);
                                strbuff.append(line);
                            } else {
                                 sat =handle.Data.satellites[i];
                                  line=Defines.formata("03",String.valueOf(sat), "int");//RTCM3Text("%3d", handle.Data.satellites[i]);
                                  strbuff.append(line);
                            }
                        }
                           line="\n";//RTCM3Text("\n");
                           strbuff.append(line);
                        o = 12;
                        j = handle.Data.numsats - 12;
                        System.out.println("handle.Data.numsats : "+handle.Data.numsats);
                        while (j > 0) {
                             line="                                "; //RTCM3Text("                                ");

                             strbuff.append(line);
                             for (i = o; i < o + 12 && i < handle.Data.numsats; ++i) {
                                if (handle.Data.satellites[i] <= Constantes.PRN_GPS_END) {
                                    sat = handle.Data.satellites[i];
                                  line="G"+Defines.formata("02",String.valueOf(sat), "int");//  RTCM3Text("G%02d", handle.Data.satellites[i]);

                                  strbuff.append(line);
                                } else if (handle.Data.satellites[i] >= Constantes.PRN_GLONASS_START && handle.Data.satellites[i] <= Constantes.PRN_GLONASS_END) {
                                    sat = handle.Data.satellites[i] - (Constantes.PRN_GLONASS_START - 1);
                                    line="R"+Defines.formata("02",String.valueOf(sat), "int");// RTCM3Text("R%02d", handle.Data.satellites[i] - (Constantes.PRN_GLONASS_START - 1));

                                    strbuff.append(line); 
                                } else if (handle.Data.satellites[i] >= Constantes.PRN_WAAS_START && handle.Data.satellites[i] <= Constantes.PRN_WAAS_END) {
                                    sat = handle.Data.satellites[i] - Constantes.PRN_WAAS_START + 20;
                                    line="S"+Defines.formata("02",String.valueOf(sat), "int");// RTCM3Text("S%02d", handle.Data.satellites[i] - Constantes.PRN_WAAS_START + 20);

                                    strbuff.append(line); 
                                } else {
                                    sat = handle.Data.satellites[i];
                                    line=Defines.formata("03",String.valueOf(sat), "int");// RTCM3Text("%3d", handle.Data.satellites[i]);

                                    strbuff.append(line); 
                                }
                            }
                             line="\n";//RTCM3Text("\n");
                             strbuff.append(line); 
                            j -= 12;
                            o += 12;
                        }//fim while
                        for (i = 0; i < handle.Data.numsats; ++i) {
                            for (j = 0; j < handle.numdatatypesGPS; ++j) {
                                int v = 0;
                                int df = handle.dataflag[j];
                                int pos = handle.datapos[j];
                                if ((handle.Data.dataflags[i] & df) != 0
                                        && !Double.isNaN(handle.Data.measdata[i][pos])
                                        && !Double.isInfinite(handle.Data.measdata[i][pos])) {
                                    v = 1;
                                }
                                else {
                                    df = handle.dataflagGPS[j];
                                    pos = handle.dataposGPS[j];

                                    if ((handle.Data.dataflags[i] & df) != 0
                                            && !Double.isNaN(handle.Data.measdata[i][pos])
                                            && !Double.isInfinite(handle.Data.measdata[i][pos])) {
                                        v = 1;
                                    }
                                }

                                if (!(v != 0)) { /* no or illegal data */
                                    line="                ";// RTCM3Text("                ");
                                    strbuff.append(line);
                                }
                                else {
                                    char lli = ' ';
                                    String snr = " ";
                                    if ((df & (Constantes.GNSSDF_L1CDATA | Constantes.GNSSDF_L1PDATA)) != 0) {
                                        if ((handle.Data.dataflags[i] & Constantes.GNSSDF_LOCKLOSSL1) != 0) {
                                            lli = '1';
                                        }
                                        //snr = "0" + String.valueOf(handle.Data.snrL1[i]);
                                        snr =  String.valueOf(handle.Data.snrL1[i]);
                                    }
                                    if ((df & (Constantes.GNSSDF_L2CDATA | Constantes.GNSSDF_L2PDATA)) != 0) {
                                        if ((handle.Data.dataflags[i] & Constantes.GNSSDF_LOCKLOSSL2) != 0) {
                                            lli = '1';
                                        }
                                        //snr = "0" + String.valueOf(handle.Data.snrL2[i]);
                                        snr = String.valueOf(handle.Data.snrL2[i]);
                                    }
                                       double dat = handle.Data.measdata[i][pos];
                                       line = Defines.formata("14,3",String.valueOf(dat),"floatd")+lli+snr;//RTCM3Text("%14.3f%c%c",handle.Data.measdata[i][pos], lli, snr);
                                       strbuff.append(line);

                                }
                                if (j % 5 == 4 || j == handle.numdatatypesGPS - 1) {
                                      line="\n";//RTCM3Text("\n");
                                      strbuff.append(line);
                                }
                            }//fim for2
                        }//fim for
                    }//rinex 2.11
                line = strbuff.toString();
                data = line.getBytes();
                //dots.write(data); DataOutPutStream dots
                }//fim if
            }// if arq != 0
            }//fim else r == 1004 a 1012
  }//fim if r
}
    
    public static void main (String[] args) {
        try {
//            getSourceTable();
            //getFromMountPoint();
            System.out.println("Principal!!!!!!\n\n");
            //teste();
        } catch (Exception ex) {
            Logger.getLogger(Ntrip_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void teste () throws IOException{
        read_rtcm_test("C:\\Users\\Rogerio\\Desktop\\ntrip_ascii.txt");
        //char[] msg = "64    €*ØúÐù]tSíàÙÁ?°c®Ö	WÅ}?ò  €ÍÜ;ÌƒÓ".toCharArray();
        for (int i = 0; i < test.length; i++) {
            getMessage(test[i]);
        }
    }
    
    static char[] test = new char[72105];
    
     public static String read_rtcm_test(String fileName) throws IOException {
         System.out.println("Testando do arquivo\n\n!");
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        StringBuilder sb = new StringBuilder();             
        String sub = "";

        reader.read(test);
        
//        String mLine = reader.readLine();
//        while ( (mLine = reader.readLine()) != null ){
//        while ( !mLine.isEmpty() ){
//            mLine = reader.readLine();

//            sb.append(mLine);
//            sb.append("\n");

//            if (mLine.isEmpty()) break; // Last line of the file
                        
          
            try {
               
                 
//                }
            } catch (Exception err) {
                err.printStackTrace();
            }
//            listaEfemeridesAtual.add(efemeride);
//        }

        
        
        reader.close();
        return sb.toString();
    }

    @Override
     public void run() {
         try {
             getFromMountPoint();
//             if(done) return;
             System.out.println("hahaha fechou a thread ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     }
}
