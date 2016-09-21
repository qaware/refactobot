package de.qaware.tools.bulkrename.scanner

import de.qaware.tools.bulkrename.model.codebase.Codebase
import de.qaware.tools.bulkrename.model.codebase.Module
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Collectors

/**
 * Simple project scanner that uses maven directory structures.
 *
 * @author Alexander Krauss alexander.krauss@qaware.de
 */
class MavenScanner : Scanner {


    override fun scanCodebase(rootDir : Path) : Codebase {

        val modules : List<Module> = Files.find(rootDir, 50, BiPredicate { path, attrs ->
            attrs.isRegularFile && path.fileName.toString().equals("pom.xml") })
                .filter { p -> !p.contains(Paths.get("target"))}
                .map { p -> createModule(p) }
                .collect(Collectors.toList<Module>())

        return Codebase(rootDir, modules);
    }




    fun createModule(pathToPom : Path) : Module {

        val rootPath = pathToPom.parent;

        return Module(rootPath.fileName.toString(),
                rootPath,
                emptyList(), // TODO scan for files
                emptyList());
    }

}