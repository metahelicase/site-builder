package site.html

class HtmlBuilder extends BuilderSupport {

    private final PrintWriter out
    private final int indentation

    HtmlBuilder(Writer out, int indentation = 4) {
        this.out = new PrintWriter(out)
        this.indentation = indentation
    }

    HtmlBuilder(OutputStream out, int indentation = 4) {
        this.out = new PrintWriter(out)
        this.indentation = indentation
    }

    HtmlBuilder(int indentation = 4) { this(System.out, indentation) }

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
