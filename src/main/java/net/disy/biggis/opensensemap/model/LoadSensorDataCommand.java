package net.disy.biggis.opensensemap.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class LoadSensorDataCommand {

  private static final String SENSOR = "sensor";
  private static final String SENSEBOX_ID = "senseBoxId";
  private static final String START = "start";
  private static final String END = "end";

  private Sensor sensor;
  private String senseBoxId;
  private ZonedDateTime start;
  private ZonedDateTime end;

  @JsonProperty(SENSOR)
  public Sensor getSensor() {
    return sensor;
  }

  @JsonProperty(SENSOR)
  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }

  @JsonProperty(SENSEBOX_ID)
  public String getSenseBoxId() {
    return senseBoxId;
  }

  @JsonProperty(SENSEBOX_ID)
  public void setSenseBoxId(String senseBoxId) {
    this.senseBoxId = senseBoxId;
  }

  @JsonProperty(START)
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  public ZonedDateTime getStart() {
    return start;
  }

  @JsonProperty(START)
  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  public void setStart(ZonedDateTime start) {
    this.start = start;
  }

  @JsonProperty(END)
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  public ZonedDateTime getEnd() {
    return end;
  }

  @JsonProperty(END)
  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  public void setEnd(ZonedDateTime end) {
    this.end = end;
  }

}
