package site.html

import org.junit.Test
import static org.junit.Assert.*

class HtmlBuilderTest {

    StringWriter out = new StringWriter()
    int indentation = 4
    HtmlBuilder document = new HtmlBuilder(out, indentation)
    String tab = ' ' * indentation

    HtmlBuilderTest script(Closure script) {
        document.with script
        return this
    }

    void generates(String html) {
        assertEquals(html, out.toString())
    }

    @Test
    void 'can generate an empty document'() {
        script {
        } generates ''
    }

    @Test
    void 'can generate an empty tag'() {
        script {
            tag()
        } generates '<tag>\n'
    }

    @Test
    void 'can generate tag with value'() {
        script {
            tag 'value'
        } generates '<tag>value</tag>\n'
    }

    @Test
    void 'can generate tag with attribute flag'() {
        script {
            tag(attribute: null)
        } generates '<tag attribute>\n'
    }

    @Test
    void 'can generate tag with attribute name and value pair'() {
        script {
            tag(attribute: 'value')
        } generates '<tag attribute="value">\n'
    }

    @Test
    void 'can generate tag with attribute with an empty value'() {
        script {
            tag(attribute: '')
        } generates '<tag attribute="">\n'
    }

    @Test
    void 'can generate tag with attribute with numeric value'() {
        script {
            tag(attribute: 0)
        } generates '<tag attribute="0">\n'
    }

    @Test
    void 'can generate tag with attribute with boolean value'() {
        script {
            tag(attribute: true)
        } generates '<tag attribute="true">\n'
    }

    @Test
    void 'can generate tag with child'() {
        script {
            tag {
                child()
            }
        } generates "<tag>\n$tab<child>\n</tag>\n"
    }

    @Test
    void 'can generate tag with children that are indented'() {
        script {
            tag {
                child {
                    grandchild()
                }
            }
        } generates "<tag>\n$tab<child>\n$tab$tab<grandchild>\n$tab</child>\n</tag>\n"
    }

    @Test
    void 'can apply builder functor'() {
        script {
            $ { it.tag() }
        } generates '<tag>\n'
    }

    @Test
    void 'tag names starting with the dollar sign are escaped'() {
        script {
            $tag()
        } generates '<tag>\n'
    }

    @Test
    void 'single line values are formatted inline'() {
        script {
            tag 'value'
        } generates '<tag>value</tag>\n'
    }

    @Test
    void 'multiple lines values are formatted by trimming lines and adding indentation'() {
        script {
            tag '''multiple
                lines
                value'''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n${tab}value\n</tag>\n"
    }

    @Test
    void 'first line of multiple lines value is dropped if blank'() {
        script {
            tag '''
                multiple
                lines
                value'''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n${tab}value\n</tag>\n"
    }

    @Test
    void 'last line of multiple lines value is dropped if blank'() {
        script {
            tag '''multiple
                lines
                value
            '''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n${tab}value\n</tag>\n"
    }

    @Test
    void 'internal blank lines of multiple lines value are preserved but not indented'() {
        script {
            tag '''multiple
                lines

                value'''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n\n${tab}value\n</tag>\n"
    }

    @Test
    void 'underscore formats single line strings without tag'() {
        script {
            _ 'text'
        } generates 'text\n'
    }

    @Test
    void 'underscore formats multiple lines strings with indentation'() {
        script {
            tag {
                _ '''
                    multiple
                    lines
                    text
                '''
            }
        } generates "<tag>\n${tab}multiple\n${tab}lines\n${tab}text\n</tag>\n"
    }

    @Test
    void 'underscore does not indent blank lines'() {
        script {
            tag {
                _ '''
                    multiple
                    lines

                    text
                '''
            }
        } generates "<tag>\n${tab}multiple\n${tab}lines\n\n${tab}text\n</tag>\n"
    }

    @Test
    void 'underscore formats an empty string as a blank line'() {
        script {
            tag {
                _ ''
            }
        } generates '<tag>\n\n</tag>\n'
    }

    @Test
    void 'underscore preserves blank spaces on a single blank line'() {
        script {
            tag {
                _ tab
            }
        } generates "<tag>\n$tab$tab\n</tag>\n"
    }

    @Test
    void 'underscore inlines its children'() {
        script {
            _ {
                tag 'inlined with'
                tag 'other tags'
            }
        } generates '<tag>inlined with</tag><tag>other tags</tag>\n'
    }

    @Test
    void 'nested underscores do not add formatting'() {
        script {
            _ {
                tag 'inlined with'
                _ {
                    tag 'other tags'
                }
            }
        } generates '<tag>inlined with</tag><tag>other tags</tag>\n'
    }

    @Test
    void 'underscore inlines nested children tags'() {
        script {
            _ {
                tag {
                    child 'inlined with'
                    child 'children tags'
                }
            }
        } generates '<tag><child>inlined with</child><child>children tags</child></tag>\n'
    }

    @Test
    void 'underscore inlines multiple lines text'() {
        script {
            _ {
                _ '''
                    multiple
                    lines
                    text
                '''
            }
        } generates 'multiple lines text\n'
    }

    @Test
    void 'underscore inlines multiple lines text dropping empty lines'() {
        script {
            _ {
                _ '''
                    multiple

                    lines

                    text
                '''
            }
        } generates 'multiple lines text\n'
    }

    @Test
    void 'underscore inlines multiple lines tag value'() {
        script {
            _ {
                tag '''
                    multiple
                    lines
                    value
                '''
            }
        } generates '<tag>multiple lines value</tag>\n'
    }

    @Test
    void 'can inline single line blank text'() {
        script {
            _ {
                tag tab
            }
        } generates "<tag>$tab</tag>\n"
    }

    @Test
    void 'can chain tag declaration after empty tag'() {
        script {
            tag() tag()
        } generates'<tag>\n<tag>\n'
    }

    @Test
    void 'can chain tag declaration after tag value'() {
        script {
            tag 'a' tag()
        } generates '<tag>a</tag>\n<tag>\n'
    }

    @Test
    void 'can chain tag declaration after tag attributes'() {
        script {
            tag(attribute: 'value') tag()
        } generates '<tag attribute="value">\n<tag>\n'
    }

    @Test
    void 'can chain tag declaration after closure'() {
        script {
            tag { child() } tag()
        } generates "<tag>\n$tab<child>\n</tag>\n<tag>\n"
    }
}
