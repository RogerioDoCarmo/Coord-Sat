/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.TECPROG.View;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Rogerio public class 
 */
public class UI_InputDialogInFrame extends JFrame{

    private static String response;
    
    public UI_InputDialogInFrame() {

        getContentPane().setBackground(Color.DARK_GRAY);
        setTitle("Gráfico de coordenadas:");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setSize(300, 200);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);

    }

    private void closeIt(){

        this.getContentPane().setVisible(false);
        this.dispose();

    }

    public static void main(){

        UI_InputDialogInFrame frame = new UI_InputDialogInFrame();
        response = JOptionPane.showInputDialog(frame, "Escolha o satélite:", "G10");
        if(!response.isEmpty()){
            frame.closeIt();
        }

    }

    /**
     * @return the response
     */
    public static String getResponse() {
        return response;
    }

}
