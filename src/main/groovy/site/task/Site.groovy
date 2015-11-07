package site.task

import site.html.HtmlBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.file.FileTreeElement
import org.codehaus.groovy.control.CompilerConfiguration

class Site extends DefaultTask {

    Site() {
        group 'build'
        description 'Build static webpages from groovy scripts.'
    }

    @TaskAction
    void executeScripts() {
        def scripts = project.fileTree(project.site.srcDir) { include '**/*.groovy' }
        scripts.visit { execute it }
    }

    void execute(FileTreeElement script) {
        if (script.file.isDirectory()) { return }
        def pageName = script.relativePath.toString() - '.groovy'
        def target = project.file("$project.site.buildDir/${pageName}.html")
        target.parentFile.mkdirs()
        def config = configuration()
        target.withWriter { out ->
            def document = new HtmlBuilder(out, project.site.indentation)
            def shell = new GroovyShell(new Binding([document: document]), config)
            shell.evaluate("document.with { ${script.file.text} }")
        }
        logger.lifecycle " >> /${pageName}.html"
    }

    private CompilerConfiguration configuration() {
        def config = new CompilerConfiguration();
        config.classpath << project.sourceSets.main.output.classesDir.toString()
        return config
    }
}
