package site.html

class HtmlBuilder extends BuilderSupport {

    private PrintWriter out;

    HtmlBuilder(Writer out) { this.out = new PrintWriter(out) }
    HtmlBuilder(OutputStream out) { this.out = new PrintWriter(out) }
    HtmlBuilder() { this(System.out) }

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
    }

    void nodeCompleted(parent, node) {
        if (parent == null) { format node }
    }

    private void format(node) {
        open node
        if (node.value != null) {
            out.print node.value
            close node
        } else if (node.children) {
            out.println()
            node.children.each { format it }
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
}
