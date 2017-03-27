package net.disy.biggis.opensensemap.processor;

import static java.text.MessageFormat.format;
import static net.disy.biggis.opensensemap.config.OpensensemapTimeSeriesDownloadConfiguration.QUERY_START_TIME;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.disy.biggis.opensensemap.geometry.IsPointWithinBoundingBox;
import net.disy.biggis.opensensemap.model.GeoJsonUtilities;
import net.disy.biggis.opensensemap.model.Measurement;
import net.disy.biggis.opensensemap.model.Sensor;

@Component
public class SensorFilter implements Processor {

  private static final Logger LOG = LoggerFactory.getLogger(SensorFilter.class);
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  private IsPointWithinBoundingBox isPointWithinBoundingBox;
  private ZonedDateTime startDate;

  public SensorFilter(
      @Autowired final IsPointWithinBoundingBox isPointWithinBoundingBox,
      @Autowired @Qualifier(QUERY_START_TIME) final ZonedDateTime minDate) {
    this.isPointWithinBoundingBox = isPointWithinBoundingBox;
    this.startDate = minDate;
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    FeatureCollection featureCollection = exchange.getIn().getBody(FeatureCollection.class);
    List<Feature> filtered = featureCollection
        .getFeatures()
        .stream()
        .filter(this::isWithinBoundingBox)
        .map(this::extractSensorsAfterStartDate)
        .filter(feature -> !GeoJsonUtilities.getBareSensorList(feature).isEmpty())
        .collect(Collectors.toList());
    featureCollection.setFeatures(filtered);
  }

  private boolean isWithinBoundingBox(Feature feature) {
    GeoJsonObject geometry = feature.getGeometry();
    if (geometry instanceof Point) {
      Point point = (Point) geometry;
      return isPointWithinBoundingBox.test(point);
    }
    LOG.debug(format("Rejected Feature ''{0}''", feature));
    return false;
  }

  private Feature extractSensorsAfterStartDate(Feature feature) {
    List<Map<String, Object>> filtered = GeoJsonUtilities
        .streamSensors(feature)
        .filter(this::hasMeasurementAfterStartDate)
        .map(GeoJsonUtilities::writeSensorToMap)
        .collect(Collectors.toList());
    feature.setProperty("sensors", filtered);
    return feature;
  }

  private boolean hasMeasurementAfterStartDate(Sensor sensor) {
    ZonedDateTime lastMeasurementDate = getLastMeasurementDate(sensor);
    if (lastMeasurementDate == null) {
      LOG.debug(format("Rejected sensor ''{0}''", sensor.getId()));
      return false;
    }
    return startDate.isBefore(lastMeasurementDate);
  }

  private ZonedDateTime getLastMeasurementDate(Sensor sensor) {
    return Optional
        .ofNullable(sensor)
        .map(Sensor::getLastMeasurement)
        .map(Measurement::getCreatedAt)
        .orElse(null);
  }
}
