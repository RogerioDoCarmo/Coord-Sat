/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.NTRIP;

/**
 *
 * @author Fernanda
 */
class BufferAux {


    public String string = "";
    public int bufferSize = 0;
    public int tbufferpos = 0;
 //   public int offset = 0;
    public StringBuffer str= new StringBuffer("");

    public BufferAux() {
        this.string = " ";
        this.bufferSize = 0;
        this.tbufferpos = 0;

    }


    public BufferAux(String string) {
        this.string = string;
    }

    public BufferAux(int tbufferpos) {
        this.tbufferpos = tbufferpos;
    }

    public BufferAux(String string, int tbufferpos) {
        this.string = string;
        this.tbufferpos = tbufferpos;
    }
}
