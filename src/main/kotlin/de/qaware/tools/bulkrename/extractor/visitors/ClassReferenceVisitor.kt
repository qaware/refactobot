package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.type.ClassOrInterfaceType
import de.qaware.tools.bulkrename.extractor.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span
import de.qaware.tools.bulkrename.model.reference.JavaQualifiedTypeReference
import de.qaware.tools.bulkrename.model.reference.JavaSimpleTypeReference

/**
 * Visitor for class references.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassReferenceVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(n: ClassOrInterfaceType?, arg: Unit) {
        if (n != null) {

            fun fail(msg: String): Nothing =
                    throw UnsupportedOperationException("%s: %s (%d:%d-%d:%d) in file %s"
                            .format(msg, n.name, n.begin.line, n.begin.column, n.end.line, n.end.column, context.getCurrentFile()))

            val fullName = (if (n.scope != null) n.scope.toString() + "." else "") + n.name

            if (n.typeArguments.typeArguments.isNotEmpty() && n.begin.line != n.end.line) {
                fail("Multiline usage of generic types is not supported")
            }
            if (n.typeArguments.typeArguments.isNotEmpty() && n.typeArguments.typeArguments.first().begin.column - (n.begin.column + fullName.length) != 1) {
                fail("Spaces in generic type references are not supported")
            }

            // extract span
            val endLocation =
                    if (n.typeArguments.typeArguments.isEmpty()) Location.oneBased(n.end.line, n.end.column + 1)
                    else Location.oneBased(n.typeArguments.typeArguments.first().begin.line, n.typeArguments.typeArguments.first().begin.column - 1)
            val span = Span(n.begin.toLocation(), endLocation)

            if (n.scope != null) {
                val target = context.getFileForClass(fullName)
                if (target != null) {
                    emit(JavaQualifiedTypeReference(context.getCurrentFile(), target, span))
                }
            } else {
                val target = context.getFileForImportedClass(fullName)
                if (target != null) {
                    emit(JavaSimpleTypeReference(context.getCurrentFile(), target, span))
                }
            }
        }
        super.visit(n, arg)
    }


}