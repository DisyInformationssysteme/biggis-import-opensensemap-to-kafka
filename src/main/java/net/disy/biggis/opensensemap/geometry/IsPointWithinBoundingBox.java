package net.disy.biggis.opensensemap.geometry;

import java.util.function.Predicate;

import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IsPointWithinBoundingBox implements Predicate<Point> {

  private final double minLat;
  private final double maxLat;
  private final double minLon;
  private final double maxLon;

  public IsPointWithinBoundingBox(
      @Value("${sensor.query.minLat}") double minLat,
      @Value("${sensor.query.maxLat}") double maxLat,
      @Value("${sensor.query.minLon}") double minLon,
      @Value("${sensor.query.maxLon}") double maxLon) {
    this.minLat = minLat;
    this.maxLat = maxLat;
    this.minLon = minLon;
    this.maxLon = maxLon;
  }

  @Override
  public boolean test(Point point) {
    LngLatAlt coordinates = point.getCoordinates();
    double latitude = coordinates.getLatitude();
    double longitude = coordinates.getLongitude();
    return minLat <= latitude && latitude <= maxLat && minLon <= longitude && longitude <= maxLon;
  }

}
