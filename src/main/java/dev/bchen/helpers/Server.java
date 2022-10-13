package dev.bchen.helpers;

import java.net.http.*;
import java.net.*;
import java.io.IOException;

import com.sun.net.httpserver.*;

/**
 * (UNUSED) Create a server that hosts the login page.
 */
public class Server {
  private HttpServer server;

  public Server() throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(8080), 0);
    System.out.println("Server starting");
    System.out.println(this.server.getAddress());

    HttpContext context = this.server.createContext("/");

    class LoginHandler implements HttpHandler {
      public void handle(HttpExchange exchange) {
        System.out.println("Request received");
      }
    }
    
    context.setHandler(new LoginHandler());

    this.server.start();
  }
}