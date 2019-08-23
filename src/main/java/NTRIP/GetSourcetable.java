
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
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
public class GetSourcetable extends Thread implements CommandListener{
    private Inicial inicio;
    private String url = new String();
    private DadosUser user = new DadosUser();
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
    private String server;
    private Display display;
    private boolean alldone = false;

    private Form tela;
    private Command comando_voltar;
    private Command comando_conectar;
    private TextField textfield;
    private TextBox textbox;
    private StringBuffer buffer = new StringBuffer( );

 
   public GetSourcetable(Inicial in,String url,Display d) {
        this.inicio = in;
        this.url = url;
        this.display = d;
        textbox = new TextBox("Pontos RTCM 3.0",buffer.toString(),1000,TextField.ANY);
        comando_voltar = new Command("Voltar", Command.CANCEL, 0);
        comando_conectar= new Command("Concluir", Command.SCREEN, 1);
        textbox.addCommand(comando_voltar);
        server = user.getServidor();
        System.out.println("conectado " +server);
        textbox.setCommandListener(this);
        this.start();

   }


    private String processarEnvio() throws IOException {
    	try {
                 int numbytes=0;


                        client = (SocketConnection)Connector.open(url); //conexão com a url especificada
                        client.setSocketOption(SocketConnection.DELAY, 0); //
                        client.setSocketOption(SocketConnection.LINGER, 5); //
                        client.setSocketOption(SocketConnection.KEEPALIVE, 0); //

                        input = client.openInputStream();
                        dataInput = new DataInputStream(input);

                        output = client.openOutputStream(); //fluxo de saída
                        dataOut = new DataOutputStream(output);

                        String request = new String("GET /  HTTP/1.1\r\nHost: "+server+"\r\nNtrip-Version: Ntrip/2.0\r\nUser-Agent: NTRIP NtripRTCM3ToRINEX/1.37\r\nConnection: close\r\nAuthorization: Basic ");
                           numbytes = (request).length();
                           buf = new char[1000]; //array de char para receber a mensagem

                           char[] novo;
                           novo= request.toCharArray() ;

                           System.arraycopy(novo, 0, buf, 0, numbytes);//copiando a mensagem para o bufer

                           int num = novo.length;//exatamente o numero de caracteres

                           buf[num++] = '\n'; //especificação da requisição
                           buf[num++] = '\n'; //especificação da requisição
                           buf[num++] = '\n'; //especificação da requisição

                           int j=0, cont=0;
                           while(buf[j] != '\0'){cont++;j++;} //contar a qtd de caracteres do buffer menos o \0

                           char[] buffinal= new char[cont-1]; //qtd de caracteres do buffer - o \0

                           System.arraycopy(buf, 0, buffinal, 0, cont-1);//copiando o buf para o buffinal

                           System.out.println(buffinal); //requisição pronta

                           for(int i= 0;i<buffinal.length;i++){ dataOut.write(buffinal[i]);}

                           dataOut.flush(); //forçando o envio

                           System.out.println("MENSAGEM ENVIADA!");

                           System.out.println("Lendo os dados recebidos...");
                           System.out.println("---------------------------------------------------------------------");

                           int dadosEntrada;
                           StringBuffer buff = new StringBuffer( );
                           buff.append("\n");

                           while (((dadosEntrada = dataInput.read())!= -1)) {

	                         buff.append( (char) dadosEntrada );

                           }
                           System.out.println(buff.toString()); //imprimindo a sourcetable


                           soucetable(buff); //chamando a tela

                        
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

public void soucetable(StringBuffer buff){
String text=new String();
        text = buff.toString();

        String[] lineSource = Defines.split(text,'\n');
       // System.out.println(lineSource.length); //linhas que contém RTCM 3
        for(int i =0;i<lineSource.length;i++){
              String imp = lineSource[i];
              String[] breaks = Defines.split(lineSource[i],';');
               if(breaks==null){}//linha nula, não é tratada

               else{if(breaks[0].equals("CAS")){

                      System.out.println(imp);
                }else if(breaks[0].equals("NET")){

                       System.out.println(imp);
                }else if(breaks[0].equals("STR")){

                       if (breaks[3].equals("RTCM 3.0")){
                           System.out.println(imp);
                           // buffer.append(breaks[0]+" "+breaks[1]+" "+breaks[2]+" "+breaks[3]+"\n" );
                          buffer.append(breaks[0]+" "+breaks[1]+" "+breaks[2]+"\n" ); //sem a string RTCM3

                       }
                }

              }

          }
         textbox.setString(buffer.toString());
         display.setCurrent(textbox);


}

 public void commandAction(Command c, Displayable d) {

        if(c == comando_voltar){
                display.setCurrent(inicio.getLista_opcoes());
                alldone = true;
           }
    }

    public void run() {
        try {


               String str = processarEnvio();
               System.out.println("Resultado: "+str);
               if(alldone) return;
               System.out.println("hahaha fechou a thread ");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}

