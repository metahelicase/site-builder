package site.task

import site.html.HtmlBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.file.FileTreeElement
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Executes the HTML pages' generation scripts. Every script has access to a
 * context object referenced by the variable called {@code site}. The site
 * object has the following fields:
 * <ul>
 * <li>{@code site.builder}: the HTML builder currently in use, that is also the
 * script delegate object.</li>
 * <li>{@code site.root}: the absolute path where the site will be deployed on
 * the target host. See the {@link site.SitePluginExtension plugin configuration}
 * for how this property can be configured.</li>
 * <li>{@code site.page}: the relative path of the page currently being
 * generated.</li>
 * </ul>
 */
class Site extends DefaultTask {

    Site() {
        group 'site'
        description 'Build static webpages from groovy scripts.'
    }

    @TaskAction
    void executeScripts() {
        def scripts = project.fileTree(project.site.scriptsDir) { include '**/*.groovy' }
        scripts.visit { execute it }
    }

    void execute(FileTreeElement script) {
        if (script.file.isDirectory()) { return }
        def page = script.relativePath.toString() - '.groovy' + '.html'
        def target = project.file("$project.site.buildDir/$page")
        def pagePath = "$project.site.root$page"
        target.parentFile.mkdirs()
        def config = configuration()
        target.withWriter { out ->
            def bindings = [
                builder: new HtmlBuilder(out, project.site.indentation),
                root: project.site.root,
                page: pagePath
            ]
            def shell = new GroovyShell(new Binding([site: bindings]), config)
            shell.evaluate("site.builder.with { ${script.file.text} }")
        }
        logger.lifecycle " >> $pagePath"
    }

    private CompilerConfiguration configuration() {
        def config = new CompilerConfiguration();
        config.classpath << project.sourceSets.main.output.classesDir.toString()
        return config
    }
}
