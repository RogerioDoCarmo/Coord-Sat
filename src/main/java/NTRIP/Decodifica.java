
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import javax.microedition.io.OutputConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fernanda
 */

/* o uso de thread permite que o dispositivo não fique travado enquanto espera por respostas de rede.
 * Escolhi fazer então, todo o codigo de conexão dentro de uma thread!
 */
public class Decodifica extends Thread implements CommandListener{

 
    private String url = new String();
    private DataOutputStream OS = null;
    private OutputStream os = null;
    private InputStream IS = null;
    private char[] buf;
    private DataInputStream is = null;
    private SocketConnection client;
    private OutputStream output = null;
    private DataOutputStream dataOut = null;
    private InputStream input = null;
    private DataInputStream dataInput = null;
    private int tiporinex;
    private String arq;
    private String server;
    private String mp;
    private String senha;
    private String user;
    private String port;

    private Form tela;
    private Inicial inicio;
    private Command comando_concluir;
    private TextField textfield;
    private Display display;
    private TextBox textbox;
    private Alert alerta_erro;
    private boolean done = false;

    char[] msg; //buffer que contém somente os dados da mensagem
    static char[] dado;
    int controle; int tam=0; int posi;
    int tam1=0;
    static char revisionstr[] = "$Revision: 1.37 $".toCharArray();
    private boolean process = true;
    OutputConnection file = null;
    DataOutputStream dops = null;
    DataOutputStream dots = null;
    FileConnection filertcm = null;
    RTCM3ParserData handle;
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


public static int CRC24(long size, char[] buf){
  int crc = 0;
  int i, j=0;

  while(size!=0)
  {
    crc ^= ((buf[j++]) << (16));
    for(i = 0; i < 8; i++)
    {
      crc <<= 1;
      if((crc & 0x1000000) != 0)
        crc ^= 0x01864cfb;
    }
    size--;
  }
  return crc;
}


    public Decodifica(Display d,Inicial in,String url, DadosUser dados,RTCM3ParserData parser) throws IOException{

                System.out.println(dados.getUsuario() );
        System.out.println(dados.getSenha() );
        System.out.println(dados.getMp() );
        System.out.println(dados.getServidor());
        System.out.println(dados.getRinex());
        System.out.println(dados.getTipoArq());
        this.server =dados.getServidor();
        this.mp=dados.getMp();
        this.senha=dados.getSenha();
        this.user=dados.getUsuario();
        this.port=dados.getPorta();


        this.url = url;
        this.inicio = in;
        this.display = d;
        this.controle=0;
        this.posi=0;
        msg= new char[2048];

        this.handle = parser;
        filertcm = (FileConnection) Connector.open("file:///root1/rtcmtext3.txt", Connector.READ_WRITE);
        if (!filertcm.exists()) {
                filertcm.create();
                              }
        dots = filertcm.openDataOutputStream();
      

         textbox = new TextBox("Gerando Arquivo","Arquivo rinex sendo gerado...Para finalizar pressione Concluir ",1000,TextField.ANY);

         comando_concluir= new Command("Concluir", Command.SCREEN, 1);
         textbox.addCommand(comando_concluir);
         textbox.setCommandListener(this);
         display.setCurrent(textbox);

        this.start();
    }

     private String processarEnvio() throws IOException {
    	try {
                 int numbytes=0;


                        client = (SocketConnection)Connector.open(url);
                        client.setSocketOption(SocketConnection.DELAY, 0);//desativado o algoritmo para peq. pacotes
                        client.setSocketOption(SocketConnection.LINGER, 5); //segundos de espera antes de fechar a conexão
                        client.setSocketOption(SocketConnection.KEEPALIVE, 0); //desativado


                        input = client.openInputStream();
                        dataInput = new DataInputStream(input);

                        output = client.openOutputStream();
                        dataOut = new DataOutputStream(output);

                        String request = new String("GET /"+mp+" HTTP/1.1\r\nHost: "+server+"\r\nNtrip-Version: Ntrip/2.0\r\nUser-Agent: NTRIP NtripRTCM3ToRINEX/1.37\r\nConnection: close\r\nAuthorization: Basic ");

                          numbytes = (request).length();
                          buf = new char[1000]; //array de char para receber a mensagem

                          char[] novo;
                          novo= request.toCharArray() ;

                          System.arraycopy(novo, 0, buf, 0, numbytes);//copiando a mensagem para o bufer


                          // função criptografia---------------------------------------------------------------------
                          String userpass = Base64Coder.encodeString(user + ":" + senha);

                          char[] u;
                          u = userpass.toCharArray();

                          System.arraycopy(u, 0, buf, novo.length, userpass.toCharArray().length);//copiando usuário e senha para o bufer

                          int num = novo.length + userpass.toCharArray().length;

                          buf[num++] = '\n'; //especificação da requisição
                          buf[num++] = '\n'; //especificação da requisição
                          buf[num++] = '\n'; //especificação da requisição

                          int j=0, cont=0;
                          while(buf[j] != '\0'){cont++;j++;}

                          char[] buffinal= new char[cont-1];

                          System.arraycopy(buf, 0, buffinal, 0, cont-1);//copiando exatamente a qntidade de carac. de buf

                          System.out.println(buffinal);
                          sendread(buffinal);



        return "Dados recebidos com sucesso!";
    	} catch (Exception ex) {
    		return "Problema no envio dos dados!";
    	}
    	finally {
    		if (output != null) {
    			output.close();
    		}
    		if (input != null) {
    			input.close();
    		}
    		if (client != null) {
                     System.out.println("fechando a conexão...");
    			client.close();//fechando conexão
    		}
    	}
    }

    public void sendread(char[] buff){
        try {

           for (int i = 0; i < buff.length; i++) {
                try {
                    dataOut.write(buff[i]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            dataOut.flush();
            System.out.println("MENSAGEM ENVIADA");
            System.out.println("Lendo os dados recebidos...");
            int dadosEntrada;
            StringBuffer messageData = new StringBuffer();
            messageData.append("\n");
            while (((dadosEntrada = dataInput.read()) != -1) && !done) {
                   messageData.append((char) dadosEntrada);
                   getMessage((char)dadosEntrada);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
return;
}

public void getMessage(char c ) {
      
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
        System.out.println(c);
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

                if(((msg[3+tam]<<16)|(msg[3+tam+1]<<8)|(msg[3+tam+2])) == CRC24(tam+3, msg)) {
                    tam2=tam;
                    decodifica(tam2);
                }

		controle=1; tam=0; posi=0;
		msg[posi]=c;
		posi=posi+1;
	}
}

   public void decodifica(int cmsg) {
      
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
            HandleByte( ret);
        } catch (IOException ex) {
             System.out.println("Não foi possível a chamada");
            ex.printStackTrace();
        }
   }

  public void HandleByte(int r) throws IOException {
      System.out.println("ret1 : "+r);
       if(r!=0){
       byte data[];
            
            if (r == 1020 || r == 1019) {
                 StringBuffer str = new StringBuffer("");
                System.out.println("ret2 : "+r);
                // abre arquivo GPS para rinex 3.0
                if (handle.rinex3 != 0 && (file = handle.gpsfile) == null) {

                        String n;
                         if(handle.gpsephemeris.length()>0 && handle.gpsephemeris != null) {n = handle.gpsephemeris;}
                         else {n = handle.glonassephemeris;}

                              if (n != null && n.length()>0) {

                              handle.gpsfile = (FileConnection)Connector.open(n, Connector.READ_WRITE);
                              if (!handle.gpsfile.exists()) {
                                   handle.gpsfile.create();
                              }
                              System.out.println("HAHA");
                              dops = handle.gpsfile.openDataOutputStream();
                              System.out.println("ABRIU O DOPS");
                              String s = "     3.00            N: GNSS NAV DATA    M: Mixed            RINEX VERSION / TYPE\n";
                              str.append(s);

                              BufferAux buffer = new BufferAux(new String(new char[100]));
                              HandleRunBy(buffer, buffer.bufferSize, null, handle.rinex3);

                              s = ""+buffer.string+"\n                                                  " +
                                    "          END OF HEADER\n";
                              str.append(s);

                              handle.gpsephemeris = "";
                              handle.glonassephemeris = "";

                              file = handle.gpsfile;

                        }
                } //if rinex3
                else {

                        if (r == 1020) {
                            System.out.println("ESTÁ NA MENSAGEM 1020 : ");

                            if (handle.glonassephemeris != null && handle.glonassephemeris.length()>0) {

                                handle.glonassfile = (FileConnection)Connector.open("file:///root1/GLONASS.txt", Connector.READ_WRITE);


                                if (!handle.glonassfile.exists()) {
                                   handle.glonassfile.create();
                                }

                                dops = handle.glonassfile.openDataOutputStream();

                                String s = "     2.10            G: GLONASS NAV DATA                     RINEX VERSION / TYPE\n";
                                str.append(s);

                                BufferAux buffer = new BufferAux(new String(new char[100]));
                                HandleRunBy(buffer, buffer.bufferSize, null, handle.rinex3);
                                s = ""+buffer.string+"\n                                                  " +
                                    "          END OF HEADER\n";
                                str.append(s);

                                handle.glonassephemeris = "";

                           }
                           file = handle.glonassfile;
                            System.out.println("RECEBEU O ARQUIVO HANDLE 1020");
                        }
                        // rinex 2.1
                        else if (r == 1019) {
                             System.out.println("ESTÁ NA MENSAGEM 1019: ");

                            if (handle.gpsephemeris != null && handle.gpsephemeris.length()>0) {

                                  handle.gpsfile = (FileConnection)Connector.open("file:///root1/GPS.txt", Connector.READ_WRITE);

                                 if (!handle.gpsfile.exists()) {
                                    handle.gpsfile.create();
                                  }
                                  dops = handle.gpsfile.openDataOutputStream();

                                  BufferAux buffer = new BufferAux(new String(new char[100]));

                                  String s = "     2.10           N: GPS NAV DATA                         RINEX VERSION / TYPE\n";
                                  str.append(s);

                                  HandleRunBy(buffer, buffer.bufferSize, null, handle.rinex3);
                                  s = ""+buffer.string+"\n                                                  " +
                                    "          END OF HEADER\n";
                                  str.append(s);

                                  handle.gpsephemeris = "";
                             }
                         file = handle.gpsfile;
                          System.out.println("RECEBEU O ARQUIVO HANDLE 1020");
                       }
                }// rinex 2.11

               // tratamento do file
                if (file != null) {
                     System.out.println("file não é null!!!");
                    if (r == 1020) {
                          System.out.println("ENTROU NA MSG 1020!!!");

                        int w = handle.ephemerisGLONASS.GPSWeek, tow = handle.ephemerisGLONASS.GPSTOW, i;

                        converttime cti = new converttime();
                        String temp;
//                        temp = "\nMensagem 1020";
//                        str.append(temp);
                        Auxiliar aux = new Auxiliar(handle.ephemerisGLONASS.GPSWeek, handle.ephemerisGLONASS.GPSTOW);
                        Tempo.updatetime(aux, handle.ephemerisGLONASS.tb * 1000, 1);
                        handle.ephemerisGLONASS.GPSWeek = (int)aux.week;
                        handle.ephemerisGLONASS.GPSTOW = (int)aux.secOfWeek;

                        Tempo.convertTime(cti, (int)aux.week,(int)aux.secOfWeek);

                        i = handle.ephemerisGLONASS.tk - 3 * 60 * 60;
                        if (i < 0) {
                            i += 86400;
                        }
                        double ik = (double) i;
                        if (handle.rinex3 != 0) {
                            temp = "R"+Defines.formata("02",String.valueOf(handle.ephemerisGLONASS.almanac_number), "int")+" "+Defines.formata("04",String.valueOf(cti.year), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.month), "int")+" "+Defines.formata("02",String.valueOf(cti.day), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.hour), "int")+" "+Defines.formata("02",String.valueOf(cti.minute), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.second), "int")+Defines.formata("19,12",String.valueOf(-handle.ephemerisGLONASS.tau), "double")+
                                    Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.gamma), "double")+Defines.formata("19,12",String.valueOf(ik), "double")+"\n";
                            temp = Defines.replace(temp, 'E','D');
                            str.append(temp);
                            /*INSERIR temp NO ARQUIVO*/

                        } else {
                            int j= cti.year % 100;
                            double k = (double) cti.second;
                            double ki = -handle.ephemerisGLONASS.tau;
                            temp = Defines.formata("02",String.valueOf(handle.ephemerisGLONASS.almanac_number), "int")+" "+Defines.formata("02",String.valueOf(j), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.month), "int")+" "+Defines.formata("02",String.valueOf(cti.day),"int")+" "+Defines.formata("02",String.valueOf(cti.hour), "int")+" "+
                                    Defines.formata("02",String.valueOf(cti.minute), "int")+Defines.formata("5,1",String.valueOf(k), "float")+
                                    Defines.formata("19,12",String.valueOf(ki), "double")+Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.gamma), "double")+
                                    Defines.formata("19,12",String.valueOf(ik), "double")+"\n";
                             temp = Defines.replace(temp, 'E','D');
                             str.append(temp);/*INSERIR temp NO ARQUIVO*/

                        }
                         if((handle.ephemerisGLONASS.flags & Constantes.GLOEPHF_UNHEALTHY) != 0){
                                temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.x_pos), "double")+Defines.formata("19,12",String.valueOf( handle.ephemerisGLONASS.x_velocity), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.x_acceleration), "double")+Defines.formata("19,12",String.valueOf(1.0), "double")+"\n";
                         }
                         else{
                                temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.x_pos), "double")+Defines.formata("19,12",String.valueOf( handle.ephemerisGLONASS.x_velocity), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.x_acceleration), "double")+Defines.formata("19,12",String.valueOf(0.0), "double")+"\n";
                         }
                         temp = Defines.replace(temp, 'E','D');
                         str.append(temp);/*INSERIR temp NO ARQUIVO*/

                         double k = (double) handle.ephemerisGLONASS.frequency_number;
                         temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.y_pos), "double")+Defines.formata("19,12",String.valueOf( handle.ephemerisGLONASS.y_velocity), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.y_acceleration), "double")+Defines.formata("19,12",String.valueOf(k), "double")+"\n";
                          temp = Defines.replace(temp, 'E','D');
                          str.append(temp);/*INSERIR temp NO ARQUIVO*/

                        k = (double) handle.ephemerisGLONASS.E;
                        temp = "   "+Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.z_pos), "double")+Defines.formata("19,12",String.valueOf( handle.ephemerisGLONASS.z_velocity), "double")+
                                Defines.formata("19,12",String.valueOf(handle.ephemerisGLONASS.z_acceleration), "double")+Defines.formata("19,12",String.valueOf(k), "double")+"\n";
                         temp = Defines.replace(temp, 'E','D');
                         str.append(temp);
                         temp = str.toString();
                         data = temp.getBytes();
                         dops.write(data);/*INSERIR temp NO ARQUIVO*/

                    } //if r == 1020
                    else  {
                         System.out.println("ENTROU NA MSG 1019!!!");
                        double d;                 /* temporary variable */
                        int i;       /* temporary variable */

                        converttime cti = new converttime();
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
                        dops.write(data);/*INSERIR temp NO ARQUIVO*/

            /* TOW */
                    }//if r == 1019
                }//if file !=null
            } //fim arq 1020 e 1019
            else {
            //System.out.println("iria entrar no arquivo");
            if(handle.observdata != null && handle.observdata.length()>0){
           // System.out.println("entrou no else!");
                int i, j, o;
                process = true;
              
                StringBuffer strbuff = new StringBuffer("");
                String line;
                converttime cti = new converttime(); // struct converttimeinfo cti;

                if (handle.init < Constantes.NUMSTARTSKIP) {/* skip first epochs to detect correct data types */
                    ++handle.init;
                    System.out.println("incrementou init: " +handle.init);
                    if (handle.init == Constantes.NUMSTARTSKIP) {
                        System.out.println("entrou pra chamar handleheader: " +handle.init);
                       strbuff = HandleHeader();
                        System.out.println("chamou handle!");
                    }
                    else {
                        for (i = 0; i < handle.Data.numsats; ++i) {
                            handle.startflags |= handle.Data.dataflags[i];
                        }
                        System.out.println("vai setar com false!");
                        process = false; // continue;
                    }//else
                 }//fim if nunstartskip
                 if(process) {

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
                dots.write(data);
                }//fim if
            }// if arq != 0
            }//fim else r == 1004 a 1012
  }//fim if r
}

public int HandleRunBy(BufferAux buffer, int buffersize, BufferAux u, int rinex3) throws IOException {

        String user = null;

        StringBuffer str = new StringBuffer("");
        if (revisionstr[0] == '$') {
                int cont = 11;
                int i = 0;
                for (; revisionstr[cont] != 0 && revisionstr[cont] != ' ' && cont < revisionstr.length ; ++cont) {
                    str.append(revisionstr[cont]);
                }
                revisionstr=str.toString().toCharArray();

         }
         user = System.getProperty("MIDlet-Vendor");
         if(user == null)user = "";

         Calendar cal = Calendar.getInstance();
         Date date = new Date();
         cal.setTime(date);
         String mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
         String dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
         String ano = String.valueOf(cal.get(Calendar.YEAR));
         String horas = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
         String minutos = String.valueOf(cal.get(Calendar.MINUTE));
         String segundos = String.valueOf(cal.get(Calendar.SECOND));

         if (u != null) u.string = user;
         String s = new String(revisionstr);
         if(rinex3 != 0){
           buffer.string = "RTCM3TORINEX "+Defines.formata("-7,7", s, "stringf")+Defines.formata("-20,20",user, "stringf")+
                   Defines.formata("04",ano, "int")+Defines.formata("02",mes, "int")+Defines.formata("02",dia, "int")+" "
                   +Defines.formata("02",horas, "int")+Defines.formata("02",minutos, "int")+Defines.formata("02",segundos, "int")+
                   " UTC PGM / RUN BY / DATE";
           System.out.println("string: "+buffer.string);
         }
         else buffer.string = "RTCM3TORINEX "+Defines.formata("-7,7", s, "stringf")+Defines.formata("-20,20",user, "stringf")
                 +Defines.formata("04",ano, "int")+"-"+Defines.formata("02",mes, "int")+"-"+Defines.formata("02",dia, "int")
                 +" "+Defines.formata("02",horas, "int")+":"+Defines.formata("02",minutos, "int")+"    PGM / RUN BY / DATE";

         return 1+buffer.string.length();

}
private StringBuffer HandleHeader() throws IOException {

        int i;
            StringBuffer strbuff = new StringBuffer("");
            HeaderData hdata = new HeaderData();
            String thebuffer = new String(new char[Constantes.MAXHEADERBUFFERSIZE]); // char thebuffer[] = new char[MAXHEADERBUFFERSIZE];
            BufferAux buffer = new BufferAux();
            buffer.string = thebuffer; // char buffer[] = thebuffer;
            buffer.bufferSize = thebuffer.length();//size_t buffersize = sizeof(thebuffer);
            i = 0; // int i;

            if(handle.rinex3 != 0){
               buffer.string = Defines.formata("9,2",String.valueOf(3.0), "float")+
                       "           OBSERVATION DATA    M (Mixed)           RINEX VERSION / TYPE\n";

            }
            else  buffer.string = Defines.formata("9,2",String.valueOf(2.11), "float")+
                       "           OBSERVATION DATA    M (Mixed)           RINEX VERSION / TYPE\n";
            strbuff.append(buffer.string);

        
             System.out.println("esta no handle header ");
            {
                final BufferAux str = new BufferAux();
                 i = HandleRunBy(buffer, buffer.bufferSize, str, handle.rinex3);

                strbuff.append(buffer.string);/*ARMAZENAR BUFFER.STRING EM ALGUM LUGAR*/
               
            }
             strbuff.append("\n");
             buffer.string = "RTCM3TORINEX                                                MARKER NAME\n";
             strbuff.append(buffer.string);

             if (!(handle.rinex3 != 0)) buffer.string = "";
             else buffer.string = "GEODETIC                                                    "
             +"MARKER TYPE\n";
             strbuff.append(buffer.string);


             buffer.string = Defines.formata("-20,20"," ", "stringf")
               +"                                        OBSERVER / AGENCY\n";

                strbuff.append(buffer.string);
      
              buffer.string =  "                                                            "
              +"REC # / TYPE / VERS\n";
              strbuff.append(buffer.string);
              buffer.string =  "                                                            "
              +"ANT # / TYPE\n";
             strbuff.append(buffer.string);
              buffer.string =
              "         .0000         .0000         .0000                  "
              +"APPROX POSITION XYZ\n";
              strbuff.append(buffer.string);
             buffer.string = "         .0000         .0000         .0000                  "
              +"ANTENNA: DELTA H/E/N\n";
              strbuff.append(buffer.string);
              if (handle.rinex3 != 0) buffer.string = "";
              else buffer.string =  "     1     1                                                "
              +"WAVELENGTH FACT L1/2\n";
              strbuff.append(buffer.string);
 
            if (handle.rinex3 != 0) {

                int flags = handle.startflags;
                BufferAux tbuffer = new BufferAux(new String(new char[6 * Constantes.RINEXENTRY_NUMBER + 1]));

                for (i = 0; i < handle.Data.numsats; ++i) {
                    flags |= handle.Data.dataflags[i];
                }

                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.C1, Constantes.C1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.L1C, Constantes.L1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.D1C, Constantes.D1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.S1C, Constantes.S1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.P1, Constantes.C1P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.L1P, Constantes.L1P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.D1P, Constantes.D1P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.S1P, Constantes.S1P);

                String s = tbuffer.str.toString();
               // System.out.println("\n\n\ncadeia:  "+s);
                buffer.string = "S  "+Defines.formata("03",String.valueOf(handle.numdatatypesGPS), "int")+
                         Defines.formata("-52,52",s, "stringf")+"  SYS / # / OBS TYPES\n";
                
                 //strbuff.append(buffer.string);
                String strS = buffer.string;
              
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.P2, Constantes.C2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.L2P, Constantes.L2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.D2P, Constantes.D2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.S2P, Constantes.S2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.C2, Constantes.C2X);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.L2C, Constantes.L2X);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.D2C, Constantes.D2X);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GPS, Constantes.S2C, Constantes.S2X);

               
                s = tbuffer.str.toString() ; System.out.println("\n\n\ncadeia:  "+s);
                buffer.string = "G  "+Defines.formata("03",String.valueOf(handle.numdatatypesGPS), "int")+
                         Defines.formata("-52,52",s, "stringf")+"  SYS / # / OBS TYPES\n";
                strbuff.append(buffer.string);
              

                if (handle.numdatatypesGPS > 13) {
                    s = tbuffer.str.toString() + 13 * 4;
                    buffer.string = "\n      "+Defines.formata("-52,52",s, "stringf")+"  SYS / # / OBS TYPES\n";
                    strbuff.append(buffer.string);
                  
                }

                tbuffer.tbufferpos = 0;
                tbuffer.str.delete(0,tbuffer.str.length());
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.C1, Constantes.C1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.L1C, Constantes.L1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.D1C, Constantes.D1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.S1C, Constantes.S1C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.P1, Constantes.C1P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.L1P, Constantes.L1P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.D1P, Constantes.D1P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.S1P, Constantes.S1P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.P2, Constantes.C2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.L2P, Constantes.L2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.D2P, Constantes.D2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.S2P, Constantes.S2P);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.C2, Constantes.C2C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.L2C, Constantes.L2C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.D2C, Constantes.D2C);
                Defines.CHECKFLAGSNEW(handle, flags, tbuffer, Constantes.GLO, Constantes.S2C, Constantes.S2C);

                s = tbuffer.str.toString();System.out.println("\n\n\ncadeia:  "+s);
                buffer.string = "R  "+Defines.formata("03",String.valueOf( handle.numdatatypesGLO), "int")+
                         Defines.formata("-52,52",s, "stringf")+"  SYS / # / OBS TYPES\n";
                strbuff.append(buffer.string);

                if (handle.numdatatypesGLO > 13) {
                    s = tbuffer.str.toString() + 13 * 4;
                      buffer.string = "\n      "+Defines.formata("-52,52",s, "stringf")+"  SYS / # / OBS TYPES\n";
                      strbuff.append(buffer.string);
                }
                strbuff.append(strS);
            }
            else {
                int flags = handle.startflags;
                int data[] = new int[Constantes.RINEXENTRY_NUMBER];
                BufferAux tbuffer = new BufferAux(new String(new char[6 * Constantes.RINEXENTRY_NUMBER + 1]));

                for (i = 0; i < Constantes.RINEXENTRY_NUMBER; ++i) {
                    data[i] = 0;
                }
                for (i = 0; i < handle.Data.numsats; ++i) {
                    flags |= handle.Data.dataflags[i];
                }
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.C1, Constantes.C1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.C2, Constantes.C2);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.P1, Constantes.P1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.P2, Constantes.P2);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.L1C, Constantes.L1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.L1P, Constantes.L1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.L2C, Constantes.L2);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.L2P, Constantes.L2);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.D1C, Constantes.D1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.D1P, Constantes.D1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.D2C, Constantes.D2);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.D2P, Constantes.D2);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.S1C, Constantes.S1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.S1P, Constantes.S1);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.S2C, Constantes.S2);
                Defines.CHECKFLAGS(handle, flags, data, buffer, Constantes.S2P, Constantes.S2);

                String s = buffer.str.toString();
                buffer.string =Defines.formata("06",String.valueOf(handle.numdatatypesGPS), "int")+
                         Defines.formata("-54,54",s, "stringf")+"# / TYPES OF OBSERV\n";
                strbuff.append(buffer.string);
                 i = 1 + buffer.string.length();

                if (handle.numdatatypesGPS > 9) {
                  s = tbuffer.str.toString() + 9 * 6;
                  buffer.string = "\n      "+Defines.formata("-54,54",s, "stringf")+"# / TYPES OF OBSERV\n";
                     strbuff.append(buffer.string);
                }
            }
            {
                converttime cti = new converttime();
                Tempo.convertTime(cti, (int)handle.Data.week,
                        (int) Math.floor(handle.Data.timeofweek / 1000.0));

                 double z = (handle.Data.timeofweek / 1000.0)/1.0 ;
                 double f =  (z - (int) z)* 1.0;
                 double j = cti.second + f;
                        buffer.string ="  "+Defines.formata("04",String.valueOf(cti.year), "int")+"    "+Defines.formata("02",String.valueOf(cti.month), "int")+"    "
                                +Defines.formata("02",String.valueOf(cti.day), "int")+"    "+Defines.formata("02",String.valueOf(cti.hour), "int")
                                +"    "+Defines.formata("02",String.valueOf(cti.minute), "int")+"   "+Defines.formata("10,7",String.valueOf(j), "float")
                                +"     GPS         "+"TIME OF FIRST OBS\n";
                        strbuff.append(buffer.string);/*INSERIR LINE NO ARQUIVO*/
            }
          buffer.string = "                                                            " + "END OF HEADER\n";
//            RTCM3Text("                                                            " + "END OF HEADER\n");
           strbuff.append(buffer.string);
           return strbuff;
        }


public void fechaArquivo() throws IOException{

                if (dops != null) {
    			dops.close();
    		}
    		if (file != null) {
    			file.close();
    		}
                if (handle.glonassfile != null) {
    			handle.glonassfile.close();
    		}
                if (handle.gpsfile != null) {
    			handle.gpsfile.close();
    		}
                if (dots != null) {
    			dots.close();

    		}if (filertcm != null) {
    			filertcm.close();
    		}
     
}

 public void commandAction(Command c, Displayable d) {

          if(c == comando_concluir){
             
               done = true;
           
               display.setCurrent(inicio.getLista_opcoes());
          }


  }

  public void run() {
        try {

               String str = processarEnvio();
               System.out.println("Resultado: "+str);
               if(done) return;
               System.out.println("hahaha fechou a thread ");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}


