package com.untyped.cometupload.csv

/* 
* An example Importer that simply grabs a File line by line,
* and sends each line as a message to the ImportListener.
*/
class DummyImporter extends Importer {
	def doImport(fph: net.liftweb.http.FileParamHolder, listener: ImportListener) {
		val lines = scala.io.Source.fromBytes(fph.file).getLines.toList
		lines.zipWithIndex.foreach {
			case (line: String, number: Int) => {
				stopIfCancelled
				listener.importProgress((number+1) + ": " + line)
				Thread.sleep(200)
			}
		}
	}
}