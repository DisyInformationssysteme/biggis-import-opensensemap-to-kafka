package net.disy.biggis.opensensemap.config;

import java.time.ZonedDateTime;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.disy.biggis.opensensemap.model.LoadSensorDataCommand;
import net.disy.biggis.opensensemap.model.TimeSeries;

@Configuration
public class OpensensemapTimeSeriesDownloadConfiguration {

  public static final String HTTPS_CLIENT_MEASUREMENTS = "opensensemap-API-client-measurement";
  public static final String JSON_LOAD_SENSOR_COMMAND = "JSON-LoadSensorDataCommand";
  public static final String JSON_LOAD_SENSOR_MEASUREMENTS = "JSON-TimeSeries";
  public static final String PATHVAR_SENSOR_ID = "sensorId";
  public static final String PATHVAR_SENSE_BOX_ID = "senseBoxId";
  public static final String POST_DATA_DOWNLOAD_COMMAND = "Post-OpensensemapDownloadTimeSeries-Command";
  public static final String POST_DATA_DOWNLOAD_LOG = "Post-OpensensemapDownloadTimeSeries-Log";
  public static final String POST_DATA_DOWNLOAD_MEASUREMENTS = "Post-OpensensemapDownloadTimeSeries-Measurments";
  public static final String QUERY_START_TIME = "queryStartTime";
  public static final String SUBSCRIBE_DATA_DOWNLOAD_COMMAND = "Subscribe-OpensensemapDownloadTimeSeries-Command";

  @Bean
  @Qualifier(QUERY_START_TIME)
  public ZonedDateTime getQueryStartTime(@Value("${sensor.query.minDate}") String minDate) {
    return ZonedDateTime.parse(minDate);
  }

  @Bean
  @Qualifier(JSON_LOAD_SENSOR_COMMAND)
  public DataFormat getLoadSensorDataCommandFormat() {
    return new JacksonDataFormat(LoadSensorDataCommand.class);
  }

  @Bean
  @Qualifier(POST_DATA_DOWNLOAD_COMMAND)
  public String getPostKafkaCommandQueue(
      @Value("${kafka.url}") final String kafkaUrl,
      @Value("${timeseries.download.commands.kafka.topic}") final String sensorTopic) {
    return new StringBuilder("kafka:")
        .append(kafkaUrl)
        .append("?")
        .append("topic=")
        .append(sensorTopic)
        .toString();
  }

  @Bean
  @Qualifier(SUBSCRIBE_DATA_DOWNLOAD_COMMAND)
  public String getReceiveKafkaCommandQueue(
      @Value("${kafka.url}") final String kafkaUrl,
      @Value("${timeseries.download.commands.kafka.topic}") final String sensorTopic) {
    return new StringBuilder("kafka:")
        .append(kafkaUrl)
        .append("?")
        .append("topic=")
        .append(sensorTopic)
        .append("&groupId=BigGIS-Import")
        .append("&consumersCount=1")
        .toString();
  }

  @Bean
  @Qualifier(HTTPS_CLIENT_MEASUREMENTS)
  public String getOpensensemapApiMeasurementsClient(
      @Value("${opensensemap.api.url}") final String configUrl) {
    return new StringBuilder(configUrl)
        .append("/boxes/")
        .append(PATHVAR_SENSE_BOX_ID)
        .append("/data/")
        .append(PATHVAR_SENSOR_ID)
        .toString();

  }

  @Bean
  @Qualifier(POST_DATA_DOWNLOAD_MEASUREMENTS)
  public String getMeasurementsKafkaCommandQueue(
      @Value("${kafka.url}") final String kafkaUrl,
      @Value("${timeseries.download.measurements.kafka.topic}") final String measurementsTopic) {
    return new StringBuilder("kafka:")
        .append(kafkaUrl)
        .append("?")
        .append("topic=")
        .append(measurementsTopic)
        .toString();
  }

  @Bean
  @Qualifier(JSON_LOAD_SENSOR_MEASUREMENTS)
  public DataFormat geTimeSeriesFormat() {
    return new JacksonDataFormat(TimeSeries.class);
  }

  @Bean
  @Qualifier(POST_DATA_DOWNLOAD_LOG)
  public String getLogKafkaCommandQueue(
      @Value("${kafka.url}") final String kafkaUrl,
      @Value("${timeseries.download.log.kafka.topic}") final String logTopic) {
    return new StringBuilder("kafka:")
        .append(kafkaUrl)
        .append("?")
        .append("topic=")
        .append(logTopic)
        .toString();
  }

}
