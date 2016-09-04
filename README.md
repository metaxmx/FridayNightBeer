Friday Night Beer
=================

[![dependency Status](https://david-dm.org/metaxmx/FridayNightBeer/status.svg?branch=master)](https://david-dm.org/metaxmx/FridayNightBeer)
[![devDependency Status](https://david-dm.org/metaxmx/FridayNightBeer/dev-status.svg?branch=master)](https://david-dm.org/metaxmx/FridayNightBeer?type=dev)


Author: Christian Simon (<simon@illucit.com>)  
Copyright: [illucIT Software GmbH](https://www.illucit.com)  
Version 0.1 Alpha (In Progress)

About
-----

FNB (Friday Night Beer) is a forum and social platform to communicate,
plan events and share pictures and comments.

Requirements
------------

* Any OS with Java Support
* Java 1.8
* Lightbend Activator or SBT
* MongoDB
* Apache HTTPD (Optional as Proxy)

Installation
------------

* Install and start MongoDB Server
* Install Java Development Toolkit (JDK), at least version 1.8
* Install Lightbend Activator *or* install sbt (Scala Build Tool)
* Checkout FNB sourcecode to target directory
  * Create directory `appdata`, writeable for application user
  * Copy application configuration `cp conf/application.conf conf/instance.conf`,
    change application settings (database settings, fnb settings) to your needs and
    remove all unchanged properties and the include statement
  * Copy public application assets: `cp public/appdata_template public/appdata` and change to your needs (e.g. favicon, logo, ...)
* Run `activator` or `sbt`
  * Activator/sbt will download project dependencies
  * In the interactive shell, execute `run` to start the Play application

Typescript Compiler Integration
-------------------------------

The frontend uses Angular2 and is written in TypeScript.
To compile the project typescript files against Angular2, the packages from Angular2 (`@angular/core` etc.) are required.
Install Node.js (including npm) on your machine, go to the project root and run
 
    npm install

The dependencies will be downloaded into a `node-modules` subfolder, which will be used by the TypeScript resolution, but is in the `.gitignore` file.

Software Used
-------------

* [Scala](http://www.scala-lang.org/) Version 2.11.8
* [SBT](http://www.scala-sbt.org/) (Scala Build Tool) Version 0.13.11
* [Play Framework](https://www.playframework.com/) Version 2.5.4
* [ReactiveMongo](http://reactivemongo.org/) Version 0.11.11
* [AngularJS](https://angular.io/) Version 2.0.0-RC1
* [Bootstrap](http://getbootstrap.com/) Version 3.3.4
* Diverse general libraries and utilities: jQuery, Guice, Guava, Joda-Time, Angular Add-Ons, Polyfills
* Frontend Assets for the Themes:
  * [Font Awesome](http://fortawesome.github.io/Font-Awesome/)
  * [FamFamFam Silk Icons](http://famfamfam.com/lab/icons/silk/)
  * Crystal Project Icons by [Everaldo Coelho](http://www.everaldo.com)


Planned Milestones
------------------

* [ ] Version 0.1 Alpha: Basic Forum Features:
  * [x] Login / Logout
  * [x] List Forums
  * [x] Show Topics in Forum
  * [x] Show Posts in Forum
  * [ ] Reply
  * [ ] Start new Threads
  * [x] Register new users
  * [ ] Basic Post formatting, e.g. bold, italic, lists
* [ ] Version 0.2 Alpha: Advanced Forum Features:
  * [ ] Pin / Unpin Topics
  * [ ] Close Topics
  * [ ] Edit Topic Title
  * [ ] Edit Posts
  * [ ] List Users
  * [ ] Change User Profile and Login Data
  * [ ] View User Profile
  * [ ] Create / Edit / Delete / Reorder Forums
* [ ] Version 0.3 Alpha: Configure Permissions:
  * [ ] Include Concept of User Groups
  * [ ] Add / Edit / Delete Groups
  * [ ] Limit Forum Access to specific users/groups
  * [ ] Limit Topic Access to specific users/groups
  * [ ] Limit Forum / Thread Operations to specific users/groups, e.g. Create, Edit, Pin, Close etc
  * [ ] Limit Page Access to specific users/groups
* [ ] Version 0.4 Alpha: Multimedia Suspport:
  * [ ] Add Attachments to Posts
  * [ ] Download Attachments in Post
  * [ ] New feature "Media" for FNB
  * [ ] Create multimedia collections, e.g for images or videos
  * [ ] Upload multimedia files to collections, bulk upload for many files at once
  * [ ] Access media collections and download files
  * [ ] Restrict access to media collections
  * [ ] Track number of downloads for each file
  * [ ] Connect media collections with forum topics, so comments to the collection can be made
* [ ] Version 0.5 Alpha: User Experience Improvements:
  * [ ] Optimize Stylesheets and behaviour for mobile clients
  * [ ] Track visited topics / posts for each user, highlight topics/forums with new content on each visit
  * [ ] Include search for posts/topics
* [ ] Version 0.6 Alpha: Chat:
  * [ ] Include Websockets-based Chat in site
  * [ ] Due to AJAX navigation always visble when opened
  * [ ] Use chat for PM-like communication between 2 users
  * [ ] Add / Edit / Delete chat rooms
  * [ ] Limit access to chat rooms to specific users/groups
* [ ] Version 0.7 Alpha: User settings:
  * [ ] Themes support
  * [ ] Languages support
  * [ ] Configurable per user
  * [ ] Global override supported 
* [ ] Version 0.8 Alpha: Events:
  * [ ] New feature "Events" for FNB
  * [ ] Plan future events with date/time and topic
  * [ ] Poll about date/time supported, "Doodle"-like
  * [ ] Users can invite others to events
  * [ ] Users can indicate if they plan to attend events
  * [ ] Edit / Delete events
  * [ ] Events can be connected to forum threads, so comments are possible
  * [ ] Events in the past can be connected to media collections
  * [ ] List of attendees can be given for past events
* [ ] Version 0.9 Alpha: API support:
  * [ ] Public REST API to support third-party tools
  * [ ] Public API for TapaTalk Forum App
  * [ ] Content-Pages visible on the site, e.g. imprint or forum rules
  * [ ] Limit Content-Page Access to specific users/groups

