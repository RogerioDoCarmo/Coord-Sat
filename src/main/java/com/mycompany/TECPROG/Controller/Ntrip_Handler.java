/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.TECPROG.Controller;

import com.mycompany.TECPROG.View.UI_Main_Form;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rogerio
 */
public class Ntrip_Handler {

    public static final String SERVER = "products.igs-ip.net";
//    public static final String     SERVER          = "170.84.40.52";
//    public static final String     SERVER          = "200.145.185.200";

    public static final Integer PORT = 2101;
//    public static final String MOUNTPOINT = "RTCM3EPH";
//    public static final String MOUNTPOINT          = "SIRGAS200002";
//    public static final String MOUNTPOINT          = "POLI1";
    public static final String MOUNTPOINT          = "IGS01";
//    public static final String MOUNTPOINT          = "PPTE1";

    public static final String USER                = System.getenv("NTRIP_USER");
    public static final String PASSWORD            = System.getenv("NTRIP_PASS");
    public static final String USR_PASS_ENCODED_64 = Base64Coder.encodeString(USER + ":" + PASSWORD);

    static Socket connectionSocket;
    static String sourceTable;

    private static BufferedReader bufRead; // to read the RTCM messages
    
    //====================================================================================
    private static boolean done = false;
    static char[] dado;
    static char[] msg = new char[2048]; //buffer que contém somente os dados da mensagem
    //static char[] dado;
    static int controle = 0;
    static int tam = 0;
    static int posi = 0;
    static int tam1 = 0;

    //---------------------------------------------------------------------------------------------
    // variáveis definidas no define
    static long numbits;
    static long bitfield;
    static int size;
    static int m = 0; //variável de iteração para percorrer a mensagem no loadbits
    //====================================================================================

    public static String getSourceTable() throws IOException {
        connectionSocket = new Socket(SERVER, PORT);

        String msg = "";

        msg += "GET / HTTP/1.1\r\n"
                + "Host: " + SERVER + "\r\n"
                + "Ntrip-Version: Ntrip/2.0\r\n"
                + "User-Agent: NTRIP TEC_Rogerio/1.0\r\n"
                + "\r\n";

        connectionSocket.getOutputStream().write(msg.getBytes());
        connectionSocket.getOutputStream().flush();

        Logger.getLogger(Ntrip_Handler.class.getName()).log(Level.INFO, "PODER-SE-IA AFIRMAR QUE: Request SOURCETABLE sent\n");

        //Creates a BufferedReader that contains the SERVER response
        BufferedReader bufRead = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        String outStr;

        StringBuilder builder = new StringBuilder();

        while ((outStr = bufRead.readLine()) != null) {
            builder.append(outStr + "\n");
        }

        sourceTable = builder.toString();

        Logger.getLogger(Ntrip_Handler.class.getName()).log(Level.INFO, "\nPODER-SE-IA AFIRMAR QUE: Request SOURCETABLE received\n");

        if (connectionSocket != null) {
            connectionSocket.close();
        }
        bufRead.close();
        return sourceTable;
    }

    public static void getFromMountPoint() throws IOException {
        connectionSocket = new Socket(SERVER, PORT);
//        msg= new char[5000];
        String wtr = "";

        wtr += "GET /" + MOUNTPOINT + " HTTP/1.1\r\n"
                + "Host: " + SERVER + "\r\n"
                + "Ntrip-Version: Ntrip/2.0\r\n"
                + "User-Agent: NTRIP TEC_Rogerio/1.0\r\n"
                + "Authorization: Basic " + USR_PASS_ENCODED_64 + "\r\n"
                + "Connection: close\r\n"
                + "Accept-Encoding: gzip\r\n"
                + "Accept-Language: pt-BR,en,*\r\n"
                + "\r\n";

        connectionSocket.getOutputStream().write(wtr.getBytes());
        connectionSocket.getOutputStream().flush();

        Logger.getLogger(Ntrip_Handler.class.getName()).log(Level.INFO, "\nPODER-SE-IA AFIRMAR QUE: Request MOUNTPOINT sent\n");

        //Creates a BufferedReader that contains the SERVER response
        bufRead = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        int dadosEntrada;

        StringBuilder builder = new StringBuilder();

        builder.append("\n");
        while ((dadosEntrada = getMountpointBuffer().read()) != -1 && !done) {
            builder.append((char) dadosEntrada);
//            builder.append("\n");
            UI_Main_Form.NtripBuffer.append((char)dadosEntrada);
//            System.out.print((char)dadosEntrada);
            //getMessage((char) dadosEntrada);
        }

        Logger.getLogger(Ntrip_Handler.class.getName()).log(Level.INFO, "\nPODER-SE-IA AFIRMAR QUE: Request MOUNTPOINT received\n");

//        sourceTable = builder.toString();        
//        System.out.println(sourceTable);
        if (connectionSocket != null) {
            connectionSocket.close();
        }

        getMountpointBuffer().close();
    }
    
    static int contt = 0;
    public static void getMessage(char c) {
       /* 
       msg[0] - preambulo
       msg[1] - bits reservados
       msg[2] - tamanho da mensagem
       msg[3] - mensagem
         *
       msg[n] - CRC
       msg[n+1] - CRC
       msg[n+2] - CRC
       */

        int tam2;
        char preambulo = 0xD3;

        System.out.print(c);
        if (c == preambulo && controle == 0) { //se for o preambulo
            msg[posi] = c; //recebendo preambulo
            controle = 1; //passou pelo pré ambulo
            posi = posi + 1;
        } else if (controle == 1 && (c >= (char) 0x00 && c <= (char) 0x03)) { // bits reservados
            msg[posi] = c; //recebendo bits reservados
            controle = 2; //passou pelos bits reservados
            posi = posi + 1;
        } else if (controle == 1 && (c >= (char) 0x00 && c <= (char) 0x03)) {
            controle = 0; //volta ao inicio para proxima mensagem
            posi = 0; //indice volta
        } else if (controle == 2) { //tamanho da mensagem
            msg[posi] = c;
            //  System.out.println("msg[2]:"+(int)msg[posi]);
            tam1 = (msg[1] & 0x03) << 8; //tamanho da mensagem
            // System.out.println("tam1:"+tam1);
            tam = tam1 | msg[2];
            System.out.println("tam:" + tam);
            tam2 = (msg[1] & 0x03) << 8 | msg[2];
            System.out.println("tam:" + tam2);
            posi = posi + 1;
            controle = 3; //passou pelo tamanho
        } else if (controle == 3 && posi < tam + 6) { //roda toda a mensagem (+3 p o posi ir p o fim da mensagem + 3 casas do CRC)
            msg[posi] = c;
            posi = posi + 1;
        } else if (controle == 3 && posi >= tam + 6) { //terminou de ler toda a mensagem
            System.out.println("\n\nTerminou a mensagem!\n\n");

//            int teste = ((msg[3 + tam] << 16) | (msg[3 + tam + 1] << 8) | (msg[3 + tam + 2]));
//            int CRC = CRC24(tam + 3, msg);
//                if( ((msg[3+tam]<<16)|(msg[3+tam+1]<<8)|(msg[3+tam+2])) == CRC24(tam+3, msg) ) {
            if (1 == 1) {
                tam2 = tam;
//                    decodifica(tam2);
            }
//decodifica(tam);
//		controle=1; tam=0; posi=0;

            try {

                controle = 0;
                tam = 0;
                posi = 0;
                msg[posi] = c;
                posi = posi + 1;
                //msg = new char[2048];
                //posi=posi+1;
            } catch (IndexOutOfBoundsException ex) {
                System.out.println("ERRO!!!");
            }
        }
    }

    /**
     * @return the bufRead
     */
    public static BufferedReader getMountpointBuffer() {
        return bufRead;
    }

}
