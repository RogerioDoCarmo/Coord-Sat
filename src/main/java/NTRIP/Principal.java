/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.microedition.midlet.*;
import javax.microedition.lcdui.Display;

/**
 * @author Fernanda
 */

/*Esta classe, implementa a classe abstrata MIDlet, ela inicia o aplicativo*/
public class Principal extends MIDlet {


    private Display d;
    private Inicial tela_inicial;

    public Principal() {

        d = Display.getDisplay(this);
        tela_inicial = new Inicial(this,d);


    }
    public void startApp() {
//       Enumeration e = FileSystemRegistry.listRoots();
//       while (e.hasMoreElements()) {
//           String rootName = (String)e.nextElement();
//           System.out.println("mounted root:"+rootName);
//       }


//         String s = Defines.formata("-7,7", "1.37", "stringf");
         d.setCurrent(tela_inicial.getLista_opcoes());
    }

    public void pauseApp() {

    }

    public void destroyApp(boolean unconditional) {
        
    }
}