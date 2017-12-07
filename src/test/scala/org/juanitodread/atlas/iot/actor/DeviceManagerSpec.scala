package org.juanitodread.atlas.iot.actor

import akka.actor.{ ActorSystem, PoisonPill }
import akka.testkit.{ TestKit, TestProbe }
import org.juanitodread.atlas.iot.UnitSpec

import scala.concurrent.duration.DurationDouble

class DeviceManagerSpec(_system: ActorSystem) extends TestKit(_system)
    with UnitSpec {

  def this() = this(ActorSystem("DeviceManagerSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  it should "be able to register a device actor" in {
    val probe = TestProbe()
    val managerActor = system.actorOf(DeviceManager.props(), "manager-actor1")

    managerActor.tell(DeviceManager.RequestTrackDevice("group", "device1"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered)
    val groupActor1 = probe.lastSender

    managerActor.tell(DeviceManager.RequestTrackDevice("group", "device2"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered)
    val groupActor2 = probe.lastSender
    groupActor1 should !==(groupActor2)
  }

}
