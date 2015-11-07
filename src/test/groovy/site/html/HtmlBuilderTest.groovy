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
        this
    }

    void generates(String html) {
        assertEquals(html, out.toString())
    }

    @Test
    void canGenerateAnEmptyDocument() {
        script {
        } generates ''
    }

    @Test
    void canGenerateAnEmptyTag() {
        script {
            tag()
        } generates '<tag>\n'
    }

    @Test
    void canGenerateTagWithValue() {
        script {
            tag 'value'
        } generates '<tag>value</tag>\n'
    }

    @Test
    void canGenerateTagWithAttributeFlag() {
        script {
            tag(attribute: null)
        } generates '<tag attribute>\n'
    }

    @Test
    void canGenerateTagWithAttributeNameValuePair() {
        script {
            tag(attribute: 'value')
        } generates '<tag attribute="value">\n'
    }

    @Test
    void canGenerateTagWithAttributeWithAnEmptyValue() {
        script {
            tag(attribute: '')
        } generates '<tag attribute="">\n'
    }

    @Test
    void canGenerateTagWithAttributeWithNumericValue() {
        script {
            tag(attribute: 0)
        } generates '<tag attribute="0">\n'
    }

    @Test
    void canGenerateTagWithAttributeWithBooleanValue() {
        script {
            tag(attribute: true)
        } generates '<tag attribute="true">\n'
    }

    @Test
    void canGenerateTagWithChild() {
        script {
            tag {
                child()
            }
        } generates "<tag>\n$tab<child>\n</tag>\n"
    }

    @Test
    void canGenerateTagWithChildrenThatAreIndented() {
        script {
            tag {
                child {
                    grandchild()
                }
            }
        } generates "<tag>\n$tab<child>\n$tab$tab<grandchild>\n$tab</child>\n</tag>\n"
    }

    @Test
    void canApplyBuilderFunctor() {
        script {
            $ { it.tag() }
        } generates '<tag>\n'
    }

    @Test
    void tagNamesStartingWithTheDollarSignAreEscaped() {
        script {
            $tag()
        } generates '<tag>\n'
    }

    @Test
    void singleLineValuesAreFormattedInline() {
        script {
            tag 'value'
        } generates '<tag>value</tag>\n'
    }

    @Test
    void multipleLinesValuesAreFormattedByTrimmingLinesAndAddingIndentation() {
        script {
            tag '''multiple
                lines
                value'''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n${tab}value\n</tag>\n"
    }

    @Test
    void firstLineOfMultipleLinesValueIsDroppedIfBlank() {
        script {
            tag '''
                multiple
                lines
                value'''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n${tab}value\n</tag>\n"
    }

    @Test
    void lastLineOfMultipleLinesValueIsDroppedIfBlank() {
        script {
            tag '''multiple
                lines
                value
            '''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n${tab}value\n</tag>\n"
    }

    @Test
    void internalBlankLinesOfMultipleLinesValueArePreservedButNotIndented() {
        script {
            tag '''multiple
                lines

                value'''
        } generates "<tag>\n${tab}multiple\n${tab}lines\n\n${tab}value\n</tag>\n"
    }

    @Test
    void underscoreFormatsSingleLineStringsWithoutTag() {
        script {
            _ 'text'
        } generates 'text\n'
    }

    @Test
    void underscoreFormatsMultipleLinesStringsWithIndentation() {
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
    void underscoreDoesNotIndentBlankLines() {
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
    void underscoreFormatsAnEmptyStringAsABlankLine() {
        script {
            tag {
                _ ''
            }
        } generates '<tag>\n\n</tag>\n'
    }

    @Test
    void underscoreInlinesItsChildren() {
        script {
            _ {
                tag 'inlined with'
                tag 'other tags'
            }
        } generates '<tag>inlined with</tag><tag>other tags</tag>\n'
    }

    @Test
    void nestedUnderscoresDoNotAddFormatting() {
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
    void underscoreInlinesNestedChildrenTags() {
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
    void underscoreInlinesMultipleLinesText() {
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
    void underscoreInlinesMultipleLinesTextDroppingEmptyLines() {
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
    void underscoreInlinesMultipleLinesTagValue() {
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
    void canInlineSingleLineBlankText() {
        script {
            _ {
                tag tab
            }
        } generates "<tag>$tab</tag>\n"
    }
}
