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
    void tagWithAnUnderscoreAsNameIsAMetatag() {
        assertTrue(tagNamed('_').metatag)
    }

    @Test
    void tagWithANameStartingWithAnUnderscoreIsNotAMetatag() {
        assertFalse(tagNamed('_tag').metatag)
    }

    @Test
    void metatagWithValueIsATextMetatag() {
        assumeTrue(textMetatag.metatag)
        assertTrue(textMetatag.textMetatag)
    }

    @Test
    void metatagWithoutValueIsAnInliningMetatag() {
        assumeTrue(inliningMetatag.metatag)
        assertTrue(inliningMetatag.inliningMetatag)
    }

    @Test
    void textMetatagIsNotAnInliningMetatag() {
        assumeTrue(textMetatag.textMetatag)
        assertFalse(textMetatag.inliningMetatag)
    }

    @Test
    void inliningMetatagIsNotATextMetatag() {
        assumeTrue(inliningMetatag.inliningMetatag)
        assertFalse(inliningMetatag.textMetatag)
    }

    @Test
    void tagIsEmptyWhenHasNoValueNorChildrenTags() {
        assumeFalse(emptyTag.metatag)
        assertTrue(emptyTag.empty)
    }

    @Test
    void tagIsParentTagWhenHasNoValueButHasChildrenTags() {
        assumeFalse(parentTag.metatag)
        assertTrue(parentTag.parent)
    }

    @Test
    void emptyTagIsNotAParentTag() {
        assumeTrue(emptyTag.empty)
        assertFalse(emptyTag.parent)
    }

    @Test
    void parentTagIsNotAnEmptyTag() {
        assumeTrue(parentTag.parent)
        assertFalse(parentTag.empty)
    }

    @Test
    void textTagIsNotAnEmptyTag() {
        assumeFalse(textTag.metatag)
        assertFalse(textTag.empty)
    }

    @Test
    void textTagIsNotAParentTag() {
        assumeFalse(textTag.metatag)
        assertFalse(textTag.parent)
    }

    @Test
    void tagNameStartingWithDollarIsEscapedByRemovingIt() {
        assertEquals('tag', tagNamed('$tag').escapedName)
    }

    @Test
    void dollarIsEscapedOnlyOnce() {
        assertEquals('$tag', tagNamed('$$tag').escapedName)
    }

    @Test
    void tagNameNotStartingWithDollarIsNotEscaped() {
        assertEquals('tag', tagNamed('tag').escapedName)
    }

    @Test
    void tagWithoutValueHasAnEmptyText() {
        assertEquals([], extractText(null))
    }

    @Test
    void singleLineTextIsNotTrimmed() {
        def blank = '    '
        assertEquals([blank], extractText(blank))
    }

    @Test
    void multilineTextIsTrimmed() {
        def multiline = ' first \n second '
        assertEquals(['first', 'second'], extractText(multiline))
    }

    @Test
    void blankFirstLineIsRemovedInMultilineText() {
        def blankFirstLine = '    \nsecond'
        assertEquals(['second'], extractText(blankFirstLine))
    }

    @Test
    void blankLastLineIsRemovedInMultilineText() {
        def blankLastLine = 'first\n    '
        assertEquals(['first'], extractText(blankLastLine))
    }

    @Test
    void blankLastLineIsNotRemovedIfItIsTheOnlyLineLeftAfterTheRemovalOfTheFirstLine() {
        def twoBlankLines = '    \n    '
        assertEquals([''], extractText(twoBlankLines))
    }
}
