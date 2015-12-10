---
title: Examples
layout: default
---

# Site Builder by Examples

Here is presented a tutorial that covers the basics of Site Builder usage and configuration.
Advanced features and use cases are not presented here.

## Scripting

The most important kind of file you will write using Site Builder is the *document script*.
It is a one-to-one mapping to an HTML document that will be included in your site.
The HTML document will have the same path and the same name of the script that generates, but with the extension `.html` instead of `.groovy`.

Alongside the document scripts, you can write any groovy class that defines a snippet or a template.
They can be instantiated by a document script and used to produce a piece of HTML in a concise and clear way.
Snippets and templates are introduced in the next section.

### Starting from scratch

To generate an HTML document, you have to create a groovy script under the `src/main/site` path.
You can build the new script with `gradle site`: an empty script will generate an empty document.

### Empty tags

> Empty tags are those that don't have a closing counterpart, like `<br>`.

You can write a tag by calling a method with the name of the desired tag, without any parameter.

{% highlight groovy %}
br()
{% endhighlight %}

{% highlight html %}
<br>
{% endhighlight %}

Note that the generated tag is not compliant with XHTML, where empty tags must end with a `/`, like `<br />`.
Site Builder follows the HTML5 specification instead.

### There isn't a predefined set of HTML tags

Actually there isn't a set of predefined methods representing HTML tags.
The method name itself is used as the tag name, so any name can be specified, even non valid HTML.
In addition, groovy lets you call methods with a name containing illegal characters by quoting it.
With this feature you can write

{% highlight groovy %}
'!DOCTYPE html'()
{% endhighlight %}

{% highlight html %}
<!DOCTYPE html>
{% endhighlight %}

> This doesn't make writing `'?php ... ?'()` a good idea...
>
> -- <cite>Your Conscience</cite>

### Text tags

A method called with a single parameter generates a tag containing that text.

{% highlight groovy %}
p 'some text'
{% endhighlight %}

{% highlight html %}
<p>some text</p>
{% endhighlight %}

### Multiline text tags

In groovy you can write multiline strings surrounded by triple quotes.
The generated HTML code will indent the text inside the containing tag.

{% highlight groovy %}
p '''multiline
    text'''
{% endhighlight %}

{% highlight html %}
<p>
    multiline
    text
</p>
{% endhighlight %}

Too keep things neat, you can start writing the text on a new line and end it with a blank one.
Site Builder ignores a blank first line and a blank last one.

{% highlight groovy %}
p '''
    multiline
    text
'''
{% endhighlight %}

{% highlight html %}
<p>
    multiline
    text
</p>
{% endhighlight %}

Internal blank lines of a text are preserved, resulting in a blank line in the target document.

{% highlight groovy %}
p '''
    This is a multiline text.
    The first and the last line are dropped if blank.

    Internal blank lines are preserved instead.
'''
{% endhighlight %}

{% highlight html %}
<p>
    This is a multiline text.
    The first and the last line are dropped if blank.

    Internal blank lines are preserved instead.
</p>
{% endhighlight %}

### Text can be any printable object

The tag value can be any groovy object.
Before the tag is written to the document, the value is converted to a string by calling its `toString` method.

{% highlight groovy %}
def number = BigInteger.valueOf(1234567890)
p number
{% endhighlight %}

{% highlight html %}
<p>1234567890</p>
{% endhighlight %}

### Tags with attributes

A method can be called with named parameters, which are translated to the tag's attributes.
Attribute values can be any groovy object, not only strings.
Their text value will be evaluated by the object's `toString` method.

{% highlight groovy %}
img(src: '/images/logo.png')

// an empty string is translated to an empty attribute
input(disabled: '')

// if null, the attribute won't have a value
input(disabled: null)

// the value can be any object
def uri = new URI('/images/logo.png')
img(src: uri)
{% endhighlight %}

{% highlight html %}
<img src="/images/logo.png">
<input disabled="">
<input disabled>
<img src="/images/logo.png">
{% endhighlight %}

### Nesting tags

To define a list of children tags, a method call must be followed by a [closure](http://www.groovy-lang.org/closures.html "Groovy Closures").
It opens a subcontext where children tags can be defined, which are formatted to the target document indented inside the enclosing tag.

{% highlight groovy %}
html {
    head {
        title 'Children Tags'
    }
    body {
        div {
            h3 'Nesting tags'
            p 'Use closures to define children tags.'
        }
    }
}
{% endhighlight %}

{% highlight html %}
<html>
    <head>
        <title>Children Tags</title>
    </head>
    <body>
        <div>
            <h3>Nesting tags</h3>
            <p>Use closures to define children tags.</p>
        </div>
    </body>
</html>
{% endhighlight %}

### The text metatag

Tags sometimes come within text, like formatting tags as `em`, `strong` and `code`.
However this behaviour cannot be achieved by using only method calls.
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

{% highlight html %}
<p>
    Interleaving
    <em>text</em>
    and
    <strong>tags</strong>
</p>
{% endhighlight %}

### The inlining metatag

When using the text metatag, you cannot remove spaces that are introduced by the new lines which are inserted after every tag end.
Text portions in an HTML document should be aggregated in separate lines to emphasize the cohesion of their content.

The *inlining metatag* solves this problem: it instructs the builder to format all its children on a single line.
It is represented by the special tag `_`, like the text metatag but accepting a subcontext closure instead of a value.

{% highlight groovy %}
_ {
    p {
        _ 'Interleaving '
        em 'and'
        _ ' inlining.'
    }
}
{% endhighlight %}

{% highlight html %}
<p>Interleaving <em>and</em> inlining.</p>
{% endhighlight %}

Note that this time you must insert whitespace to separate words and tags by yourself.
You can write the script on a single line too, to graphically represent the inlining effect in the target document.

{% highlight groovy %}
_ { p { _ 'Interleaving ' em 'and' _ ' inlining.' } }
{% endhighlight %}

{% highlight html %}
<p>Interleaving <em>and</em> inlining.</p>
{% endhighlight %}

## Snippets and Templates

HTML documents often contain repeated structures, like navigation buttons or thumbnails with captions.
The components can also be shared between multiple documents, which define the site look and feel.

Site Builder implements a document extension mechanism with the *extension metatag*.
Tags can be declared in a separate groovy file or within a closure, and then included into the document script.
The extension metatag is named `$` and accepts a callable object, which will be given the current builder as its argument.

{% highlight groovy %}
def extension = { builder -> ... }

$ extension
{% endhighlight %}

The distinction between snippets and templates is only conceptual.
You can think of a template as a configurable document while a snippet is a subcomponent that can be included.
The extension metatag simply considers both as document extensions.

### Extending with a closure

Small snippets that are used only in a single document can be specified as closures in the same script.
For example, if a link appears multiple times inside a document, it can be defined as a snippet.

{% highlight groovy %}
def twitter = { it.with {
    a(href: 'https://twitter.com/MetaHelicase', '@MetaHelicase')
} }

_ { _ 'Follow ' $ twitter _ ' on twitter.' }
{% endhighlight %}

{% highlight html %}
Follow <a href="https://twitter.com/MetaHelicase">@MetaHelicase</a> on twitter.
{% endhighlight %}

When the closure is defined inside the document script, it has the document builder as implicit delegate.
Therefore the closure argument can be ignored.

{% highlight groovy %}
def twitter = {
    a(href: 'https://twitter.com/MetaHelicase', '@MetaHelicase')
}

_ { _ 'Follow ' $ twitter _ ' on twitter.' }
{% endhighlight %}

{% highlight html %}
Follow <a href="https://twitter.com/MetaHelicase">@MetaHelicase</a> on twitter.
{% endhighlight %}

Snippets can be parameterized by defining a closure with parameters.
However the extension metatag requires a closure that accepts the builder as its only argument, so when invoked the closure must be armored inside an other closure.

{% highlight groovy %}
def twitter = { account ->
    a(href: "https://twitter.com/$account", "@$account")
}

_ { _ 'Follow ' $ { twitter 'MetaHelicase' } _ ' on twitter.' }
{% endhighlight %}

{% highlight html %}
Follow <a href="https://twitter.com/MetaHelicase">@MetaHelicase</a> on twitter.
{% endhighlight %}

### Extending with a class

Extensions can be defined as groovy classes under the path `src/main/groovy`.
An extension class must implement the `call` method that accepts the document builder as its argument.

For example, you can define a footer component for the HTML documents of your site.

{% highlight groovy %}
class Footer {

    String copyrightYear
    String email
    String phone

    void call(document) {
        document.with {
            footer {
                p {
                    small('style': 'color:grey;') {
                        _ "&copy; Copyright $copyrightYear, Owner,"
                        _ { _ 'email: ' a('href': "mailto:$email", email) _ ','}
                        _ { _ 'tel. ' a('href': "tel:$phone", phone) }
                    }
                }
            }
        }
    }
}
{% endhighlight %}

Then a footer instance can be configured and included into the document.

{% highlight groovy %}
$ new Footer(copyrightYear: '2015-2016', email: 'mail@example.com', phone: '001231231234
{% endhighlight %}

{% highlight html %}
<footer>
    <p>
        <small style="color:grey;">
            &copy; Copyright 2015-2016, Owner,
            email: <a href="mailto:mail@example.com">mail@example.com</a>,
            tel. <a href="tel:001231231234">001231231234</a>
        </small>
    </p>
</footer>
{% endhighlight %}
