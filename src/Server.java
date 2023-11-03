import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Have Server.java running, then in another terminal console type 'telnet localhost 8888' to connect
 * disconnect: ctrl + ], then type in 'close'
 */
public class Server
{
  final int port = 8888;
  final ServerSocket server;
  final ArrayList<Handler> handlers = new ArrayList<>();

  public void show_message(Handler sender, String message) {
    for (Handler handler : handlers) {
      if (handler != sender) {
        handler.output.println(message);
      }
    }
  }

  public static void main(String[] args) throws IOException { new Server().host(); }

  public Server() throws IOException { server = new ServerSocket(port); }

  public void host() throws IOException {
    while (true) {
      Socket client = server.accept();
      Handler handler = new Handler(client);
      show_message(handler, "[SERVER]: a new user has joined.");
      handlers.add(new Handler(client));
      new Thread(handler).start();
    }
  }

  public class Handler implements Runnable {
    final Socket client;
    final PrintWriter output;
    final BufferedReader input;

    public Handler(Socket client) throws IOException {
      this.client = client;
      output = new PrintWriter(client.getOutputStream(),true);
      input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    @Override
    public void run() {
      try {
        output.println("enter your username: ");
        String username = input.readLine();
        output.println("welcome to the chat room " + username + ", currently there are " + handlers.size() + " users total in the chat room.");
        String message;
        while ((message = input.readLine()) != null) { Server.this.show_message(Handler.this, "[" + username + "]: " + message); }
        Server.this.show_message(Handler.this, "a user has left. " + handlers.size() + " remain in the chat room.");
        handlers.remove(Handler.this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
