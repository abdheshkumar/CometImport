# A Comet-based pattern for importing files, written in Lift.

This project is a proof-of-concept for a file uploading paradigm using Comet.

The `/index` page is a form for choosing and uploading a file. When a file is chosen, it is submitted to the `/comet-import` page, which:

1. extracts the file;
2. starts a `CometActor` to display progress;
3. spawns a thread that processes the file line-by-line, sending progress reports to the `CometActor`.

The above is an instantiation of an importer pattern, comprising:
* `ImportListener`, a trait that provides some basic progress reporting tools;
* `Importer`, a trait that provides synchronous and asynchronous importing facilities, reporting to an `ImportListener`;
* 

The specific incarnation deployed in Lift uses a `DummyImporter`, which simply pulls the file apart line-by-line and sends each line to the `CometActor` as a message (with a short delay). However, the pattern can be applied by subclassing `Importer` with the desired behaviour.