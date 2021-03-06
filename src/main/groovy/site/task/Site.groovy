package site.task

import site.html.HtmlBuilder
import java.nio.file.Paths
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.file.FileTreeElement
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Executes the HTML pages' generation scripts.
 * Every script has access to a context object referenced by the variable called {@code site}.
 * The site object has the following fields:
 * <ul>
 * <li>{@code site.builder}: the HTML builder currently in use, that is also the script delegate object.</li>
 * <li>
 * {@code site.root}: the absolute path where the site will be deployed on the target host.
 * See the {@link site.SitePluginExtension plugin configuration} for how this property can be configured.
 * </li>
 * <li>{@code site.page}: the full path of the page currently being generated.</li>
 * <li>{@code site.home}: the relative path to the site home location.</li>
 * <li>{@code site.indentation}: the number of spaces used in each indentation level.</li>
 * </ul>
 * In the {@code site} block of the build script, custom parameters can be specified, which are collected inside the {@code site} object.
 */
class Site extends DefaultTask {

    Site() {
        group 'site'
        description 'Build static webpages from groovy scripts.'
        inputs.sourceDir { project.site.scriptsDir }
        inputs.dir { project.sourceSets.main.allSource.srcDirs }
        outputs.dir { project.site.buildDir }
    }

    @TaskAction
    void executeScripts() {
        def scripts = project.fileTree(project.site.scriptsDir) { include '**/*.groovy' }
        scripts.visit { if (!it.isDirectory()) execute it }
    }

    void execute(FileTreeElement script) {
        def pageRelativePath = script.path - '.groovy' + '.html'
        def pageAbsolutePath = project.site.absoluteRoot() + pageRelativePath
        def page = project.file(project.site.buildDir + pageAbsolutePath)
        page.parentFile.mkdirs()
        page.withWriter { out ->
            def builder = new HtmlBuilder(out, project.site.indentation)
            builder.metaClass.site = bindings(builder, pageAbsolutePath)
            def shell = new GroovyShell(configuration())
            shell.setProperty('site', builder.site)
            try {
                shell.evaluate("site.builder.with { ${script.file.text} }")
            } catch (Exception exception) {
                logger.error(" ✘  $pageAbsolutePath")
                throw new TaskExecutionException(this, exception)
            }
        }
        logger.lifecycle " ✔  $pageAbsolutePath"
    }

    String relativeHomePath(String page, String root) {
        def home = Paths.get(page).parent.relativize(Paths.get(root)).toString()
        return (home.empty ? '.' : home) + '/'
    }

    Map bindings(HtmlBuilder builder, String page) {
        def root = project.site.absoluteRoot()
        def bindings = [
            builder: builder,
            root: root,
            page: page,
            home: relativeHomePath(page, root),
            indentation: project.site.indentation
        ]
        bindings << project.site.parameters
        return bindings
    }

    CompilerConfiguration configuration() {
        def config = new CompilerConfiguration()
        config.classpathList = project.sourceSets.main.runtimeClasspath*.toString()
        return config
    }
}
