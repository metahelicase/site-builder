package site.task

import org.gradle.api.tasks.Copy

class SiteResources extends Copy {

    SiteResources() {
        from project.site.resourcesDir
        into project.site.buildDir
        group 'build'
        description 'Copies the site resources to the site build directory.'
    }
}
