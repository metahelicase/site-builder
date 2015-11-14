package site

/** Site plugin configuration. */
class SitePluginExtension {

    /** The directory containing the document generation scripts. */
    final String srcDir = 'src/main/site'

    /** The directory containing the site resources, like images, CSS and JavaScript files. */
    final String resourcesDir = 'src/main/resources'

    /** The target directory where the generated pages and the site resources are put. */
    final String buildDir = 'build/site'

    /** The tab width used to indent HTML tags, default is 4 spaces. */
    int indentation = 4

    /** The path where the site will be deployed on the target host, default is {@code /}. */
    String root = '/'
}
