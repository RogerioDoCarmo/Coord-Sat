/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fernanda
 */
class glonassephemeris {
    public  int    GPSWeek;
 public int    GPSTOW;
 public int    flags;              /* GLOEPHF_xxx */
 public int    almanac_number;
 public int    frequency_number;   /* ICD-GLONASS data position */
 public int    tb;                 /* [s]     [f2b70-76] */
 public int    tk;                 /* [s]     [f1b65-76] */
 public int    E;                  /* [days]  [f4b49-53] */
 public double tau;                /* [s]     [f4b59-80] */
 public double gamma;              /*         [f3b69-79] */
 public double x_pos;              /* [km]    [f1b09-35] */
 public double x_velocity;         /* [km/s]  [f1b41-64] */
 public double x_acceleration;     /* [km/s^2][f1b36-40] */
 public double y_pos;              /* [km]    [f2b09-35] */
 public double y_velocity;         /* [km/s]  [f2b41-64] */
 public double y_acceleration;     /* [km/s^2][f2b36-40] */
 public double z_pos;              /* [km]    [f3b09-35] */
 public double z_velocity;         /* [km/s]  [f3b41-64] */
 public double z_acceleration;     /* [km/s^2][f3b36-40] */


 public int getE() {
        return E;
    }

    public void setE(int E) {
        this.E = E;
    }

    public int getGPSTOW() {
        return GPSTOW;
    }

    public void setGPSTOW(int GPSTOW) {
        this.GPSTOW = GPSTOW;
    }

    public int getGPSWeek() {
        return GPSWeek;
    }

    public void setGPSWeek(int GPSWeek) {
        this.GPSWeek = GPSWeek;
    }

    public int getAlmanac_number() {
        return almanac_number;
    }

    public void setAlmanac_number(int almanac_number) {
        this.almanac_number = almanac_number;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getFrequency_number() {
        return frequency_number;
    }

    public void setFrequency_number(int frequency_number) {
        this.frequency_number = frequency_number;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getTau() {
        return tau;
    }

    public void setTau(double tau) {
        this.tau = tau;
    }

    public int getTb() {
        return tb;
    }

    public void setTb(int tb) {
        this.tb = tb;
    }

    public int getTk() {
        return tk;
    }

    public void setTk(int tk) {
        this.tk = tk;
    }

    public double getX_acceleration() {
        return x_acceleration;
    }

    public void setX_acceleration(double x_acceleration) {
        this.x_acceleration = x_acceleration;
    }

    public double getX_pos() {
        return x_pos;
    }

    public void setX_pos(double x_pos) {
        this.x_pos = x_pos;
    }

    public double getX_velocity() {
        return x_velocity;
    }

    public void setX_velocity(double x_velocity) {
        this.x_velocity = x_velocity;
    }

    public double getY_acceleration() {
        return y_acceleration;
    }

    public void setY_acceleration(double y_acceleration) {
        this.y_acceleration = y_acceleration;
    }

    public double getY_pos() {
        return y_pos;
    }

    public void setY_pos(double y_pos) {
        this.y_pos = y_pos;
    }

    public double getY_velocity() {
        return y_velocity;
    }

    public void setY_velocity(double y_velocity) {
        this.y_velocity = y_velocity;
    }

    public double getZ_acceleration() {
        return z_acceleration;
    }

    public void setZ_acceleration(double z_acceleration) {
        this.z_acceleration = z_acceleration;
    }

    public double getZ_pos() {
        return z_pos;
    }

    public void setZ_pos(double z_pos) {
        this.z_pos = z_pos;
    }

    public double getZ_velocity() {
        return z_velocity;
    }

    public void setZ_velocity(double z_velocity) {
        this.z_velocity = z_velocity;
    }


}
