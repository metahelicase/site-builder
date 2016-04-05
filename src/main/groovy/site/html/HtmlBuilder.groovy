package site.html

/** Defines a DSL for HTML code generation. */
class HtmlBuilder extends BuilderSupport {

    private final TagFormatter formatter

    /**
     * Constructs a builder that writes HTML to the given writer, indenting tags by the given tab width.
     */
    HtmlBuilder(Writer out, int indentation = 4) {
        formatter = TagFormatter.indenting(new PrintWriter(out), indentation)
    }

    /**
     * Constructs a builder that writes HTML to the given stream, indenting tags by the given tab width.
     */
    HtmlBuilder(OutputStream out, int indentation = 4) {
        formatter = TagFormatter.indenting(new PrintWriter(out), indentation)
    }

    /**
     * Constructs a builder that writes HTML to the standard output, indenting tags by the given tab width.
     */
    HtmlBuilder(int indentation = 4) {
        this(System.out, indentation)
    }

    /**
     * Runs the given callable object with this builder as its delegate (if the extension is a closure) or as its argument.
     * This method can be used to include HTML snippets in the document by running the relative snipped generation code.
     */
    HtmlBuilder $(Object extension) {
        if (extension in Closure) {
            extension.delegate = this
            extension()
        } else {
            extension this
        }
        return this
    }

    /** Matches a tag without value and attributes. */
    Tag createNode(Object name) {
        [name: name]
    }

    /** Matches a text tag with a value. */
    Tag createNode(Object name, Object value) {
        [name: name, value: value]
    }

    /** Matches a tag with a set of key-value attributes. */
    Tag createNode(Object name, Map attributes) {
        [name: name, attributes: attributes]
    }

    /** Matches a tag with a value and a set of key-value attributes. */
    Tag createNode(Object name, Map attributes, Object value) {
        [name: name, attributes: attributes, value: value]
    }

    /** Assignes the child tag to its parent. */
    void setParent(Object parent, Object child) {
        parent.children << child
    }

    /** Formats recursively a top-level tag. */
    void nodeCompleted(Object parent, Object node) {
        if (parent == null) { formatter.format node }
    }

    /**
     * Invoked after every node creation, returns this builder so tag declarations can be chained on a single line.
     */
    HtmlBuilder postNodeCompletion(Object parent, Object node) {
        this
    }
}
