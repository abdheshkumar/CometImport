package com.untyped.cometupload.csv

import net.liftweb.actor._

/*
* An ImportListener that reports messages to a LiftActor
*/
class LiftActorImportListener(val actor: LiftActor) extends ImportListener {
	def importProgress(msg: String) = actor ! ImportProgress(msg)
	def importStarted(msg: String) = actor ! ImportStarted(msg)
	def importSucceeded(msg: String) = actor ! ImportSucceeded(msg)
	def importFailed(msg: String) = actor ! ImportFailed(msg)
	def importPreviewSucceeded(msg: String) = actor ! ImportPreviewSucceeded(msg)
}

/*
* LiftActor must handle these messages.
*/
case class ImportProgress(val msg: String)
case class ImportStarted(val msg: String)
case class ImportPreviewSucceeded(val msg: String)
case class ImportSucceeded(val msg: String)
case class ImportFailed(val msg: String)
