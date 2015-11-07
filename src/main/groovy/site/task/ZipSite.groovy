package site.task

import org.gradle.api.tasks.bundling.Zip

class ZipSite extends Zip {

    ZipSite() {
        from project.site.buildDir
        destinationDir = project.buildDir
        group 'site'
        description 'Assembles a zip archive containing the built site.'
    }
}
