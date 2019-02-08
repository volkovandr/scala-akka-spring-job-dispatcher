package org.example.ScalaAkkaSpringTryout.model

import java.time.Instant

case class JobMetadata(
                        jobId: String,
                        var status: String = "PENDING",
                        submittedAt: Instant = Instant.now(),
                        var startedAt: Option[Instant] = None,
                        var finishedAt: Option[Instant] = None,
                        var failedAt: Option[Instant] = None,
                        var errorMessage: Option[String] = None
                      ) {
  def toJson: String =
    s"""{
       |"jobId": "$jobId",
       |"status": "$status",
       |"timing": {$timing}
       |${if(errorMessage.nonEmpty) {", \"error\": \"" + errorMessage.get + "\""} else ""}}""".stripMargin

  private def timing: String = List(
    Some("\"submittedAt\": " + submittedAt.getEpochSecond.toString),
    startedAt.map("\"startedAt\": " + _.getEpochSecond.toString),
    finishedAt.map("\"finishedAt\": " + _.getEpochSecond.toString),
    failedAt.map("\"failedAt\": " + _.getEpochSecond.toString)
  ).filter(_.nonEmpty).map(_.get).mkString(", ")

  def setStartedAt(startedAt: Instant): JobMetadata = {
    this.startedAt = Some(startedAt)
    this
  }

  def setFinishedAt(finishedAt: Instant): JobMetadata = {
    this.finishedAt = Some(finishedAt)
    this
  }

  def setFailedAt(failedAt: Instant): JobMetadata = {
    this.failedAt = Some(failedAt)
    this
  }

  def setErrorMessage(errorMessage: String): JobMetadata = {
    this.errorMessage = Some(errorMessage)
    this
  }

  def setStatus(status: String): JobMetadata = {
    this.status = status
    this
  }

  def isComplete: Boolean = finishedAt.isDefined || failedAt.isDefined
  def completionTime: Option[Instant] = if(finishedAt.isDefined) finishedAt else failedAt
}
