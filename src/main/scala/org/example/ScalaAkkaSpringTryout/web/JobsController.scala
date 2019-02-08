package org.example.ScalaAkkaSpringTryout.web

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import org.example.ScalaAkkaSpringTryout.actors.Dispatcher._
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RestController}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

@RestController
class JobsController(dispatcherActor: ActorRef) {

  private implicit val timeout: Timeout = Timeout(10 seconds)

  @GetMapping(path = Array("/jobs/start"))
  def startJob(): String = Try(
    Await.result(dispatcherActor ? TriggerJob, timeout.duration)
      .asInstanceOf[JobTriggered]
      .jobMetadata.toJson
  ).getOrElse("Error: timeout")

  @GetMapping(path = Array("/jobs"))
  def reportAllJobs(): String = Try(
    "[" + Await.result(dispatcherActor ? ReportJobs, timeout.duration)
      .asInstanceOf[JobsStatusReport]
      .jobs
      .toList
      .sortBy(_._1.drop(4).toInt)
      .map(_._2.toJson)
      .reduceOption(_ + ",\n" + _)
      .getOrElse("") + "]"
  ).getOrElse("Error: timeout")

  @GetMapping(path = Array("/jobs/{jobId}"))
  def reportJob(@PathVariable jobId: String): String = Try(
    Await.result(dispatcherActor ? ReportJobStatus(jobId), timeout.duration)
      .asInstanceOf[JobStatusReport]
      .jobMetadata.get.toJson
  ).getOrElse("Error: timeout or job not found")

}
