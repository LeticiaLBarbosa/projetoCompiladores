﻿Java Compiler Project
====================

This is just a project to exercise the concepts and theory of compilers. This is a JAVA compiler and it will be built using JFlex and Cup.

## Tools ##
### JFlex 1.6###

JFlex is a scanner generator. JFlex reads a lexical specification and outputs java sources for the generated scanner. If you don't have experience with JFlex, access the [official website](http://jflex.de/) and try it! See the ```README``` file for more information and examples.

### CUP 11b ###
CUP is a LALR (lookahead-LR method) parser generator. CUP reads a syntacitc specification and outputs the java sources for the generated parser. If you dont't have experience with CUP, access the [manual](https://www.cs.princeton.edu/~appel/modern/java/CUP/manual.html#intro) for more information.

## Building the Project ##

* First, make sure you have [Ant](http://ant.apache.org/) installed in your computer.
* By this moment, you just need to run ```ant``` on the project's root directory.
