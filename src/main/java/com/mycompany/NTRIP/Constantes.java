/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.NTRIP;
/**
 *
 * @author Fernanda
 */
public class Constantes {
     public static final int MAXHEADERBUFFERSIZE = 4096;
     public static final int RINEXENTRY_NUMBER =    10;
     public static final int MAXHEADERLINES = 50;
     public static final double R2R_PI = 3.1415926535898; // #define R2R_PI      3.1415926535898

     public static final int GLOEPHF_UNHEALTHY = (1<<0);// #define GLOEPHF_UNHEALTHY       (1<<0) /* set if unhealty satellite, f2b78 */
     public static final int GLOEPHF_PAVAILABLE = (1<<3); // #define GLOEPHF_PAVAILABLE  (1<<3)
     public static final int GLOEPHF_ALMANACHEALTHY = (1<<2);// #define GLOEPHF_ALMANACHEALTHY  (1<<2)
     public static final int GLOEPHF_ALMANACHEALTHOK = (1<<1);// #define GLOEPHF_ALMANACHEALTHOK (1<<1)
     public static final int GLOEPHF_P10TRUE =(1<<4);// #define GLOEPHF_P10TRUE         (1<<4)
     public static final int GLOEPHF_P11TRUE = (1<<5);// #define GLOEPHF_P11TRUE         (1<<5)
     public static final int GLOEPHF_P2TRUE = (1<<6);// #define GLOEPHF_P2TRUE          (1<<6)
     public static final int GLOEPHF_P3TRUE = (1<<7);// #define GLOEPHF_P3TRUE          (1<<7)
     public static final int LEAPSECONDS  =   14;// #define LEAPSECONDS     14 /* only needed for approx. time */
     public static int GPSLEAPSTART = 19;/* 19 leap seconds existed at 6.1.1980 */
     public static final int GPSEPHF_L2PCODEDATA = (1<<0);// #define GPSEPHF_L2PCODEDATA    (1<<0) /* set, if NAV data OFF on L2 P-code, s1w4b01 */
     public static final int GPSEPHF_L2PCODE  = (1<<1);// #define GPSEPHF_L2PCODE        (1<<1) /* set, if P-code available, s1w3b12 */
     public static final int GPSEPHF_L2CACODE = (1<<2);// #define GPSEPHF_L2CACODE       (1<<2) /* set, if CA-code available, s1w3b11 */
     public static final int GPSEPHF_VALIDATED = (1<<3);// #define GPSEPHF_VALIDATED      (1<<3) /* data is completely valid */

    public static final int PRN_GPS_START =  1;
    public static final int PRN_GPS_END =  32;
    public static final int PRN_GLONASS_START =  38;
    public static final int PRN_GLONASS_END =  61;
    public static final int PRN_WAAS_START =  120;
    public static final int PRN_WAAS_END =  138;



  public static final int GNSSENTRY_C1DATA  =   0;// #define GNSSENTRY_C1DATA     0
  public static final int GNSSENTRY_C2DATA  =   1;// #define GNSSENTRY_C2DATA     1
  public static final int GNSSENTRY_P1DATA  =   2;// #define GNSSENTRY_P1DATA     2
  public static final int GNSSENTRY_P2DATA  =   3;// #define GNSSENTRY_P2DATA     3
  public static final int GNSSENTRY_L1CDATA  =   4;// #define GNSSENTRY_L1CDATA    4
  public static final int GNSSENTRY_L1PDATA  =   5;// #define GNSSENTRY_L1PDATA    5
  public static final int GNSSENTRY_L2CDATA  =   6;// #define GNSSENTRY_L2CDATA    6
  public static final int GNSSENTRY_L2PDATA  =   7;// #define GNSSENTRY_L2PDATA    7
  public static final int GNSSENTRY_D1CDATA  =   8;// #define GNSSENTRY_D1CDATA    8
  public static final int GNSSENTRY_D1PDATA  =   9;// #define GNSSENTRY_D1PDATA    9
  public static final int GNSSENTRY_D2CDATA  =   10;// #define GNSSENTRY_D2CDATA    10
  public static final int GNSSENTRY_D2PDATA  =   11;// #define GNSSENTRY_D2PDATA    11
  public static final int GNSSENTRY_S1CDATA  =   12;// #define GNSSENTRY_S1CDATA    12
  public static final int GNSSENTRY_S1PDATA  =   13;// /#define GNSSENTRY_S1PDATA    13
  public static final int GNSSENTRY_S2CDATA  =   14;// #define GNSSENTRY_S2CDATA    14
  public static final int GNSSENTRY_S2PDATA  =   15;// #define GNSSENTRY_S2PDATA    15
  public static final int GNSSENTRY_NUMBER  =   16;// #define GNSSENTRY_NUMBER     16 /* number of types!!! */

  //    public static final int NUMSTARTSKIP = 1; // nao principal
    public static final int NUMSTARTSKIP = 3; // principal


 public static final int GNSSDF_C1DATA    =     (1<<GNSSENTRY_C1DATA);
 public static final int GNSSDF_C2DATA     =    (1<<GNSSENTRY_C2DATA);
 public static final int GNSSDF_P1DATA     =    (1<<GNSSENTRY_P1DATA);
 public static final int GNSSDF_P2DATA     =    (1<<GNSSENTRY_P2DATA);
 public static final int GNSSDF_L1CDATA    =    (1<<GNSSENTRY_L1CDATA);
 public static final int GNSSDF_L1PDATA    =    (1<<GNSSENTRY_L1PDATA);
 public static final int GNSSDF_L2CDATA     =   (1<<GNSSENTRY_L2CDATA);
 public static final int GNSSDF_L2PDATA    =    (1<<GNSSENTRY_L2PDATA);
 public static final int GNSSDF_D1CDATA    =    (1<<GNSSENTRY_D1CDATA);
 public static final int GNSSDF_D1PDATA    =    (1<<GNSSENTRY_D1PDATA);
 public static final int GNSSDF_D2CDATA    =    (1<<GNSSENTRY_D2CDATA);
 public static final int GNSSDF_D2PDATA    =    (1<<GNSSENTRY_D2PDATA);
 public static final int GNSSDF_S1CDATA    =    (1<<GNSSENTRY_S1CDATA);
 public static final int GNSSDF_S1PDATA    =    (1<<GNSSENTRY_S1PDATA);
 public static final int GNSSDF_S2CDATA    =    (1<<GNSSENTRY_S2CDATA);
 public static final int GNSSDF_S2PDATA    =    (1<<GNSSENTRY_S2PDATA) ;


 public static final int RINEXENTRY_C1DATA =    0;
    public static final int RINEXENTRY_C2DATA =    1;
    public static final int RINEXENTRY_P1DATA =    2;
    public static final int RINEXENTRY_P2DATA =    3;
    public static final int RINEXENTRY_L1DATA =    4;
    public static final int RINEXENTRY_L2DATA =    5;
    public static final int RINEXENTRY_D1DATA =    6;
    public static final int RINEXENTRY_D2DATA =    7;
    public static final int RINEXENTRY_S1DATA =    8;
    public static final int RINEXENTRY_S2DATA =    9;

    public static final String C1 = "C1";
    public static final String C2 = "C2";
    public static final String P1 = "P1";
    public static final String P2 = "P2";
    public static final String L1C = "L1C";
    public static final String L1P = "L1P";
    public static final String L2C = "L2C";
    public static final String L2P = "L2P";
    public static final String D1C = "D1C";
    public static final String D1P = "D1P";
    public static final String D2C = "D2C";
    public static final String D2P = "D2P";
    public static final String S1C = "S1C";
    public static final String S1P = "S1P";
    public static final String S2C = "S2C";
    public static final String S2P = "S2P";
    public static final String GPS = "GPS";
    public static final String GLO = "GLO";


    public static final String L1 = "L1";
    public static final String L2 = "L2";
    public static final String D1 = "D1";
    public static final String D2 = "D2";
    public static final String S1 = "S1";
    public static final String S2 = "S2";

    public static final String C1C = "C1C";
    public static final String C1P = "C1P";
    public static final String C2P = "C2P";
    public static final String C2X = "C2X";
    public static final String L2X = "L2X";
    public static final String D2X = "D2X";
    public static final String S2X = "S2X";
    public static final String C2C = "C2C";



 /* Additional flags for the data field, which tell us more. */
public static final int GNSSDF_LOCKLOSSL1   =  (1<<29);  /* lost lock on L1 */
public static final int GNSSDF_LOCKLOSSL2  =   (1<<30);  /* lost lock on L2 */

public static final double LIGHTSPEED    =     2.99792458e8; // #define LIGHTSPEED         2.99792458e8    /* m/sec */
public static final double GPS_FREQU_L1  =    1575420000.0; // #define GPS_FREQU_L1       1575420000.0  /* Hz */
public static final double GPS_FREQU_L2   =    1227600000.0; // #define GPS_FREQU_L2       1227600000.0  /* Hz */
public static final double GPS_WAVELENGTH_L1 = (LIGHTSPEED / GPS_FREQU_L1); // #define GPS_WAVELENGTH_L1  (LIGHTSPEED / GPS_FREQU_L1) /* m */
public static final double GPS_WAVELENGTH_L2 = (LIGHTSPEED / GPS_FREQU_L2); // #define GPS_WAVELENGTH_L2  (LIGHTSPEED / GPS_FREQU_L2) /* m */

public static final double GLO_FREQU_L1_BASE = 1602000000.0;  /* Hz */
public static final double GLO_FREQU_L2_BASE = 1246000000.0;  /* Hz */
public static final double GLO_FREQU_L1_STEP =     562500.0;  /* Hz */
public static final double GLO_FREQU_L2_STEP =     437500.0;  /* Hz */

 public static double GLO_FREQU_L1(double a) {
        return (GLO_FREQU_L1_BASE + (a) * GLO_FREQU_L1_STEP);
    }

    public static double GLO_FREQU_L2(double a) {
        return (GLO_FREQU_L2_BASE + (a) * GLO_FREQU_L2_STEP);
    }

    public static double GLO_WAVELENGTH_L1(double a) {
        return (LIGHTSPEED / GLO_FREQU_L1(a));  /* m */
    }

    public static double GLO_WAVELENGTH_L2(double a) {
        return (LIGHTSPEED / GLO_FREQU_L2(a)); /* m */
    }
}
