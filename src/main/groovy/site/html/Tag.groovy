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

    /** The columns of whitespace used to indent this tag. */
    int indentation = 0

    /** Whether this tag should be formatted on a single line. */
    boolean inline = false

    /**
     * Whether this tag is a text containing metatag.
     * See {@link HtmlBuilder} for how this metatag works.
     */
    boolean isTextMetatag() {
        name == '_' && value != null
    }

    /**
     * Whether this tag inlines its children tags.
     * See {@link HtmlBuilder} for how this metatag works.
     */
    boolean isInlineMetatag() {
        name == '_' && value == null
    }

    /** Whether this is a self-closing tag. */
    boolean isSelfClosing() {
        name != '_' && value == null && children.empty
    }

    /** Whether this tag has children tags. */
    boolean isParent() {
        name != '_' && value == null && !children.empty
    }

    /** If the tag name starts with the <code>$</code> sign, removes it. */
    void escapeName() {
        if (name.startsWith('$')) { name = name.substring 1 }
    }
}
