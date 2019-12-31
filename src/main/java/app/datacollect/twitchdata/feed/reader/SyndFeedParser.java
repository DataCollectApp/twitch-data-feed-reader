package app.datacollect.twitchdata.feed.reader;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class SyndFeedParser {
  SyndFeed parseFeed(String feedString) {
    try (final XmlReader reader = new XmlReader(new ByteArrayInputStream(feedString.getBytes()))) {
      return new SyndFeedInput().build(reader);
    } catch (IOException | FeedException ex) {
      throw new RuntimeException(ex);
    }
  }
}
