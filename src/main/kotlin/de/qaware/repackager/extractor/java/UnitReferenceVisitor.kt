package de.qaware.repackager.extractor.java

/**
 * UnitReferencVisitor. Specializes ReferenceVisitor to the trivial unit context.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
abstract class UnitReferenceVisitor(context: ReferenceExtractionContext) : ReferenceVisitor<Unit>(context) {

    override fun initialContext() : Unit = Unit

}
