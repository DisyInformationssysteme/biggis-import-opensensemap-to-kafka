package net.disy.biggis.opensensemap.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.geojson.Feature;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class GeoJsonUtilities {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  private GeoJsonUtilities() {
  }

  public static Stream<Sensor> streamSensors(Feature feature) {
    return getBareSensorList(feature)
        .stream()
        .map(GeoJsonUtilities::readSensor);
  }

  public static List<Map<String, Object>> getBareSensorList(Feature feature) {
    return feature.getProperty("sensors");
  }

  public static Sensor readSensor(Map<String, Object> jsonMap) {
    return JSON_MAPPER.convertValue(jsonMap, Sensor.class);
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> writeSensorToMap(Sensor sensor) {
    return JSON_MAPPER.convertValue(sensor, Map.class);
  }
}
