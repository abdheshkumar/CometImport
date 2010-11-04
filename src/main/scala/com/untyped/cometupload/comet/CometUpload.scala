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
	
	override def render = <lift:children>
		<div class="file-info">
			File name: {fileContent.map(v => Text(v.fileName))}<br />
			MIME Type: {fileContent.map(v => Box.legacyNullTest(v.mimeType).map(Text).openOr(Text("No mime type supplied")))}<br />
			File length: {fileContent.map(v => Text(v.file.length.toString))}<br />
			MD5 Hash: {fileContent.map(v => Text(hexEncode(md5(v.file))))}<br />
		</div>
		<div id={stickyId} class="sticky-message">
			{sticky.openOr(Text("watver"))}
		</div>
		<div id={messagesId} class="comet-messages">
			{messages.flatMap(renderMessage _)}
		</div>
	</lift:children>

	private def sendSticky(msg: NodeSeq) {
		partialUpdate(SetHtml(stickyId, msg))
		sticky = msg
	}
	
	private def sendMessage(msg: NodeSeq) {
		partialUpdate(PrependHtml(messagesId, renderMessage(msg)))
		messages = msg :: messages
	}

	private def renderMessage(msg: NodeSeq): NodeSeq = <div>{msg}</div>

	override def lowPriority = {
		case InitImport(fph: FileParamHolder) => {
			println("import.running= " + importer.isRunning)
			if(!importer.isRunning) {
				println("InitImport " + fph)
				fileContent = Full(fph)
				messages = Nil
				importer.asyncImport(
					Source.fromBytes(fph.file).getLines.toList,
					new LiftActorImportListener(this),
					true)
				reRender(true)
			}
		}
		case ImportStarted(msg) => {
			val clearAndRestartMessage: NodeSeq = {
				<lift:children>
					{Text(msg)}
					{SHtml.a(() => { importer.cancel; Noop }, Text("cancel"))}
				</lift:children>
			}
			sendSticky(clearAndRestartMessage)
		}
		case ImportProgress(msg) => {
			sendMessage(<span>{msg}</span>)
		}
		case ImportSucceeded(msg) => {
			sendSticky(msg)
			fileContent = Empty
		}
		case ImportFailed(msg) => {
			sendSticky(msg)
			fileContent = Empty
		}
	}
}