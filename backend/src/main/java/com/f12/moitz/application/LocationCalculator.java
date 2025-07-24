package com.f12.moitz.application;

import com.f12.moitz.domain.Point;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Component;

@Component
public class LocationCalculator {

    public Point calculateCenterPoint(List<Point> places) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (Point place : places) {
            coordinates.add(new Coordinate(place.getX(), place.getY()));
        }

        Coordinate[] pointArray = coordinates.toArray(new Coordinate[0]);
        Coordinate midpoint = calculateMidpoint(pointArray);

        return new Point(midpoint.x, midpoint.y);
    }

    private Coordinate calculateMidpoint(Coordinate[] geoPoints) {
        double x = 0, y = 0, z = 0;
        int count = geoPoints.length;

        for (Coordinate geo : geoPoints) {
            Coordinate cart = toCartesian(geo.y, geo.x);
            x += cart.x;
            y += cart.y;
            z += cart.z;
        }

        x /= count;
        y /= count;
        z /= count;

        Coordinate avgCartesian = new Coordinate(x, y, z);
        return toGeoCoordinate(avgCartesian);
    }

    private Coordinate toCartesian(double latDeg, double lonDeg) {
        double latRad = Math.toRadians(latDeg);
        double lonRad = Math.toRadians(lonDeg);

        double x = Math.cos(latRad) * Math.cos(lonRad);
        double y = Math.cos(latRad) * Math.sin(lonRad);
        double z = Math.sin(latRad);

        return new Coordinate(x, y, z);
    }

    private Coordinate toGeoCoordinate(Coordinate coord) {
        double hyp = Math.sqrt(coord.x * coord.x + coord.y * coord.y);
        double latRad = Math.atan2(coord.z, hyp);
        double lonRad = Math.atan2(coord.y, coord.x);

        double latDeg = Math.toDegrees(latRad);
        double lonDeg = Math.toDegrees(lonRad);

        return new Coordinate(lonDeg, latDeg);
    }

}
