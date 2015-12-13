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

class SiteTest extends PluginTest {

    static final String TASK = 'site'

    @Test
    void 'site succeds in an empty project layout'() {
        BuildResult build = run TASK
        assertEquals(SUCCESS, build.task(":$TASK").outcome);
    }

    @Test
    void 'execution of an empty script generates an empty html file'() {
        def scriptsDir = project.newFolder 'src', 'main', 'site'
        new File(scriptsDir, 'index.groovy').createNewFile()
        BuildResult build = run TASK
        def page = new File(project.root, 'build/site/index.html')
        assertEquals('', page.text);
    }

    @Test
    void 'execution of a script containing tag declarations generates an html file containing the declared tags'() {
        def scriptsDir = project.newFolder 'src', 'main', 'site'
        new File(scriptsDir, 'index.groovy') << '''
            '!DOCTYPE html'()
            html(lang: 'en') {
                head {
                    title 'Title'
                } body {
                    p 'Content'
                }
            }
        '''
        BuildResult build = run TASK
        def page = new File(project.root, 'build/site/index.html')
        def html = [
            '<!DOCTYPE html>',
            '<html lang="en">',
            '    <head>',
            '        <title>Title</title>',
            '    </head>',
            '    <body>',
            '        <p>Content</p>',
            '    </body>',
            '</html>',
            ''
        ].join('\n')
        assertEquals(html, page.text);
    }

    @Test
    void 'site scripts can access the site.root variable'() {
        new File(project.root, 'build.gradle') << '''
            site {
                root '/root/'
            }
        '''
        def scriptsDir = project.newFolder 'src', 'main', 'site'
        new File(scriptsDir, 'index.groovy') << '''a(href: site.root, 'Home')'''
        BuildResult build = run TASK
        def page = new File(project.root, 'build/site/index.html')
        def html = '<a href="/root/">Home</a>\n'
        assertEquals(html, page.text);
    }

    @Test
    void 'site scripts can access the site.page variable'() {
        new File(project.root, 'build.gradle') << '''
            site {
                root '/root/'
            }
        '''
        def scriptsDir = project.newFolder 'src', 'main', 'site'
        new File(scriptsDir, 'index.groovy') << '''a(href: site.page, 'this page')'''
        BuildResult build = run TASK
        def page = new File(project.root, 'build/site/index.html')
        def html = '<a href="/root/index.html">this page</a>\n'
        assertEquals(html, page.text);
    }
}
