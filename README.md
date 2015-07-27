Friday Night Beer
=================

Author: Christian Simon <simon@illucit.com>  
Copyright: illucIT Software GmbH  
Version 0.1 Alpha

About
-----

FNB (Friday Night Beer) is a forum and social platform to communicate,
plan events and share pictures and comments.

Requirements
------------

* Any OS with Java Support
* Java 1.6
* Typesafe Activator
* MongoDB
* Apache HTTPD (Optional as Proxy)

Installation
------------

* Install and start MongoDB Server
* Install Java Development Toolkit (JDK), at least version 1.6
* Install Typesafe Activator
* Checkout FNB sourcecode to target directory
  * Create directory `appdata`, writeable for application user
  * Copy application configuration `cp conf/application.conf conf/instance.conf`,
    change application settings (database settings, fnb settings) to your needs and
    remove all unchanged properties and the include statement
  * Copy public application assets: `cp public/appdata_template public/appdata` and change to your needs (e.g. favicon, logo, ...)
* Run `activator`
  * Activator will download dependencies with `sbt` (Scala build tool)
  * In the interactive shell, execute `run` to start the Play! application
