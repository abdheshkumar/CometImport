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
		S.request match {
			case Full(req: Req) => {
				req.uploadedFiles match {
					case Nil => Text("No files uploaded")
					case files: List[FileParamHolder] => {
						// extract the first file submitted against <input name='uploaded-file' type='file' />
						files.find(_.name == "uploaded-file") match {
							case Some(file: FileParamHolder) => {
								S.session match {
									case Full(session: LiftSession) => {
										session.findComet("CometUpload", Empty) match {
											case Full(cometActor: CometActor) => {
												cometActor ! InitImport(file)
												Text("Started upload")
											}
											case _ => {
												Text("No existing comet actor to message")
											}
										}
									}
									case _ => Text("No session")
								}
							}
							case _ => Text("No matching file")
						}
					}
				}
			}
			case _ => Text("No request")
		}
	}
}