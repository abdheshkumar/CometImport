package com.untyped.cometupload.csv

import scala.actors._

import net.liftweb.actor._

trait ImportListener {
	def importStarted(msg: String)
	def importProgress(msg: String)
	def importSucceeded(msg: String)
	def importFailed(msg: String)
	def importPreviewSucceeded(msg: String)
}

case class ImportProgress(val msg: String)
case class ImportStarted(val msg: String)
case class ImportPreviewSucceeded(val msg: String)
case class ImportSucceeded(val msg: String)
case class ImportFailed(val msg: String)

class LiftActorImportListener(val actor: LiftActor) extends ImportListener {
	def importProgress(msg: String) = actor ! ImportProgress(msg)
	def importStarted(msg: String) = actor ! ImportStarted(msg)
	def importSucceeded(msg: String) = actor ! ImportSucceeded(msg)
	def importFailed(msg: String) = actor ! ImportFailed(msg)
	def importPreviewSucceeded(msg: String) = actor ! ImportPreviewSucceeded(msg)
}

class MessageQueueImportListener extends ImportListener {
	private var _messages: List[String] = Nil
	
	def messages = _messages.reverse
	
	def importProgress(msg: String) = _messages = msg :: _messages
	def importStarted(msg: String) = _messages = msg :: _messages
	def importSucceeded(msg: String) = _messages = msg :: _messages
	def importFailed(msg: String) = _messages = msg :: _messages
	def importPreviewSucceeded(msg: String) = _messages = msg :: _messages
}
