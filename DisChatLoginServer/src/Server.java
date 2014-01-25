import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Server {

	private ServerSocket serverListener = null;
	private Socket socket;
	private DataInputStream inputFromMobile;
	private DataOutputStream outputToMobile;
	private Connection con = null;
	private PreparedStatement checkEmail;
	private PreparedStatement checkPwd;
	
	/**
	 * @param args - null
	 */
	public static void main(String[] args) {
		new Server();		
	}
	
	public Server()
	{
				
		// Create a server socket
		try 
		{
			// server listens to port 8885
			serverListener = new ServerSocket(8885);

			// incoming connection, pass to thread
			while(true){
				socket = serverListener.accept();
				Runnable login = new HandleTask(socket);
				new Thread(login).start();
			}
	
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	class HandleTask implements Runnable{

		private Socket s;
		private boolean login_status = false;
		
		public HandleTask(Socket s){
			this.s = s;
			
			// Establish connection to the db
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dischatdatabase", "root", "2289");
			}
			catch (ClassNotFoundException e){
				e.printStackTrace();
				System.err.println("Cannot locate jdbc driver");
			}
			catch (SQLException e){
				e.printStackTrace();
				System.err.println("db access error");
			}	
		}
		
		@Override
		public void run() {
			
			try {
				DataInputStream input = new DataInputStream(s.getInputStream());
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				
				
				String usr_email = input.readUTF();
				String usr_pwd = input.readUTF();
				
				// check does usr credential is correct
				checkEmail = con.prepareStatement("select password from user where email = ?");
				checkEmail.setString(1, usr_email);
				ResultSet result = checkEmail.executeQuery();

				// if result.next() return false then that signifies email does not exist 
				// if true then email exist
				if (result.next()) {    
					 if (result.getString(1).equals(usr_pwd))
						 login_status = true;
				}
				
				System.out.println("login_status is" + login_status);
				out.writeBoolean(login_status);
				
			}
			catch (IOException | SQLException e){
				e.printStackTrace();
			}
			
		}
		
	}
	
}



