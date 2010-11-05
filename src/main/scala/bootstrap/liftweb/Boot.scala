package bootstrap.liftweb

import _root_.net.liftweb.http.{LiftRules}
import _root_.net.liftweb.sitemap.{SiteMap, Menu, Loc}


class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.untyped.cometupload")

    // build sitemap
    val entries = List(
		Menu("Import a file") / "index", 
		Menu("Uploader") / "comet-upload" >> Loc.Hidden)
                  
    LiftRules.setSiteMap(SiteMap(entries:_*))

    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    
  }
}