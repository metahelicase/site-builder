---
title: Examples
layout: default
---

# Site Builder by Examples

### Does an empty script generate anything?

Yes. An empty document.

### How do I declare an empty tag, like `<br>`?

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

Actually there isn't a set of predefined methods that get translated to tags.
The method name itself is used as the tag name, so any name can be specified, even non valid HTML.
Groovy lets you call methods with a name containing illegal characters by quoting it.
With this feature you can write

{% highlight html %}
<!DOCTYPE html>
{% endhighlight %}

with the method call

{% highlight groovy %}
'!DOCTYPE html'()
{% endhighlight %}

> This does not make writing `'?php ... ?'()` a good idea...
>
> -- <cite>Your Conscience</cite>

### And a tag containing text?

A method called with a string parameter generates a tag containing that text.
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
