package app.datacollect.twitchdata.feed.reader;

import app.datacollect.twitchdata.feed.events.Event;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TwitchDataFeedReader {
  private final SyndFeedFetcher syndFeedFetcher;
  private String lastReadId;

  public TwitchDataFeedReader(
      String feedUrl, String feedUsername, String feedPassword, int feedPageSize) {
    this.syndFeedFetcher = new SyndFeedFetcher(feedUrl, feedUsername, feedPassword, feedPageSize);
    this.lastReadId = null;
  }

  public Optional<String> getLastReadId() {
    return Optional.ofNullable(lastReadId);
  }

  public void setLastReadId(String lastReadId) {
    this.lastReadId = lastReadId;
  }

  public List<Event> getPage() {
    final String marker = Optional.ofNullable(lastReadId).orElse("start");
    final SyndFeed syndFeed = syndFeedFetcher.performRequest(marker);
    final List<SyndEntry> syndEntries = syndFeed.getEntries();
    if (!syndEntries.isEmpty()) {
      setLastReadId(syndEntries.get(syndEntries.size() - 1).getUri());
    }
    return syndEntries.stream()
        .map(
            syndEntry -> {
              final String eventId = syndEntry.getUri();
              final List<SyndCategory> syndCategories = syndEntry.getCategories();
              final EventType eventType = extractEventType(eventId, syndCategories);
              final ObjectType objectType = extractObjectType(eventId, syndCategories);
              final Version version = extractVersion(eventId, syndCategories);
              return EventMapper.mapToEvent(
                  eventId,
                  eventType,
                  objectType,
                  version,
                  syndEntry.getContents().get(0).getValue());
            })
        .collect(Collectors.toList());
  }

  private EventType extractEventType(String eventId, List<SyndCategory> syndCategories) {
    return EventType.valueOf(extractCategory("eventType", eventId, syndCategories).getName());
  }

  private ObjectType extractObjectType(String eventId, List<SyndCategory> syndCategories) {
    return ObjectType.valueOf(extractCategory("objectType", eventId, syndCategories).getName());
  }

  private Version extractVersion(String eventId, List<SyndCategory> syndCategories) {
    return Version.valueOf(extractCategory("version", eventId, syndCategories).getName());
  }

  private SyndCategory extractCategory(
      String categoryName, String eventId, List<SyndCategory> syndCategories) {
    return syndCategories.stream()
        .filter(syndCategory -> syndCategory.getTaxonomyUri().equals(categoryName))
        .findFirst()
        .orElseThrow(categoryMissing(categoryName, eventId));
  }

  private Supplier<IllegalStateException> categoryMissing(String categoryName, String eventId) {
    return () ->
        new IllegalStateException(
            String.format(
                "Failed to extract '%s' category from event with id '%s'", categoryName, eventId));
  }
}
