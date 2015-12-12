package site.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/** Initializes the directory layout of the site project. */
class InitSite extends DefaultTask {

    InitSite() {
        group 'site'
        description 'Initializes the directory layout of the site project.'
    }

    @TaskAction
    void createSiteProjectLayout() {
        def siteDir = project.file(project.site.scriptsDir)
        siteDir.mkdirs()
        createExampleDocumentScript(siteDir)
        project.sourceSets.main.groovy.srcDirs.each { it.mkdirs() }
        project.sourceSets.main.resources.srcDirs.each { it.mkdirs() }
    }

    private void createExampleDocumentScript(File siteDir) {
        def script = new File(siteDir, 'index.groovy')
        if (script.exists()) {
            logger.warn "skipping example script creation (file already exists)"
        } else {
            script.append InitSite.class.getResourceAsStream('/site/task/index.groovy')
        }
    }
}
