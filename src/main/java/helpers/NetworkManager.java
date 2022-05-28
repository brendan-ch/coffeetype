package helpers;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.Timer;

import java.awt.event.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// Handle test updates
// Create and start when test begins
// Contains a timer object to send test data
// Contains methods to set data to send
// Discard when test ends
class TestUpdateManager extends Thread {
  // Send an update to the server every # seconds when test is active
  public static final int UPDATE_DELAY = 1000;
  
  /**
   * Interval to send updates to the server.
   */
  private Timer updateInterval;

  private HttpClient client;

  private String typed;
  private String roomKey;
  private String playerId;

  public TestUpdateManager(String roomKey, String playerId, HttpClient client) {
    this.roomKey = roomKey;
    this.playerId = playerId;
    this.client = client;
  }

  @Override
  public void run() {
    class TimerListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        // Send an update
        sendUpdate();
      }
    }

    // Start the timer and start sending updates
    this.updateInterval = new Timer(UPDATE_DELAY, new TimerListener());
    this.updateInterval.start();
  }

  /**
   * Stop the timer and remove references.
   */
  public void stopUpdating() {
    this.updateInterval.stop();
    this.updateInterval = null;
  }

  public void setTyped(String typed) {
    this.typed = typed;
  }

  private void sendUpdate() {
    // POST an update to the server
    // Build the request body
    JSONObject body = new JSONObject();
    body.put("playerId", this.playerId);
    body.put("roomKey", this.roomKey);
    body.put("typed", this.typed);

    // Build a new request
    HttpRequest request = HttpRequest.newBuilder(
      URI.create(NetworkManager.BASE_URL + "/api/post/testData")
    )
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .POST(BodyPublishers.ofString(body.toString()))
      .build();

    CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, BodyHandlers.ofString());
    // JSONParser parser = new JSONParser();

    // try {
    //   JSONObject result = (JSONObject) parser.parse(future.get().body());
    // } catch(InterruptedException e) {

    // } catch(ExecutionException e) {
      
    // } catch(ParseException e) {

    // }
  }
}

// Create a separate thread
class LongPollingManager extends Thread {
  public static final int MAX_LISTENERS = 1;

  private String roomKey;
  private String playerId;
  private HttpClient client;

  private JSONArray players;

  private EventListener[] listeners;
  private int numListeners;

  public LongPollingManager(String roomKey, String playerId, HttpClient client) {
    this.roomKey = roomKey;
    this.playerId = playerId;
    this.client = client;

    this.listeners = new EventListener[MAX_LISTENERS];
  }

  public JSONArray getPlayers() {
    return this.players;
  }
  
  @Override
  public void run() {
    while (this.roomKey != null && this.playerId != null) {
      this.longPoll();
    }
  }

  public void stopLoop() {
    this.roomKey = null;
    this.playerId = null;
  }

  /**
   * Subscribe to room updates.
   */
  public void longPoll() {
    // Send a HTTP request to create a room
    HttpRequest request = HttpRequest.newBuilder(
      URI.create(NetworkManager.BASE_URL + "/api/get/update?playerId=" + this.playerId + "&roomKey=" + this.roomKey)
    )
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .GET()
      .build();

    CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, BodyHandlers.ofString());
    JSONParser parser = new JSONParser();

    try {
      JSONObject result = (JSONObject) parser.parse(future.get().body());

      if (!((Boolean) result.get("success"))) {
        return;
      }
      
      String networkEvent = (String) result.get("event");
      String[] networkEvents = {"TEST_START", "TEST_END", "WORDS_UPDATE", "PLAYERS_UPDATE"};
      if (networkEvent.equals("PLAYERS_UPDATE") || networkEvent.equals("TEST_END")) {
        // Update the local players array
        this.players = (JSONArray) ((JSONObject) result.get("data")).get("players");
      }
      

      for (EventListener listener : listeners) {
        if (listener != null) {
          // Match local events to network events, using indices
          int[] localEvents = {Event.NETWORK_TEST_START, Event.NETWORK_TEST_END, Event.NETWORK_WORDS_UPDATE, Event.NETWORK_PLAYERS_UPDATE};

          // Perform linear search
          int index = -1;
          for (int i = 0; i < networkEvents.length; i++) {
            if (networkEvents[i].equals(networkEvent)) {
              index = i;
            }
          }

          if (index > -1) {
            listener.actionPerformed(new Event(localEvents[index]));
          }
        }
      }
    } catch(InterruptedException e) {

    } catch(ExecutionException e) {
      
    } catch(ParseException e) {

    }
  }

  // "Add" an event listener
  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener; 
    }
  }
}

// Store information about the current room
// Serves as replacement for StatsTracker during multiplayer session
// Re-use some events used by StatsTracker
public class NetworkManager {
  public static final String BASE_URL = "http://localhost:3000";
  public static final int MAX_LISTENERS = 10;
  public static final int MAX_PLAYERS = 10;
  
  private String roomKey;
  private String playerId;

  private String nickname;

  private HttpClient client;
  private LongPollingManager longPollingManager;
  private TestUpdateManager testUpdateManager;

  private JSONArray players;

  private String characters;
  private String typed;

  private EventListener[] listeners;
  private int numListeners;

  public NetworkManager() {
    // https://www.twilio.com/blog/5-ways-to-make-http-requests-in-java
    this.nickname = "coffeetype player";
    this.client = HttpClient.newHttpClient();

    this.characters = "";
    this.typed = "";

    this.listeners = new EventListener[MAX_LISTENERS];
  }

  public JSONArray getPlayers() {
    return this.players;
  }

  /**
   * Whether the client is connected to a room.
   * @return
   */
  public boolean getConnectionStatus() {
    return this.roomKey != null && this.playerId != null;
  }

  public String getCharacters() {
    return this.characters;
  }

  public String getTyped() {
    return this.typed;
  }

  public void addLetter(String toAdd) {
    if (this.testUpdateManager == null) return;

    this.typed += toAdd;

    this.testUpdateManager.setTyped(this.typed);
  }

  /**
   * Delete a letter from the `typed` string.
   */
  public void deleteLetter() {
    if (this.typed.length() > 0 && this.testUpdateManager != null) {
      this.typed = this.typed.substring(0, this.typed.length() - 1);

      this.testUpdateManager.setTyped(this.typed);
    }
  }

  // Join a room, set roomKey and playerId
  public void joinRoom(String roomKey) {
    // Build the request body
    JSONObject body = new JSONObject();
    body.put("playerName", this.nickname);
    body.put("roomKey", this.roomKey);

    // Send a HTTP request to create a room
    HttpRequest request = HttpRequest.newBuilder(
      URI.create(BASE_URL + "/api/post/join")
    )
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .POST(BodyPublishers.ofString(body.toString()))
      .build();

    CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, BodyHandlers.ofString());

    JSONParser parser = new JSONParser();

    try {
      JSONObject result = (JSONObject) parser.parse(future.get().body());

      if (!((Boolean) result.get("success"))) {
        return;
      }

      // Set room key and player ID
      this.roomKey = (String) result.get("roomKey");
      this.playerId = (String) result.get("playerId");
      this.players = (JSONArray) ((JSONObject) result.get("data")).get("players");

      fireEventListeners(new Event(Event.NETWORK_STATUS_CHANGE));

      this.initializeLongPolling();
    } catch(InterruptedException e) {

    } catch(ExecutionException e) {
      
    } catch(ParseException e) {

    }
  }

  /**
   * Create a room.
   */
  public void createRoom() {
    if (roomKey != null || playerId != null) return;

    // Build the request body
    JSONObject body = new JSONObject();
    body.put("playerName", this.nickname);

    // Send a HTTP request to create a room
    HttpRequest request = HttpRequest.newBuilder(
      URI.create(BASE_URL + "/api/post/createRoom")
    )
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .POST(BodyPublishers.ofString(body.toString()))
      .build();

    CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, BodyHandlers.ofString());

    JSONParser parser = new JSONParser();

    try {
      JSONObject result = (JSONObject) parser.parse(future.get().body());

      if (!((Boolean) result.get("success"))) {
        return;
      }

      // Set room key and player ID
      this.roomKey = (String) result.get("roomKey");
      this.playerId = (String) result.get("playerId");
      this.players = (JSONArray) ((JSONObject) result.get("data")).get("players");

      fireEventListeners(new Event(Event.NETWORK_STATUS_CHANGE));

      this.initializeLongPolling();
    } catch(InterruptedException e) {

    } catch(ExecutionException e) {
      
    } catch(ParseException e) {

    }
  }

  public void initializeLongPolling() {
    if (this.longPollingManager != null) return;

    this.longPollingManager = new LongPollingManager(roomKey, playerId, client);
    this.longPollingManager.start();

    // Add some event listeners
    class LongPollingListener implements EventListener {
      public void actionPerformed(Event e) {
        if (longPollingManager != null && e.EVENT_TYPE == Event.NETWORK_PLAYERS_UPDATE) {
          // Update the JSONObject array
          players = longPollingManager.getPlayers();
        } else if (e.EVENT_TYPE == Event.NETWORK_TEST_START) {
          // Start sending updates
          initializeTestUpdates();
        } else if (e.EVENT_TYPE == Event.NETWORK_TEST_END) {
          // Stop sending updates
          stopTestUpdates();
        }

        // Pass events up
        fireEventListeners(e);
      }
    }

    this.longPollingManager.addEventListener(new LongPollingListener());
  }

  public void stopLongPolling() {
    if (this.longPollingManager == null) return;

    this.longPollingManager.stopLoop();
    this.longPollingManager = null;
  }

  public void initializeTestUpdates() {
    if (this.testUpdateManager != null) return;

    this.testUpdateManager = new TestUpdateManager(roomKey, playerId, client);
    this.testUpdateManager.start();
  }

  public void stopTestUpdates() {
    if (this.testUpdateManager == null) return;

    this.testUpdateManager.stopUpdating();
    this.testUpdateManager = null;
  }

  /**
   * Disconnect from the room.
   */
  public void exitRoom() {
    if (this.playerId == null) return;

    // Send request to exit room
    // Build the request body
    JSONObject body = new JSONObject();
    body.put("playerId", this.playerId);

    // Send a HTTP request to create a room
    HttpRequest request = HttpRequest.newBuilder(
      URI.create(BASE_URL + "/api/post/exit")
    )
      .header("accept", "application/json")
      .header("content-type", "application/json")
      .POST(BodyPublishers.ofString(body.toString()))
      .build();

    CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, BodyHandlers.ofString());

    JSONParser parser = new JSONParser();

    try {
      JSONObject result = (JSONObject) parser.parse(future.get().body());

      // Remove room key and player ID
      this.roomKey = null;
      this.playerId = null;

      this.stopLongPolling();
      this.stopTestUpdates();

      // Fire event listeners
      fireEventListeners(new Event(Event.NETWORK_STATUS_CHANGE));
    } catch(InterruptedException e) {

    } catch(ExecutionException e) {
      
    } catch(ParseException e) {

    }

  }

  // "Add" an event listener
  public void addEventListener(EventListener listener) {
    if (numListeners < MAX_LISTENERS) {
      this.listeners[numListeners++] = listener; 
    }
  }

  private void fireEventListeners(Event e) {
    // Pass events up
    for (EventListener listener : listeners) {
      if (listener != null) {
        listener.actionPerformed(e);
      }
    }
  }
}