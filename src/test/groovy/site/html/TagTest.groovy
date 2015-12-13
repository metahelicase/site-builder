package site.html

import org.junit.Test
import static org.junit.Assert.*
import static org.junit.Assume.*

class TagTest {

    Tag textMetatag = [name: '_', value: '']
    Tag inliningMetatag = [name: '_']
    Tag emptyTag = [name: 'tag']
    Tag textTag = [name: 'tag', value: '']
    Tag parentTag = [name: 'tag', children: [[name: 'child'] as Tag]]

    Tag tagNamed(String name) {
        [name: name]
    }

    List<String> extractText(String text) {
        ([name: 'tag', value: text] as Tag).text
    }

    @Test
    void 'tag with an underscore as name is a metatag'() {
        assertTrue(tagNamed('_').metatag)
    }

    @Test
    void 'tag with a name starting with an underscore is not a metatag'() {
        assertFalse(tagNamed('_tag').metatag)
    }

    @Test
    void 'metatag with value is a text metatag'() {
        assumeTrue(textMetatag.metatag)
        assertTrue(textMetatag.textMetatag)
    }

    @Test
    void 'metatag without value is an inlining metatag'() {
        assumeTrue(inliningMetatag.metatag)
        assertTrue(inliningMetatag.inliningMetatag)
    }

    @Test
    void 'text metatag is not an inlining metatag'() {
        assumeTrue(textMetatag.textMetatag)
        assertFalse(textMetatag.inliningMetatag)
    }

    @Test
    void 'inlining metatag is not a text metatag'() {
        assumeTrue(inliningMetatag.inliningMetatag)
        assertFalse(inliningMetatag.textMetatag)
    }

    @Test
    void 'tag is empty when has no value nor children tags'() {
        assumeFalse(emptyTag.metatag)
        assertTrue(emptyTag.empty)
    }

    @Test
    void 'tag is parent tag when has no value but has children tags'() {
        assumeFalse(parentTag.metatag)
        assertTrue(parentTag.parent)
    }

    @Test
    void 'empty tag is not a parent tag'() {
        assumeTrue(emptyTag.empty)
        assertFalse(emptyTag.parent)
    }

    @Test
    void 'parent tag is not an empty tag'() {
        assumeTrue(parentTag.parent)
        assertFalse(parentTag.empty)
    }

    @Test
    void 'text tag is not an empty tag'() {
        assumeFalse(textTag.metatag)
        assertFalse(textTag.empty)
    }

    @Test
    void 'text tag is not a parent tag'() {
        assumeFalse(textTag.metatag)
        assertFalse(textTag.parent)
    }

    @Test
    void 'tag name starting with dollar is escaped by removing it'() {
        assertEquals('tag', tagNamed('$tag').escapedName)
    }

    @Test
    void 'dollar is escaped only once'() {
        assertEquals('$tag', tagNamed('$$tag').escapedName)
    }

    @Test
    void 'tag name not starting with dollar is not escaped'() {
        assertEquals('tag', tagNamed('tag').escapedName)
    }

    @Test
    void 'tag without value has an empty text'() {
        assertEquals([], extractText(null))
    }

    @Test
    void 'single line text is not trimmed'() {
        def blank = '    '
        assertEquals([blank], extractText(blank))
    }

    @Test
    void 'multiline text is trimmed'() {
        def multiline = ' first \n second '
        assertEquals(['first', 'second'], extractText(multiline))
    }

    @Test
    void 'blank first line is removed in multiline text'() {
        def blankFirstLine = '    \nsecond'
        assertEquals(['second'], extractText(blankFirstLine))
    }

    @Test
    void 'blank last line is removed in multiline text'() {
        def blankLastLine = 'first\n    '
        assertEquals(['first'], extractText(blankLastLine))
    }

    @Test
    void 'blank last line is not removed if it is the only line left after the removal of the first line'() {
        def twoBlankLines = '    \n    '
        assertEquals([''], extractText(twoBlankLines))
    }
}
