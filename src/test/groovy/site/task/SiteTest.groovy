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
    void 'site is up-to-date in an empty project layout'() {
        BuildResult build = run TASK
        assertEquals(UP_TO_DATE, build.task(":$TASK").outcome);
    }

    @Test
    void 'site succeeds after the build of an empty script'() {
        newFile 'src/main/site/index.groovy'
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
    void 'site preserves the directory structure'() {
        newFile 'src/main/site/path/index.groovy'
        BuildResult build = run TASK
        def page = file 'build/site/path/index.html'
        assertTrue(page.exists());
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
        newFile('src/main/site/path/index.groovy') << '''a(href: site.page, 'this page')'''
        BuildResult build = run TASK
        def page = file 'build/site/path/index.html'
        def html = '<a href="/root/path/index.html">this page</a>\n'
        assertEquals(html, page.text);
    }

    @Test
    void 'site scripts can access the site.indentation variable'() {
        file('build.gradle') << '''
            site {
                indentation 2
            }
        '''
        newFile('src/main/site/path/index.groovy') << '''p "$site.indentation"'''
        BuildResult build = run TASK
        def page = file 'build/site/path/index.html'
        def html = '<p>2</p>\n'
        assertEquals(html, page.text);
    }

    @Test
    void 'site scripts can set site global parameters using method call syntax'() {
        file('build.gradle') << '''
            site {
                title 'Title'
            }
        '''
        newFile('src/main/site/path/index.groovy') << '''title site.title'''
        BuildResult build = run TASK
        def page = file 'build/site/path/index.html'
        def html = '<title>Title</title>\n'
        assertEquals(html, page.text);
    }

    @Test
    void 'site scripts can set site global parameters using property assignment syntax'() {
        file('build.gradle') << '''
            site {
                title = 'Title'
            }
        '''
        newFile('src/main/site/path/index.groovy') << '''title site.title'''
        BuildResult build = run TASK
        def page = file 'build/site/path/index.html'
        def html = '<title>Title</title>\n'
        assertEquals(html, page.text);
    }

    @Test
    void 'site classes can access the site variable from the builder object'() {
        file('build.gradle') << '''
            site {
                title 'Title'
            }
        '''
        newFile('src/main/groovy/Title.groovy') << '''
            class Title {
                void call(document) { document.with {
                    title site.title
                }}
            }
        '''
        newFile('src/main/site/index.groovy') << '''$ new Title()'''
        BuildResult build = run TASK
        def page = file 'build/site/index.html'
        def html = '<title>Title</title>\n'
        assertEquals(html, page.text);
    }

    @Test
    void 'site is up-to-date if nothing changed after last build'() {
        newFile 'src/main/site/index.groovy'
        BuildResult build = run TASK
        assumeTrue(build.task(":$TASK").outcome == SUCCESS);
        BuildResult anotherBuild = run TASK
        assertEquals(UP_TO_DATE, anotherBuild.task(":$TASK").outcome);
    }

    @Test
    void 'site is run again if a script changed after last build'() {
        def script = newFile 'src/main/site/index.groovy'
        BuildResult build = run TASK
        assumeTrue(build.task(":$TASK").outcome == SUCCESS);
        script << '''h1('Title')'''
        BuildResult anotherBuild = run TASK
        assertEquals(SUCCESS, anotherBuild.task(":$TASK").outcome);
    }

    @Test
    void 'site is run again if a groovy class changed after last build'() {
        newFile 'src/main/site/index.groovy'
        def groovyFile = newFile('src/main/groovy/Empty.groovy') << 'class Empty {}'
        BuildResult build = run TASK
        assumeTrue(build.task(":$TASK").outcome == SUCCESS);
        groovyFile.text = 'abstract class Empty {}'
        BuildResult anotherBuild = run TASK
        assertEquals(SUCCESS, anotherBuild.task(":$TASK").outcome);
    }
}
