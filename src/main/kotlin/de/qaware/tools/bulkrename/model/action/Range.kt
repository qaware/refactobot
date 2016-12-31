package de.qaware.tools.bulkrename.model.action

/**
 * Model for the range of a reference
 *
 * @author Alexander Lannes alexander.lannes@qaware.de
 */
data class Range (

        /**
         * Line of code the reference is in
         */
        val line : Int,

        /**
         * Column in the line the reference begins
         */
        val column : Int,

        /**
         * Number of characters the reference is long
         */
        val length : Int
)