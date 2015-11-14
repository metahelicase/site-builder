package site.task

import org.gradle.api.tasks.Copy

/** Copies the site resources to the site build directory. */
class SiteResources extends Copy {

    SiteResources() {
        from project.site.resourcesDir
        into project.site.buildDir
        group 'site'
        description 'Copies the site resources to the site build directory.'
    }
}
