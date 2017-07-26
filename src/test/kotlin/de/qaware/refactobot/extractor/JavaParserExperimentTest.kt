package de.qaware.refactobot.extractor

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import org.junit.Test
import java.io.FileReader

/**
 * Simple test for javaparser library.
 *
 * Demonstrates the use of the API for parsing and syntax tree traversal.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class JavaParserExperimentTest {

    @Test
    @Throws(Exception::class)
    fun testJavaparser() {

        FileReader("src/test/resources/TestClass.java").use { reader ->

            val ast = JavaParser.parse(reader)
            val visitor = ImportDeclarationVisitor()
            visitor.visit(ast, Unit)
        }
    }

    /**
     * [VoidVisitorAdapter]
     */
    private class ImportDeclarationVisitor : VoidVisitorAdapter<Unit>() {

//        override fun visit(importDeclaration: ImportDeclaration, arg: Unit) {
//
//            println("Found import: " + importDeclaration.name + " at position " + importDeclaration.range)
//            println("Found import: " + importDeclaration.name + " at position " + importDeclaration.range)
//
//            super.visit(importDeclaration, arg)
//        }

        override fun visit(typeReference: ClassOrInterfaceType, arg: Unit) {

            println("Found class reference: " + typeReference.name + " at position " + typeReference.range)
            super.visit(typeReference, arg)
        }
    }

}