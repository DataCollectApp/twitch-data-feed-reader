# twitch-data-feed-reader
Library used by consumers of the twitch data feed

### How to
Get up to 200 events from the beginning of the feed:
```java
TwitchDataFeedReader feedReader = new TwitchDataFeedReader("https://foo.bar/feed", "username", "password", 200);
List<Event> events = feedReader.getPage();
```
Get up to 200 events after a specific event:
```java
TwitchDataFeedReader feedReader = new TwitchDataFeedReader("https://foo.bar/feed", "username", "password", 200);
feedReader.setLastReadId("<ID of the last read event>");
List<Event> events = feedReader.getPage();
```
After you get one page, you can get the ID of the last read event like this:
```java
Optional<String> lastReadId = feedReader.getLastReadId();
```
