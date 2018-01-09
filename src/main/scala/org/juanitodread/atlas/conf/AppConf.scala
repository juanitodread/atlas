package org.juanitodread.atlas.conf

import com.typesafe.config.ConfigFactory

trait AppConf {
  private[this] val config = ConfigFactory.load()

  private[this] val testConf = config.getConfig("test")

  val simpleConf = testConf.getString("name")
}