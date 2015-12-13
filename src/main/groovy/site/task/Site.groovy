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
        scripts.visit { if (!it.isDirectory()) execute it }
    }

    void execute(FileTreeElement script) {
        def pageRelativePath = script.path - '.groovy' + '.html'
        def pageAbsolutePath = "$project.site.root$pageRelativePath"
        def page = project.file "$project.site.buildDir/$pageRelativePath"
        page.parentFile.mkdirs()
        page.withWriter { out ->
            def builder = new HtmlBuilder(out, project.site.indentation)
            def shell = new GroovyShell(bindings(builder, pageAbsolutePath), configuration())
            shell.evaluate("site.builder.with { ${script.file.text} }")
        }
        logger.lifecycle " >> $pageAbsolutePath"
    }

    Binding bindings(HtmlBuilder builder, String page) {
        new Binding([site: [
            builder: builder,
            root: project.site.root,
            page: page
        ]])
    }

    CompilerConfiguration configuration() {
        def config = new CompilerConfiguration()
        config.classpathList = project.sourceSets.main.runtimeClasspath*.toString()
        return config
    }
}
