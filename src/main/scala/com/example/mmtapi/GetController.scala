package com.example.mmtapi

import java.io.File
import info.kwarc.mmt.api.frontend.{ConsoleHandler, Controller}
import info.kwarc.mmt.api.utils.FilePath

object GetController {
  def get(pathname:String, archname:String): Controller = {
    val ctrl = new Controller()
    // All logging goes to console
    ctrl.report.addHandler(ConsoleHandler)
    val root = new File(pathname)
    ctrl.addArchive(root)
    val TUTORIALArchive = ctrl.backend.getArchive(archname).get
    TUTORIALArchive.allContent
    TUTORIALArchive.readRelational(FilePath("/"), ctrl, "rel")
    return ctrl
  }
}
