package com.example.mmtapi

import java.io.PrintWriter

import scala.collection.mutable.ArrayBuffer
import info.kwarc.mmt.api.Path
import info.kwarc.mmt.api.frontend.Controller
import info.kwarc.mmt.api.ontology.{IsStructure, IsTheory, IsView, TheoryGraph, Unary}
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import scalax.collection.io.dot._
import sys.process._


object MakeGraph {
  def test(ctrl : Controller, onlyview : Boolean):Unit = {

    val e2:LDiEdge[Int] = LDiEdge(1,2)("foo")

    val tg = new TheoryGraph(ctrl.depstore)

    // Nodes
    val theories = ctrl.depstore.getInds(IsTheory).toIterable
    // Edges
    val edges = ArrayBuffer[LDiEdge[Path]]()
    val it = ctrl.depstore.getInds(IsView)
    val it2 = if (onlyview) List() else ctrl.depstore.getInds(IsStructure)
    for (p <- it ++ it2) {
      val e2:LDiEdge[Path] = LDiEdge(tg.domain(p).get,tg.codomain(p).get)(p)
      edges += e2
    }
    // val t = (g get currentNode).withKind(BreadthFirst).withMaxDepth(depth.toInt)
    val g : Graph[Path, LDiEdge] = Graph.from(theories,edges)
    val drg=DotRootGraph(directed = true, id = Some(Id("Wikipedia_Example")))
    val sub = DotSubGraph(ancestor = drg, subgraphId = Id("A"), attrList = List(DotAttr(Id("rank"), Id("same"))))

    def edgeTransformer(innerEdge: Graph[Path, LDiEdge]#EdgeT): Option[(DotGraph, DotEdgeStmt)] = {
      val edge  = innerEdge.edge
      val label : Path = edge.label.asInstanceOf[Path]
      val t : Unary = ctrl.depstore.getType(label).get
      Some(
        drg,
        DotEdgeStmt(
          NodeId(edge.from.value.last),
          NodeId(edge.to.value.last),
          List(DotAttr(Id("label"), Id(if (t == IsStructure)  " " else label.last)),
            DotAttr(Id("style"),Id(if (t == IsView) "dotted" else "solid"))
          )))
    }

    def nodeTransformer(innerNode: Graph[Path, LDiEdge]#NodeT): Option[(DotGraph, DotNodeStmt)] =

      Some(
        (DotSubGraph(ancestor = drg, subgraphId = Id(innerNode.toString()), attrList = List(DotAttr(Id("rank"), Id("same")))), DotNodeStmt(NodeId(innerNode.value.last), Seq.empty[DotAttr])))


    val d = g.toDot(dotRoot=drg,

      edgeTransformer = edgeTransformer,
      cNodeTransformer = Some(nodeTransformer),
      )
    val x6 = new PrintWriter("/Users/ksb/scp_tmp/test3.dot") { write(d); close }
    val x7 = "dot -Tpng /Users/ksb/scp_tmp/test3.dot -o /Users/ksb/scp_tmp/test3.png" !
    val x8 = "open /Users/ksb/scp_tmp/test3.png" !

  }
}
