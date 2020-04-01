import java.io.File

import info.kwarc.mmt.api.frontend.{ConsoleHandler, Controller}
import info.kwarc.mmt.api.ontology.IsTheory
import info.kwarc.mmt.api.utils.FilePath

object Test {
  def main(args: Array[String]): Unit = {
    val ctrl = new Controller()
    // All logging goes to console
    ctrl.report.addHandler(ConsoleHandler)

    val mmtArchiveHome = ctrl.getHome / "archives" / "MathHub" / "MMT"
    ctrl.addArchive(mmtArchiveHome / "urtheories")

    // The identifier "MMT/urtheories" is specified in "MMT/urtheories/META-INF/MANIFEST.MF"
    // In general every archive specified its ID there.
    val urtheoriesArchive = ctrl.backend.getArchive("MMT/urtheories").get
    // The next two lines trigger processing of the whole archive and make the data
    // available in ctrl.depstore, the dependency store - among others.
    urtheoriesArchive.allContent
    urtheoriesArchive.readRelational(FilePath("/"), ctrl, "rel")

    // Get and print all individual ("inds") objects which are a theory
    val theories = ctrl.depstore.getInds(IsTheory)
    theories foreach println
  }
}
