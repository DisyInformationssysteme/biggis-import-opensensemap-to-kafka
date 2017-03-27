package net.disy.biggis.opensensemap.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeSeries {

  private static final String SENSOR = "sensor";
  private static final String MEASUREMENTS = "measurements";

  private Sensor sensor;
  private List<Measurement> measurements;

  @JsonProperty(SENSOR)
  public Sensor getSensor() {
    return sensor;
  }

  @JsonProperty(SENSOR)
  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }

  @JsonProperty(MEASUREMENTS)
  public List<Measurement> getMeasurements() {
    return measurements;
  }

  @JsonProperty(MEASUREMENTS)
  public void setMeasurements(List<Measurement> measurements) {
    this.measurements = measurements;
  }

}
