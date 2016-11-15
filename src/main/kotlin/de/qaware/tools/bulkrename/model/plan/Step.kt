package de.qaware.tools.bulkrename.model.plan

/**
 * A step in a refactoring plan.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 * @author Florian Engel florian.engel@qaware.de
 */
data class Step(

        /**
         * A map with a matcher expression (value) for arbitrary refactoring subjects (e.g. file name) which should be matched.
         * Must at least contain an expression for every subject defined in targetExpression.
         */
        val sourceExpressions: Map<RefactoringSubject, Regex>,

        /**
         * A map with a target expression (value) for arbitrary refactoring subjects. A source expression must
         * exist for every refactoring subject contained in this map.
         */
        val targetExpression: Map<RefactoringSubject, Regex>

)