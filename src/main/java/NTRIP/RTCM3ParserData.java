

import java.util.Date;
import javax.microedition.io.file.FileConnection;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fernanda
 */
public class RTCM3ParserData {

   
  public char[] Message = new char[2048]; /* input-buffer */
  public int    MessageSize;              /* current buffer size */
  public int    NeedBytes;                /* bytes wanted for next run */
  public int    SkipBytes;                /* bytes to skip in next round */
  public long    GPSWeek;
  public long    GPSTOW;                  /* in seconds */
  public gnssdata Data = new gnssdata();
  public gpsephemeris ephemerisGPS = new gpsephemeris();
  public glonassephemeris ephemerisGLONASS = new glonassephemeris();
  public gnssdata DataNew = new gnssdata();
  public int    size;
  public int[] lastlockl1 = new int[64];
  public int[] lastlockl2= new int[64];
  public static boolean NO_RTCM3_MAIN = false;

  public int[] datapos = new int[Constantes.RINEXENTRY_NUMBER];
  public int[] dataflag = new int[Constantes.RINEXENTRY_NUMBER];
  /* for RINEX2 GPS and GLO are both handled in GPS */
  public int[] dataposGPS = new int[Constantes.RINEXENTRY_NUMBER] ; /* SBAS has same entries */
  public int[] dataflagGPS = new int[Constantes.RINEXENTRY_NUMBER];
  public int[] dataposGLO = new int[Constantes.RINEXENTRY_NUMBER]; /* only used for RINEX3 */
  public int[] dataflagGLO = new int[Constantes.RINEXENTRY_NUMBER];
  public int   numdatatypesGPS;
  public int   numdatatypesGLO; /* only used for RINEX3 */
  public int   validwarning;
  public int   init;
  public int   startflags;
  public int   rinex3; //ele é executado pelo usuário
  public String  headerfile;
  public String  glonassephemeris="";
  public String  gpsephemeris="";
  public String  observdata="";
  public double antX;
  public double antY;
  public double antZ;
  public double antH;
  public char[] antenna= new char[256+1];
  public int  blocktype;
  public FileConnection glonassfile=null;
  public FileConnection gpsfile=null;
  public FileConnection observ;


  public RTCM3ParserData() {
            Date date = new Date();
            long tempo = date.getTime()/1000;  // time_t tim;
            System.out.println("tempo: " +tempo);
            // tim = time(0) - ((10*365+2+5)*24*60*60+LEAPSECONDS);
            tempo= (date.getTime() / 1000)  - ((10*365+2+5)*24*60*60+ Constantes.LEAPSECONDS);
            this.GPSWeek = (tempo/(7*24*60*60));
            this.GPSTOW = (tempo%(7*24*60*60));
             System.out.println("handle.GPSWeek: " +GPSWeek);
            System.out.println("handle.GPSTOW: " +GPSTOW);
}

    public double getAntH() {
        return antH;
    }

    public void setAntH(double antH) {
        this.antH = antH;
    }

    public double getAntX() {
        return antX;
    }

    public void setAntX(double antX) {
        this.antX = antX;
    }

    public double getAntY() {
        return antY;
    }

    public void setAntY(double antY) {
        this.antY = antY;
    }

    public double getAntZ() {
        return antZ;
    }

    public void setAntZ(double antZ) {
        this.antZ = antZ;
    }

    public char[] getAntenna() {
        return antenna;
    }

    public void setAntenna(char[] antenna) {
        this.antenna = antenna;
    }

    public int getBlocktype() {
        return blocktype;
    }

    public void setBlocktype(int blocktype) {
        this.blocktype = blocktype;
    }

    public gnssdata getData() {
        return Data;
    }

    public void setData(gnssdata Data) {
        this.Data = Data;
    }

    public gnssdata getDataNew() {
        return DataNew;
    }

    public void setDataNew(gnssdata DataNew) {
        this.DataNew = DataNew;
    }

    public long getGPSTOW() {
        return GPSTOW;
    }

    public void setGPSTOW(long GPSTOW) {
        this.GPSTOW = GPSTOW;
    }

    public long getGPSWeek() {
        return GPSWeek;
    }

    public void setGPSWeek(long GPSWeek) {
        this.GPSWeek = GPSWeek;
    }

    public char[] getMessage() {
        return Message;
    }

    public void setMessage(char[] Message) {
        this.Message = Message;
    }

    public int getMessageSize() {
        return MessageSize;
    }

    public void setMessageSize(int MessageSize) {
        this.MessageSize = MessageSize;
    }

    public static boolean isNO_RTCM3_MAIN() {
        return NO_RTCM3_MAIN;
    }

    public static void setNO_RTCM3_MAIN(boolean NO_RTCM3_MAIN) {
        RTCM3ParserData.NO_RTCM3_MAIN = NO_RTCM3_MAIN;
    }

    public int getNeedBytes() {
        return NeedBytes;
    }

    public void setNeedBytes(int NeedBytes) {
        this.NeedBytes = NeedBytes;
    }

    public int getSkipBytes() {
        return SkipBytes;
    }

    public void setSkipBytes(int SkipBytes) {
        this.SkipBytes = SkipBytes;
    }

    public int[] getDataflag() {
        return dataflag;
    }

    public void setDataflag(int[] dataflag) {
        this.dataflag = dataflag;
    }

    public int[] getDataflagGLO() {
        return dataflagGLO;
    }

    public void setDataflagGLO(int[] dataflagGLO) {
        this.dataflagGLO = dataflagGLO;
    }

    public int[] getDataflagGPS() {
        return dataflagGPS;
    }

    public void setDataflagGPS(int[] dataflagGPS) {
        this.dataflagGPS = dataflagGPS;
    }

    public int[] getDatapos() {
        return datapos;
    }

    public void setDatapos(int[] datapos) {
        this.datapos = datapos;
    }

    public int[] getDataposGLO() {
        return dataposGLO;
    }

    public void setDataposGLO(int[] dataposGLO) {
        this.dataposGLO = dataposGLO;
    }

    public int[] getDataposGPS() {
        return dataposGPS;
    }

    public void setDataposGPS(int[] dataposGPS) {
        this.dataposGPS = dataposGPS;
    }

    public glonassephemeris getEphemerisGLONASS() {
        return ephemerisGLONASS;
    }

    public void setEphemerisGLONASS(glonassephemeris ephemerisGLONASS) {
        this.ephemerisGLONASS = ephemerisGLONASS;
    }

    public gpsephemeris getEphemerisGPS() {
        return ephemerisGPS;
    }

    public void setEphemerisGPS(gpsephemeris ephemerisGPS) {
        this.ephemerisGPS = ephemerisGPS;
    }

    public String getGlonassephemeris() {
        return glonassephemeris;
    }

    public void setGlonassephemeris(String glonassephemeris) {
        this.glonassephemeris = glonassephemeris;
    }

    public String getGpsephemeris() {
        return gpsephemeris;
    }

    public void setGpsephemeris(String gpsephemeris) {
        this.gpsephemeris = gpsephemeris;
    }

    public String getHeaderfile() {
        return headerfile;
    }

    public void setHeaderfile(String headerfile) {
        this.headerfile = headerfile;
    }

    public int getInit() {
        return init;
    }

    public void setInit(int init) {
        this.init = init;
    }

    public int getLastlockl1(int v) {
        return lastlockl1[v];
    }

    public void setLastlockl1(int[] lastlockl1) {
        this.lastlockl1 = lastlockl1;
    }

    public int[] getLastlockl2() {
        return lastlockl2;
    }

    public void setLastlockl2(int[] lastlockl2) {
        this.lastlockl2 = lastlockl2;
    }

    public int getNumdatatypesGLO() {
        return numdatatypesGLO;
    }

    public void setNumdatatypesGLO(int numdatatypesGLO) {
        this.numdatatypesGLO = numdatatypesGLO;
    }

    public int getNumdatatypesGPS() {
        return numdatatypesGPS;
    }

    public void setNumdatatypesGPS(int numdatatypesGPS) {
        this.numdatatypesGPS = numdatatypesGPS;
    }

    public int getRinex3() {
        return rinex3;
    }

    public void setRinex3(int rinex3) {
        this.rinex3 = rinex3;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartflags() {
        return startflags;
    }

    public void setStartflags(int startflags) {
        this.startflags = startflags;
    }

    public int getValidwarning() {
        return validwarning;
    }

    public void setValidwarning(int validwarning) {
        this.validwarning = validwarning;
    }



}

