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