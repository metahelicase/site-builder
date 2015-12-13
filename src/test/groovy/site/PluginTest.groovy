package site

import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

abstract class PluginTest {

    static final String PLUGIN = 'org.metahelicase.site-builder'

    @Rule
    public final TemporaryFolder project = new TemporaryFolder()

    @Before
    void setup() {
        def buildScript = project.newFile 'build.gradle'
        buildScript.text = "plugins { id '$PLUGIN' }"
    }

    List<File> classpath() {
        def pluginTestClasspath = System.getProperty('plugin.test.classpath')
        return pluginTestClasspath.readLines().collect { new File(it) }
    }

    File file(String path) {
        new File(project.root, path)
    }

    File newFile(String path) {
        def file = new File(project.root, path)
        file.parentFile.mkdirs()
        file.createNewFile()
        return file
    }

    BuildResult run(String task) {
        return GradleRunner.create()
                .withProjectDir(project.root)
                .withPluginClasspath(classpath())
                .withArguments(task, '--stacktrace')
                .build()
    }
}
