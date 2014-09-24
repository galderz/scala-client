* Interacting with non-default caches
* Lifespan and max idle time bigger than 30 days
* Force a test that creates an error situation
* Default lifespan/maxidle -> Javadoc says it also affects get operation but no track of a test or anything
  * Implementation wise: pass in Option[Duration] (type aliased to Expiration)
  * None is default and it means use default lifespan/maxidle
  * Some(x) where x is negative or 0 duration means live forever
  * Some(x) is positive but above 30 days, considered UNIX time (absolute)
  * Some(x) is positive but below 30 days, consider relative time
  * Q. Is there a better way to represent this with case classes? Quite possibly!