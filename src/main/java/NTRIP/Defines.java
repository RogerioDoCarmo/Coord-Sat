
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fernanda
 */
public class Defines {

    /**Função para a formatação dos valores para imprimi-los nos arquivos.
     * Esta plataforma não possui API's específicas para isso, por tanto,
     * implementei a minha função para a formatação, de acordo com o que
     * os arquivos rinex especificam.
     * format é o formato no qual deve ser impresso;
     * valor é o valor a ser impresso;
     * tipo é a especificação do tipo do valor.
     */
  public final static String formata(String format, String valor, String tipo){
     System.out.print("\n----------------------------------------------------------------");
      System.out.print("\n"+format);
     System.out.print("\n"+valor);
     System.out.print( "\n"+tipo);
     char[] v = valor.toCharArray();
     char[] str = null;
     char[] camp = new char[100];
    
     if(tipo.equals("stringf")||tipo.equals("string")){
         int n = 0,i=0;
         if(tipo.equals("stringf")){ //string com especificação de ajuste
             String[] campos = split(format,',');
             camp = campos[0].toCharArray();
             n = Integer.parseInt(campos[1]);
         }
         else { //string comum
             camp = format.toCharArray();
             format = format.substring(1, format.length());
             n = Integer.parseInt(format);
         }
         str = new char[n];
         while(i < n){ str[i++] = ' '; }
         if(camp[0] == '-' ){ //posicionado a esquerda
             if(v.length<n){ //garantindo espaço
                 System.arraycopy( v, 0,str, 0, v.length);
                 //System.out.print("str: "+str[0]);
             }
             else {
                 System.arraycopy( v, 0,str, 0, n);
               //  System.out.print("str: "+str[0]);
             }

         }
         else { //posicionado à direita
             if(v.length<n){
                 System.arraycopy( v, 0,str, n - v.length, v.length);
             }
             else System.arraycopy( v, 0,str, 0, n);
         }
      }
     if(tipo == "int"){
       int i = 0;
       int n = Integer.parseInt(format);
       str = new char[n];
       int l = valor.length();
       if (l <= n){
           while(i < n){ str[i++] = '0'; }
           System.arraycopy( v, 0,str, n - v.length, v.length);
       }
       else{ System.out.print("\nErro de formatação!!!");}

     }

     /*Aqui o double possui elevação "E" para que apareça dessa forma no arquivo, como estudado*/
     if(tipo.equals("double")){
         String sub = "";String sub2 = "";String sub3 = "";String sub4 = "";String sub5 = "";
         int i=0;
         char[] str1 = null; char[] str2 = null;
         String[] campos = split(format,',');
         int vl1 = Integer.parseInt(campos[0]);// parte inteira do formato
         int vl2 = Integer.parseInt(campos[1]);// parte decimal do formato

         str = new char[vl1]; //parte inteira

         int p=0;
          boolean b=false;
          while(p<v.length){
          if(v[p++] == 'E'){b = true;} //verificando se é elevado
          }
         /*Se não for elevado, transforma para que seja, conforme visto na formatação dos arquivos
          esta plataforma não especifica valores com elevação positiva "E+" por isso eu implementei*/
         if(!b){
          int pos,fim;
          boolean t=false;
           p=0;
           while(p<v.length){
           if(v[p++] == '-'){ t = true;} //verificando se é negativo
          }
          pos = valor.indexOf(".");
          if(t) sub = valor.substring(1, valor.indexOf(".")); //pegando a parte inteira sem sinal
          else sub = valor.substring(0, valor.indexOf(".")); //pegando a parte inteira
          sub2 = valor.substring(valor.indexOf(".")+1, valor.length()); //pegando a parte decimal
          sub3 = (sub+sub2).trim();//transformando em numero "junto"
          str1 = new char [sub3.length()+1];
          i=0;
          while(i < str1.length){ str1[i++] = ' '; } //preenchendo com espaço
          str1[1] = '.';
          char[] var3 = sub3.toCharArray();
          System.arraycopy( var3, 0,str1,0, 1);
          System.arraycopy( var3, 1,str1,2, var3.length-1);
          if(t)fim = pos-2;
          else fim = pos-1;
          sub4 = "E"+"+"+String.valueOf(fim);
          sub5 = (String.valueOf(str1)+sub4).trim();
          System.out.print("\nsub5: "+sub5);
          valor = sub5;
           if(t) valor = ("-"+sub5).trim();
         }

         sub = valor.substring(0,valor.indexOf(".")); // parte inteira do valor
         sub2= valor.substring(valor.indexOf(".")+1,valor.indexOf("E")); // parte decimal do valor
         sub3= valor.substring(valor.indexOf("E"),valor.length()); // parte da elevação

         str1 = new char [sub.length()+1];
         str2 = new char [vl2];
         char[] var1 = sub.toCharArray();
         char[] var2 = sub2.toCharArray();
         char[] var3 = sub3.toCharArray();
         System.out.print("\nvar1: "+String.valueOf(var1));
         System.out.print("\nvar2: "+String.valueOf(var2));
         System.out.print("\nvar3: "+String.valueOf(var3));


         while(i < str.length){ str[i++] = ' '; } //preenchendo com espaço
         i=0;
         while(i < str1.length){ str1[i++] = ' '; } //preenchendo com espaço
         str1[i-1] = '.';
         i=0;
         while(i < str2.length){ str2[i++] = '0'; } //preenchendo com 0

         if(var2.length>str2.length) System.arraycopy( var2, 0,str2, 0, str2.length);
         else System.arraycopy( var2, 0,str2, 0, var2.length);
         System.arraycopy( var1, 0,str1,0, var1.length);

         System.arraycopy( var3, 0,str,str.length-var3.length, var3.length);
         System.arraycopy( str2, 0,str, str.length-(var3.length+str2.length), str2.length);
         System.arraycopy( str1, 0,str,str.length-(var3.length+str2.length+str1.length), str1.length);

         System.out.println("\n"+sub4);
     }

     /*Aqui o double e o float possuem elevação "E", porém para que não apareça dessa forma no arquivo,
      é chamado outro método para transforma-lo num valor com casas decimais sem elevação
      */
     if(tipo.equals("float")||tipo.equals("floatd")||tipo.equals("doubled")){
         if(tipo.equals("floatd")||tipo.equals("doubled")){valor = formatDouble(valor);}
         String sub = "";String sub2 = "";
         boolean t=false;
         int i=0;
         char[] str1 = null; char[] str2 = null;
         char[] z = valor.toCharArray();
         String[] campos = split(format,',');
         int vl1 = Integer.parseInt(campos[0]);// parte inteira do formato
         int vl2 = Integer.parseInt(campos[1]);// parte decimal do formato
         str2 = new char [vl2]; //tamanho da parte decimal especificada pelo formato

         /*Após a transformação, pode ser que o valor não contenha mais o ponto "."
          os dois casos são tratados aqui,
          */
         while(i<z.length){
         if(z[i++] == '.'){ t = true;} //verificando se tem ponto
         }
         if(t){
            sub = valor.substring(0,valor.indexOf(".")); // parte inteira do valor
            sub2= valor.substring(valor.indexOf(".")+1,valor.length()); // parte decimal do valor
            str = new char[vl1]; //parte inteira do formato
            str1 = new char [sub.length()+1]; //"."
            char[] var1 = sub.toCharArray();
            char[] var2 = sub2.toCharArray();
            System.out.print("\nvar1: "+String.valueOf(var1));
            System.out.print("\nvar2: "+String.valueOf(var2));
            i=0;
            while(i < str.length){ str[i++] = ' '; }
            i=0;
            while(i < str1.length){ str1[i++] = ' '; } //preenchendo com espaço
            str1[i-1] = '.';
            i=0;
            while(i < str2.length){ str2[i++] = '0'; } //preenchendo com 0

            if(var2.length>str2.length) {System.out.print("\nNumero com ponto e maior que o formato");System.arraycopy( var2, 0,str2, 0, str2.length);}
            else {System.out.print("\nNumero com ponto e menor que o formato");System.arraycopy( var2, 0,str2, 0, var2.length);}
            System.arraycopy( var1, 0,str1, str1.length-var1.length-1, var1.length);
            System.arraycopy( str2, 0,str, str.length-str2.length, str2.length);
            System.arraycopy( str1, 0,str,str.length-str2.length-str1.length, str1.length);
         }
         else{
            str = new char[vl1]; //parte inteira do formato
            str1 = new char [z.length+1]; //tamanho do numero + "."
            i=0;
            while(i < str.length){ str[i++] = ' '; }
            i=0;
            while(i < str1.length){ str1[i++] = ' '; } //preenchendo com espaço
            str1[i-1] = '.';
            i=0;
            while(i < str2.length){ str2[i++] = '0'; } //preenchendo com 0
            if(z.length < vl1){ //se for menor que o tamanho do formato
                 System.out.print("\nNumero sem ponto e menor que o formato");
                System.arraycopy( z, 0,str1, str1.length-z.length-1, z.length);// deixando o ponto "."
                System.arraycopy( str2, 0,str, str.length-str2.length, str2.length); //copiando o str2 no final
                System.arraycopy( str1, 0,str,str.length-str2.length-str1.length, str1.length);//copiando o str1 no final

            }
            else {
                 System.out.print("\nNumero sem ponto e maior que o formato");
                 str1 = new char [vl1];
                 i=0;
                 while(i < str1.length){ str1[i++] = ' '; } //preenchendo com espaço
                 str1[str1.length-vl2-1] = '.';
                System.arraycopy( z, 0,str1, 0, vl1-vl2-1);//parte inteira sendo copiada
                System.arraycopy( z,  vl1-vl2-1 ,str1, vl1-vl2, vl2);//parte decimal sendo copiada
                System.arraycopy( str1, 0,str,str.length-str1.length, str1.length);//copiando o valor todo justificado a direita
            }
          }

     }
     String s = String.valueOf(str);
     int p = s.length();
//     System.out.print("\np: "+p);
     System.out.print("\na string final é:"+s);
     return s;
 }

  /* Retira os 'E's do double e o faz numero com suas casas decimais */
  public static String formatDouble(String x){
  System.out.println("\nx:"+x);
    String sub,q,r;
    int p=0, i=0;
    boolean t=false, g = false,l = false;
    char[] c = null;
    char[] z1 = x.toCharArray(); //valor inteiro
     char[] z;
    if (z1[0]=='-'){ //verificando se eh negativo
        z = new char[z1.length-1]; //valor sem sinal
        System.out.println("\nz1.length: "+z1.length);
        System.out.println("\nz.length: "+z.length);
        System.arraycopy(z1, 1, z ,0, z1.length-1); //copiando valor sem sinal
        l = true; //é negativo
    }
    else{
        z = new char[z1.length];
        System.arraycopy( z1, 0,z,0, z1.length); //copia todo o valor
    }
    String h = String.valueOf(z); //transforma em string
    while(p<z.length){
      if(z[p++] == 'E'){ g = true;} //verificando se é elevado //
    }
    if(g){
      p=0;
      while(p<z.length){
         if(z[p++] == '-'){ t = true;} //verificando se é elevado negativa//
      }
      if(t){
         int b = h.indexOf("-");
         sub = h.substring(b+1, h.length()).trim(); //pegando o valor a que foi elevado negativa//
         int tam = Integer.parseInt(sub);
         q = h.substring(0, h.indexOf(".")); //pegando a parte inteira
         r = h.substring(h.indexOf(".")+1, h.indexOf("E")); //pegando a parte decimal
         h = (q+r).trim();//transformando em numero "junto"
         z = h.toCharArray();
         char [] ch = new char[tam + z.length + 1]; // tamanho de q foi elevado + numero + um do ponto
         while(i < ch.length){ ch[i++] = '0'; } //preenchendo com 0
         ch[1] = '.';
         System.arraycopy( z, 0,ch, ch.length-z.length, z.length);
         h = String.valueOf(ch);
       System.out.println("\nh: "+h);
     }
     else {
         i=0;
         int b = h.indexOf("E");
         sub = h.substring(b+1, h.length()); //pegando o valor a que foi elevado positiva//
         System.out.println("\nsub: "+sub);
         int tam = Integer.parseInt(sub);
         q = h.substring(0, h.indexOf(".")); //pegando a parte inteira
         r = h.substring(h.indexOf(".")+1, h.indexOf("E")); //pegando a parte decimal
         h = (q+r).trim();//transformando em numero "junto"
         z = h.toCharArray();
         System.out.println("\nh: "+h);
         if(r.length()>tam){ //parte decimal maior que o estipulado,ainda terá parte decimal

           char [] ch = new char[h.length()+1];// tamanho do numero + "."

           System.arraycopy( z, 0,ch, 0, tam+1); //copia toda a parte inteira
           System.arraycopy( z, tam+1,ch, tam+2, z.length-(tam+1)); //parte decimal

           ch[tam+1] = '.'; //colocando ponto
           h = String.valueOf(ch);
         System.out.println("\nh: "+h);
         }
         else{
           char [] ch = new char[tam + 1]; //tamanho da elevação mais um da parte inteira
           while(i < ch.length){ ch[i++] = '0'; } //preenchendo com 0
           System.arraycopy( z, 0,ch, 0, z.length);
           h = String.valueOf(ch);
         System.out.println("\nh: "+h);
        }
     }
    }
    if(l){ h = (String.valueOf(z1[0]).trim()+h.trim());System.out.println("\nh: "+h);

    }
    return h;
 }

    public final static String[] split( String text, char separador ) {

      if ( text == null ) {
         return null;
      }

      int tamText = text.length();
      if ( tamText == 0 ) {// se for vazio
         return null;
      }
      Vector vetor = new Vector();
      int i = 0;
      int start = 0; // posição onde posso começar a pegar os caracteres
      boolean perm  = false;
      while ( i < tamText ) { // percorre caracter a caracter

        if ( text.charAt( i ) == separador ) {
            if ( perm ) {
               // pegando o pedaço da string entre os separadores
               vetor.addElement( text.substring( start, i ).trim() );//trim()-retira espaços iniciais e finais
               perm = false;
            }
            start = ++i;
            continue;
         }
         perm = true;
         i++;
      }
      if ( perm ) {
        vetor.addElement( text.substring( start, i ).trim() ); // ultima posição da lista
      }

      String[]  listElements  = new String[vetor.size()];
      vetor.copyInto( listElements ); //copiando lista(vector) para listaElementos

    return listElements;
    }


    public static void CHECKFLAGSNEW(RTCM3ParserData parser, int flags, BufferAux tbuffer, String a, String b, String c) {

        if ((flags & GNSSDF_xDATA(b)) != 0) {
            if (a.equals("GPS")) {
                parser.dataflagGPS[parser.numdatatypesGPS] = GNSSDF_xDATA(b);
                parser.dataposGPS[parser.numdatatypesGPS] = GNSSENTRY_xDATA(b);
                ++parser.numdatatypesGPS;
            } else if (a.equals("GLO")){
                parser.dataflagGLO[parser.numdatatypesGLO] = GNSSDF_xDATA(b);
                parser.dataposGLO[parser.numdatatypesGLO] = GNSSENTRY_xDATA(b);
                ++parser.numdatatypesGLO;
            }
            System.out.println("\n\n\nadicionando:  "+c);
            tbuffer.str.append(" " + c);

            tbuffer.tbufferpos += 4;
        }
    }


    public static void CHECKFLAGS(RTCM3ParserData parser, int flags, int data[], BufferAux tbuffer, String a, String b) {

        if ((flags & GNSSDF_xDATA(a)) != 0) {
            int vb = RINEXENTRY_xDATA(b);
            
            if (data[vb] != 0) {
                parser.dataflagGPS[data[vb] - 1] = GNSSDF_xDATA(a);
                parser.dataposGPS[data[vb] - 1] = GNSSENTRY_xDATA(a);
            } else {
                
                parser.dataflag[parser.numdatatypesGPS] = GNSSDF_xDATA(a);
              
                parser.datapos[parser.numdatatypesGPS] = GNSSENTRY_xDATA(a);
                 
                data[vb] = ++parser.numdatatypesGPS;
                
                tbuffer.str.append( "    " + b);

                tbuffer.tbufferpos += 6;
            }
            
        }

    }

  public static String replace(String string, char c,char c1){
  char[] str = string.toCharArray();
  for(int j = 0;j<str.length;j++){
   if (str[j] == c) str[j] = c1;
  }
  String nova = String.valueOf(str);
 
  return nova;
}

    private static int RINEXENTRY_xDATA(String str) {

        if(str.equals("C1")) {
            return Constantes.RINEXENTRY_C1DATA;
        } else if(str.equals("C2")){
            return Constantes.RINEXENTRY_C2DATA;
        } else if(str.equals("P1")){
            return Constantes.RINEXENTRY_P1DATA;
        } else if(str.equals("P2")){
            return Constantes.RINEXENTRY_P2DATA;
        } else if(str.equals("L1")){
            return Constantes.RINEXENTRY_L1DATA;
        } else if(str.equals("L2")){
            return Constantes.RINEXENTRY_L2DATA;
        } else if(str.equals("D1")){
            return Constantes.RINEXENTRY_D1DATA;
        } else if(str.equals("D2")){
            return Constantes.RINEXENTRY_D2DATA;
        } else if(str.equals("S1")){
            return Constantes.RINEXENTRY_S1DATA;
        } else {
            return Constantes.RINEXENTRY_S2DATA;
        }
    }

    private static int GNSSENTRY_xDATA(String str) {

        if (str.equals("C1")) {
            return Constantes.GNSSENTRY_C1DATA;
        } else if (str.equals("C2")) {
            return Constantes.GNSSENTRY_C2DATA;
        } else if (str.equals("P1")) {
            return Constantes.GNSSENTRY_P1DATA;
        } else if (str.equals("P2")) {
            return Constantes.GNSSENTRY_P2DATA;
        } else if (str.equals("L1C")) {
            return Constantes.GNSSENTRY_L1CDATA;
        } else if (str.equals("L1P")) {
            return Constantes.GNSSENTRY_L1PDATA;
        } else if (str.equals("L2C")) {
            return Constantes.GNSSENTRY_L2CDATA;
        } else if (str.equals("L2P")) {
            return Constantes.GNSSENTRY_L2PDATA;
        } else if (str.equals("D1C")) {
            return Constantes.GNSSENTRY_D1CDATA;
        } else if (str.equals("D1P")) {
            return Constantes.GNSSENTRY_D1PDATA;
        } else if (str.equals("D2C")) {
            return Constantes.GNSSENTRY_D2CDATA;
        } else if (str.equals("D2P")) {
            return Constantes.GNSSENTRY_D2PDATA;
        } else if (str.equals("S1C")) {
            return Constantes.GNSSENTRY_S1CDATA;
        } else if (str.equals("S1P")) {
            return Constantes.GNSSENTRY_S1PDATA;
        } else if (str.equals("S2C")) {
            return Constantes.GNSSENTRY_S2CDATA;
        } else {
            return Constantes.GNSSENTRY_S2PDATA;
        }

    }

    private static int GNSSDF_xDATA(String str) {

        if (str.equals("C1")) {
            return Constantes.GNSSDF_C1DATA;
        } else if (str.equals("C2")) {
            return Constantes.GNSSDF_C2DATA;
        } else if (str.equals("P1")) {
            return Constantes.GNSSDF_P1DATA;
        } else if (str.equals("P2")) {
            return Constantes.GNSSDF_P2DATA;
        } else if (str.equals("L1C")) {
            return Constantes.GNSSDF_L1CDATA;
        } else if (str.equals("L1P")) {
            return Constantes.GNSSDF_L1PDATA;
        } else if (str.equals("L2C")) {
            return Constantes.GNSSDF_L2CDATA;
        } else if (str.equals("L2P")) {
            return Constantes.GNSSDF_L2PDATA;
        } else if (str.equals("D1C")) {
            return Constantes.GNSSDF_D1CDATA;
        } else if (str.equals("D1P")) {
            return Constantes.GNSSDF_D1PDATA;
        } else if (str.equals("D2C")) {
            return Constantes.GNSSDF_D2CDATA;
        } else if (str.equals("D2P")) {
            return Constantes.GNSSDF_D2PDATA;
        } else if (str.equals("S1C")) {
            return Constantes.GNSSDF_S1CDATA;
        } else if (str.equals("S1P")) {
            return Constantes.GNSSDF_S1PDATA;
        } else if (str.equals("S2C")) {
            return Constantes.GNSSDF_S2CDATA;
        } else {
            return Constantes.GNSSDF_S2PDATA;
        }

    }
}
