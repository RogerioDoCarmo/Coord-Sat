/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.TECPROG.Controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.PrintWriter;

/**
 *
 * @author Rogerio
 */
public class FTP_Handler {
    private String mServerName;
    private Integer mPortNumber;
    private String mUserName;
    private String mPassword;
    private String mFileName;

    public File getmNewFile() {
        return mNewFile;
    }

    private File mNewFile;

    private String resp;

    public FTP_Handler(String server, int portNumber, String user, String password,
                       String fileName, File localFile){
        this.mServerName = server;
        this.mPortNumber = portNumber;
        this.mUserName = user;
        this.mPassword = password;
        this.mFileName = fileName;
        this.mNewFile = localFile;
    }


    public boolean downloadAndSaveFile() throws IOException {

        FTPClient ftp = null;

        try {
            ftp = new FTPClient();

            ftp.connect(mServerName, mPortNumber);
            ftp.enterLocalPassiveMode();
//            Log.d("FTP", "Connected. Reply: " + ftp.getReplyString());

            ftp.login(mUserName, mPassword);
            ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();

            boolean success = false;

            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    mNewFile))) {
                success = ftp.retrieveFile(mFileName, outputStream);
            }

            return success;
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
//            return resp;
        }
        finally {
            if (ftp != null) {
                ftp.logout();
                ftp.disconnect();
            }
        }
        return false;
    }
}