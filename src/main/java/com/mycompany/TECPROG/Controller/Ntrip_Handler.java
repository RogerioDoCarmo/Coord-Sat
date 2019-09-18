/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.TECPROG.Controller;

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
    public static final String     SERVER           = "products.igs-ip.net";
//    public static final String     SERVER          = "170.84.40.52";
//    public static final String     SERVER          = "200.145.185.200";
    
    public static final Integer      PORT           = 2101;
    public static final String MOUNTPOINT           = "RTCM3EPH";
//    public static final String MOUNTPOINT          = "SIRGAS200002";
//    public static final String   MOUNTPOINT        = "POLI1";
//    public static final String MOUNTPOINT          = "IGS01";
//    public static final String MOUNTPOINT          = "PPTE1";
    
    public static final String      USER            = System.getenv("NTRIP_USER");
    public static final String  PASSWORD            = System.getenv("NTRIP_PASS");
    public static final String  USR_PASS_ENCODED_64 = Base64Coder.encodeString(USER +":"+ PASSWORD);
    
    static Socket connectionSocket;
    static String sourceTable;
    
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
      
        while((outStr = bufRead.readLine()) != null){
            builder.append(outStr+"\n");
        }
                
        sourceTable = builder.toString();        

        Logger.getLogger(Ntrip_Handler.class.getName()).log(Level.INFO, "\nPODER-SE-IA AFIRMAR QUE: Request SOURCETABLE received\n");
        
        if (connectionSocket != null) {
            connectionSocket.close();
        }
        bufRead.close();
        return sourceTable;
    }
    
}
