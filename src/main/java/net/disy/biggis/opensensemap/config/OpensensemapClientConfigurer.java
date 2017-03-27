package net.disy.biggis.opensensemap.config;

import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(OpensensemapClientConfigurer.OPENSENSEMAP_HTTPS_CLIENT)
public class OpensensemapClientConfigurer implements HttpClientConfigurer {

  public static final String OPENSENSEMAP_HTTPS_CLIENT = "opensensemapHttpClientConfigurer";

  @Autowired
  @Qualifier(OpensensemapSensorPollingConfiguration.OPENSENSEMAP_SSL)
  private LayeredConnectionSocketFactory socketFactory;

  @Override
  public void configureHttpClient(HttpClientBuilder clientBuilder) {
    clientBuilder.setSSLSocketFactory(socketFactory);
    HttpClientConnectionManager clientConnectionManager = createConnectionManager();
    clientBuilder.setConnectionManager(clientConnectionManager);
  }

  private HttpClientConnectionManager createConnectionManager() {
    Registry<ConnectionSocketFactory> registry = RegistryBuilder
        .<ConnectionSocketFactory> create()
        .register("https", socketFactory)
        .build();
    return new PoolingHttpClientConnectionManager(registry);
  }

}
