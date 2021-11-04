package $package$.server

import $package$.protobuf.service.{EchoRequest, EchoResponse}
import $package$.protobuf.service.EchoServiceGrpc.EchoService

import scala.concurrent.Future

class EchoServiceImpl extends EchoService {
  override def echo(request: EchoRequest): Future[EchoResponse] = {
    println(s"Request: \$request")
    val response = EchoResponse(originalMessage = request.message, echoMessage = s"Echo: \${request.message}")
    println(s"Response: \$response")
    Future.successful(response)
  }
}
