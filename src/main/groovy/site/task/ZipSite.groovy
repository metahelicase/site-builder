package site.task

import org.gradle.api.tasks.bundling.Zip

class ZipSite extends Zip {

    ZipSite() {
        from project.site.buildDir
        destinationDir = project.buildDir
        group 'build'
        description 'Assembles a zip containing the built site.'
    }
}
