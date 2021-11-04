package $package$.server

import $package$.instrumentation.{Metrics, Tracing}
import $package$.protobuf.service.EchoServiceGrpc
import io.grpc.{ServerBuilder, ServerInterceptors}

import scala.concurrent.ExecutionContext.global

object Main {
  def main(args: Array[String]): Unit = {
    val metrics = Metrics.resource(8081)
    val jaeger = Tracing.jaeger("server")

    val serverBuilder: ServerBuilder[_] = ServerBuilder.forPort(8080)
    serverBuilder.addService(
      ServerInterceptors.intercept(
        EchoServiceGrpc.bindService(new EchoServiceImpl, global),
        new MeteredServerInterceptor,
        new TracedServerInterceptor(jaeger)
      )
    )

    val server = serverBuilder.build()

    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      jaeger.close()
      metrics.stop()
      server.shutdown()
      System.err.println("*** server shut down")
    }

    server.start()
    server.awaitTermination()
  }
}
