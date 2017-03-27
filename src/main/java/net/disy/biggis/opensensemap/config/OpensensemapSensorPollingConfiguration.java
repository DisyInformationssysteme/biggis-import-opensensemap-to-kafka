package net.disy.biggis.opensensemap.config;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class OpensensemapSensorPollingConfiguration {

  private static final String PROTOCOL = "quartz2://";
  private static final String TIMER = "sensors";
  private static final String SENSE_BOXES_RESPONSE_FORMAT = "geojson";

  public static final String SENSOR_POLLING_CONFIGURATION = "sensorPollingConfiguration";
  public static final String SENSE_BOXES_QUERY = "senseBoxesQuery";
  public static final String OPENSENSEMAP_SSL = "https://api.opensensemap.org";
  public static final String GEOJSON_FEATURE_COLLECTION = "GeoJSON-FeatureCollection";
  public static final String HTTPS_CLIENT_BOXES = "opensensemap-API-client-boxes";
  public static final String KAFKA_SENSORS_CONFIGURATION = "sensorKafkaConfiguration";

  @Bean
  @Qualifier(SENSOR_POLLING_CONFIGURATION)
  public String getQuartzConfiguration(
      @Value("${sensor.query.polling.cron}") final String cronExpression,
      @Value("${sensor.query.polling.timezone}") final String timezone) {
    final StringBuilder configuration = new StringBuilder()
        .append(PROTOCOL)
        .append(TIMER)
        .append("?")
        .append("cron=")
        .append(cronExpression);
    if (isNotBlank(timezone)) {
      configuration.append("&").append("trigger.timeZone=").append(timezone);
    }
    return configuration.toString();
  }

  @Bean
  @Qualifier(HTTPS_CLIENT_BOXES)
  public String getOpensensemapApiClient(
      @Value("${opensensemap.api.url}") final String configUrl) {
    String strippedUrl = StringUtils.removeStart(configUrl, "https://");
    strippedUrl = StringUtils.removeStart(strippedUrl, "http://");
    return new StringBuilder("https4://")
        .append(strippedUrl)
        .append("/boxes")
        .append("?httpClientConfigurer=")
        .append(OpensensemapClientConfigurer.OPENSENSEMAP_HTTPS_CLIENT)
        .append("&mapHttpMessageHeaders=false")
        .toString();
  }

  @Bean
  @Qualifier(SENSE_BOXES_QUERY)
  public String getSenseBoxesQuery(
      @Value("${sensor.query.phenomenon}") final String phenomenon,
      @Value("${sensor.query.exposure}") final String exposure) {
    return new StringBuilder()
        .append("phenomenon=")
        .append(phenomenon)
        .append("&")
        .append("format=")
        .append(SENSE_BOXES_RESPONSE_FORMAT)
        .append("&")
        .append("exposure=")
        .append(exposure)
        .toString();
  }

  @Bean
  @Qualifier(OPENSENSEMAP_SSL)
  public LayeredConnectionSocketFactory opensenseMapSslSocketFactory(
      @Value("${opensensemap.trustStore.file}") final Resource trustedCertificates,
      @Value("${opensensemap.trustStore.password}") final String storePassword)
      throws Exception {
    KeyStore trustStore = readTrustedCertificates(trustedCertificates, storePassword);
    final SSLContext sslContext = SSLContextBuilder
        .create()
        .loadTrustMaterial(trustStore, null)
        .build();
    return new SSLConnectionSocketFactory(sslContext);
  }

  private KeyStore readTrustedCertificates(
      final Resource trustedCertificates,
      final String storePassword)
      throws KeyStoreException,
      IOException,
      NoSuchAlgorithmException,
      CertificateException {
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    try (InputStream in = trustedCertificates.getInputStream()) {
      trustStore.load(in, storePassword.toCharArray());
    }
    return trustStore;
  }

  @Bean
  @Qualifier(GEOJSON_FEATURE_COLLECTION)
  public DataFormat getGeoJsonFeatureCollectionFormat() {
    return new JacksonDataFormat(FeatureCollection.class);
  }

  @Bean
  @Qualifier(KAFKA_SENSORS_CONFIGURATION)
  public String getKafkaSensorsProcuder(
      @Value("${kafka.url}") final String kafkaUrl,
      @Value("${sensor.kafka.topic}") final String sensorTopic) {
    return new StringBuilder("kafka:")
        .append(kafkaUrl)
        .append("?")
        .append("topic=")
        .append(sensorTopic)
        .toString();

  }
}
