package net.disy.biggis.opensensemap.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Sensor {

  private static final String ID = "_id";
  private static final String SENSOR_TYPE = "sensorType";
  private static final String TITLE = "title";
  private static final String UNIT = "unit";
  private static final String LAST_MEASUREMENT = "lastMeasurement";

  private String title;
  private String unit;
  private String sensorType;
  private String id;
  private Measurement lastMeasurement;
  private Map<String, Object> other = new HashMap<>();

  @JsonProperty(TITLE)
  public String getTitle() {
    return title;
  }

  @JsonProperty(TITLE)
  public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty(UNIT)
  public String getUnit() {
    return unit;
  }

  @JsonProperty(UNIT)
  public void setUnit(String unit) {
    this.unit = unit;
  }

  @JsonProperty(SENSOR_TYPE)
  public String getSensorType() {
    return sensorType;
  }

  @JsonProperty(SENSOR_TYPE)
  public void setSensorType(String sensorType) {
    this.sensorType = sensorType;
  }

  @JsonProperty(ID)
  public String getId() {
    return id;
  }

  @JsonProperty(ID)
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty(LAST_MEASUREMENT)
  public Measurement getLastMeasurement() {
    return lastMeasurement;
  }

  @JsonProperty(LAST_MEASUREMENT)
  public void setLastMeasurement(Measurement lastMeasurement) {
    this.lastMeasurement = lastMeasurement;
  }

  @JsonAnyGetter
  public Map<String, Object> getOther() {
    return other;
  }

  @JsonAnySetter
  public void setOther(String name, Object value) {
    other.put(name, value);
  }

}
