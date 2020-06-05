package com.example.mmtapi
import scala.io.Source
import java.nio.file.{Files, Paths}

import scala.collection.mutable.Queue
import info.kwarc.mmt.api.{DPath, LocalName, MPath, Path}
import info.kwarc.mmt.api.frontend.Controller
import info.kwarc.mmt.api.modules.Theory
import info.kwarc.mmt.api.utils.URI
import info.kwarc.mmt.api.ontology.{EdgeTo, IncludeEdge, TheoryGraph, ViewEdge}
import info.kwarc.mmt.api.ontology.Parse

import scala.collection.mutable


object Query {
  type TPath = List[(Path, List[EdgeTo])]
  type TPaths = List[TPath]

  def main(args: Array[String]): Unit = {
    assert(Files.exists(Paths.get(args(0)))) // Check path to inputs
    val Array(path : String,
              project_id:String,
              mname : String,
              root : String,
              qkind : String,
              tags : String,
              depth: String
             ) = Source.fromFile(args(0)).getLines().toArray


    val ctrl : Controller = GetController.get()
    val tg = new TheoryGraph(ctrl.depstore)
    val nodemap = new mutable.HashMap[String,Theory]()

    //ctrl.getTheory(tg.nodes.toList(6).asInstanceOf[MPath]) // THIS (from Test folder) IS NOT FOUND
    // BUT IF I ENTER DEBUGGER HERE AND EXECUTE SAME LINE OF CODE, IT SUCCEEDS
    for (t<-tg.nodes) {
      //nodemap+=((t.last,ctrl.getTheory(t.asInstanceOf[MPath])))
    }
    val nm = nodemap.toMap
    val t :Theory= ctrl.getTheory(tg.nodes.toList(0).asInstanceOf[MPath])
    println(t)
    val includeStructure: Boolean = qkind match  {
      case "partial" => true
      case "full" => false
      case _ => throw new Exception("Bad value for query kind arg: "+qkind)
    }
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

    while  (tpaths.nonEmpty && seen.size < depth.toInt) {
      val currentPath = tpaths.dequeue()
      val currentNode : Path = tg.codomain(currentPath.last._1).getOrElse(currentPath.last._1)
      for (e <- tg.edgesFrom(currentNode)
           if !e._2(0).backwards;
           if includeStructure || e._2(0).edge.isInstanceOf[ViewEdge]) {
        val newPath = currentPath ::: List(e)
        seen += newPath
        tpaths.enqueue(newPath)
      }
    }

    val (g,em) = MakeGraph.test(ctrl)
    //val A = ctrl.getTheory(mpth ? LocalName.parse(root))
    println("DONE")
  }
}

// a+b⋅c <- OMA(OMS(+),OMS(a),OMA(⋅,OMS(b),OMA(c)))
// λx:t. x+a <- OMBIND({x:t},OMA(+,OMV(x),OMS(a)))
