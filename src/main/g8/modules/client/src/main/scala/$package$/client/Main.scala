package $package$.client

import $package$.instrumentation.{Metrics, Tracing}
import $package$.protobuf.service.{EchoRequest, EchoServiceGrpc}
import io.grpc.ManagedChannelBuilder

object Main {
  def main(args: Array[String]): Unit = {
    val metrics = Metrics.resource(8082)
    val tracer = Tracing.jaeger("client")

    val channelBuilder = ManagedChannelBuilder.forAddress("localhost", 8080)
    channelBuilder.usePlaintext()
    val channel = channelBuilder.build()
    val client = EchoServiceGrpc
      .blockingStub(channel)
      .withInterceptors(new MeteredClientInterceptor, new TracedClientInterceptor(tracer))

    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC client since JVM is shutting down")
      metrics.stop()
      tracer.close()
      System.err.println("*** client shut down")
    }

    while (true) {
      val request = EchoRequest("test")
      val response = client.echo(request)
      println(s"Response from server: \$response")
      Thread.sleep(1000)
    }
  }
}
