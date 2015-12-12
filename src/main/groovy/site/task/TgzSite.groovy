package site.task

import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Compression

/** Assembles a tgz (gzip compressed tar) archive containing the built site. */
class TgzSite extends Tar {

    TgzSite() {
        from { project.site.buildDir }
        compression Compression.GZIP
        group 'site'
        description 'Assembles a tgz (gzip compressed tar) archive containing the built site.'
    }
}
