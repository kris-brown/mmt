package com.example.mmtapi

import java.io.PrintWriter

import scala.collection.mutable.ArrayBuffer
import info.kwarc.mmt.api.modules.Theory
import info.kwarc.mmt.api.frontend.Controller
import info.kwarc.mmt.api.ontology.{Edge, EdgeTo, IsTheory, StructureEdge, TheoryGraph, Unary, ViewEdge, IncludeEdge => IE}
import scalax.collection.Graph
import scalax.collection.edge.LkDiHyperEdge
import scalax.collection.io.dot._
import scalax.collection.edge.Implicits._
import info.kwarc.mmt.api.archives.Relational
import info.kwarc.mmt.api.parser.HasParentInfo

import scala.collection.mutable
import sys.process._
import info.kwarc.mmt.api.{DPath, LocalName, MPath, Path}
import info.kwarc.mmt.api.utils.URI


object MakeGraph {

  private def edgeToString(e:Edge) : String = {
    if (e.isInstanceOf[StructureEdge]) {
      return e.asInstanceOf[StructureEdge].id.last
    } else if (e.isInstanceOf[ViewEdge]) { return e.asInstanceOf[ViewEdge].id.last}
    else { return " "}
  }

  def test(ctrl : Controller): (Graph[String,LkDiHyperEdge],
                                Map[(String,String,String),Edge]) = {

    val tg = new TheoryGraph(ctrl.depstore)
    val edgemap = new mutable.HashMap[(String, String, String),Edge]()

    // Nodes

    val theories = tg.nodes.toList
    // Edges
    val edges = ArrayBuffer[LkDiHyperEdge[String]]()
    for( fromPath  <- theories.sortBy(x=>x.toString()).reverse){
     for ((toPath:Path, es:List[EdgeTo]) <- tg.edgesFrom(fromPath)) {
       for (e <- es) {
         if (!e.backwards) {
           val tup = (fromPath.last, toPath.last, edgeToString(e.edge))
           edgemap+=((tup,e.edge))
           val e2: LkDiHyperEdge[String] = LkDiHyperEdge(fromPath.last, toPath.last)(tup)
           edges+=e2
         }
      }
     }
    }

    val g : Graph[String, LkDiHyperEdge] = Graph.from(tg.nodes.map(x=>x.last).toList,edges)
    val drg=DotRootGraph(directed = true, id = Some(Id("ROOT")))

    def edgeTransformer(innerEdge: Graph[String, LkDiHyperEdge]#EdgeT): Option[(DotGraph, DotEdgeStmt)] = {
      val (from,to,label) = innerEdge.edge.label.asInstanceOf[(String,String,String)]
      val edge : Edge = edgemap.get((from,to,label)).get
      println("FROM ", from, "  TO ", to)
      Some(drg, DotEdgeStmt(NodeId(from), NodeId(to),
          List(DotAttr(Id("label"), Id(label)),
               DotAttr(Id("style"),Id(if (edge.isInstanceOf[ViewEdge]) "dotted" else "solid"))
          )))
    }

    def nodeTransformer(innerNode: Graph[String, LkDiHyperEdge]#NodeT): Option[(DotGraph, DotNodeStmt)] =

      Some(
        (DotSubGraph(ancestor = drg, subgraphId = Id(innerNode.value),
                     attrList = List(DotAttr(Id("rank"), Id("same")))),
          DotNodeStmt(NodeId(innerNode.value), Seq.empty[DotAttr])))


    val d = g.toDot(dotRoot=drg,

      edgeTransformer = edgeTransformer,
      cNodeTransformer = Some(nodeTransformer),
      )
    val x6 = new PrintWriter("/Users/ksb/scp_tmp/test3.dot") { write(d); close }
    val x7 = "dot -Tpng /Users/ksb/scp_tmp/test3.dot -o /Users/ksb/scp_tmp/test3.png" !
    val x8 = "open /Users/ksb/scp_tmp/test3.png" !

    val zz = g.get("A").edges.toList(3).edge.label
    return (g, edgemap.toMap)

  }
}
