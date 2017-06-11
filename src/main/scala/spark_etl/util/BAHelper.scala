package spark_etl.util

import java.io.{File, PrintWriter}

import scala.io.Source

object BAHelper {
  def copySqls(sourceDir: File, targetDir: File): Seq[(String, String)] = {
    rmdir(targetDir)
    val sqlFiles = descendants(sourceDir).filter(_.getName.toLowerCase.endsWith(".sql"))
    sqlFiles.map {
      f =>
        val contents = Source.fromFile(f).mkString
        val target = new File(targetDir, f.getAbsolutePath.replace(sourceDir.getAbsolutePath, ""))
        val targetParent = target.getParentFile
        targetParent.mkdirs()
        new PrintWriter(target) {
          // FIXME: transform
          val contents2 = "-- dev version\n" + contents
          write(contents2)
          close()
        }
        (f.getPath, target.getPath)
    }
  }

  private def descendants(f: File): Seq[File] = {
    val children = f.listFiles
    if (children == null)
      Nil
    else
      children ++ children.filter(_.isDirectory).flatMap(descendants)
  }

  private def rmdir(file: File): Unit = {
    if (file.isDirectory)
      file.listFiles.foreach(rmdir)
    if (file.exists && !file.delete)
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
  }
}
