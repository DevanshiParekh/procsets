package com.semaifour.facesix.jni.bean;

import java.io.Serializable;

/**
 * Bean utility to store coordinates - latitude and longitude
 */
public class Coordinate implements Serializable {

    /**
     * Version
     */
    private static final long serialVersionUID = -8585735281682659427L;

    private double latitude;
    private double longitude;
    private String macaddr;

    public Coordinate() {
        super();
    }

    public Coordinate(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinate(double latitude, double longitude, String macaddr) {
        super();
        this.latitude 	= latitude;
        this.longitude  = longitude;
        this.macaddr    = macaddr;
    }
    
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMacaddr() {
		return macaddr;
	}

	public void setMacaddr(String macaddr) {
		this.macaddr = macaddr;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Coordinate other = (Coordinate) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude)) {
            return false;
        }
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude)) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "Coordinate [latitude=" + latitude + ", longitude=" + longitude + ",macaddr" + macaddr + "]";
	}
}
