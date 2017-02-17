package de.qaware.tools.bulkrename.extractor.java.visitors

import com.github.javaparser.ast.PackageDeclaration
import de.qaware.tools.bulkrename.extractor.java.JavaPackageReference
import de.qaware.tools.bulkrename.extractor.java.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.java.ReferenceVisitor

/**
 * Visitor for constructor declarations. Names of constructors must be adapted when the class is renamed.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class PackageDeclarationVisitor(context: ReferenceExtractionContext) : ReferenceVisitor(context) {

    override fun visit(decl: PackageDeclaration?, arg: Unit) {

        if (decl != null) {
            emit(JavaPackageReference(context.getCurrentFile(), decl.toSpan()))
        }
        super.visit(decl, arg)
    }

}
