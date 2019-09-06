package com.mycompany.NTRIP;

import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fernanda
 *
 *
 *
 * Esta classe implementa a tela para a informação dos parametros a serem
 * passados para a conexão com o servidor para receber a Sourcetable
 *
 */
public class ConexãoSource implements CommandListener{


    private Form tela;

    private StringItem texto;

    private Command cancelar;
    private Command conectar;
    private TextField texto_servidor;
    private TextField texto_porta;
    private Alert alerta;
    private Alert alerta_erro;
    private Display display;
    private Inicial tela_inicial;
    private DadosUser user=new DadosUser();;

    private GetSourcetable registros;
    private String url;

    ConexãoSource(Display d,Inicial tela_inicial) {
        
        this.display = d;
        this.tela_inicial = tela_inicial;


        tela = new Form("Conexão");
        texto_servidor = new TextField("Servidor: ", "", 16, TextField.ANY);
        texto_porta = new TextField("Porta: ", "", 8, TextField.ANY);
        texto = new StringItem("Deseja realmente realizar a conexão??","");

        cancelar = new Command("Cancelar", Command.CANCEL, 0);
        conectar = new Command("Conectar", Command.SCREEN, 1);

        alerta = new Alert("Conexão", "Conexão efetuada!", null, AlertType.WARNING);
        alerta_erro = new Alert("Atenção!", "Preencha todos os campos!!", null, AlertType.WARNING);
        tela.append(texto_servidor);
        tela.append(texto_porta);
        tela.append(texto);

        tela.addCommand(cancelar);
        tela.addCommand(conectar);

        tela.setCommandListener(this);

        display.setCurrent(tela);
    }

    public TextField getTexto_porta() {
        return texto_porta;
    }

    public void setTexto_porta(TextField texto_porta) {
        this.texto_porta = texto_porta;
    }

    public TextField getTexto_servidor() {
        return texto_servidor;
    }

    public void setTexto_servidor(TextField texto_servidor) {
        this.texto_servidor = texto_servidor;
    }

    public void commandAction(Command c, Displayable d) {

        if(d == tela){

            if(c == cancelar){
                display.setCurrent(tela_inicial.getLista_opcoes());
            }

            if(c == conectar){
               
                if(texto_servidor.getString().equals("") || texto_porta.getString().equals(""))   display.setCurrent(alerta_erro);
                 
                user.setServidor(this.getTexto_servidor().getString());
                url = "socket://"+this.getTexto_servidor().getString()+":"+this.getTexto_porta().getString();
                registros = new GetSourcetable(tela_inicial,url,display);
                display.setCurrent(alerta,tela_inicial.getLista_opcoes());

            }
        }
    }

}
