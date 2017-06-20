import ClusterListener.{AddMe, BroadcastedMessage, RemoveMe}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.{Actor, ActorLogging, Address}
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, Routee, Router}

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class ClusterListener(akkaPort: Int, seedPort: Option[Int]) extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  var router = Router(BroadcastRoutingLogic(), Vector[ActorRefRoutee]())


  // Join the seed node with the given address
  val remoteActor = seedPort map {
    port =>
      val address = Address("akka.tcp", "ClusterSystem", "127.0.0.1", port)
      cluster.joinSeedNodes(immutable.Seq(address))

      context.actorSelection(address.toString + "/user/ClusterListener")
  }

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])

    remoteActor.foreach(_ ! AddMe)

    seedPort.filter(_ == akkaPort).foreach { _ =>
      context.system.scheduler.schedule(FiniteDuration(20, SECONDS), FiniteDuration(20, SECONDS)) {
        router.route(BroadcastedMessage, self)
      }
    }
  }

  override def postStop(): Unit = {
    remoteActor.foreach(_ ! RemoveMe)
    cluster.unsubscribe(self)
  }

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info(
        "Member is Removed: {} after {}",
        member.address, previousStatus)
    case AddMe =>
      log.info("Adding router member: {}", sender().path)
      router = router.addRoutee(sender())
    case RemoveMe =>
      log.info("Removing router member: {}", sender().path)
      router = router.removeRoutee(sender())
    case BroadcastedMessage =>
      log.info("Received a broadcasted Message")
    case _: MemberEvent => // ignore
  }
}

object ClusterListener {

  case object AddMe

  case object RemoveMe

  case object BroadcastedMessage

}