package site.html

import org.junit.Test
import static org.junit.Assert.*

class HtmlBuilderTest {

    StringWriter out = new StringWriter()
    HtmlBuilder document = new HtmlBuilder(out, 0)

    void generates(String html) {
        assertEquals(html, out.toString())
    }

    @Test
    void canGenerateAnEmptyDocument() {
        document.with {}
        generates ''
    }

    @Test
    void canGenerateAnEmptyTag() {
        document.with {
            tag()
        }
        generates '<tag>\n'
    }

    @Test
    void canGenerateTagWithValue() {
        document.with {
            tag 'value'
        }
        generates '<tag>value</tag>\n'
    }

    @Test
    void canGenerateTagWithAttributeFlag() {
        document.with {
            tag(attribute: null)
        }
        generates '<tag attribute>\n'
    }

    @Test
    void canGenerateTagWithAttributeNameValuePair() {
        document.with {
            tag(attribute: 'value')
        }
        generates '<tag attribute="value">\n'
    }

    @Test
    void canGenerateTagWithAttributeWithAnEmptyValue() {
        document.with {
            tag(attribute: '')
        }
        generates '<tag attribute="">\n'
    }

    @Test
    void canGenerateTagWithAttributeWithNumericValue() {
        document.with {
            tag(attribute: 0)
        }
        generates '<tag attribute="0">\n'
    }

    @Test
    void canGenerateTagWithChild() {
        document.with {
            tag {
                child()
            }
        }
        generates '<tag>\n<child>\n</tag>\n'
    }

    @Test
    void canGenerateTagWithChildrenThatAreIndented() {
        new HtmlBuilder(out, 2).with {
            tag {
                child {
                    grandchild()
                }
            }
        }
        generates '<tag>\n  <child>\n    <grandchild>\n  </child>\n</tag>\n'
    }

    @Test
    void canApplyBuilderFunctor() {
        document.with {
            $ { it.tag() }
        }
        generates '<tag>\n'
    }

    @Test
    void tagNamesStartingWithTheDollarSignAreEscaped() {
        document.with {
            $tag()
        }
        generates '<tag>\n'
    }

    @Test
    void singleLineValuesAreFormattedInline() {
        new HtmlBuilder(out, 2).with {
            tag 'value'
        }
        generates '<tag>value</tag>\n'
    }

    @Test
    void multipleLinesValuesAreFormattedByTrimmingLinesAndAddingIndentation() {
        new HtmlBuilder(out, 2).with {
            tag '''multiple
                lines
                value'''
        }
        generates '<tag>\n  multiple\n  lines\n  value\n</tag>\n'
    }

    @Test
    void firstLineOfMultipleLinesValueIsDroppedIfBlank() {
        new HtmlBuilder(out, 2).with {
            tag '''
                multiple
                lines
                value'''
        }
        generates '<tag>\n  multiple\n  lines\n  value\n</tag>\n'
    }

    @Test
    void lastLineOfMultipleLinesValueIsDroppedIfBlank() {
        new HtmlBuilder(out, 2).with {
            tag '''multiple
                lines
                value
            '''
        }
        generates '<tag>\n  multiple\n  lines\n  value\n</tag>\n'
    }

    @Test
    void internalBlankLinesOfMultipleLinesValueArePreservedButNotIndented() {
        new HtmlBuilder(out, 2).with {
            tag '''multiple
                lines

                value'''
        }
        generates '<tag>\n  multiple\n  lines\n\n  value\n</tag>\n'
    }

    @Test
    void underscoreFormatsASingleLineStringWithoutTag() {
        document.with {
            _ 'text'
        }
        generates 'text\n'
    }

    @Test
    void underscoreFormatsAMultipleLinesStringWithoutTagWithIndentation() {
        new HtmlBuilder(out, 2).with {
            tag {
                _ '''
                    multiple
                    lines
                    text
                '''
            }
        }
        generates '<tag>\n  multiple\n  lines\n  text\n</tag>\n'
    }

    @Test
    void underscoreDoesNotIndentBlankLines() {
        new HtmlBuilder(out, 2).with {
            tag {
                _ '''
                    multiple
                    lines

                    text
                '''
            }
        }
        generates '<tag>\n  multiple\n  lines\n\n  text\n</tag>\n'
    }

    @Test
    void underscoreFormatsAnEmptyStringAsABlankLine() {
        new HtmlBuilder(out, 2).with {
            tag {
                _ ''
            }
        }
        generates '<tag>\n\n</tag>\n'
    }

    @Test
    void underscoreInlinesItsChildren() {
        new HtmlBuilder(out, 2).with {
            _ {
                tag 'inlined with'
                tag 'other tags'
            }
        }
        generates '<tag>inlined with</tag><tag>other tags</tag>\n'
    }

    @Test
    void nestedUnderscoresDoNotAddFormatting() {
        new HtmlBuilder(out, 2).with {
            _ {
                tag 'inlined with'
                _ {
                    tag 'other tags'
                }
            }
        }
        generates '<tag>inlined with</tag><tag>other tags</tag>\n'
    }

    @Test
    void underscoreInlinesNestedChildrenTags() {
        new HtmlBuilder(out, 2).with {
            _ {
                tag {
                    child 'inlined with'
                    child 'children tags'
                }
            }
        }
        generates '<tag><child>inlined with</child><child>children tags</child></tag>\n'
    }

    @Test
    void underscoreInlinesMultipleLinesText() {
        new HtmlBuilder(out, 2).with {
            _ {
                _ '''
                    multiple
                    lines
                    text
                '''
            }
        }
        generates 'multiple lines text\n'
    }

    @Test
    void underscoreInlinesMultipleLinesTextDroppingEmptyLines() {
        new HtmlBuilder(out, 2).with {
            _ {
                _ '''
                    multiple

                    lines

                    text
                '''
            }
        }
        generates 'multiple lines text\n'
    }
}
