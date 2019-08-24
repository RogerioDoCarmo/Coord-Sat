/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NTRIP;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rogerio
 */
public class Ntrip_Main {
    public static final String     SERVER          = "products.igs-ip.net";
    public static final Integer      PORT          = 2101;
    public static final String MOUNTPOINT          = "RTCM3EPH01";
    public static final String       USER          = "";
    public static final String   PASSWORD          = "";
    public static final String USR_PASS_ENCODED_64 = "cm9nZXJpdTo3MTg5MA==";
    
    static String sourceTable;  
    
    public static void getSourceTable() throws IOException {
        Socket s = new Socket(SERVER, PORT);

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

        bufRead.close();
    }
    
    public static void getFromMountPoint() throws IOException {
        Socket s = new Socket(SERVER, PORT);        
        
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
        int outStr;

        StringBuilder builder = new StringBuilder();
      
        while((outStr = bufRead.read()) != -1){
            builder.append((char)outStr).append("\n");
            System.out.print((char)outStr);
        }

        Logger.getLogger(Ntrip_Main.class.getName()).log(Level.INFO, "Request MOUNTPOINT received\n");
        
        sourceTable = builder.toString();        
        System.out.println(sourceTable);

        bufRead.close();
    }
    
    public static void main (String[] args) {
        try {
//            getSourceTable();
            getFromMountPoint();
        } catch (IOException ex) {
            Logger.getLogger(Ntrip_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //    public static void getSourceTable () throws IOException {
//        
//        String SERVER = "products.igs-ip.net";
//        Integer PORT  = 2101;
//        
//        Socket client = null;
//        char[] buf;
//        
//        InputStream input = null;
//        DataInputStream dataInput = null;
//    
//        OutputStream output = null;
//        DataOutputStream dataOut = null;
//        
//        try{
//            
//            int numbytes=0;
//            
//                client = new Socket(SERVER,PORT);
//
////                client.setKeepAlive(false);
////                client.setSoLinger(false, 5);
//                client.setSoTimeout(60000);
//
//                input = client.getInputStream();
//                dataInput = new DataInputStream(input);
//
//                output = client.getOutputStream();
//                dataOut = new DataOutputStream(output);
//
////                SERVER = "78.46.41.26";
//                
//                String request = "GET /  HTTP/1.1\r\n"
//                               + "Host: " +SERVER+ "\r\n"
//                               + "Ntrip-Version: Ntrip/1.0\r\n"
//                               + "User-Agent: NTRIP Coord_Sat/1.0\r\n"
//                               + "Authorization: Basic"                         
//                               + "Accept-Encoding: identity\r\n"                        
//                               + "Accept-Language: pt-BR,en,*\r\n"
//                               + "Connection: close";
////                               + "\r\n";
//                request = "GET / HTTP/1.1\r\n"
//                        + "Host: 78.46.41.26:2101\r\n"
//                        + "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0\r\n"
//                        + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"
//                        + "Accept-Language: pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3\r\n"
//                        + "Accept-Encoding: gzip, deflate\r\n"
//                        + "Connection: keep-alive\r\n"
//                        + "Upgrade-Insecure-Requests: 1\r\n"
//                        + "\r\n";
//  
//                Logger.getLogger(Ntrip_Main.class.getName()).log(Level.INFO, "Requisição Criada!\n");
//
//                numbytes = (request).length();
//
//                buf = new char[1000]; //array de char para receber a mensagem
//
//                char[] novo;
//                novo = request.toCharArray();
//
//                System.arraycopy(novo, 0, buf, 0, numbytes);//copiando a mensagem para o bufer
//
//                int num = novo.length;//exatamente o numero de caracteres
//
//                buf[num++] = '\n'; //especificação da requisição
//                buf[num++] = '\n'; //especificação da requisição
//                buf[num++] = '\n'; //especificação da requisição
//
//                int j=0, cont=0;
//                while (buf[j] != '\0') { //contar a qtd de caracteres do buffer menos o \0
//                    cont++;
//                    j++;
//                } 
//
//                char[] buffinal = new char[cont-1]; //qtd de caracteres do buffer - o \0
//
//                System.arraycopy(buf, 0, buffinal, 0, cont-1);//copiando o buf para o buffinal
//
//                System.out.println(buffinal); //requisição pronta
//
//                for(int i= 0;i < buffinal.length; i++){
//                    dataOut.write(buffinal[i]);
//                }
//
//                dataOut.flush(); //forçando o envio
//
//                Logger.getLogger(Ntrip_Main.class.getName()).log(Level.INFO, "Mensagem Enviada!\n");
//
//                System.out.println("Lendo os dados recebidos...");
//                           System.out.println("---------------------------------------------------------------------");
//
//                           
//                
//                int dadosEntrada;
//                StringBuffer buff2 = new StringBuffer( );
//                buff2.append("\n");
//
//                
//                while (((dadosEntrada = dataInput.read())!= -1)) {
//                    buff2.append( (char) dadosEntrada );
//                }
//
//               System.out.println(buff2.toString()); //imprimindo a sourcetable
//        } catch (Exception ex) {
//    		System.out.println("Problema no envio dos dados!");
//                ex.printStackTrace();
//    	}
//        finally {
//    		if (output != null) {
//    			output.close();
//    		}
//    		if (input != null) {
//    			input.close();
//    		}
//    		if (client != null) {
//                     System.out.println("fechando a conexão...");
//    			client.close();//fechando conexão
//    		}
//    	}
//        
//    }
    
}
