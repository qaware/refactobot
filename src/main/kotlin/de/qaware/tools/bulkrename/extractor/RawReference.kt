package de.qaware.tools.bulkrename.extractor

import de.qaware.tools.bulkrename.model.operation.Span

data class RawReference(
        val referenceType: ReferenceType,
        val span: Span,
        val scope: String?,
        val name: String
)