package com.untyped.cometupload.csv

trait Importer {
	
	private class PreviewCompleteException(msg: String) extends Exception(msg)
	private class CancelledException(msg: String) extends Exception(msg)
	
	private var cancelled: Boolean = false
	private var running: Boolean = false
	
	final def isRunning = running
	
	final def cancel { cancelled = true }
	
	final def stopIfCancelled {
		if(cancelled) throw new CancelledException("Import cancelled")
	}
	
	final def asyncImport(lines: List[String], listener: ImportListener, preview: Boolean) = {
		new Thread() {
			override def run() { 
				withTransaction { 
					syncImport(lines, listener, preview) 
				}
			}
		}.start()
	}
	
	final def syncImport(lines: List[String], listener: ImportListener, preview: Boolean): Boolean = {
		try {
			running = true
			cancelled = false
			withTransaction { 
				listener.importStarted("Import started")
				doImport(lines, listener)
				if(preview) throw new PreviewCompleteException("Preview complete")
				stopIfCancelled
				listener.importSucceeded("W00t!")
				true
			}
		} catch {
			case exn: PreviewCompleteException => {
				listener.importSucceeded(exn.getMessage)
				true
			}
			case exn: Exception => {
				listener.importFailed(exn.getMessage)
				false
			}
		} finally {
			println("finally")
			running = false
		}
	}
	
	def doImport(lines: List[String], listener: ImportListener)
	
	def withTransaction[T](f : => T): T = f
	
}

class DummyImporter extends Importer {
	
	def doImport(lines: List[String], listener: ImportListener) {
		lines.foreach { line =>
			stopIfCancelled
			listener.importProgress(line)
			Thread.sleep(1000)
		}
	}
	
}
