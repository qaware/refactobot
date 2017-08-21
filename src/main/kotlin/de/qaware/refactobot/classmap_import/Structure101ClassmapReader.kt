package de.qaware.refactobot.classmap_import

import org.jdom2.input.SAXBuilder
import java.io.File

/**
 * Reader for structure101 classmap xml files.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
object Structure101ClassmapReader {

    fun readClassmapXml(filename: String): Map<String, String> {
        return SAXBuilder().build(File(filename))
                .rootElement.getChild("set").getChild("namemap").getChildren("map")
                .map { Pair(it.getAttributeValue("from"), it.getAttributeValue("to")) }
                .filterNot { it.first.contains('$') } // ignore elements with $, as they just live in the bytecode representation.
                .toMap()
    }

}
