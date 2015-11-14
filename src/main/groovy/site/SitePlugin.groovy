package site

import site.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Compression

/**
 * Gradle plugin for building sites using HTML code generation. It depends on
 * groovy and the groovy plugin for script compilation.
 * <p>
 * The plugin defines four tasks. The {@code site} task runs the scripts that
 * define the pages' structure and generates the relative HTML documents. The
 * {@code siteResources} task simply copies the resources to the site target
 * directory preserving the folder structure. The {@code zipSite} and
 * {@code tgzSite} tasks create an archive containing the built site.
 *
 * @see SitePluginExtension for the plugin configuration parameters
 */
class SitePlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.plugins.apply(GroovyPlugin)
        project.dependencies { compile localGroovy() }
        project.extensions.create('site', SitePluginExtension)
        project.task('site', type: Site, dependsOn: 'compileGroovy')
        project.task('siteResources', type: SiteResources)
        project.task('zipSite', type: ZipSite, dependsOn: ['site', 'siteResources'])
        project.task('tgzSite', type: TgzSite, dependsOn: ['site', 'siteResources'])
    }
}
