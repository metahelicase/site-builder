package site

/** Site plugin configuration. */
class SitePluginExtension {

    /** The directory containing the document generation scripts. */
    String scriptsDir = 'src/main/site'

    /** The target directory where the generated pages and the site resources are put. */
    String buildDir = 'build/site'

    /** The tab width used to indent HTML tags, default is 4 spaces. */
    int indentation = 4

    /** The path where the site will be deployed on the target host, default is {@code /}. */
    String root = '/'

    /** Makes absolute the root path, adding a slash to the left end and the right end of the path if missing. */
    String absoluteRoot() {
        (root.startsWith('/') ? '' : '/') + root + (root.startsWith('/') ? '' : '/')
    }

    /** Global parameters shared among all the site's pages. */
    Map parameters = [:]

    def methodMissing(String name, args) {
        if (args.length > 0) { parameters[name] = args[0] }
    }

    def propertyMissing(String name, value) {
        parameters[name] = value
    }
}
