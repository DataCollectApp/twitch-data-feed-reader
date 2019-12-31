package app.datacollect.twitchdata.feed.reader;

import app.datacollect.twitchdata.feed.events.Event;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import app.datacollect.twitchdata.feed.events.chatmessage.v1.ChatMessageEventV1;
import org.json.JSONObject;

class EventMapper {
  static Event mapToEvent(
      String eventId, EventType eventType, ObjectType objectType, Version version, String content) {
    if (objectType == ObjectType.CHAT_MESSAGE) {
      if (eventType == EventType.CHAT_MESSAGE_SNAPSHOT) {
        if (version == Version.V1) {
          return new ChatMessageEventV1(new JSONObject(content));
        }
      }
    }
    throw new IllegalStateException(
        String.format(
            "Event with id '%s', eventType '%s', objectType '%s' and version '%s' could not be mapped to an event",
            eventId, eventType.name(), objectType.name(), version.name()));
  }
}
