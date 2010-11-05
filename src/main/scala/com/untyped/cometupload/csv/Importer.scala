package com.untyped.cometupload.csv

import net.liftweb.http.FileParamHolder

trait Importer {
	/*
	* Internal exception subclasses for rolling backa preview and cancelling an import
	*/
	private class PreviewCompleteException(msg: String) extends Exception(msg)
	private class CancelledException(msg: String) extends Exception(msg)
	
	/*
	* Control variables for the import process.
	*/
	private var cancelled: Boolean = false
	private var running: Boolean = false
	
	/*
	* @return true if this import is running; false otherwise
	*/
	final def isRunning = running
	
	/*
	* Interrupts and cancels the current import.
	*/
	final def cancel { cancelled = true }
	
	/*
	* Used to stop an import process if a cancel command has been issued.
	*/
	final def stopIfCancelled {
		if(cancelled) throw new CancelledException("Import cancelled")
	}
	
	/*
	* Performs an asynchronous import of the given fph content.
	* Starts a new syncImport inside a new Thread.
	*
	* @param fph A FileParamHolder containing the file contents.
	* @param listener An ImportListener object that listens to the current import process.
	* @param preview Set to true to abort the current transaction on import complete (rollback).
	*/
	final def asyncImport(fph: FileParamHolder, listener: ImportListener, preview: Boolean) = {
		new Thread() {
			override def run() { syncImport(fph, listener, preview) }
		}.start()
	}
	
	/*
	* Performs a synchronous import of the given fph content.
	*
	* @param fph A FileParamHolder containing the file contents.
	* @param listener An ImportListener object that listens to the current import process.
	* @param preview Set to true to abort the current transaction on import complete (rollback).
	*/
	final def syncImport(fph: FileParamHolder, listener: ImportListener, preview: Boolean): Boolean = {
		try {
			running = true
			cancelled = false
			withTransaction { 
				listener.importStarted(if(preview) "Importing in preview mode" else "Importing")
				doImport(fph, listener)
				// rollback, if in preview mode
				if(preview) throw new PreviewCompleteException("Preview complete")
				stopIfCancelled
				listener.importSucceeded("w00t!")
				true
			}
		} catch {
			case exn: PreviewCompleteException => {
				listener.importPreviewSucceeded(exn.getMessage)
				true
			}
			case exn: Exception => {
				listener.importFailed(exn.getMessage)
				false
			}
		} finally {
			running = false
		}
	}
	
	/*
	* Must be overridden - defines the importing behaviour.
	*
	* @param fph A FileParamHolder containing the file contents.
	* @param listener An ImportListener that listens for notifications during the import process.
	*/
	def doImport(fph: FileParamHolder, listener: ImportListener)
	
	/*
	* Override this if you need some kind of transaction capabilities.
	*/
	def withTransaction[T](f : => T): T = f
}
