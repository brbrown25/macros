package com.bbrownsound.macros

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox

class Debug extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro DebugImpl.impl
}

object DebugImpl {
  def impl(c: blackbox.Context)(annottees: c.Expr[Any]*) = {
    import c.universe._
    annottees.map(_.tree).toList match {
      case (vd: ValDef) :: Nil => {
        c.Expr[c.universe.ValDef](q"""
            ..$vd
            println("[%s:%s] %s - %s".format(
              ${c.enclosingPosition.source.path}.replaceAll(".*\\/scala\\/", "").replaceAll(".(scala|java)$$", "").replaceAll("/", "."),
              ${c.enclosingPosition.line},
              ${vd.name.encodedName.toString},
              ${vd.name}))
            ()
          """)
      }
      case _ => c.abort(c.enclosingPosition, "Annotation @Debug can be used only with vals")
    }
  }
}
