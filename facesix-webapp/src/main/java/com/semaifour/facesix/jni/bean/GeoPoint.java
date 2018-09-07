package com.semaifour.facesix.jni.bean;

import java.io.Serializable;

/**
 * Bean utility store image pixel coordinates, latitude and longitude.
 */
public class GeoPoint implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8585735281682659427L;

    private double x;
    private double y;
    private double latitude;
    private double longitude;
    private String status;

    public GeoPoint() {
        super();
    }

    public GeoPoint(double x, double y, double latitude, double longitude, String status) {
        super();
        this.x = x;
        this.y = y;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setStatus(String status) {
        this.status = status;
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
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
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
        GeoPoint other = (GeoPoint) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude)) {
            return false;
        }
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GeoPoint [x=" + x + ", y=" + y + ", latitude=" + latitude + ", longitude=" + longitude + ", status="
                + status + "]";
    }

}
