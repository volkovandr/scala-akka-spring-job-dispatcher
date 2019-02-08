package org.example.ScalaAkkaSpringTryout.actors

import java.time.Instant

import akka.actor.Actor
import org.example.ScalaAkkaSpringTryout.actors.Dispatcher._
import org.example.ScalaAkkaSpringTryout.actors.Doer.Start
import org.example.ScalaAkkaSpringTryout.helper.Logging
import org.example.ScalaAkkaSpringTryout.model.JobMetadata
import scala.concurrent.duration._

class Dispatcher extends Actor with Logging {

  private val doer = context.actorOf(Doer())

  override def receive: Receive = receive(0, Map())

  override def preStart(): Unit = {
    context.system.scheduler.schedule(10 seconds, 10 seconds, self, CleanupCache)(context.dispatcher)
    super.preStart()
  }

  def receive(lastId: Int, jobStatusMap: Map[String, JobMetadata]): Receive = {
    case TriggerJob =>
      log.debug("TriggerJob")
      val job = JobMetadata(s"job-$lastId")
      doer ! Start(job.jobId)
      sender() ! JobTriggered(job)
      context become receive(lastId + 1, jobStatusMap + (job.jobId -> job))
    case StartedJob(jobId, time: Instant) =>
      log.debug("StartedJob")
      if (jobStatusMap.contains(jobId))
        context become receive(lastId, jobStatusMap + (jobId -> jobStatusMap(jobId).setStartedAt(time).setStatus("IN_PROGRESS")))
    case FinishedJob(jobId, time) =>
      log.debug("FinishedJob")
      context become receive(lastId, jobStatusMap + (jobId -> jobStatusMap(jobId).setFinishedAt(time).setStatus("FINISHED")))
    case FailedJob(jobId, time, ex) =>
      log.debug("FailedJob")
      context become receive(lastId, jobStatusMap + (jobId -> jobStatusMap(jobId).setFailedAt(time).setStatus("FAILED").setErrorMessage(ex.getMessage)))
    case ReportJobStatus(jobId) =>
      log.debug("ReportJobStatus")
      sender() ! JobStatusReport(jobId, jobStatusMap.get(jobId))
    case ReportJobs =>
      log.debug("ReportJobs")
      sender() ! JobsStatusReport(jobStatusMap)
    case CleanupCache =>
      log.debug("CleanupCache")
      context become receive(lastId, removeStaleJobMetadataItems(jobStatusMap))
  }

  private def removeStaleJobMetadataItems(jobStatusMap: Map[String, JobMetadata]): Map[String, JobMetadata] =
    jobStatusMap.filter(item => item._2 match {
      case jobMetadata if !jobMetadata.isComplete => true
      case jobMetadata if jobMetadata.completionTime.isDefined =>
        jobMetadata.completionTime.get.getEpochSecond < Instant.now().getEpochSecond - 10
    })

}

object Dispatcher {

  case object TriggerJob

  case class JobTriggered(jobMetadata: JobMetadata)

  case class StartedJob(jobId: String, time: Instant)

  case class FinishedJob(jobId: String, time: Instant)

  case class FailedJob(jobId: String, time: Instant, ex: Throwable)

  case class ReportJobStatus(jobId: String)

  case class JobStatusReport(jobId: String, jobMetadata: Option[JobMetadata])

  case object ReportJobs

  case class JobsStatusReport(jobs: Map[String, JobMetadata])

  case object CleanupCache

}