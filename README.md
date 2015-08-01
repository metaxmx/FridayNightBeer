Friday Night Beer
=================

Author: Christian Simon <simon@illucit.com>  
Copyright: illucIT Software GmbH  
Version 0.1 Alpha (In Progress)

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

Planned Milestones
------------------

* [ ] Version 0.1 Alpha: Basic Forum Features:
  * [x] Login / Logout
  * [x] List Forums
  * [x] Show Topics in Forum
  * [x] Show Posts in Forum
  * [x] Reply
  * [ ] Create / Edit / Delete / Reorder Forums
  * [x] Start new Threads
  * [ ] Register new users
  * [x] Basic Post formatting, e.g. bold, italic, lists
* [ ] Version 0.2 Alpha: Advanced Forum Features:
  * [ ] Pin / Unpin Topics
  * [ ] Close Topics
  * [ ] Edit Topic Title
  * [ ] Edit Posts
  * [ ] List Users
  * [ ] Change User Profile and Login Data
  * [ ] View User Profile
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
