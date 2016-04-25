---
title: Site Builder
layout: default
root: "./"
---

# Site Builder Gradle Plugin

*Build HTML documents with modular snippets and templates written in groovy*

The Site Builder gradle plugin is a static site generation tool.
It's goal is to avoid writing HTML pages directly, but at the same time to let an high degree of site customization.

HTML documents have often repeated structures, like headers, footers, images with captions and section layouts.
Most static site generation tools define a custom markup language inside HTML that can be used to include external snippets and to configure templates.
They also define a context within every page, so variable usages are substituted with their value when the document is built.

Site Builder inverts this approach: the only language used to structure documents is groovy.
HTML tags are inserted into the document by using groovy method calls, where the method name corresponds to the tag name and the method arguments are the tag's attributes and content.
With Site Builder you can use an entire programming language to define HTML documents in a cleaner and more concise way.

[Learn how to use Site Builder by examples](./examples "Site Builder by Examples")

### Quick Setup

Copy the following [gradle](http://gradle.org/gradle-download/ "Download gradle") script to a file named `build.gradle`

{% highlight groovy %}
plugins {
    id 'org.metahelicase.site-builder' version '1.0'
}
{% endhighlight %}

then initialize the directory layout of the project by running the task

```
gradle initSite
```

Now you can build the generated example page with

```
gradle site
```
