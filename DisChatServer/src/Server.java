import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;


public class Server {

	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream inputFromMobile;
	private DataOutputStream outputToMobile;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Server();
		
	}
	
	public Server(){
		
		try {
			
			// Create a server socket
			serverSocket = new ServerSocket(8885);
			
			// Listen for a connection request
			socket = serverSocket.accept();
			
			// Create data input and output streams
			inputFromMobile = new DataInputStream(socket.getInputStream());
			outputToMobile = new DataOutputStream(socket.getOutputStream());
			
			
			while(true){
				
				String message = inputFromMobile.readUTF();
				
				outputToMobile.writeUTF(message);
				
			}
			
			
		} catch (IOException e) {
			
		}
		
		
		
	}

}
