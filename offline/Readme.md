# Bahmni Offline

### Problem
The Bahmni front-end should work without having network connectivity to the Bahmni backend, and when 
in network it should sync all the data back to the backend and maintain consistency and prevent loss
of data while doing the same. 

### Requirement
The Bahmni front-end should work offline on various platforms when accessed via different methods.
#### Platforms
* Google Chrome (Any Operating System)
* Firefox (Any Operating System)
* Google Chrome Application (On Desktops)
* Android Application (Android 4.1.2 +)
The UI should be consistent across all platforms and use of APIs by the front-end on all platforms should
remain the same.

### Spike Result
There were multiple spikes around various commonly used technologies to support an offline front-end.
One of the most important constraint was, the development of UI should only take place on one codebase.
That means all the development should take place only on the Angular Application for the UI, and 
the wrappers for all platforms should only emphasize on running the Angular Application in their own
sandbox. 

Now there are 4 fronts to the problem (Static Files = Angular App)
* Loading/packaging of static files
* Storage of data without network connectivity
* Upgrading of static files
* Syncing of data between backend and frontend data store in network connectivity

#### Packaging static files
The front-end application would have to be loaded when accessed via Chrome Browser, i.e when accessed via
regular Chrome browser. For platform specific application the minified static files will be packaged along
the application, so that even the initial download of static files or network connectivity is not required.
There will be a service worker integration of the same. Service Workers have their own cache on supported
browsers. There will be one class of service workers which cache all the files with extension of 
.js, .css, .html, .ttf. These service workers will be platform agnostic, and will be part of the main AngularJS
codebase. Android Application and Chrome Application provide a webview where the Angular App can be loaded as it
would on a regular browser and also provide network interceptor to redirect call for static files to their 
own resources. The service worker responsible for caching the static files won't care whether the application is
loaded in a Chrome webview, Android Webview or Regular Chrome Browser.

#### Storage of data without network connectivity
Various things were looked at it for this. There are multiple APIs available like App-Cache, WebSQL, Platform
specific storage, IndexedDb or alternative third party solutions.
1. App-Cache - It is availble on multiple platforms but has a 5Mb hard limit, and is no longer under active development.
2. WebSQL - No longer under active development.
3. Platform specific storage - Redundant developement effort on various platforms and regular application access
via Chrome won't have those features which are available on specific platforms
4. IndexedDB - Under active development, the size limit can be bypassed, is available on all platforms and keeps the 
application generic with development effort focussed only around one area.

The idea is to have a separate class of Service workers which check for network connectivity and dependent on that
they insert data into the indexed db and fire a network call if network is available, and if network is not available, 
the data is just stored in IndexedDb and the call is added to a queue. The data will be stored on the IndexedDB
regardless of network availability because the data captured from that device will always be relevant for further
use from that device. IndexedDB has a 50MB limit on Desktops and 5MB limit on Mobile devices. That can be extended 
using the quota management API. Although its better not to go more than 50MB of Data on mobiles specially, because it leads
to slow down of the app.

#### Upgrading of static files
We currently when minified also version our js and css. We will have to start versioning our HTML as well, 
and when a file is not found in cache it will lead to an upgrade because the caching is key-value based and the key will
be the url which will change if the version changes, this can be done only when device is in network. 

#### Syncing of data
Not yet looked into

### More Tech
#### Android - 
On Android we need to support Android version 4.1.2+.
Native Fully Fledged Android Application - Redundant dev effort.
Frameworks like Cordova, IonicFramework - Do not satisfy our requirements. 
Our requirements are now support for Service Workers and IndexedDb, Cordova won't give us that. 
Native Android Application with Packaged Static files and CrossWalk - 
We can use Android's Webview itself, but the native Android WebView on all versions below 4.4 does not support
IndexedDB or Service Workers because it uses Android's older browser and we needed Chromium Browser based WebView, 
since it supports  what we need. 
Crosswalk bundles along Chromium libraries to provide all the features of the latest Chromium on Android browser features.
And it supports all Android 4.0+.

#### Chrome App - 
Chrome Application also we can go with a webview based approach but it is under active development. Another alternative
is we make modifications to the angular codebase to check for injection of an Absolute URL for the backend, which might be
required for the Chrome App if the webview doesn't support the network call interceptor to load the static files from the packaged resources. 

#### Service Workers - 
The idea is to have service workers cache the static files and facilitate storage of data to indexedDB
so that it is supported on all chromium based platforms, may it be desktop, webview or chrome apps. 
The caching of static files is simple enough to implement, and support for upgrade of static files comes
naturally because of key value pair caching and versioning of static files from minification.
Check this out for some more code - https://github.com/Bhamni/openmrs-module-bahmniapps/tree/offline



### Resources (Further Reading)
* Service Workers - http://www.html5rocks.com/en/tutorials/service-worker/introduction/
* Crosswalk - https://crosswalk-project.org/
* IndexedDB - http://demo.agektmr.com/storage/
* Chrome Offline Storage - https://developer.chrome.com/apps/offline_storage
* Offline Storage - http://www.html5rocks.com/en/tutorials/offline/quota-research/