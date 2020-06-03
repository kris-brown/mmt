package com.example.mmtapi
import scala.io.Source
import java.nio.file.{Files, Paths}

import scala.collection.mutable.Queue
import info.kwarc.mmt.api.{DPath, LNStep, LocalName, MPath, Path}
import scalax.collection.edge.LDiEdge
import info.kwarc.mmt.api.frontend.Controller
import scalax.collection.{Graph, GraphTraversal}
import scalax.collection.GraphTraversal.BreadthFirst
import info.kwarc.mmt.api.utils.URI
import info.kwarc.mmt.api.ontology.{EdgeTo, TheoryGraph, ViewEdge}


object Query {
  type TPath = List[(Path, List[EdgeTo])]
  type TPaths = List[TPath]

  def main(args: Array[String]): Unit = {
    assert(Files.exists(Paths.get(args(0)))) // Check path to inputs
    val Array(pth : String, aname:String, mname : String, root : String, qkind : String, tags : String, depth: String
             ) = Source.fromFile(args(0)).getLines().toArray
    val ctrl : Controller = GetController.get(pth,aname)
    val includeStructure: Boolean = qkind match  {
      case "partial" => true
      case "full" => false
      case _ => throw new Exception("Bad value for query kind arg: "+qkind)
    }
    val tg = new TheoryGraph(ctrl.depstore)
    val uri : URI = URI.apply("http://cds.omdoc.org")
    val mpth : DPath = new DPath(uri) / LocalName.parse(mname)
    val tpaths = new Queue[TPath]
    var seen : Set[TPath] = Set()

    for (e <- tg.edgesFrom(mpth ? LocalName.parse(root))
          if !e._2(0).backwards;
          ee = e._2.head.edge;
          if includeStructure || ee.isInstanceOf[ViewEdge]) {
      seen += List(e)
      tpaths.enqueue(List(e))
    }
    println("t ", tpaths.nonEmpty, "s", seen.size, "d", depth.toInt)

    while  (tpaths.nonEmpty && seen.size < depth.toInt) {
      val currentPath = tpaths.dequeue()
      val currentNode : Path = tg.codomain(currentPath.last._1).getOrElse(currentPath.last._1)
      println("\n\tcurrentPath.last._1", currentPath.last._1, "\n\tcurrentNode ", currentNode)
      for (e <- tg.edgesFrom(currentNode)
           if !e._2(0).backwards;
           if includeStructure || e._2(0).edge.isInstanceOf[ViewEdge]) {
        val newPath = currentPath ::: List(e)
        println("currPath size ", currentPath.size,"Newpath size ", newPath.size)
        seen += newPath
        tpaths.enqueue(newPath)
      }
    }

    MakeGraph.test(ctrl,false)
    println("DONE")
  }
}
