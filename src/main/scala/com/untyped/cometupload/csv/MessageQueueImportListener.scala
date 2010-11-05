package com.untyped.cometupload.csv

/*
* A listener that just accumulates messages into a List.
*/
class MessageQueueImportListener extends ImportListener {
	private var _messages: List[String] = Nil
	
	def messages = _messages.reverse
	
	def importProgress(msg: String) = _messages = msg :: _messages
	def importStarted(msg: String) = _messages = msg :: _messages
	def importSucceeded(msg: String) = _messages = msg :: _messages
	def importFailed(msg: String) = _messages = msg :: _messages
	def importPreviewSucceeded(msg: String) = _messages = msg :: _messages
}
