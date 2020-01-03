package app.datacollect.twitchdata.feed.reader;

import app.datacollect.twitchdata.feed.events.Event;
import app.datacollect.twitchdata.feed.events.EventType;
import app.datacollect.twitchdata.feed.events.ObjectType;
import app.datacollect.twitchdata.feed.events.Version;
import app.datacollect.twitchdata.feed.events.chatmessage.v1.ChatMessageEventV1;
import app.datacollect.twitchdata.feed.events.clearchat.v1.ClearChatEventV1;
import app.datacollect.twitchdata.feed.events.clearmessage.v1.ClearMessageEventV1;
import app.datacollect.twitchdata.feed.events.globalclearchat.v1.GlobalClearChatEventV1;
import app.datacollect.twitchdata.feed.events.userjoin.v1.UserJoinEventV1;
import app.datacollect.twitchdata.feed.events.userleave.v1.UserLeaveEventV1;
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
    } else if (objectType == ObjectType.USER_JOIN) {
      if (eventType == EventType.USER_JOIN_SNAPSHOT) {
        if (version == Version.V1) {
          return new UserJoinEventV1(new JSONObject(content));
        }
      }
    } else if (objectType == ObjectType.USER_LEAVE) {
      if (eventType == EventType.USER_LEAVE_SNAPSHOT) {
        if (version == Version.V1) {
          new UserLeaveEventV1(new JSONObject(content));
        }
      }
    } else if (objectType == ObjectType.CLEAR_MESSAGE) {
      if (eventType == EventType.CLEAR_MESSAGE_SNAPSHOT) {
        if (version == Version.V1) {
          return new ClearMessageEventV1(new JSONObject(content));
        }
      }
    } else if (objectType == ObjectType.CLEAR_CHAT) {
      if (eventType == EventType.CLEAR_CHAT_SNAPSHOT) {
        if (version == Version.V1) {
          return new ClearChatEventV1(new JSONObject(content));
        }
      }
    } else if (objectType == ObjectType.GLOBAL_CLEAR_CHAT) {
      if (eventType == EventType.GLOBAL_CLEAR_CHAT_SNAPSHOT) {
        if (version == Version.V1) {
          return new GlobalClearChatEventV1(new JSONObject(content));
        }
      }
    }
    throw new IllegalStateException(
        String.format(
            "Event with id '%s', eventType '%s', objectType '%s' and version '%s' could not be mapped to an event",
            eventId, eventType.name(), objectType.name(), version.name()));
  }
}
