# Mapcode Library for Scala

Copyright (C) 2014-2015 Stichting Mapcode Foundation (http://www.mapcode.com)

----

# License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Original C library created by Pieter Geelen. Work on Java version
of the mapcode library by Rijn Buve and Matthew Lowden.

Scala version developed by Eric Bowman and Ruud Diterwich.

# Getting started

This library uses scala. To develop, you need [sbt](http://http://www.scala-sbt.org/).

To run the tests:

	$ sbt test

To work in a continuous compile & test loop, run the sbt CLI:

	$ sbt
	[info] Loading global plugins from /Users/ebowman/.sbt/0.13/plugins
	[info] Loading project definition from /Users/ebowman/src/mapcode-scala/project
	[info] Set current project to mapcode-scala (in build file:/Users/ebowman/src/mapcode-scala/)
	>

Then from there, you can leave sbt in a continuous “detect changes and compile and run the tests” loop like:

	> ~test

To generate a coverage report, we use [scoverage](https://github.com/scoverage/sbt-scoverage). You can run this and view the coverage report like:

	$ sbt clean coverage test

This will generate an HTML report in ``./target/scala-2.11/scoverage-report/index.html``.

You can learn more about mapcode at the [mapcode website](http://www.mapcode.com/). This implementation was based on the Java implementation at [https://github.com/mapcode-foundation/mapcode-java](https://github.com/mapcode-foundation/mapcode-java).

# Using Git and `.gitignore`

It's good practice to set up a personal global .gitignore file on your machine which filters a number of files
on your file systems that you do not wish to submit to the Git repository. You can set up your own global
`~/.gitignore` file by executing:
`git config --global core.excludesfile ~/.gitignore`

In general, add the following file types to `~/.gitignore` (each entry should be on a separate line):
`*.com *.class *.dll *.exe *.o *.so *.log *.sql *.sqlite *.tlog *.epoch *.swp *.hprof *.hprof.index *.releaseBackup *~`

If you're using a Mac, filter:
`.DS_Store* Thumbs.db`

If you're using IntelliJ IDEA, filter:
`*.iml *.iws .idea/`

If you're using Eclips, filter:
`.classpath .project .settings .cache`

If you're using NetBeans, filter:
`nb-configuration.xml *.orig`

The local `.gitignore` file in the Git repository itself to reflect those file only that are produced by executing
regular compile, build or release commands, such as:
`target/ out/`

# Code Style Settings in IntelliJ IDEA

Code style in Scala is not quite as rigorous as in Java. The focus is more on
readability rather than necessarily a precise formatting style. Scala is a lot
more expressive than Java, and sometimes different styles make sense in context
to impart maximum readability on what can be some very dense code.

Having said that, this code has run through the default Scala style formatter
in IntelliJ 13.1.5 w/Scala plugin 0.41.2. It doesn't seem to be possible to
export exactly what those settings are; when you try to export, it just refers
to the current defaults.

If you are modifying these files, please try to follow the style that is in
them. I tend to use the latest IntelliJ default style, which does evolve slowly
over time.

