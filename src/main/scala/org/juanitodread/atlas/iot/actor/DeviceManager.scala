package org.juanitodread.atlas.iot.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, Props, Terminated }

object DeviceManager {
  def props(): Props = Props(new DeviceManager())

  final case class RequestTrackDevice(groupId: String, deviceId: String)
  final case object DeviceRegistered
}

class DeviceManager extends Actor with ActorLogging {
  import org.juanitodread.atlas.iot.actor.DeviceManager._

  var deviceActors = Map.empty[String, ActorRef]
  var actorDevices = Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("DeviceManager started")
  override def postStop(): Unit = log.info("DeviceManager stopped")

  override def receive: Receive = {
    case trackMsg @ RequestTrackDevice(groupId, _) =>
      deviceActors.get(trackMsg.groupId) match {
        case Some(ref) =>
          ref forward trackMsg

        case None =>
          log.info(s"Creating device group actor for ${groupId}")
          val groupActor = context.actorOf(DeviceGroup.props(groupId), s"group-$groupId")
          context.watch(groupActor)
          groupActor forward trackMsg
          deviceActors += groupId -> groupActor
          actorDevices += groupActor -> groupId
      }

    case Terminated(groupActor) =>
      val groupId = actorDevices(groupActor)
      log.info(s"Device group actor for ${groupId} has been terminated")
      deviceActors -= groupId
      actorDevices -= groupActor
  }
}
