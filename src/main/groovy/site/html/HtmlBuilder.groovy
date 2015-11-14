package site.html

/**
 * Defines a DSL for HTML code generation.
 * <p>
 * HTML tags are inserted by calling methods with the relative tag name. Tag
 * attributes are specified as named parameters and tag content as the single
 * unnamed parameter. Children tags can be added in a closure following the
 * parent tag definition.
 * <p>
 * For example
 *
 * <pre><code>html(lang: 'en') {
 *    head {
 *        title 'Generated Page'
 *    }
 *    body {
 *        h1 'An HTML generation example'
 *    }
 *}</code></pre>
 *
 * generates the following HTML code
 *
 * <pre>{@code
 *<html lang="en">
 *    <head>
 *        <title>Generated Page</title>
 *    </head>
 *    <body>
 *        <h1>An HTML generation example</h1>
 *    </body>
 *</html>
 *}</pre>
 *
 * <h2>The text metatag</h2>
 *
 * The DSL defines the special tag {@code _} (underscore) that has two
 * behaviours. When followed by a value, it generates text without a wrapping
 * tag. This can be used when writing tags inside text.
 *
 * <pre><code>_ 'This text'
 *em 'emphasizes'
 *_ 'the _ tag usage'</code></pre>
 *
 * generates
 *
 * <pre>{@code
 *This text
 *<em>emphasizes</em>
 *the _ tag usage
 *}</pre>
 *
 * The second behaviour is children inlining: when followed by a closure, the
 * {@code _} tag puts on a single line tags and multiline text.
 *
 * <pre><code>_ {
 *    span(id: 'example') {
 *        _ 'All this code is '
 *        em 'inlined'
 *        _ ' by the '
 *        code '_'
 *        _ ' tag'
 *    }
 *}</code></pre>
 *
 * generates
 *
 * <pre>{@code
 *<span id="example">All this code is <em>inlined</em> by the <code>_</code> tag</span>
 *}</pre>
 *
 * <h2>The extension metatag</h2>
 *
 * The DSL provides the <code>$</code> (dollar) command tag that executes a
 * callable object passing it the builder currently in use. This can be used to
 * include configurable HTML snippets.
 *
 * <pre><code>def link = { text, link ->
 *    return { it.a(href: link, text) }
 *}
 *
 *div(id: 'example') {
 *    $ link('A link', '#')
 *}</code></pre>
 *
 * generates
 *
 *<pre>{@code
 *<div id="example">
 *    <a href="#">A link</a>
 *</div>
 *}</pre>
 *
 * <h2>Escape tag names</h2>
 *
 * The HTML builder does not define methods with a name that belongs to an HTML
 * tag. However, if a custom tag is used and its name matches one of the
 * builder's method names, then it can be prepended by the <code>$</code>
 * symbol. The builder will remove the first heading <code>$</code> sign before
 * assigning a tag the given name.
 */
class HtmlBuilder extends BuilderSupport {

    private final PrintWriter out
    private final int indentation

    /**
     * Constructs a builder that writes HTML to the given writer, indenting tags
     * by the given tab width.
     */
    HtmlBuilder(Writer out, int indentation = 4) {
        this.out = new PrintWriter(out)
        this.indentation = indentation
    }

    /**
     * Constructs a builder that writes HTML to the given stream, indenting tags
     * by the given tab width.
     */
    HtmlBuilder(OutputStream out, int indentation = 4) {
        this.out = new PrintWriter(out)
        this.indentation = indentation
    }

    /**
     * Constructs a builder that writes HTML to the standard output, indenting
     * tags by the given tab width.
     */
    HtmlBuilder(int indentation = 4) { this(System.out, indentation) }

    /**
     * Applies the given callable object to this builder. This method can be
     * used to include HTML snippets in the document by running the relative
     * snipped generation code.
     */
    void $(extend) {
        extend this
    }

    Tag createNode(name) {
        [name: name]
    }

    Tag createNode(name, value) {
        [name: name, value: value]
    }

    Tag createNode(Object name, Map attributes) {
        [name: name, attributes: attributes]
    }

    Tag createNode(Object name, Map attributes, Object value) {
        [name: name, attributes: attributes, value: value]
    }

    void setParent(parent, child) {
        parent.children << child
        child.inline = parent.inline || parent.inlineMetatag
        child.indentation = parent.indentation + indentation
    }

    void nodeCompleted(parent, node) {
        if (parent == null) { new TagFormatter().format node }
    }

    private class TagFormatter {

        void format(Tag tag) {
            if (tag.textMetatag) { formatText tag }
            else if (tag.inlineMetatag) { formatInline tag }
            else if (tag.selfClosing) { formatSingle tag }
            else if (tag.parent) { formatParent tag }
            else { formatValue tag }
        }

        private void formatText(Tag tag) {
            def text = tag.value.toString()
            if (singleLine(text)) {
                if (!text.empty) { indent tag }
                out << text
                wrap tag
            } else {
                def padding = tag.inline ? ' ' : ' ' * tag.indentation
                formatLines tag.inline, padding, collectLinesFrom(text)
            }
        }

        private void formatInline(Tag tag) {
            indent tag
            tag.children.each { format it }
            wrap tag
        }

        private void formatSingle(Tag tag) {
            tag.escapeName()
            indent tag
            open tag
            wrap tag
        }

        private void formatParent(Tag tag) {
            tag.escapeName()
            indent tag
            open tag
            wrap tag
            tag.children.each { format it }
            indent tag
            close tag
            wrap tag
        }

        private void formatValue(Tag tag) {
            tag.escapeName()
            indent tag
            open tag
            formatValueOf tag
            close tag
            wrap tag
        }

        private void open(Tag tag) {
            out << '<' << tag.name
            tag.attributes.each { key, value -> out << (value == null ? " $key" : " $key=\"$value\"") }
            out << '>'
        }

        private void close(Tag tag) {
            out << '</' << tag.name << '>'
        }

        private void indent(Tag tag) {
            if (!tag.inline) { out << ' ' * tag.indentation }
        }

        private void wrap(Tag tag) {
            if (!tag.inline) { out.newLine() }
        }

        private void formatValueOf(Tag tag) {
            def text = tag.value.toString()
            if (singleLine(text)) {
                out << text
            } else {
                def padding = tag.inline ? ' ' : ' ' * (tag.indentation + indentation)
                wrap tag
                formatLines tag.inline, padding, collectLinesFrom(text)
                indent tag
            }
        }

        private static boolean singleLine(String text) {
            !text.contains('\n')
        }

        private void formatLines(boolean inline, String padding, List<String> lines) {
            formatPadded(inline ? '' : padding, lines.head())
            lines.tail().each { line ->
                if (!inline) { out.newLine() }
                formatPadded padding, line
            }
            if (!inline) { out.newLine() }
        }

        private void formatPadded(String padding, String line) {
            if (!line.empty) { out << padding << line }
        }

        private List<String> collectLinesFrom(String text) {
            def lines = text.split('\n')*.trim()
            if (lines.size() == 1) { return lines }
            if (lines.head().empty) { lines = lines.tail() }
            if (lines.size() == 1) { return lines }
            if (lines.last().empty) { lines = lines.subList(0, lines.size() - 1) }
            return lines
        }
    }
}
