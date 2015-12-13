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

class SiteResourcesTest extends PluginTest {

    static final String TASK = 'siteResources'

    @Test
    void 'siteResources is always up-to-date in an empty project layout'() {
        BuildResult build = run TASK
        assertEquals(UP_TO_DATE, build.task(":$TASK").outcome);
    }

    @Test
    void 'siteResources copies the site resources to the site build directory'() {
        def content = 'content'
        newFile('src/main/resources/resource.txt') << content
        BuildResult build = run TASK
        def copiedResource = file 'build/site/resource.txt'
        assertEquals(content, copiedResource.text)
    }

    @Test
    void 'after files are copied siteResources succeeds'() {
        newFile('src/main/resources/resource.txt')
        assumeFalse(file('build/site/resource.txt').exists())
        BuildResult build = run TASK
        assumeTrue(file('build/site/resource.txt').exists())
        assertEquals(SUCCESS, build.task(":$TASK").outcome);
    }

    @Test
    void 'if files were already copied siteResources is up-to-date'() {
        newFile('src/main/resources/resource.txt')
        assumeFalse(file('build/site/resource.txt').exists())
        BuildResult build = run TASK
        assumeTrue(file('build/site/resource.txt').exists())
        assumeTrue(build.task(":$TASK").outcome == SUCCESS);
        BuildResult anotherBuild = run TASK
        assertEquals(UP_TO_DATE, anotherBuild.task(":$TASK").outcome);
    }
}
