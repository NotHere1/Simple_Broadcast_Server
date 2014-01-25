import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.PreparedStatement;


public class Server {

	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream inputFromMobile;
	private DataOutputStream outputToMobile;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Class.forName("com.mysql.jdbc.Driver");
		
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dischatdatabase", "root", "2289");
		
		java.sql.PreparedStatement statement = con.prepareStatement("select * from dischatdatabase.user");
		
		ResultSet result = statement.executeQuery();
		
		while(result.next())
		{
			System.out.println(result.getString(2));
		}
		
		
	}
}
//		new Server();
		
		
		
	//}
	
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

//}
