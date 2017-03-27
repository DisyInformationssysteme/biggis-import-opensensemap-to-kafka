package net.disy.biggis.opensensemap.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Measurement {

  private static final String CREATED_AT = "createdAt";
  private static final String VALUE = "value";

  private BigDecimal value;
  private ZonedDateTime createdAt;

  @JsonProperty(VALUE)
  @JsonSerialize(using = BigDecimalSerializer.class)
  public BigDecimal getValue() {
    return value;
  }

  @JsonProperty(VALUE)
  public void setValue(BigDecimal value) {
    this.value = value;
  }

  @JsonProperty(CREATED_AT)
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  @JsonProperty(CREATED_AT)
  @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

}
