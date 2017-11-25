package org.juanitodread.atlas.iot.actor

import akka.actor.{ Actor, ActorLogging, Props }

object IotSupervisor {
  def props(): Props = Props(new IotSupervisor())
}

class IotSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("IoT App started")
  override def postStop(): Unit = log.info("IoT App stopped")

  override def receive: Receive = Actor.emptyBehavior
}
