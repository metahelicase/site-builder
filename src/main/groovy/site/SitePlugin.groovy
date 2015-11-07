package site

import site.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Compression

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
