package net.disy.biggis.opensensemap.model;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperTest {
  private final String MEASUREMENT = "{ \"value\": \"23.35\", \"createdAt\": \"2017-03-20T08:55:19.187Z\"}";
  private final String SENSOR = "{"
      + "\"title\": \"Temperatur\","
      + "\"unit\": \"°C\","
      + "\"boxes_id\": \"53c576f2e79c90a0102f67e9\","
      + "\"sensorType\": \"HDC1008\","
      + "\"_id\": \"580f30787ac61b001098326a\","
      + " \"lastMeasurement\": {"
      + "          \"value\": \"23.35\","
      + "          \"createdAt\": \"2017-03-20T08:55:19.187Z\""
      + "}}";

  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void unmarshallMeasurement() throws Exception {
    Measurement measurement = mapper.readValue(MEASUREMENT, Measurement.class);

    assertThat(measurement.getValue(), is(equalTo(new BigDecimal("23.35"))));
    assertThat(
        measurement.getCreatedAt(),
        is(equalTo(ZonedDateTime.parse("2017-03-20T08:55:19.187Z"))));
  }

  @Test
  public void measurementRoundtrip() throws Exception {
    Measurement measurement = mapper.readValue(MEASUREMENT, Measurement.class);
    String json = mapper.writeValueAsString(measurement);

    assertThat(json, stringContainsInOrder(asList("\"value\"", ":", "\"23.35\"")));
    assertThat(
        json,
        stringContainsInOrder(asList("\"createdAt\"", ":", "\"2017-03-20T08:55:19.187Z\"")));

  }

  @Test
  public void unmarshallSensor() throws Exception {
    Sensor sensor = mapper.readValue(SENSOR, Sensor.class);

    assertThat(sensor.getTitle(), is("Temperatur"));
    assertThat(sensor.getUnit(), is("°C"));
    assertThat(sensor.getSensorType(), is("HDC1008"));
    assertThat(sensor.getId(), is("580f30787ac61b001098326a"));
    assertThat(sensor.getLastMeasurement(), both(isA(Measurement.class)).and(notNullValue()));
  }
}
