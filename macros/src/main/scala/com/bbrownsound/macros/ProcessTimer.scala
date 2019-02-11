package com.bbrownsound.macros

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class ProcessTimer extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro ProcessTimer.impl
}

object ProcessTimer {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods def $methodName[..$tpes](...$args): $returnType = { ..$body }" :: Nil => {
          q"""$mods def $methodName[..$tpes](...$args): $returnType =  {
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
          }"""
        }
        case _ => c.abort(c.enclosingPosition, "Annotation @ProcessTimer can be used only with methods")
      }
    }
    c.Expr[Any](result)
  }
}
