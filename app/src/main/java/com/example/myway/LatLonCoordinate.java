package com.example.myway;

import org.jetbrains.annotations.NotNull;

public class LatLonCoordinate {
    private final double latitude;
    private final double longitude;

    public LatLonCoordinate(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SVY21Coordinate asSVY21() {
        return SVY21.computeSVY21(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LatLonCoordinate other = (LatLonCoordinate) obj;
        if (Double.doubleToLongBits(latitude) != Double
                .doubleToLongBits(other.latitude))
            return false;
        return Double.doubleToLongBits(longitude) == Double
                .doubleToLongBits(other.longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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
    public @NotNull String toString() {
        return "LatLonCoordinate [latitude=" + latitude + ", longitude="
                + longitude + "]";
    }
}
