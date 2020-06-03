package com.example.mmtapi

import language.implicitConversions
import scalax.collection.Graph
import scalax.collection.GraphPredef.EdgeAssoc
import scalax.collection.edge.LDiEdge
import scalax.collection.edge.Implicits.any2XEdgeAssoc
import scalax.collection.io.dot.{Indent, DotGraph, AttrSeparator, DotAttr, DotEdgeStmt, DotNodeStmt, DotRootGraph, DotSubGraph, Id, NodeId, Record, Spacing}
import scalax.collection.io.dot._
import java.io.PrintWriter
import sys.process._

object TExportTest  {
  def main(args: Array[String]):Unit = {
    test1()
    test2()
    println("DONE")
  }
  def test1() : Unit = {
    val g = Graph[String, LDiEdge](
      ("A1" ~+> "A2") ("f"),
      ("A2" ~+> "A3") ("g"),
      ("A1" ~+> "B1") (""),
      ("A1" ~+> "B1") (""),
      ("A2" ~+> "B2") ("(g o f)'"),
      ("A3" ~+> "B3") (""),
      ("B1" ~+> "B3") (""),
      ("B2" ~+> "B3") ("g'"))

    val root = DotRootGraph(directed = true, id = Some(Id("Wikipedia_Example")))
    val subA = DotSubGraph(ancestor = root, subgraphId = Id("A"), attrList = List(DotAttr(Id("rank"), Id("same"))))
    val subB = DotSubGraph(ancestor = root, subgraphId = Id("B"), attrList = List(DotAttr(Id("rank"), Id("same"))))

    def edgeTransformer(innerEdge: Graph[String, LDiEdge]#EdgeT): Option[(DotGraph, DotEdgeStmt)] = {
      val edge = innerEdge.edge
      val label = edge.label.asInstanceOf[String]
      Some(
        root,
        DotEdgeStmt(
          NodeId(edge.from.toString),
          NodeId(edge.to.toString),
          if (label.nonEmpty) List(DotAttr(Id("label"), Id(label)))
          else Nil))
    }

    def nodeTransformer(innerNode: Graph[String, LDiEdge]#NodeT): Option[(DotGraph, DotNodeStmt)] =
      Some(
        (if (innerNode.value.head == 'A') subA else subB, DotNodeStmt(NodeId(innerNode.toString), Seq.empty[DotAttr])))

    val dot = g.toDot(
      dotRoot = root,
      edgeTransformer = edgeTransformer,
      cNodeTransformer = Some(nodeTransformer),
      spacing = multilineCompatibleSpacing)

    println("WRITING DOT")
    val x0 = new PrintWriter("/Users/ksb/scp_tmp/test.dot") { write(dot); close }
    val x1 = "dot -Tpng /Users/ksb/scp_tmp/test.dot -o /Users/ksb/scp_tmp/test.png" !
    val x2 = "open /Users/ksb/scp_tmp/test.png" !

    val (expected_1, expected_2) = {
      val expected_header_sorted =
        """digraph Wikipedia_Example {
          |  A1 -> A2 [label = f]
          |  A1 -> B1
          |  A2 -> A3 [label = g]
          |  A2 -> B2 [label = "(g o f)'"]
          |  A3 -> B3
          |  B1 -> B3
          |  B2 -> B3 [label = "g'"]""".stripMargin
      val expected_footer =
        """
          |}""".stripMargin
      val expected_sub_A_sorted =
        """
          |  subgraph A {
          |    A1
          |    A2
          |    A3
          |    rank = same
          |  }""".stripMargin
      val expected_sub_B_sorted =
        """
          |  subgraph B {
          |    B1
          |    B2
          |    B3
          |    rank = same
          |  }""".stripMargin
      (
        expected_header_sorted + expected_sub_A_sorted + expected_sub_B_sorted + expected_footer,
        expected_header_sorted + expected_sub_B_sorted + expected_sub_A_sorted + expected_footer)
    }
    val x3 = new PrintWriter("/Users/ksb/scp_tmp/test2.dot") { write(expected_1); close }
    val x4 = "dot -Tpng /Users/ksb/scp_tmp/test2.dot -o /Users/ksb/scp_tmp/test2.png" !
    val x5 = "open /Users/ksb/scp_tmp/test2.png" !
    val x6 = new PrintWriter("/Users/ksb/scp_tmp/test3.dot") { write(expected_2); close }
    val x7 = "dot -Tpng /Users/ksb/scp_tmp/test3.dot -o /Users/ksb/scp_tmp/test3.png" !
    val x8 = "open /Users/ksb/scp_tmp/test3.png" !

  }


  def test2() : Unit = {
    val hg   = Graph(1 ~> 2 ~> 3)
    val root = DotRootGraph(directed = true, id = None)
    val dot = hg.toDot(
      dotRoot = root,
      edgeTransformer = e => None,
      hEdgeTransformer = Some(h => {
        val source = h.edge.source.toString
        h.edge.targets map (target => (root, DotEdgeStmt(NodeId(source), NodeId(target.toString))))
      }),
    )
    println(dot)
  }


  private val multilineCompatibleSpacing = Spacing(
    indent = Indent.TwoSpaces,
    graphAttrSeparator = new AttrSeparator("""
                                             |""".stripMargin) {})

  private def sortMid(dot: String): String = {
    val lines = dot.linesWithSeparators.toBuffer
    val mid   = lines.tail.init
    s"${lines.head}${mid.sorted.mkString}${lines.last}"
  }
}

case class Node(id: Id, label: Record.RLabel)
