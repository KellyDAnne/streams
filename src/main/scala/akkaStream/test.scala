package akkaStream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{Await, ExecutionContext, Future}
/**
  * Created by keanne on 23/02/2018.
  */
object test extends App{

  implicit val system = ActorSystem("streamtest")
  implicit val materializer = ActorMaterializer()

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._
    val in = Source(List("Je suis une première phrase!","j'en suis une deuxième,","Et moi alors? je compte pas..."))
      /*.log("start").withAttributes(
      Attributes.logLevels(
        onElement = Logging.WarningLevel,
        onFinish = Logging.InfoLevel,
        onFailure = Logging.DebugLevel
      )
    )*/
    //val out = Sink.ignore
    val out = Sink.foreach[Array[String]](_.foreach(println(_)))

    //val bcast = builder.add(Broadcast[String](2))
    //val merge = builder.add(Merge[String](2))

    val f1 = Flow[String].map(_.split(" "))
    val f2 = Flow[Array[String]].map(_.map(_.replace("!","")))

      val f5 = Flow[String].foldAsync(Seq[String]()) { case (acc, apiResult) => Future(Seq(apiResult) ++ acc) }
      .mapConcat                    { syncObjects           => identity(syncObjects.distinct.toList) }

              in ~> f1 ~> f2 ~> out
    ClosedShape
  })

  g.run()

  //system.terminate()
}
