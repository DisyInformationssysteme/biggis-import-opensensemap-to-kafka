package net.disy.biggis.opensensemap.routes;

import java.nio.charset.StandardCharsets;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import net.disy.biggis.opensensemap.config.OpensensemapSensorPollingConfiguration;
import net.disy.biggis.opensensemap.config.OpensensemapTimeSeriesDownloadConfiguration;
import net.disy.biggis.opensensemap.model.TimeSeries;
import net.disy.biggis.opensensemap.processor.DownloadTimeseriesDataQueryCreator;
import net.disy.biggis.opensensemap.processor.DownloadTimeseriesResultCreator;
import net.disy.biggis.opensensemap.processor.LoadSensorDataCommandFactory;

@Component
public class GetTimeSeriesRouteBuilder extends RouteBuilder {

  public static final String ROUTE_ID = "Get time series";

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.SUBSCRIBE_DATA_DOWNLOAD_COMMAND)
  private String getKafkaTimeSeriesCommand;

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.SUBSCRIBE_DATA_DOWNLOAD_COMMAND)
  private String postKafkaTimeSeriesCommand;

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.POST_DATA_DOWNLOAD_MEASUREMENTS)
  private String kafkaTimeSeriesMeasurements;

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.JSON_LOAD_SENSOR_COMMAND)
  private DataFormat loadSensorDataCommand;

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.JSON_LOAD_SENSOR_MEASUREMENTS)
  private DataFormat measurements;

  @Autowired
  @Qualifier(OpensensemapSensorPollingConfiguration.HTTPS_CLIENT_BOXES)
  private String opensensemapClient;

  @Autowired
  private DownloadTimeseriesDataQueryCreator createDownloadRequest;

  @Autowired
  private DownloadTimeseriesResultCreator timeSeriesCreator;

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.POST_DATA_DOWNLOAD_LOG)
  private String kafkaTimeSeriesLog;

  @Autowired
  private LoadSensorDataCommandFactory loadSensorDataCommandFactory;

  // @formatter:off
  @Override
  public void configure() throws Exception {
    from(getKafkaTimeSeriesCommand)
        .routeId(ROUTE_ID)
        .streamCaching()
        .unmarshal(loadSensorDataCommand)
        .process(createDownloadRequest)
        .to(opensensemapClient)
        .process(timeSeriesCreator)
        .choice()
            .when(this::hasMeasurements)
                .marshal(measurements)
                .convertBodyTo(String.class, StandardCharsets.UTF_8.name())
                .to(kafkaTimeSeriesMeasurements)               
            .end()
        .end()
        .setBody(header(DownloadTimeseriesDataQueryCreator.HEADER_COMMAND))
        .marshal(loadSensorDataCommand)
        .log(LoggingLevel.INFO, "${in.body}")
        .convertBodyTo(String.class, StandardCharsets.UTF_8.name())
        .to(kafkaTimeSeriesLog)               
        .process(loadSensorDataCommandFactory::createFollowUpCommand)
        .choice()
            .when(body().isNotEqualTo(null))
                .marshal(loadSensorDataCommand)
                .convertBodyTo(String.class, StandardCharsets.UTF_8.name())
                .to(postKafkaTimeSeriesCommand)
            .end()
        .end();
  }
  // @formatter:on

  private boolean hasMeasurements(Exchange exchange) {
    return !exchange.getIn().getBody(TimeSeries.class).getMeasurements().isEmpty();
  }

}
