package site.task

import site.PluginTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import static org.junit.Assert.*
import static org.junit.Assume.*
import org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.BuildResult
import static org.gradle.testkit.runner.TaskOutcome.*

class InitSiteTest extends PluginTest {

    static final String TASK = 'initSite'

    @Test
    void 'initSite succeds in an empty project layout'() {
        BuildResult build = run TASK
        assertEquals(SUCCESS, build.task(":$TASK").outcome);
    }

    @Test
    void 'initSite creates the site scripts directory'() {
        def scriptsDir = new File(project.root, 'src/main/site')
        assumeFalse(scriptsDir.exists())
        BuildResult build = run TASK
        assertTrue(scriptsDir.isDirectory());
    }

    @Test
    void 'initSite creates the resources directory'() {
        def scriptsDir = new File(project.root, 'src/main/resources')
        assumeFalse(scriptsDir.exists())
        BuildResult build = run TASK
        assertTrue(scriptsDir.isDirectory());
    }

    @Test
    void 'initSite creates the groovy classes directory'() {
        def scriptsDir = new File(project.root, 'src/main/groovy')
        assumeFalse(scriptsDir.exists())
        BuildResult build = run TASK
        assertTrue(scriptsDir.isDirectory());
    }

    @Test
    void 'initSite creates the example script in index.groovy if that file does not already exist'() {
        def exampleScript = new File(project.root, 'src/main/site/index.groovy')
        assumeFalse(exampleScript.exists())
        def content = InitSiteTest.class.getResourceAsStream('/site/task/index.groovy').text
        BuildResult build = run TASK
        assertEquals(content, exampleScript.text);
    }

    @Test
    void 'initSite does not overwrite index.groovy with the example script if that file already exists'() {
        def scriptsDir = project.newFolder 'src', 'main', 'site'
        def exampleScript = new File(scriptsDir, 'index.groovy')
        exampleScript.createNewFile()
        assumeTrue(exampleScript.exists())
        BuildResult build = run TASK
        assertEquals('', exampleScript.text);
    }
}
