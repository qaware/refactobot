package de.qaware.refactobot.classmap_import

import org.junit.Test
import java.io.File

/**
 * TODO describe type.

 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassMapImporterImplTest {

    @Test
    fun importClassMap() {

        val filePath = File(javaClass.classLoader.getResource("classmap/classmap.xml").file).absolutePath
        ClassMapImporterImpl().importClassMap(filePath)

    }

}