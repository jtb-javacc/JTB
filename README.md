# Java Tree Builder (JTB)

## Overview

<span style="color:red">JTB</span> is a *syntax tree builder* to be used with the <span style="color:red">JavaCC</span> (Java Compiler Compiler) parser generator.  

JTB was originally developed at UCLA up to version 1.3.2 [JTB 132](http://compilers.cs.ucla.edu/jtb/) or [JTB < 1.3.2](http://compilers.cs.ucla.edu/jtb/jtb-2003/).  

It has been upgraded in version 1.4.0+ (2009-2010), for a real life project - a translator for the Uniface L3G language (PROC) to Java, ran on 5000+ source files in less than 30 mn -, and has been embedded within the [Eclipse JavaCC plugin](https://sourceforge.net/projects/eclipse-javacc/).

From 2011 to 2016 it was also provided as a stand-alone tool in the same hosting environment as JavaCC (java.net), which closed.

Since 2017 it is also provided as a stand-alone tool in the same new hosting environment as JavaCC [JTB at GitHub](https://github.com/jtb-javacc/JTB).

Version 1.5.0 brings interesting new features. See [Overview page](doc/wiki/Overview.md) in Wiki.

## Version

Last update : Sept, 2023 - version 1.5.1 committed on GitHub (version 1.5.0 was never committed).  

## Requirements

JTB needs a JDK 1.8+ since version 1.5.0.  
Previous versions needed JDK 1.7+.

## Wiki

See the [project's Wiki page](doc/wiki/Home.md).

## Release notes

See [Release notes page](doc/wiki/Release_notes.md) in Wiki.

## Short history

04/2021 - 1.5.0 : New features, some fixes, internal refactoring, still aligned with JavaCC 5.0 / 7.0.x (see [Release notes](doc/wiki/Release_notes.md) and [How to use](doc/wiki/How_to_use.md)):  

11/2009 - 1.4.0 -> 12/2017 - 1.4.13 : New version published (aligned with JavaCC 4.2 / 5.0), also integrated under the JavaCC Eclipse Plug-in; ongoing fixes  

xx/2005 - 1.3.2 : Last (known) version from UCLA Compilers Group : see JTB 1.3.2 home page  

## Support

See [Support page](doc/wiki/Support.md) in Wiki.

## Licenses

JTB 1.3.2 was licensed under the [BSD license](http://compilers.cs.ucla.edu/jtb/license.html).  

JTB  1.4.x+ adds the [GPL license](http://www.gnu.org/licenses/gpl.html) and the [CECILL license](https://opensource.org/licenses/CECILL-2.1).  

