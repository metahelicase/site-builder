---
title: Examples
layout: default
---

# Site Builder by Examples

Here is presented a tutorial that covers the basics of Site Builder usage and configuration.
It is structured following a question & answer scheme that introduces the tool in an intuitive way.
Advanced features and use cases are not presented in this tutorial.

## Scripting

The most important kind of file you will write using Site Builder is the *document script*.
It is a one-to-one mapping to an HTML document that will be included in your site.
The HTML document will have the same path and the same name of the script that generates it.

Alongside the document scripts, you can write any groovy class that defines a snippet or a template.
They can be instantiated by a document script and used to produce a piece of HTML in a concise and clear way.
Snippets and templates are introduced in the following section.

### Does an empty script generate anything?

Yes, an empty file.

### How do I declare an empty tag?

> Empty tags are those that don't have a closing counterpart, like `<br>`.

By calling a method with the same name as the desired tag, without any parameter.
The method call

{% highlight groovy %}
br()
{% endhighlight %}

generates the tag

{% highlight html %}
<br>
{% endhighlight %}

Note that the generated tag is not compliant with XHTML, where empty tags must end with a /, like `<br />`.

### What is the set of HTML tags I can use?

Actually there isn't a set of predefined methods representing HTML tags.
The method name itself is used as the tag name, so any name can be specified, even non valid HTML.
In addition, groovy lets you call methods with a name containing illegal characters by quoting it.
With this feature you can write

{% highlight html %}
<!DOCTYPE html>
{% endhighlight %}

with the method call

{% highlight groovy %}
'!DOCTYPE html'()
{% endhighlight %}

> This doesn't make writing `'?php ... ?'()` a good idea...
>
> -- <cite>Your Conscience</cite>

### How do I declare a tag containing text?

A method called with a single parameter generates a tag containing that text.
For example

{% highlight groovy %}
p 'some text'
{% endhighlight %}

generates

{% highlight html %}
<p>some text</p>
{% endhighlight %}

### What if the contained text is multiline?

The groovy language let you write multiline strings, surrounded by triple quotes.
Too keep things neat, you can start writing the text on a new line and end it with a blank one.
Site builder ignores a blank first line and a blank last one.
Therefore writing

{% highlight groovy %}
p '''multiline
    text'''
{% endhighlight %}

or

{% highlight groovy %}
p '''
    multiline
    text
'''
{% endhighlight %}

generates the same HTML:

{% highlight html %}
<p>
    multiline
    text
</p>
{% endhighlight %}

Internal blank lines are preserved, resulting in a blank line in the target document.

{% highlight groovy %}
p '''
    This is a multiline text.
    The first and the last line are dropped if blank.

    Internal blank lines are left instead.
'''
{% endhighlight %}

The generated tag will contain the same lines, properly indented.

{% highlight html %}
<p>
    This is a multiline text.
    The first and the last line are dropped if blank.

    Internal blank lines are left instead.
</p>
{% endhighlight %}

### So the tag value must be of the string type...

No, the tag value can be any groovy object.
Before the tag is rendered to the document, the value is converted to a string by calling its `toString` method.

{% highlight groovy %}
def number = BigInteger.valueOf(1234567890)
p number
{% endhighlight %}

becomes

{% highlight html %}
<p>1234567890</p>
{% endhighlight %}

### How do I add attributes to a tag?

A method can be called with named parameters, which are translated to the tag's attributes.
Attribute values can be any groovy object, not only strings.
Their text value will be evaluated by the object's `toString` method.
Therefore

{% highlight groovy %}
// simple text attribute
img(src: '/images/logo.png')

// an empty string is translated
// to an empty attribute
input(disabled: '')

// if null, the attribute
// won't have a value
input(disabled: null)

// the value can be any object
def uri = new URI('/images/logo.png')
img(src: uri)
{% endhighlight %}

is translated to

{% highlight html %}
<img src="/images/logo.png">
<input disabled="">
<input disabled>
<img src="/images/logo.png">
{% endhighlight %}

### What about nesting tags?

A method call can be followed by a [closure](http://www.groovy-lang.org/closures.html "Groovy Closures").
It opens a subcontext where children tags can be defined, which are formatted to the target document indented inside the enclosing tag.
For example

{% highlight groovy %}
html {
    head {
        title 'Children Tags'
    }
    body {
        div {
            h3 'Are indented properly'
            p 'Inside the document.'
        }
    }
}
{% endhighlight %}

generates the document

{% highlight html %}
<html>
    <head>
        <title>Children Tags</title>
    </head>
    <body>
        <div>
            <h3>Are indented properly</h3>
            <p>Inside the document.</p>
        </div>
    </body>
</html>
{% endhighlight %}

### Is it possible to interleave text and tags?

Tags sometimes come within text, like formatting tags as `em`, `strong` and `code`.
However this behaviour cannot be achieved by using only methods.

Site Builder has a particular way to add text to the document without a containing tag: the *text metatag*.
It is a special tag named `_`, that accepts a value as its argument and formats it without adding any tag.
You can interleave text and tags like in

{% highlight groovy %}
p {
    _ 'Interleaving'
    em 'text'
    _ 'and'
    strong 'tags'
}
{% endhighlight %}

that generates

{% highlight html %}
<p>
    Interleaving
    <em>text</em>
    and
    <strong>tags</strong>
</p>
{% endhighlight %}

### Sorry but it looks like a...

Bad solution.

In fact you cannot remove spaces that are introduced by the new lines that are inserted after every tag end.
And a long text with nested tags inside it will be formatted as an ugly mess.

Text portions in an HTML document should be aggregated in separate lines to emphasize the cohesion of their content.
The *inlining metatag* solves this problem: it instructs the builder to format all its children on a single line.
It is represented by the special tag `_`, like the text metatag but accepting a subcontext closure instead of a value.
The script

{% highlight groovy %}
_ {
    p {
        _ 'Interleaving '
        em 'and'
        _ ' inlining.'
    }
}
{% endhighlight %}

generates the HTML document with the paragraph formatted on a single line:

{% highlight html %}
<p>Interleaving <em>and</em> inlining.</p>
{% endhighlight %}

Note that this time you must insert whitespace to separate words and tags where needed by yourself.
