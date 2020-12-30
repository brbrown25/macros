package com.bbrownsound.macros

import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox

class ToStringObfuscate(fieldsToObfuscate: Any*) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ToStringObfuscateImpl.impl
}

object ToStringObfuscateImpl {
  def obfuscateValue(value: Any): String = {
    value match {
      case v: String => "*" * v.length
      case x => "*" * x.toString.length
    }
  }

  def _toString(x: Product, hide: List[String]): String = {
//scala 2.13 makes this much easier
//    x.productElementNames
//      .zip(x.productIterator)
//      .map {
//        case (field, value) =>
//          hide
//            .find(_ == field)
//            .map(obfuscateValue)
//            .getOrElse(value)
//      }
//      .toList
//      .mkString(x.productPrefix + "(", ",", ")")
    x.getClass.getDeclaredFields
      .map { field =>
        field.setAccessible(true)
        val result = hide
          .find(_ == field.getName)
          .map { _ =>
            obfuscateValue(field.get(x))
          }
          .getOrElse(field.get(x))
        field.setAccessible(false)
        result
      }
      .toList
      .mkString(x.productPrefix + "(", ",", ")")
  }

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[c.universe.ClassDef] = {
    import c.universe._

    def extractAnnotationParameters(tree: Tree): List[c.universe.Tree] = tree match {
      case q"new $name(..$params)  " => params
      case _ => throw new Exception("ToStringObfuscate annotation must have at least one parameter.")
    }

    // deconstructing things
    def extractCaseClassesParts(classDecl: ClassDef) = classDecl match {
      case q"case class $className(..$fields) extends ..$parents { ..$body}  " =>
        (className, fields, parents, body)
    }

    def modifiedDeclaration(classDecl: ClassDef): c.Expr[ClassDef] = {
      val (className, fields, parents, body) = extractCaseClassesParts(classDecl)
      val sensitiveFields = extractAnnotationParameters(c.prefix.tree)
      val newToString = q"""
       override def toString: ${typeOf[String]} = {
        com.bbrownsound.macros.ToStringObfuscateImpl._toString(this, $sensitiveFields)
       }
      """

      val params = fields
        .collect { case vd: ValDef =>
          vd
        }
        .map(_.duplicate)

      //$ acts here as unquoting this basically constructs a new class def for us
      c.Expr[ClassDef](
        q"""
        case class $className (..$params) extends ..$parents {
          $newToString
          ..$body
        }
        """
      )
    }

    annottees.map(_.tree) toList match {
      // add support for case classes
      case (classDecl: ClassDef) :: Nil => modifiedDeclaration(classDecl)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
