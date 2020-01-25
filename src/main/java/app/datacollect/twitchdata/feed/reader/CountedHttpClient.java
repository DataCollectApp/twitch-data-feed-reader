package app.datacollect.twitchdata.feed.reader;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class CountedHttpClient {

  private HttpClient httpClient;
  private int count;

  public CountedHttpClient() {
    this.httpClient = HttpClient.newHttpClient();
    this.count = 0;
  }

  public HttpResponse<String> send(HttpRequest httpRequest) {
    try {
      if (shouldResetHttpClient()) {
        resetHttpClient();
      }
      final var httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
      count++;
      return httpResponse;
    } catch (IOException | InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }

  private boolean shouldResetHttpClient() {
    return count == 999;
  }

  private void resetHttpClient() {
    httpClient = HttpClient.newHttpClient();
    count = 0;
  }
}
