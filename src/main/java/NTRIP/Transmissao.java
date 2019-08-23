
import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
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
 * Classe para implementar a tela que contém os parametros
 * requeridos para a autorização
 */
public class Transmissao implements CommandListener{
    private Form tela;
    private Inicial inicio;
    private Command comando_voltar;
    private Command comando_concluir;

    private Display display;
    private Decodifica registros;
    private String url;
    private Alert alerta_erro;

    private TextField texto_servidor;
    private TextField texto_porta;
    private TextField texto_usuario;
    private TextField texto_senha;
    private TextField texto_mp ;

    private ChoiceGroup rinex, arqs;
    DadosUser dados;

    public Alert getAlerta_erro() {
        return alerta_erro;
    }

    public void setAlerta_erro(Alert alerta_erro) {
        this.alerta_erro = alerta_erro;
    }

    public TextField getTexto_mp() {
        return texto_mp;
    }

    public void setTexto_mp(TextField texto_mp) {
        this.texto_mp = texto_mp;
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

    public TextField getTexto_usuario() {
        return texto_usuario;
    }

    public void setTexto_usuario(TextField texto_usuario) {
        this.texto_usuario = texto_usuario;
    }

    public TextField getTexto_senha() {
        return texto_senha;
    }

    public void setTexto_senha(TextField texto_senha) {
        this.texto_senha = texto_senha;
    }




    Transmissao(Display display,Inicial tela_inicial) {
        this.display = display;
        this.inicio = tela_inicial;

         dados = new DadosUser();

         tela = new Form("Definição de parametros");

         texto_servidor = new TextField("Servidor: ", "", 16, TextField.ANY);
         texto_porta = new TextField("Porta: ", "", 8, TextField.ANY);
         texto_usuario = new TextField("Usuário: ", "", 20, TextField.ANY);
         texto_senha = new TextField("Senha: ", "", 10, TextField.PASSWORD);
         texto_mp = new TextField("MountPoint: ", "", 10, TextField.ANY);
         String[] strings = {"3.0","2.11"};
         rinex = new ChoiceGroup("Rinex", ChoiceGroup.EXCLUSIVE,strings, null);
         String[] string = {"Ephemeris GPS","Ephemeris GLONASS","Observação"};
         arqs = new ChoiceGroup("Tipo do Arquivo a gerar", ChoiceGroup.EXCLUSIVE,string, null);


        alerta_erro = new Alert("Erro", "Por favor preencha todos os campos corretamente!", null, AlertType.ALARM);

        tela.append(texto_servidor);
        tela.append(texto_porta);
        tela.append(texto_usuario);
        tela.append(texto_senha);
        tela.append(texto_mp);
        tela.append(rinex);
        tela.append(arqs);

        comando_voltar = new Command("Voltar", Command.CANCEL, 0);
        comando_concluir= new Command("Concluir", Command.SCREEN, 1);
        tela.addCommand(comando_voltar);
        tela.addCommand(comando_concluir);

        alerta_erro = new Alert("Atenção!", "Preencha todos os campos!!", null, AlertType.WARNING);
        tela.setCommandListener(this);

//         texto_servidor = new TextField("Servidor: ", "200.145.185.200", 16, TextField.ANY);
//         texto_porta = new TextField("Porta: ", "2101", 8, TextField.ANY);
//          texto_usuario = new TextField("Usuário: ", "shimabukuro", 20, TextField.ANY);
//           texto_senha = new TextField("Senha: ", "integrity", 10, TextField.PASSWORD);
//            texto_mp = new TextField("MountPoint: ", "PPTE1", 10, TextField.ANY);
        display.setCurrent(tela);

    }

    public void commandAction(Command c, Displayable d) {
          if(c == comando_voltar){
                display.setCurrent(inicio.getLista_opcoes());
            }

            if(c == comando_concluir){

                if(texto_servidor.getString().equals("") || texto_porta.getString().equals("")
                        || texto_usuario.getString().equals("")|| texto_senha.getString().equals("")
                        || texto_mp.getString().equals("")){

                     display.setCurrent(alerta_erro);
                }
                else{
                      if(rinex.isSelected(0)) dados.setRinex(3);
                      else dados.setRinex(0);
                      dados.setMp(this.getTexto_mp().getString());
                      dados.setPorta(this.getTexto_porta().getString());
                      dados.setSenha(this.getTexto_senha().getString());
                      dados.setServidor(this.getTexto_servidor().getString());
                      if(arqs.isSelected(0)) dados.setTipoArq("Ephemeris GPS");
                      else if(arqs.isSelected(1)) dados.setTipoArq("Ephemeris GLONASS");
                      else dados.setTipoArq("Observação");
                      dados.setUsuario(this.getTexto_usuario().getString());
                      String modo = "dados";
                      url = "socket://"+this.getTexto_servidor().getString()+":"+this.getTexto_porta().getString();
                      System.out.println(url);
                try {
//                    String arq = "OBS";
                     RTCM3ParserData parser = new RTCM3ParserData();
                     parser.rinex3 = dados.rinex;
                     if(dados.tipoArq == "Ephemeris GPS"){ parser.gpsephemeris = "file:///root1/GPS.txt";}
                     else if(dados.tipoArq  == "Ephemeris GLONASS")parser.glonassephemeris = "file:///root1/GLONASS.txt";
                     else parser.observdata = "file:///root1/rtcmtext.txt";

                    registros = new Decodifica(display, inicio, url, dados, parser);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                }
            }

    }

}
