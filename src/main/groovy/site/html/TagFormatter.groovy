package site.html

/**
 * Formats an HTML tag based on its kind.
 * Tags can be either:
 *
 * <ul>
 * <li>text metatag</li>
 * <li>inlining metatag</li>
 * <li>empty tag</li>
 * <li>parent tag</li>
 * <li>text tag</li>
 * </ul>
 *
 * See the package description for a description about tag kinds and metatags.
 */
abstract class TagFormatter {

    /** The stream where the HTML tags are written to. */
    PrintWriter out

    /** Formats the given tag writing it directly to the configured stream. */
    void format(Tag tag) {
        if (tag.textMetatag) { formatLines tag.text }
        else if (tag.inliningMetatag) { formatInlined tag.children }
        else if (tag.empty) { formatEmpty tag }
        else if (tag.parent) { formatParent tag }
        else { formatText tag }
    }

    /** Formats the text of a text metatag. */
    abstract void formatLines(List<String> text);

    /** Formats a sequence of tags on a single line. */
    abstract void formatInlined(List<Tag> tags);

    /** Formats the given tag without its closing counterpart. */
    void formatEmpty(Tag tag) {
        pad(); open tag; newLine()
    }

    /** Formats the given tag, and recursively all its children on a nested level of indentation. */
    void formatParent(Tag tag) {
        pad(); open tag; newLine()
        indented().format tag.children
        pad(); close tag; newLine()
    }

    /**
     * Formats the given tag and the text it encloses.
     * If the text lays on a single line, the tag is formatted inlined too.
     * Otherwise the text is placed in nested lines inside the tag.
     */
    void formatText(Tag tag) {
        def text = tag.text
        if (text.tail().empty) {
            pad(); open tag; out << text.head(); close tag; newLine()
        } else {
            pad(); open tag; newLine()
            indented().formatLines text
            pad(); close tag; newLine()
        }
    }

    /** Formats a whole sequence of consecutive tags. */
    void format(List<Tag> tags) {
        tags.each { format it }
    }

    /**
     * Formats the opening tag.
     * For example, an empty tag without attributes is formatted as
     * <pre>{@code <tag>}</pre>
     * while a tag with attributes contains the attributes list
     * <pre>{@code <tag key1="value1" key2="value2" ...>}</pre>
     */
    void open(Tag tag) {
        out << '<' << tag.escapedName
        tag.attributes.each { key, value -> out << (value == null ? " $key" : " $key=\"$value\"") }
        out << '>'
    }

    /**
     * Formats the closing tag.
     * For example: {@code </tag>}.
     */
    void close(Tag tag) {
        out << '</' << tag.escapedName << '>'
    }

    /** Formats a blank padding based on the current nesting depth and the indentation size. */
    abstract void pad();

    /** Starts a new line. */
    abstract void newLine();

    /** Returns a formatter with the indentation level increased by one. */
    abstract TagFormatter indented();

    /**
     * Returns an indenting formatter that writes to the given stream.
     * The indentation is the tab width, that increases the padding when the nesting depth grows.
     */
    static TagFormatter indenting(PrintWriter out, int indentation, String initialPadding = '') {
        [out: out, tab: ' ' * indentation, padding: initialPadding] as Indenting
    }

    /** Returns a formatter that inlines all the tags it processes, writing them to the given stream. */
    static TagFormatter inlining(PrintWriter out) {
        [out: out] as Inlining
    }

    private static class Indenting extends TagFormatter {

        String tab
        String padding

        void pad() {
            out << padding
        }

        void newLine() {
            out.newLine()
        }

        Indenting indented() {
            [out: out, tab: tab, padding: padding + tab]
        }

        void formatLines(List<String> text) {
            text.each { line ->
                if (!line.empty) { out << padding << line }
                out.newLine()
            }
        }

        void formatInlined(List<Tag> tags) {
            pad(); inlining(out).format tags; newLine()
        }
    }

    private static class Inlining extends TagFormatter {

        void pad() {}

        void newLine() {}

        Inlining indented() {
            this
        }

        void formatLines(List<String> text) {
            out << text.head()
            text.tail().each { line ->
                if (!line.empty) { out << ' ' << line }
            }
        }

        void formatInlined(List<Tag> tags) {
            format tags
        }
    }
}
