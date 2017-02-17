package de.qaware.tools.bulkrename.extractor.visitors

import com.github.javaparser.ast.PackageDeclaration
import de.qaware.tools.bulkrename.extractor.JavaPackageReference
import de.qaware.tools.bulkrename.extractor.ReferenceExtractionContext
import de.qaware.tools.bulkrename.extractor.ReferenceVisitor

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
