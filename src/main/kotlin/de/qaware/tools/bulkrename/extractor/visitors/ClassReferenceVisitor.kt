package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.type.ClassOrInterfaceType
import de.qaware.tools.bulkrename.extractor.RawReference
import de.qaware.tools.bulkrename.extractor.ReferenceType
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor
import de.qaware.tools.bulkrename.model.codebase.File
import de.qaware.tools.bulkrename.model.operation.Location
import de.qaware.tools.bulkrename.model.operation.Span

/**
 * Visitor for class references.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassReferenceVisitor(val file: File) : ReferenceVisitor() {

    override fun visit(n: ClassOrInterfaceType?, arg: Unit) {
        if (n != null) {
            if (n.typeArguments.typeArguments.isNotEmpty() && n.begin.line != n.end.line) {
                // The range of the name is no longer correct for multiline generics, so we simply do not support it.
                throw UnsupportedOperationException("Multiline usage of generic types is not supported: %s (%d:%d-%d:%d) in file %s"
                        .format(n.name, n.begin.line, n.begin.column, n.end.line, n.end.column, file))
            }
            val fullName = (if (n.scope != null) n.scope.toString() + "." else "") + n.name
            if (n.typeArguments.typeArguments.isNotEmpty() && n.typeArguments.typeArguments.first().begin.column - (n.begin.column + fullName.length) != 1) {
                throw UnsupportedOperationException("Spaces in generic type references are not supported: %s (%d:%d-%d:%d) in file %s"
                        .format(n.name, n.begin.line, n.begin.column, n.end.line, n.end.column, file))
            }
            val type = if (n.scope != null) ReferenceType.FQ_CLASS_OR_INTERFACE_REFERENCE else ReferenceType.CLASS_OR_INTERFACE_REFERENCE
            val endLocation =
                    if (n.typeArguments.typeArguments.isEmpty()) Location.oneBased(n.end.line, n.end.column + 1)
                    else Location.oneBased(n.typeArguments.typeArguments.first().begin.line, n.typeArguments.typeArguments.first().begin.column - 1)
            emit(RawReference(type,
                    Span(Location.oneBased(n.begin.line, n.begin.column), endLocation),
                    n.scope?.toString(),
                    n.name
            ))
        }
        super.visit(n, arg)
    }


}
