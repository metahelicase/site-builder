package site.task

import org.gradle.api.tasks.Copy

class SiteResources extends Copy {

    SiteResources() {
        from 'src/main/resources'
        into 'build/site'
        group 'build'
        description 'Copies the site resources to the site build directory.'
    }
}
