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
        indent node
        open node
        if (node.value != null) {
            out.print node.value
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
}
