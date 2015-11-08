package site.html

class Tag {

    String name
    Map attributes = [:]
    Object value

    List<Tag> children = []

    int indentation = 0
    boolean inline = false

    boolean isTextMetatag() {
        name == '_' && value != null
    }

    boolean isInlineMetatag() {
        name == '_' && value == null
    }

    boolean isSelfClosing() {
        name != '_' && value == null && children.empty
    }

    boolean isParent() {
        name != '_' && value == null && !children.empty
    }

    void escapeName() {
        if (name.startsWith('$')) { name = name.substring 1 }
    }
}
