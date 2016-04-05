# Site Builder Gradle Plugin

Build HTML documents with modular snippets and templates written in groovy.

[![Build Status](https://travis-ci.org/metahelicase/site-builder.svg?branch=master)](https://travis-ci.org/metahelicase/site-builder)

## Quick setup

Copy the following gradle script to a file named `build.gradle`

```
plugins {
    id 'org.metahelicase.site-builder' version '1.0'
}
```

then initialize the directory layout of the project by running the task

```
gradle initSite
```

Now you can build the generated example page with

```
gradle site
```

More details can be found at [site-builder.metahelicase.org](http://site-builder.metahelicase.org/)
