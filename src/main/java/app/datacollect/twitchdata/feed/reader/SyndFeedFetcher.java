package app.datacollect.twitchdata.feed.reader;

import com.rometools.rome.feed.synd.SyndFeed;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

class SyndFeedFetcher {
  private final CountedHttpClient httpClient;
  private final String feedUrl;
  private final String feedUsername;
  private final String feedPassword;
  private final int feedPageSize;

  SyndFeedFetcher(String feedUrl, String feedUsername, String feedPassword, int feedPageSize) {
    this.httpClient = new CountedHttpClient();
    this.feedUrl = feedUrl;
    this.feedUsername = feedUsername;
    this.feedPassword = feedPassword;
    this.feedPageSize = feedPageSize;
  }

  private static String basicAuth(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }

  SyndFeed performRequest(String marker) {
    final URI uri =
        URI.create(String.format("%s?marker=%s&limit=%s", feedUrl, marker, feedPageSize));
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", basicAuth(feedUsername, feedPassword))
            .timeout(Duration.ofMillis(10000))
            .build();
    final HttpResponse<String> response = httpClient.send(request);
    if (response.statusCode() != 200) {
      throw new IllegalStateException(
          String.format(
              "GET request to '%s' failed with status code '%s'", uri, response.statusCode()));
    }
    return new SyndFeedParser().parseFeed(response.body());
  }
}
