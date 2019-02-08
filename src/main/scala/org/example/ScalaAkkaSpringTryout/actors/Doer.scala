package org.example.ScalaAkkaSpringTryout.actors

import java.time.Instant

import akka.actor.{Actor, Props}
import org.example.ScalaAkkaSpringTryout.actors.Dispatcher.{FailedJob, FinishedJob, StartedJob}
import org.example.ScalaAkkaSpringTryout.actors.Doer.{JobFailedException, Start}
import org.example.ScalaAkkaSpringTryout.helper.Logging

import scala.util.Random

class Doer extends Actor with Logging {
  override def receive: Receive = {
    case Start(jobId) =>
      log.info(s"Starting job [$jobId]")
      sender() ! StartedJob(jobId, Instant.now())
      Thread.sleep(3000)
      if(Random.nextBoolean()) throw new JobFailedException(jobId)
      Thread.sleep(3000)
      log.info(s"Done with job [$jobId]")
      sender() ! FinishedJob(jobId, Instant.now())
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    message foreach {
      case Start(jobId) =>
        log.info(s"Failed job [$jobId], exception: ${reason.getMessage}")
        sender() ! FailedJob(jobId, Instant.now(), reason)
    }
    super.preRestart(reason, message)
  }
}

object Doer {

  case class Start(jobId: String)
  class JobFailedException(jobId: String) extends RuntimeException(s"Job [$jobId] failed!")

  def apply(): Props = Props(new Doer)
}
