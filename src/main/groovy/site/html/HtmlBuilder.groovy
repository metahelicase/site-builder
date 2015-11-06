package site.html

class HtmlBuilder extends BuilderSupport {

    private final PrintWriter out
    final int indentation

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
        child.indentation = parent.indentation + indentation
    }

    void nodeCompleted(parent, node) {
        if (parent == null) { format node }
    }

    private void format(node) {
        if (node.name == '_') {
            formatText node
            return
        }
        escape node
        indent node
        open node
        if (node.value != null) {
            formatTextOf node
            close node
        } else if (node.children) {
            out.println()
            node.children.each { format it }
            indent node
            close node
        }
        out.println()
    }

    private void open(node) {
        out.print "<$node.name"
        node.attributes.each { key, value -> out.print((value != null) ? " $key=\"$value\"" : " $key") }
        out.print '>'
    }

    private void close(node) {
        out.print "</$node.name>"
    }

    private void indent(node) {
        out.print ' ' * node.indentation
    }

    private void escape(node) {
        if (node.name.startsWith('$')) { node.name = node.name.substring 1 }
    }

    private void formatText(node) {
        def text = node.value.toString()
        if (text.empty) {
            out.println()
        } else if (!text.contains('\n')) {
            out.print ' ' * node.indentation
            out.println text
        } else {
            def lines = text.split('\n').collect { it.trim() }
            if (lines.head().empty) { lines = lines.tail() }
            if (lines.last().empty) { lines = dropLastIn lines }
            def padding = ' ' * node.indentation
            lines.each { line ->
                if (!line.empty) {
                    out.print padding
                    out.print line
                }
                out.println()
            }
        }
    }

    private void formatTextOf(node) {
        def text = node.value.toString()
        if (!text.contains('\n')) {
            out.print text
        } else {
            def lines = text.split('\n').collect { it.trim() }
            if (lines.head().empty) { lines = lines.tail() }
            if (lines.last().empty) { lines = dropLastIn lines }
            def padding = ' ' * (node.indentation + indentation)
            out.println()
            lines.each { line ->
                if (!line.empty) {
                    out.print padding
                    out.print line
                }
                out.println()
            }
            indent node
        }
    }

    private static List dropLastIn(List list) {
        list.subList(0, list.size() - 1)
    }
}
