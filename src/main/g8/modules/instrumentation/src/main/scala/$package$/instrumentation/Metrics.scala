package $package$.instrumentation

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.HTTPServer

import java.net.InetSocketAddress

object Metrics {
  def resource(port: Int = 8081): HTTPServer = {
    val registry = CollectorRegistry.defaultRegistry
    new HTTPServer(new InetSocketAddress(port), registry)
  }
}
