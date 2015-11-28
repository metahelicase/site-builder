/**
 * DSL for HTML documents generation.
 *
 * <h2>HTML building process</h2>
 *
 * HTML tags are inserted by calling methods with the relative tag name.
 * Tag attributes are specified as named parameters and tag content as the single unnamed parameter.
 * Children tags can be added in a closure following the parent tag definition.
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
 * generates the following HTML code:
 *
 * <pre><code>&lt;html lang="en"&gt;
 *    &lt;head&gt;
 *        &lt;title&gt;Generated Page&lt;/title&gt;
 *    &lt;/head&gt;
 *    &lt;body&gt;
 *        &lt;h1&gt;An HTML generation example&lt;/h1&gt;
 *    &lt;/body&gt;
 *&lt;html&gt;</code></pre>
 *
 * <h2>Metatags</h2>
 *
 * Some kind of HTML layout cannot be specified by using only tags, for example when interleaving text and formatting tags.
 * Therefore the DSL provides a small set of special tags, called <em>metatags</em>, that control the HTML document layout.
 *
 * <h3>The text metatag</h3>
 *
 * The DSL defines the special metatag {@code _} (underscore) that has two behaviours.
 * When followed by a value, it generates text without a wrapping tag.
 * This can be used when writing text interleaved with tags.
 * <p>
 * For example
 *
 * <pre><code>_ 'This text'
 *em 'emphasizes'
 *_ 'the _ tag usage'</code></pre>
 *
 * generates
 *
 * <pre><code>This text
 *&lt;em&gt;emphasizes&lt;/em&gt;
 *the _ tag usage</code></pre>
 *
 * <h3>The inlining metatag</h3>
 *
 * The second behaviour is children inlining.
 * When followed by a closure, the {@code _} metatag puts on a single line tags and multiline text.
 * <p>
 * For example
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
 * <pre><code>&lt;span id="example"&gt;All this code is &lt;em&gt;inlined&lt;/em&gt; by the &lt;code&gt;_&lt;/code&gt; tag&lt;/span&gt;</code></pre>
 *
 * <h3>The extension metatag</h3>
 *
 * The DSL provides the <code>$</code> (dollar) command metatag that executes a callable object by passing it the builder currently in use.
 * This can be used to include configurable HTML snippets.
 * <p>
 * For example
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
 *<pre><code>&lt;div id="example"&gt;
 *    &lt;a href="#"&gt;A link&lt;/a&gt;
 *&lt;/div&gt;</code></pre>
 *
 * <h2>Escaped tag names</h2>
 *
 * The HTML builder does not define methods with a name that belongs to an HTML tag.
 * However, if a custom tag is used and its name matches one of the builder's method names, then it can be prepended by the <code>$</code> symbol.
 * The builder will remove the first heading <code>$</code> sign before assigning a tag the given name.
 */
package site.html
