/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.NTRIP;

/**
 *
 * @author Fernanda
 */
public class DadosUser {



    public String servidor;
    public String porta;
    public String usuario;
    public String senha;
    public String mp;
    public String tipoArq; //tipo do arquivo a ser gerado
    public int rinex; //tipo do rinex

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public int getRinex() {
        return rinex;
    }

    public void setRinex(int rinex) {
        this.rinex = rinex;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getTipoArq() {
        return tipoArq;
    }

    public void setTipoArq(String tipoArq) {
        this.tipoArq = tipoArq;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String tusuario) {
        this.usuario = tusuario;
    }


}
