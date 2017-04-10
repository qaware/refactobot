package de.qaware.tools.bulkrename.classmap_import

import de.qaware.tools.bulkrename.model.plan.RefactoringSubject
import de.qaware.tools.bulkrename.model.plan.SchematicRefactoringPlan
import de.qaware.tools.bulkrename.model.plan.Step
import de.qaware.tools.bulkrename.util.classToFile
import de.qaware.tools.bulkrename.util.splitPath
import org.jdom2.input.SAXBuilder
import java.io.File

/**
 * Importer for class maps from structure101.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class ClassMapImporterImpl : ClassMapImporter {

    /** Just a pair of strings representing a class mapping */
    private data class ClassMapEntry(val from: String, val to: String)


    override fun importClassMap(filename: String): SchematicRefactoringPlan {

        // read XML file
        val doc = SAXBuilder().build(File(filename))


        val entries = doc.rootElement.getChild("set").getChild("namemap").getChildren("map")
                .map { ClassMapEntry(it.getAttributeValue("from"), it.getAttributeValue("to")) }
                .filterNot { it.from.contains('$') }
                .map { mkStep(it) }

        return SchematicRefactoringPlan(entries);
    }

    private fun mkStep(entry : ClassMapEntry): Step {

        val (fromPath, fromFile) = splitPath(classToFile(entry.from))
        val (toPath, toFile) = splitPath(classToFile(entry.to))

        return Step(mapOf(
                Pair(RefactoringSubject.FILE_PATH,
                        Step.Replacement(Regex.fromLiteral(fromPath.toString()), toPath.toString())),
                Pair(RefactoringSubject.FILE_NAME,
                        Step.Replacement(Regex.fromLiteral(fromFile), toFile))
        ))
    }

}
