package com.untyped.cometupload.snippet

import xml.{Text, NodeSeq}
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.common._

import com.untyped.cometupload.comet.InitImport

class ChooseFile {
	/*
	* This snippet restarts the CometUpload on page reload.
	* It's primary purpose is to take an uploaded file and send it
	* to the CometUpload as a message, but it also does some basic
	* handling of the unexpected...
	*/
	def render(xhtml: NodeSeq): NodeSeq = {
		// find first file in current request submitted against <input type=file name=uploaded-file />
		S.request.map(_.uploadedFiles.find(_.name == "uploaded-file") match {
			case Some(file: FileParamHolder) => {
				// find the current Comet actor and send it a message to start import
				S.session.map(_.findComet("CometUpload", Empty) match {
					case Full(cometActor: CometActor) => {
						cometActor ! InitImport(file)
						<span />
					}
					case _ => {
						<div class="default-message">No existing comet actor to message</div>
					}
				}).openOr(<div class="default-message">No session</div>)
			}
			case _ => <div class="default-message">There are no matching files. You should probably <a href="/">select a file to upload</a></div>
		}).openOr(<div class="default-message">No request</div>)
	}
}