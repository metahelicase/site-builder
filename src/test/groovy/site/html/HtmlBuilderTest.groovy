package site.html

import org.junit.Test
import static org.junit.Assert.*

class HtmlBuilderTest {

    StringWriter out = new StringWriter()
    HtmlBuilder document = new HtmlBuilder(out)

    @Test
    void canGenerateAnEmptyDocument() {
        document.with {}
        assertEquals('', out.toString())
    }

    @Test
    void canGenerateAnEmptyTag() {
        document.with {
            tag()
        }
        assertEquals('<tag>\n', out.toString())
    }

    @Test
    void canGenerateTagWithValue() {
        document.with {
            tag 'value'
        }
        assertEquals('<tag>value</tag>\n', out.toString())
    }

    @Test
    void canGenerateTagWithAttributeFlag() {
        document.with {
            tag(attribute: '')
        }
        assertEquals('<tag attribute>\n', out.toString())
    }

    @Test
    void canGenerateTagWithAttributeNameValuePair() {
        document.with {
            tag(attribute: 'value')
        }
        assertEquals('<tag attribute="value">\n', out.toString())
    }

    @Test
    void canGenerateTagWithChild() {
        document.with {
            tag {
                child()
            }
        }
        assertEquals('<tag>\n<child>\n</tag>\n', out.toString())
    }
}
