package com.bbrownsound.macros

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox

class ProcessTimer extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ProcessTimerImpl.impl
}

object ProcessTimerImpl {
  def impl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[c.universe.DefDef] = {
    import c.universe._

    annottees.map(_.tree).toList match {
      case q"$mods def $methodName[..$tpes](...$args): $returnType = { ..$body }  " :: Nil => {
        c.Expr[c.universe.DefDef](q"""$mods def $methodName[..$tpes](...$args): $returnType =  {
            import java.time.{Duration, LocalDateTime, ZoneOffset}
            import org.slf4j.LoggerFactory
            val start = LocalDateTime.now(ZoneOffset.UTC)
            val result = {..$body}
            val end = LocalDateTime.now(ZoneOffset.UTC)
            val runtime = Duration.between(start, end)
            val logger = LoggerFactory.getLogger("com.bbrownsound.macros.ProcessTimer")
            val cp = ${c.enclosingPosition.source.path}.replaceAll(".*\\/scala\\/", "").replaceAll(".(scala|java)$$", "").replaceAll("/", ".")
            logger.info("%s.%s elapsed time: %s".format(cp, ${methodName.toString}, runtime.toPretty))
            result
          }""")
      }
      case _ => c.abort(c.enclosingPosition, "Annotation @ProcessTimer can be used only with methods")
    }
  }
}
