package site.task

import site.html.HtmlBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.file.FileTreeElement
import org.codehaus.groovy.control.CompilerConfiguration

class Site extends DefaultTask {

    Site() {
        dependsOn 'compileGroovy'
        group 'build'
        description 'Build static webpages from groovy scripts.'
    }

    @TaskAction
    void executeScripts() {
        def scripts = project.fileTree('src/main/site') { include '**/*.groovy' }
        scripts.visit { execute it }
    }

    void execute(FileTreeElement script) {
        if (script.file.isDirectory()) { return }
        def pageName = script.relativePath.toString() - '.groovy'
        def target = project.file("$project.buildDir/site/${pageName}.html")
        target.parentFile.mkdirs()
        def out = new PrintWriter(new FileOutputStream(target))
        def document = new HtmlBuilder(out)
        def shell = new GroovyShell(new Binding([document: document]), configuration())
        shell.evaluate("document.with { ${script.file.text} }")
        out.flush()
        println " >> /${pageName}.html"
    }
    
    private CompilerConfiguration configuration() {
        def config = new CompilerConfiguration();
        config.classpath << 'build/classes/main'
        return config
    }
}
