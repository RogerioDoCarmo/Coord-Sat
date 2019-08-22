package com.mycompany.coord_sat;

public class CoordenadaGNSS implements Comparable<CoordenadaGNSS> {
    private String PRN;
    private int PRN_INT;
    private double X;
    private double Y;
    private double Z;
    private double dts; // Erro do relógio em segundos em relação ao sistema de tempo da constelacao.
    
    @Override
    public String toString(){
        return  "PRN: "    + PRN + "\n" +
                "X [km]: " + X   + "\n" +
                "Y [km]: " + Y   + "\n" +
                "Z [km]: " + Z   + "\n" +
                "Dts[s]: " + dts + "\n";
    }

    /**
     * Creates an instance of a GPS coordinate to represent a satellite ou receiver position.
     * @param PRN The satellite CONSTELATION_LETTER (G,R,E or C) + ID (PRN).
     * @param X The X coordinate of the object in the WGS 84
     * @param Y The Y coordinate of the object in the WGS 84
     * @param Z The Z coordinate of the object in the WGS 84
     * @param dts The error of the clock to the GPS time system <b>in seconds</b>.
     */
    public CoordenadaGNSS (String PRN, double X, double Y, double Z, double dts ){
        this.PRN = PRN;
        this.PRN_INT = Integer.valueOf(PRN.substring(1,2));
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.dts = dts;
    }

//    public CoordenadaGNSS(Parcel in) {
//        PRN = in.readInt();
//        X = in.readDouble();
//        Y = in.readDouble();
//        Z = in.readDouble();
//        dts = in.readDouble();
//    }

     /**
     * @return the PRN_INT
     */
    public int getPRN_INT() {
        return PRN_INT;
    }
    
    public String getPRN() {
        return PRN;
    }

    public void setPRN(String PRN) {
        this.PRN = PRN;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    /**
     *
     * @return The error of the satellite to the GPS time system <b>in seconds</b>.
     */
    public double getDts() {
        return dts;
    }

    /**
     * @param dts The error of the satellite to the GPS time system <b>in seconds</b>.
     */
    public void setDts(double dts) {
        this.dts = dts;
    }

    @Override
    public int compareTo(CoordenadaGNSS another) {
        return (this.getPRN_INT() - another.getPRN_INT());
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(PRN);
//        dest.writeDouble(X);
//        dest.writeDouble(Y);
//        dest.writeDouble(Z);
//        dest.writeDouble(dts);
//    }
//
//    public static final Parcelable.Creator<CoordenadaGPS> CREATOR = new Parcelable.Creator<CoordenadaGPS>() {
//        public CoordenadaGNSS createFromParcel(Parcel in) {
//            return new CoordenadaGNSS(in);
//        }
//
//        public CoordenadaGNSS[] newArray(int size) {
//            return new CoordenadaGNSS[size];
//        }
//    };

}
