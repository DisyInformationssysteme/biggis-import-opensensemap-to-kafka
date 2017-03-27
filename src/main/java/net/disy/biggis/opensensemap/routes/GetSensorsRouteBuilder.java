package net.disy.biggis.opensensemap.routes;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.spi.DataFormat;
import org.geojson.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import net.disy.biggis.opensensemap.config.OpensensemapSensorPollingConfiguration;
import net.disy.biggis.opensensemap.config.OpensensemapTimeSeriesDownloadConfiguration;
import net.disy.biggis.opensensemap.model.GeoJsonUtilities;
import net.disy.biggis.opensensemap.model.LoadSensorDataCommand;
import net.disy.biggis.opensensemap.model.Sensor;
import net.disy.biggis.opensensemap.processor.LoadSensorDataCommandFactory;
import net.disy.biggis.opensensemap.processor.SensorFilter;

@Component
public class GetSensorsRouteBuilder extends RouteBuilder {

  public static final String ROUTE_ID = "Get senseboxes";
  private static final String ROUTE_ID_SCHEDULE = "Schedule initial download";

  private static final String SCHEDULE_DOWNLOADS = "seda:split-for-download";

  @Autowired
  @Qualifier(OpensensemapSensorPollingConfiguration.SENSOR_POLLING_CONFIGURATION)
  private String pollingConfiguration;

  @Autowired
  @Qualifier(OpensensemapSensorPollingConfiguration.HTTPS_CLIENT_BOXES)
  private String opensensenmapApiClient;

  @Autowired
  @Qualifier(OpensensemapSensorPollingConfiguration.SENSE_BOXES_QUERY)
  private String senseBoxesQuery;

  @Autowired
  @Qualifier(OpensensemapSensorPollingConfiguration.GEOJSON_FEATURE_COLLECTION)
  private DataFormat featureCollection;

  @Autowired
  private SensorFilter sensorFilter;

  @Autowired
  @Qualifier(OpensensemapSensorPollingConfiguration.KAFKA_SENSORS_CONFIGURATION)
  private String kafkaSensorProducer;

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.POST_DATA_DOWNLOAD_COMMAND)
  private String kafkaTimeSeriesCommand;

  @Autowired
  private LoadSensorDataCommandFactory loadSensorDataCommandFactory;

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.JSON_LOAD_SENSOR_COMMAND)
  private DataFormat loadSensorDataCommand;

  @Override
  // @formatter:off
  public void configure() throws Exception {
    from(pollingConfiguration)
        .routeId(ROUTE_ID)
        .streamCaching()
        .setHeader(Exchange.HTTP_QUERY, constant(senseBoxesQuery))
        .to(opensensenmapApiClient)
        .unmarshal(featureCollection)
        .process(sensorFilter)
        .to(SCHEDULE_DOWNLOADS)
        .marshal(featureCollection)
        .process(this::setKafkaKeyForPolling)
        .convertBodyTo(String.class, StandardCharsets.UTF_8.name())
        .to(kafkaSensorProducer);

    from(SCHEDULE_DOWNLOADS)
        .routeId(ROUTE_ID_SCHEDULE)
        .split(simple("${body.features}"))
          .setHeader(LoadSensorDataCommandFactory.SENSEBOX_ID, simple("${body.properties[_id]}"))
          .setHeader(LoadSensorDataCommandFactory.SENSEBOX_CREATE_DATE, simple("${body.properties[createdAt]}"))
          .process(this::extractSensors)
          .split(body())
            .process(loadSensorDataCommandFactory::createInitialCommand)
            .process(this::setKafkaKeysForInitialCommand)
            .marshal(loadSensorDataCommand)
            .convertBodyTo(String.class, StandardCharsets.UTF_8.name())
            .to(kafkaTimeSeriesCommand)
          .end()
        .end();
  }
  // @formatter:on

  private void setKafkaKeyForPolling(Exchange exchange) {
    final Date quartzFireTime = exchange.getIn().getHeader("fireTime", Date.class);
    final String messageKey = quartzFireTime.toInstant().toString();
    exchange.getIn().setHeader(KafkaConstants.KEY, messageKey);
  }

  private void setKafkaKeysForInitialCommand(Exchange exchange) {
    Message inMessage = exchange.getIn();
    String sensorId = Optional
        .of(inMessage)
        .map(in -> in.getBody(LoadSensorDataCommand.class))
        .map(LoadSensorDataCommand::getSensor)
        .map(Sensor::getId)
        .orElse("UNKNOWN");
    inMessage.setHeader(KafkaConstants.KEY, sensorId);
  }

  private void extractSensors(Exchange exchange) {
    Feature feature = exchange.getIn().getBody(Feature.class);
    List<Sensor> sensors = GeoJsonUtilities.streamSensors(feature).collect(Collectors.toList());
    exchange.getIn().setBody(sensors);
  }
}
