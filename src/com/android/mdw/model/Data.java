package com.android.mdw.model;

public class Data {
	private double contador;
    private double accelerometerX;
    private double accelerometerY;
    private double accelerometerZ;
    private double giroX;
    private double giroY;
    private double giroZ;

    public Data() {
    }

    
    public Data(double contador, double accelerometerX, double accelerometerY, double accelerometerZ, double giroX, double giroY, double giroZ) {
        this.contador = contador;
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
        this.giroX = giroX;
        this.giroY = giroY;
        this.giroZ = giroZ;
    }

    /**
     * @return the contador
     */
    public double getContador() {
        return contador;
    }

    /**
     * @param contador the contador to set
     */
    public void setContador(double contador) {
        this.contador = contador;
    }

    /**
     * @return the accelerometerX
     */
    public double getAccelerometerX() {
        return accelerometerX;
    }

    /**
     * @param accelerometerX the accelerometerX to set
     */
    public void setAccelerometerX(double accelerometerX) {
        this.accelerometerX = accelerometerX;
    }

    /**
     * @return the accelerometerY
     */
    public double getAccelerometerY() {
        return accelerometerY;
    }

    /**
     * @param accelerometerY the accelerometerY to set
     */
    public void setAccelerometerY(double accelerometerY) {
        this.accelerometerY = accelerometerY;
    }

    /**
     * @return the accelerometerZ
     */
    public double getAccelerometerZ() {
        return accelerometerZ;
    }

    /**
     * @param accelerometerZ the accelerometerZ to set
     */
    public void setAccelerometerZ(double accelerometerZ) {
        this.accelerometerZ = accelerometerZ;
    }

    /**
     * @return the giroX
     */
    public double getGiroX() {
        return giroX;
    }

    /**
     * @param giroX the giroX to set
     */
    public void setGiroX(double giroX) {
        this.giroX = giroX;
    }

    /**
     * @return the giroY
     */
    public double getGiroY() {
        return giroY;
    }

    /**
     * @param giroY the giroY to set
     */
    public void setGiroY(double giroY) {
        this.giroY = giroY;
    }

    /**
     * @return the giroZ
     */
    public double getGiroZ() {
        return giroZ;
    }

    /**
     * @param giroZ the giroZ to set
     */
    public void setGiroZ(double giroZ) {
        this.giroZ = giroZ;
    }
}
