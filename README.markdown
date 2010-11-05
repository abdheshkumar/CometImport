# A Comet-based pattern for importing files, written in Lift.

This project is a proof-of-concept for a file uploading paradigm using Comet.

The [/index][index] page is a form for choosing and uploading a file. When a file is chosen, it is submitted as form-data to the [/comet-import][comet-import] page, which:

1. extracts the file;
2. starts a [`CometActor`][comet-actor] to display progress;
3. spawns a thread that processes the file line-by-line, sending progress reports to the `CometActor`.

The above is an instantiation of an importer pattern, comprising:
* [`ImportListener`][import-listener], a trait that provides some basic progress reporting tools;
* [`Importer`][importer], a trait that provides synchronous and asynchronous importing facilities, reporting to an [`ImportListener`][import-listener];
* 

The specific incarnation deployed in Lift uses a [`DummyImporter`][dummy-importer], which simply pulls the file apart line-by-line and sends each line to the [`CometActor`][comet-actor] as a message (with a short delay). However, the pattern can be applied by subclassing [`Importer`][importer] with the desired behaviour.

[index] https://github.com/junglebarry/CometImport/blob/master/src/main/webapp/index.html
[comet-import] https://github.com/junglebarry/CometImport/blob/master/src/main/webapp/comet-upload.html
[importer] https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/Importer.scala
[import-listener] https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/ImportListener.scala
[dummy-importer] https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/Importer.scala
[comet-actor] http://scala-tools.org/mvnsites-snapshots/liftweb/scaladocs/net/liftweb/http/CometActor.html