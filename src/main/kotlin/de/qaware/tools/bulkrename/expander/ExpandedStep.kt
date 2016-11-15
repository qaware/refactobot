package de.qaware.tools.bulkrename.expander

import de.qaware.tools.bulkrename.model.plan.RefactoringSubject

/**
 * An expanded step. Expanded steps apply to a single file and describe the complete transformation from
 * every source field to its corresponding target field.
 *
 * @author Florian Engel florian.engel@qaware.de
 */
data class ExpandedStep(
        val sourceExpressions: Map<RefactoringSubject, String>,
        val targetExpression: Map<RefactoringSubject, String>
)
