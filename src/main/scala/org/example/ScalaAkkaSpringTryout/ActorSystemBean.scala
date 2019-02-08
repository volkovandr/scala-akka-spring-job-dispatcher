package org.example.ScalaAkkaSpringTryout

import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem, Props}
import org.example.ScalaAkkaSpringTryout.actors.Dispatcher
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ActorSystemBean {

  private val logger = Logger.getLogger(classOf[ActorSystemBean].getName)

  @Bean(destroyMethod = "terminate")
  def system: ActorSystem = {
    logger.info("Creating Actor system")
    ActorSystem("main")
  }

  @Bean
  def dispatcherActor(system: ActorSystem): ActorRef = system.actorOf(Props[Dispatcher], "dispatcher")

}
