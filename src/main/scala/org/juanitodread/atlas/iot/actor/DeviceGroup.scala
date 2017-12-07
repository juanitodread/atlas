package org.juanitodread.atlas.iot.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, Props, Terminated }
import org.juanitodread.atlas.iot.actor.DeviceManager._

object DeviceGroup {
  def props(groupId: String): Props = Props(new DeviceGroup(groupId))

  final case class RequestDeviceList(requestId: Long)
  final case class ReplyDeviceList(requestId: Long, ids: Set[String])
}

class DeviceGroup(groupId: String) extends Actor with ActorLogging {
  import org.juanitodread.atlas.iot.actor.DeviceGroup._

  var deviceActors = Map.empty[String, ActorRef]
  var actorDevices = Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("DeviceGroup {} started", groupId)
  override def postStop(): Unit = log.info("DeviceGroup {} stopped", groupId)

  override def receive: Receive = {
    case trackMsg @ RequestTrackDevice(`groupId`, _) =>
      deviceActors.get(trackMsg.deviceId) match {
        case Some(deviceActor) =>
          deviceActor forward trackMsg

        case None =>
          log.info(s"Creating device actor for ${trackMsg.deviceId}")
          val deviceActor = context.actorOf(Device.props(groupId, trackMsg.deviceId), s"device-${trackMsg.deviceId}")
          context.watch(deviceActor)
          deviceActors += trackMsg.deviceId -> deviceActor
          actorDevices += deviceActor -> trackMsg.deviceId
          deviceActor forward trackMsg
      }

    case RequestTrackDevice(groupId, deviceId) =>
      log.warning(
        "Ignoring TrackDevice request for {}. This actor is responsible for {}.",
        groupId, this.groupId
      )

    case RequestDeviceList(requestId) ⇒
      sender() ! ReplyDeviceList(requestId, deviceActors.keySet)

    case Terminated(deviceActor) =>
      val deviceId = actorDevices(deviceActor)
      log.info(s"Device actor for ${deviceId} has been terminated")
      actorDevices -= deviceActor
      deviceActors -= deviceId
  }
}
