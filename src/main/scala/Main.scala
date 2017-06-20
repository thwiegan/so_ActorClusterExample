import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}


object Main extends App {

  //Starting system with port from command line arg
  val system = ActorSystem("ClusterSystem", ConfigFactory.load().withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(args(0).toInt)))

  //Starting Cluster listener
  system.actorOf(Props(new ClusterListener(args(0).toInt, args.lift(1).map(_.toInt))), "ClusterListener")

}
