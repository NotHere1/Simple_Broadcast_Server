import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;


public class Server {

	private ServerSocket serverSocket;
	private Socket socket;
	private ExecutorService executor = null;
	private SortedMap<String, Client_Handler> client_tree = null; 
	
	/**
	 * @param args null
	 */
	public static void main(String[] args) {
		
		try {
			new Server();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Performs and calls all the necessary server operations
	 * @throws IOException
	 */
	public Server() throws IOException {	
			
		// Create a treemap for storing client information ~ fail safe
		// Downside, performance hampered by locking of the map
		SortedMap<String, Client_Handler> client_tree 
				= Collections.synchronizedSortedMap(new TreeMap<String, Client_Handler>());
		
		// Create a fixed thread pool with maximum 10 threads (for now)
		executor = Executors.newFixedThreadPool(10);		
		
		// create a server socket instance
		serverSocket = new ServerSocket(8888);
		
		while (true) {			
			System.err.println("listening");
			socket = serverSocket.accept();
			System.err.println("accepted" + socket.getRemoteSocketAddress());
			executor.execute(new Client_Handler(socket, client_tree));
		}
	}

	
	
	/**
	 * A threaded class that handle all of the client interfaces
	 */
	class Client_Handler implements Runnable {

		// class fields
		private SocketAddress client_addr;
		private Socket socket;
		private BufferedReader in;
		private DataOutputStream out;
		private boolean is_connected = false;
		private SortedMap<String, Client_Handler> client_tree; // object references to the actual tree
		
		public Client_Handler(Socket socket, SortedMap<String, Client_Handler> client_tree) {
			
			// Instantiate fields
			this.client_addr = socket.getRemoteSocketAddress();
			this.socket = socket;
			this.client_tree = client_tree;
			
			// add this client thread to the tree, <key - unique ip, value - instance of the thread>
			client_tree.put(client_addr + "", this);
			
			// Change connection status to true
			is_connected = true;
		
		} // end constructor
		
		/**
		 * Listen and transmits client' inputs 
		 */
		@Override
		public void run() {
			
			try 
			{
				// Thread's input and output streams
				in = new BufferedReader(
						new InputStreamReader(
							socket.getInputStream(), "UTF-8"));
			
				out = new DataOutputStream(
						socket.getOutputStream());
			
				String user_msg;
				
				// establish initial handshake with client
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("indicate", "true");
				JSONObject initial_contact = new JSONObject(map);
			
				// Send out initial Json for handshake
				out.writeUTF(initial_contact.toString() + "\n");
				
				// debugging
				System.out.println(initial_contact.toString());
				
				// Listen and broadcast client's inputs
				while ((user_msg = in.readLine())!=null) {
				
					//debugging
					//System.out.println(user_msg);
					
					// remove any irregularities from beginning of the JSON ~~JSONOject Bug!
					StringBuilder sb = new StringBuilder(user_msg);
					while(sb.charAt(0)!='{') {
						sb.deleteCharAt(0);
					}
					
					// another strange irregularites with JSONObject
					if (sb.charAt(1) == '{' && sb.charAt(0) == '{') {
						sb.deleteCharAt(0);
					}
					
					// Retrieve incoming client json
					JSONObject json_input = new JSONObject(sb.toString());
				
					// debugging
					System.out.println(json_input.toString());
					
					// broadcast to all connected clients in tree
					broadcastToAll(json_input.toString());
				
				} 
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				
				// in.readline return null
				close_client_socket();
				
				// debugging
				System.out.println("in finally");
				
				// close streams
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		} // end run
		
		
		/**
		 * Remove socket from list when connection terminates
		 */
		private void close_client_socket() {
			
			System.err.println("Closing connection to " + client_addr);
			
			// reset connection boolean
			is_connected = false;
			
			// remove client from the tree
			client_tree.remove(client_addr + "");
			
			// debugging
			System.out.println("tree size is " + client_tree.size());
		}
		
		
		/**
		 * Broadcast message to all users
		 * @param out_json json containing message to be sent
		 * @throws IOException ~~~~ :) ~~~~
		 */
		private void broadcastToAll(String out_json) throws IOException {		
			
			for (String key : client_tree.keySet()) {
				client_tree.get(key).broadcast(out_json);
			}
		}
		
		/**
		 * Broadcast message to a single user
		 * @param out_json json containing message to be sent
		 */
		private void broadcast(String out_json) throws IOException {
			out.writeUTF(out_json + "\n");
		}
		
	} // end client_handler Runnable
} // end program
