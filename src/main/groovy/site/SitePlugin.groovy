package site

import site.task.*
import org.gradle.api.Plugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Compression

class SitePlugin implements Plugin {

    def void apply(project) {
        project.task('site', type: Site, dependsOn: 'compileGroovy')
        project.task('siteResources', type: SiteResources)
        project.task('zipSite', type: ZipSite, dependsOn: ['site', 'siteResources'])
        project.task('tgzSite', type: TgzSite, dependsOn: ['site', 'siteResources'])
    }
}
