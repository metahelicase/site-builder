package site.html

/** HTML tag properties. */
class Tag {

    /** The string that identifies this tag. */
    String name

    /** Key-value pairs, considered flags when they have a {@code null} value. */
    Map attributes = [:]

    /** Evaluates to the string enclosed by this tag. */
    Object value

    /** The tags contained by this one. */
    List<Tag> children = []

    /**
     * Whether this tag is a metatag.
     * See the {@link site.html package info} about metatags usage.
     */
    boolean isMetatag() {
        name == '_'
    }

    /**
     * Whether this tag is a metatag containing text.
     * See the {@link site.html package info} for how this metatag works.
     */
    boolean isTextMetatag() {
        metatag && value != null
    }

    /**
     * Whether this tag is a metatag that inlines its children tags.
     * See the {@link site.html package info} for how this metatag works.
     */
    boolean isInliningMetatag() {
        metatag && value == null
    }

    /**
     * Whether this is an empty tag.
     * An empty tag is a tag without its closing counterpart, but can still have attributes.
     */
    boolean isEmpty() {
        !metatag && value == null && children.empty
    }

    /** Whether this tag has children tags. */
    boolean isParent() {
        !metatag && value == null && !children.empty
    }

    /** If the tag name starts with the <code>$</code> sign, removes it and returns the escaped name. */
    String getEscapedName() {
        name.startsWith('$') ? name.substring(1) : name
    }

    /**
     * Returns the textual representation of the tag's value.
     * If the text contains a single line, it is returned as is, otherwise the text lines are trimmed.
     * In the multiline case, the first and the last line are dropped if blank.
     */
    List<String> getText() {
        if (value == null) { return [] }
        def text = value.toString().split('\n').toList()
        isSingleLine(text) ? text : (Tag.&removeFirstLineIfEmpty >> Tag.&removeLastLineIfEmpty)(text*.trim())
    }

    private static boolean isSingleLine(List<String> text) {
        text.tail().empty
    }

    private static List<String> removeFirstLineIfEmpty(List<String> text) {
        (!isSingleLine(text) && text.head().empty) ? text.tail() : text
    }

    private static List<String> removeLastLineIfEmpty(List<String> text) {
        (!isSingleLine(text) && text.last().empty) ? text.init() : text
    }
}
