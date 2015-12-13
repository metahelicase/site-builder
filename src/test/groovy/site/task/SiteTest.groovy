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
        newFile 'src/main/site/index.groovy'
        BuildResult build = run TASK
        def page = file 'build/site/index.html'
        assertEquals('', page.text);
    }

    @Test
    void 'execution of a script containing tag declarations generates an html file containing the declared tags'() {
        newFile('src/main/site/index.groovy') << '''
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
        def page = file 'build/site/index.html'
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
        file('build.gradle') << '''
            site {
                root '/root/'
            }
        '''
        newFile('src/main/site/index.groovy') << '''a(href: site.root, 'Home')'''
        BuildResult build = run TASK
        def page = file 'build/site/index.html'
        def html = '<a href="/root/">Home</a>\n'
        assertEquals(html, page.text);
    }

    @Test
    void 'site scripts can access the site.page variable'() {
        file('build.gradle') << '''
            site {
                root '/root/'
            }
        '''
        newFile('src/main/site/index.groovy') << '''a(href: site.page, 'this page')'''
        BuildResult build = run TASK
        def page = file 'build/site/index.html'
        def html = '<a href="/root/index.html">this page</a>\n'
        assertEquals(html, page.text);
    }
}
