package com.bbrownsound.macros

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class ToStringObfuscate(fieldsToObfuscate: String*) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ToStringObfuscateImpl.impl
}

// Companion object supplies implementation of the macro
object ToStringObfuscateImpl {

  def obfuscateValue(value: String) = "*" * value.length

  // TODO explain whitebox vs blackbox context
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def extractAnnotationParameters(tree: Tree): List[c.universe.Tree] = tree match {
      case q"new $name(..$params)" => params
      case _ => throw new Exception("ToStringObfuscate annotation must have at least one parameter.")
    }

    // deconstructing things
    def extractCaseClassesParts(classDecl: ClassDef) = classDecl match {
      case q"case class $className(..$fields) extends ..$parents { ..$body}" =>
        (className, fields, parents, body)
    }

    def replaceCaseClassSensitiveValues(tree: Tree) =  tree match {
      case Literal(Constant(field: String)) =>
        q"""
          ${TermName(field)} = com.bbrownsound.macros.ToStringObfuscateImpl.obfuscateValue(this.${TermName(field)})
        """
      case _ => c.abort(c.enclosingPosition, s"[obfuscateValue] Match error with $tree")
    }

    val sensitiveFields = extractAnnotationParameters(c.prefix.tree)

    val fieldReplacements = sensitiveFields map (replaceCaseClassSensitiveValues(_))

    def extractNewToString(sensitiveFields: List[Tree]) = q"""
       override def toString: ${typeOf[String]} = {
        scala.runtime.ScalaRunTime._toString(this.copy(..$fieldReplacements))
       }
    """

    def modifiedDeclaration(classDecl: ClassDef): c.Expr[Any] = {
      val (className, fields, parents, body) = extractCaseClassesParts(classDecl)
      val newToString = extractNewToString(sensitiveFields)

      val params = fields.asInstanceOf[List[ValDef]] map {p => p.duplicate}

      // $ acts here as unquoting this basically constructs a new class def for us
      c.Expr[Any](
        q"""
        case class $className (..$params) extends ..$parents {
          $newToString
          ..$body
        }
        """
      )
    }

    annottees map (_.tree) toList match {
      // add support for case classes
      case (classDecl: ClassDef) :: Nil => modifiedDeclaration(classDecl)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
