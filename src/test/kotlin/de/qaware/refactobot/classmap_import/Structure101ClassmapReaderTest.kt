package de.qaware.refactobot.classmap_import

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

/**
 * Simple test for Structure101ClassmapReader.

 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class Structure101ClassmapReaderTest {

    @Test
    fun importClassMap() {

        val filePath = File(javaClass.classLoader.getResource("classmap/classmap.xml").file).absolutePath
        val classmap = Structure101ClassmapReader.readClassmapXml(filePath)

        assertThat(classmap).isEqualTo(mapOf(
                "org.example.code.ClassA" to "org.example.newpackage.ClassARenamed",
                "org.example.code.ClassB" to "org.example.newpackage.ClassB",
                "org.example.code.ClassC" to "org.example.newpackage.ClassC"
                // note: $ entry in the xml file does not appear here.
        ))
    }
}