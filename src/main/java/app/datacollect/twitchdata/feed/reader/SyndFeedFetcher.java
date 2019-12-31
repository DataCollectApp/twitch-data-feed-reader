package app.datacollect.twitchdata.feed.reader;

import com.rometools.rome.feed.synd.SyndFeed;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;

class SyndFeedFetcher {
  private final String feedUrl;
  private final String feedUsername;
  private final String feedPassword;
  private final int feedPageSize;

  SyndFeedFetcher(String feedUrl, String feedUsername, String feedPassword, int feedPageSize) {
    this.feedUrl = feedUrl;
    this.feedUsername = feedUsername;
    this.feedPassword = feedPassword;
    this.feedPageSize = feedPageSize;
  }

  private static String basicAuth(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }

  SyndFeed performRequest(String marker) {
    final HttpClient client = HttpClient.newHttpClient();
    final URI uri =
        URI.create(String.format("%s?marker=%s&limit=%s", feedUrl, marker, feedPageSize));
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", basicAuth(feedUsername, feedPassword))
            .timeout(Duration.ofMillis(10000))
            .build();
    final HttpResponse<String> response;
    try {
      response = client.send(request, BodyHandlers.ofString());
    } catch (IOException | InterruptedException ex) {
      throw new RuntimeException(ex);
    }
    if (response.statusCode() != 200) {
      throw new IllegalStateException(
          String.format(
              "GET request to '%s' failed with status code '%s'", uri, response.statusCode()));
    }
    return new SyndFeedParser().parseFeed(response.body());
  }
}
