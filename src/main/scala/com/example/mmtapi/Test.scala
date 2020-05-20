import java.io.File

import info.kwarc.mmt.api.frontend.{ConsoleHandler, Controller}
import info.kwarc.mmt.api.ontology.{IsTheory, IsView, TheoryGraph}
import info.kwarc.mmt.api.utils.FilePath

object Test {
  def main(args: Array[String]): Unit = {
    val ctrl = new Controller()
    // All logging goes to console
    ctrl.report.addHandler(ConsoleHandler)
    val root = new File("/Users/ksb/IdeaProjects/testmhub/MMT_archives/MMT/tutorial")
    ctrl.addArchive(root)
    val TUTORIALArchive = ctrl.backend.getArchive("MMT/TUTORIAL").get
    TUTORIALArchive.allContent
    TUTORIALArchive.readRelational(FilePath("/"), ctrl, "rel")
    val theories = ctrl.depstore.getInds(IsTheory)
    val views = ctrl.depstore.getInds(IsView)
    val tg = new TheoryGraph(ctrl.depstore)
    val nodes = Set("Monoid", "NonGrpMon", "NatPlusTimes")
    val Array(mon, npt, ngm) = tg.nodes.toArray.sortBy(x => x.last).filter(x => {nodes.contains(x.last)})
    val edgesOutOfMonoid = tg.edgesFrom(mon)

    println("Theories")
    theories foreach println
    println("VIEWS")
    views foreach println
    println("Edges out of Monoid")
    edgesOutOfMonoid foreach println

    println("END")


  }
}
