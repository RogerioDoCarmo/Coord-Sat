package NTRIP;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fernanda
 */
class gpsephemeris {

  public int    flags;            /* GPSEPHF_xxx */
  public int    satellite;        /*  SV ID   ICD-GPS data position */
  public int    IODE;             /*          [s2w3b01-08]              */
  public int    URAindex;         /*  [1..15] [s1w3b13-16]              */
  public int    SVhealth;         /*          [s1w3b17-22]              */
  public int    GPSweek;          /*          [s1w3b01-10]              */
  public int    IODC;             /*          [s1w3b23-32,w8b01-08]     */
  public int    TOW;              /*  [s]     [s1w2b01-17]              */
  public int    TOC;              /*  [s]     [s1w8b09-24]              */
  public int    TOE;              /*  [s]     [s2w10b1-16]              */
  public double clock_bias;       /*  [s]     [s1w10b1-22, af0]         */
  public double clock_drift;      /*  [s/s]   [s1w9b09-24, af1]         */
  public double clock_driftrate;  /*  [s/s^2] [s1w9b01-08, af2]         */
  public double Crs;              /*  [m]     [s2w3b09-24]              */
  public double Delta_n;          /*  [rad/s] [s2w4b01-16 * Pi]         */
  public double M0;               /*  [rad]   [s2w4b17-24,w5b01-24 * Pi]*/
  public double Cuc;              /*  [rad]   [s2w6b01-16]              */
  public double e;                /*          [s2w6b17-24,w6b01-24]     */
  public double Cus;              /*  [rad]   [s2w8b01-16]              */
  public double sqrt_A;           /*  [m^0.5] [s2w8b16-24,w9b01-24]     */
  public double Cic;              /*  [rad]   [s3w3b01-16]              */
  public double OMEGA0;           /*  [rad]   [s3w3b17-24,w4b01-24 * Pi]*/
  public double Cis;              /*  [rad]   [s3w5b01-16]              */
  public double i0;               /*  [rad]   [s3w5b17-24,w6b01-24 * Pi]*/
  public double Crc;              /*  [m]     [s3w701-16]               */
  public double omega;            /*  [rad]   [s3w7b17-24,w8b01-24 * Pi]*/
  public double OMEGADOT;         /*  [rad/s] [s3w9b01-24 * Pi]         */
  public double IDOT;             /*  [rad/s] [s3w10b9-22 * Pi]         */
  public double TGD;              /*  [s]     [s1w7b17-24]              */

    public gpsephemeris() {
        this.flags = 0;
        this.satellite = 0;
        this.IODE = 0;
        this.URAindex = 0;
        this.SVhealth = 0;
        this.GPSweek = 0;
        this.IODC = 0;
        this.TOW = 0;
        this.TOC = 0;
        this.TOE = 0;
        this.clock_drift = 0;
        this.clock_driftrate = 0;
        this.Delta_n = 0;
        this.M0 = 0;
        this.e =0;
        this.Cus = 0;
        this.sqrt_A = 0;
        this.Cic = 0;
        this.OMEGA0 = 0;
        this.Cis = 0;
        this.i0 = 0;
        this.Crc = 0;
        this.omega = 0;
        this.OMEGADOT = 0;
        this.IDOT = 0;
        this.TGD = 0;
    }




  public double getCic() {
        return Cic;
    }

    public void setCic(double Cic) {
        this.Cic = Cic;
    }

    public double getCis() {
        return Cis;
    }

    public void setCis(double Cis) {
        this.Cis = Cis;
    }

    public double getCrc() {
        return Crc;
    }

    public void setCrc(double Crc) {
        this.Crc = Crc;
    }

    public double getCrs() {
        return Crs;
    }

    public void setCrs(double Crs) {
        this.Crs = Crs;
    }

    public double getCuc() {
        return Cuc;
    }

    public void setCuc(double Cuc) {
        this.Cuc = Cuc;
    }

    public double getCus() {
        return Cus;
    }

    public void setCus(double Cus) {
        this.Cus = Cus;
    }

    public double getDelta_n() {
        return Delta_n;
    }

    public void setDelta_n(double Delta_n) {
        this.Delta_n = Delta_n;
    }

    public int getGPSweek() {
        return GPSweek;
    }

    public void setGPSweek(int GPSweek) {
        this.GPSweek = GPSweek;
    }

    public double getIDOT() {
        return IDOT;
    }

    public void setIDOT(double IDOT) {
        this.IDOT = IDOT;
    }

    public int getIODC() {
        return IODC;
    }

    public void setIODC(int IODC) {
        this.IODC = IODC;
    }

    public int getIODE() {
        return IODE;
    }

    public void setIODE(int IODE) {
        this.IODE = IODE;
    }

    public double getM0() {
        return M0;
    }

    public void setM0(double M0) {
        this.M0 = M0;
    }

    public double getOMEGA0() {
        return OMEGA0;
    }

    public void setOMEGA0(double OMEGA0) {
        this.OMEGA0 = OMEGA0;
    }

    public double getOMEGADOT() {
        return OMEGADOT;
    }

    public void setOMEGADOT(double OMEGADOT) {
        this.OMEGADOT = OMEGADOT;
    }

    public int getSVhealth() {
        return SVhealth;
    }

    public void setSVhealth(int SVhealth) {
        this.SVhealth = SVhealth;
    }

    public double getTGD() {
        return TGD;
    }

    public void setTGD(double TGD) {
        this.TGD = TGD;
    }

    public int getTOC() {
        return TOC;
    }

    public void setTOC(int TOC) {
        this.TOC = TOC;
    }

    public int getTOE() {
        return TOE;
    }

    public void setTOE(int TOE) {
        this.TOE = TOE;
    }

    public int getTOW() {
        return TOW;
    }

    public void setTOW(int TOW) {
        this.TOW = TOW;
    }

    public int getURAindex() {
        return URAindex;
    }

    public void setURAindex(int URAindex) {
        this.URAindex = URAindex;
    }

    public double getClock_bias() {
        return clock_bias;
    }

    public void setClock_bias(double clock_bias) {
        this.clock_bias = clock_bias;
    }

    public double getClock_drift() {
        return clock_drift;
    }

    public void setClock_drift(double clock_drift) {
        this.clock_drift = clock_drift;
    }

    public double getClock_driftrate() {
        return clock_driftrate;
    }

    public void setClock_driftrate(double clock_driftrate) {
        this.clock_driftrate = clock_driftrate;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public double getI0() {
        return i0;
    }

    public void setI0(double i0) {
        this.i0 = i0;
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }

    public int getSatellite() {
        return satellite;
    }

    public void setSatellite(int satellite) {
        this.satellite = satellite;
    }

    public double getSqrt_A() {
        return sqrt_A;
    }

    public void setSqrt_A(double sqrt_A) {
        this.sqrt_A = sqrt_A;
    }




}
