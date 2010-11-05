package com.untyped.cometupload.comet

import scala.io.Source
import scala.xml._

import net.liftweb._
import http._
import SHtml._ 
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.actor._
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds.{PrependHtml}
import net.liftweb.http.js.JE._

import com.untyped.cometupload.csv._

// external message type
case class InitImport(val fph: FileParamHolder)

class CometUpload extends CometActor {
	// unique ID for part of page to render message into
	private val messagesId = uniqueId + "-messages"
	private val stickyId = uniqueId + "-sticky"
	
	// internal state
	private var fileContent: Box[FileParamHolder] = Empty
	private var messages: List[NodeSeq] = Nil
	private var sticky: Box[NodeSeq] = Empty
	
	private val importer: Importer = new DummyImporter
	
	override def render = Group(
		<div class="file-info">
			<p>Basic rubrick from <a href="http://demo.liftweb.net/file_upload">the file upload demo</a></p>
			{ fileContent match {
				case Full(file: FileParamHolder) => <div>
					Currently uploading: {Text(file.fileName)}<br />
					MIME Type: {Box.legacyNullTest(file.mimeType).map(Text).openOr(Text("No mime type supplied"))}<br />
					File length: {Text(file.file.length.toString)}<br />
					MD5 Hash: {Text(hexEncode(md5(file.file)))}<br />
				</div>
				case _ => Text("No file selected")
			}}
		</div>
		<div class="sticky-message">
			<strong>Status: </strong>
			<span id={stickyId}>{ sticky.openOr(Text("Nothing to see here")) }</span>
		</div>
		<div id={messagesId} class="messages">
			{messages.flatMap(msg => <div class="message">{msg}</div>)}
		</div>)

	private def sendStickyMessage(msg: NodeSeq) {
		partialUpdate(SetHtml(stickyId, <span class="comet-sticky">{msg}</span>))
		sticky = msg
	}
	
	private def sendMessage(msg: NodeSeq) {
		partialUpdate(PrependHtml(messagesId, <div class="comet-message">{msg}</div>))
		messages = msg :: messages
	}

	/*
	* A message type that starts an import in either preview or final mode.
	*/
	private case class InitImportMode(val isPreview: Boolean)
	
	override def lowPriority = {
		case InitImport(fph: FileParamHolder) => {
			// if no import in progress, start a new import in preview mode
			if(!importer.isRunning) {
				fileContent = Full(fph)
				this ! InitImportMode(true)
			}
		}
		case InitImportMode(isPreview: Boolean) => {
			fileContent match {
				case Full(fph) => {
					// reset and spawn a new asynchronous import
					messages = Nil
					importer.asyncImport(fph, new LiftActorImportListener(this), isPreview)
					reRender(true)
				}
				case _ => {
				}
			}
		}
		case ImportStarted(msg) => {
			val clearAndRestartMessage: NodeSeq = {
				<lift:children>
					{Text(msg)}
					{SHtml.a(() => { importer.cancel; Noop }, Text("cancel"))}
				</lift:children>
			}
			sendStickyMessage(clearAndRestartMessage)
		}
		case ImportProgress(msg) => {
			sendMessage(<span>{msg}</span>)
		}
		case ImportPreviewSucceeded(msg) => {
			val beginForRealMessage: NodeSeq = {
				<lift:children>
					{ Text(msg) }
					{ SHtml.a(() => { this ! InitImportMode(false); Noop }, Text("upload for real")) }
				</lift:children>
			}
			sendStickyMessage(beginForRealMessage)
		}
		case ImportSucceeded(msg) => {
			sendStickyMessage(Text(msg))
			fileContent = Empty
		}
		case ImportFailed(msg) => {
			sendStickyMessage(Text(msg))
			fileContent = Empty
		}
	}
}