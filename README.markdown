# A Comet-based pattern for importing files, written in Lift.

This project is a proof-of-concept for a file uploading paradigm using Comet. Essentially, you upload a file to the site, and it reports import progress via Comet.

## The demo app.

... runs on port 8765 by default. Change by overriding project/build/Project.scala jettyPort

The [index.html][index] page is a form for choosing and uploading a file. When a file is chosen, it is submitted as form-data to the [comet-import.html][comet-import] page, which:

1. extracts the file;
2. starts a [`CometActor`][comet-actor] to display progress;
3. spawns a thread (via the [`ChooseFile`][choose-file] snippet) that processes the file line-by-line, sending progress reports to the [`CometActor`][comet-actor].

The import process also has a preview mode, which is turned on here. Essentially, the file is uploaded in preview mode to begin with, and progress is reported. Once the upload is complete, the user is presented with the option to import the file again, but this time "for real". Either import processed can be cancelled at any time.

## Generic pattern for creating an importer.

The above is an instantiation of an importer pattern, comprising:

 * [`ImportListener`][import-listener], a trait that provides some basic progress reporting tools;
 * [`Importer`][importer], a trait that provides synchronous and asynchronous importing facilities, reporting to an [`ImportListener`][import-listener];

This pattern can be applied by subclassing [`Importer`][importer] with the desired behaviour, and providing a (custom) instance of [`ImportListener`][import-listener].

## More details on how the pattern is used in the demo

The specific incarnation deployed in this example Lift webapp uses a [`DummyImporter`][dummy-importer], which simply pulls the file apart line-by-line and sends each line to a [`ImportListener`][import-listener] as a message (with a short delay). 

Two [`ImportListener`][import-listener] subtypes are provided: 

1. [`MessageQueueImportListener`][queue-listener], which accumulates messages into a list;
2. [`LiftActorImportListener`][actor-listener], which sends messages on to [`CometUpload`][comet-upload], our progress-reporting Comet-backed page.

[index]: https://github.com/junglebarry/CometImport/blob/master/src/main/webapp/index.html "index.html"
[comet-import]: https://github.com/junglebarry/CometImport/blob/master/src/main/webapp/comet-upload.html "comet-import.html"
[choose-file]: https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/snippet/ChooseFile.scala "ChooseFile"
[importer]: https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/Importer.scala "Importer"
[import-listener]: https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/ImportListener.scala "ImportListener"
[dummy-importer]: https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/DummyImporter.scala "DummyImporter"
[queue-listener]:  https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/MessageQueueImportListener.scala "MessageQueueImportListener"
[actor-listener]:  https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/csv/LiftActorImportListener.scala "LiftActorImportListener"
[comet-upload]: https://github.com/junglebarry/CometImport/blob/master/src/main/scala/com/untyped/cometupload/comet/CometUpload.scala "CometUpload"
[comet-actor]: http://scala-tools.org/mvnsites-snapshots/liftweb/scaladocs/net/liftweb/http/CometActor.html "CometActor"
