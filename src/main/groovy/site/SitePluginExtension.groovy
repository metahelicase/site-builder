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

    /** Global variables shared among all the site's pages. */
    Expando global = new Expando()
}
