import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.sql.*;

public class Server {

	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream inputFromMobile;
	private DataOutputStream outputToMobile;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
//		new Server();
		
		// Accessing driver from the JAR file
		Class.forName("com.mysql.jdbc.Driver");
		
		// Creating a variable for the connection called "con"
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dischatdatabase","root","2289");
		
		// Query
		PreparedStatement statement = con.prepareStatement("select * from user");
		
		// Creating a variable to execute query
		ResultSet result = statement.executeQuery();
		
		while(result.next()){
			System.out.println("username is: " + result.getString(1));
		}
		
	}
	
//	public Server(){
//		
//		try {
//			
//			// Create a server socket
//			serverSocket = new ServerSocket(8885);
//			
//			// Listen for a connection request
//			socket = serverSocket.accept();
//			
//			// Create data input and output streams
//			inputFromMobile = new DataInputStream(socket.getInputStream());
//			outputToMobile = new DataOutputStream(socket.getOutputStream());
//			
//			
//			while(true){
//				
//				String message = inputFromMobile.readUTF();
//				
//				outputToMobile.writeUTF(message);
//				
//			}
//			
//			
//		} catch (IOException e) {
//			
//		}
//		
//		
//		
//	}

}
