package de.qaware.repackager.rewrites

/**
 * Specification of a refactoring problem used to test the engine.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
data class RefactoringProblem (

        /**
         * Each rule maps a fqcn to an (optional) new fqcn. Then the class is to be moved.
         * If the target is identical, the class is not moved.
         */
        val moveRules: List<Pair<String, String>>,

        val originalFile: String,

        val expectedResult: String

)