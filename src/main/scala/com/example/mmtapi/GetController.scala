package com.example.mmtapi

import java.io.File
import info.kwarc.mmt.api.frontend.{ConsoleHandler, Controller}
import info.kwarc.mmt.api.utils.FilePath

object GetController {
  def get(): Controller = {
    val ctrl = new Controller()

    ctrl.report.addHandler(ConsoleHandler)
    ctrl.addArchive(new File("/Users/ksb/IdeaProjects/math/MMT/urtheories"))
    val urtheoriesArchive = ctrl.backend.getArchive("MMT/urtheories").get
    urtheoriesArchive.allContent
    urtheoriesArchive.readRelational(FilePath("/"), ctrl, "rel")

    ctrl.addArchive(new File("/Users/ksb/IdeaProjects/math/Test"))
    val TESTArchive = ctrl.backend.getArchive("TEST").get
    TESTArchive.allContent
    TESTArchive.readRelational(FilePath("/"), ctrl, "rel")
    return ctrl
  }
}
