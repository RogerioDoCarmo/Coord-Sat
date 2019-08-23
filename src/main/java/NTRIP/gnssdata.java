/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fernanda
 */
public class gnssdata {

  private static final int GNSSENTRY_NUMBER  =   16 ;/* number of types!!! */
  int     flags;              /* GPSF_xxx */
  long    week;               /* week number of GPS date */
  int     numsats;
  double  timeofweek;         /* milliseconds in GPS week */
  double[][] measdata= new double[24][GNSSENTRY_NUMBER];  /* data fields */
  int[]    dataflags= new int[24];      /* GPSDF_xxx */
  int[]    satellites = new int[24];     /* SV - IDs */
  int[]    snrL1 =new int[24];          /* Important: all the 5 SV-specific fields must */
  int[]    snrL2 =new int[24];          /* have the same SV-order */


  
    public int getDataflags(int num) {
        return dataflags[num];
    }

    public void setDataflags(int pos, int v) {
        this.dataflags[pos] = v;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public double[][] getMeasdata() {
        return measdata;
    }

    public void setMeasdata(int i,int j,double v) {
        this.measdata[i][j] = v;
    }

    public int getNumsats() {
        return numsats;
    }

    public void setNumsats(int numsats) {
        this.numsats = numsats;
    }

    public int getSatellites(int num) {
        return satellites[num];
    }

    public void setSatellites(int[] satellites) {
        this.satellites = satellites;
    }

     public void setSatellites(int valor, int pos) {
        this.satellites[pos] = valor;
    }

    public int[] getSnrL1() {
        return snrL1;
    }

    public void setSnrL1(int[] snrL1new) {
        this.snrL1 = snrL1new;
    }

    public int[] getSnrL2new() {
        return snrL2;
    }

    public void setSnrL2(int[] snrL2new) {
        this.snrL2 = snrL2new;
    }

    public double getTimeofweek() {
        return timeofweek;
    }

    public void setTimeofweek(double timeofweek) {
        this.timeofweek = timeofweek;
    }

    public long getWeek() {
        return week;
    }

    public void setWeek(long week) {
        this.week = week;
    }

  

}
