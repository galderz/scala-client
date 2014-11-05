* Interacting with non-default caches
* Lifespan and max idle time bigger than 30 days uses absolute unix time
* Lifespan and max idle time less than 30 days uses relative time
* Force a test that creates an error situation
* Test passing multiple flags at the same time
* Test putIfAbsent() can do put with lifespan/maxidle
* Test replace() can do put with lifespan/maxidle
* Test remove with version