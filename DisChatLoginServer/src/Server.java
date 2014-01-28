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
		private DataInputStream in = null;
		private DataOutputStream out =null;
		private Connection con = null;
		
		public HandleTask(Socket s){
			this.s = s;
			
			// Establish connection to the db
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dischatdatabase", "root", "2289");
				
				in = new DataInputStream(s.getInputStream());
				out = new DataOutputStream(s.getOutputStream());
			}
			catch (ClassNotFoundException e){
				e.printStackTrace();
				System.err.println("Cannot locate jdbc driver");
			}
			catch (SQLException e){
				e.printStackTrace();
				System.err.println("db access error");
			} 
			catch (IOException e) {
				e.printStackTrace();
				System.err.println("IO Error at server");
			}
		}
		
		/**
		 * Performs login verification function
		 */
		private void login_check() {
			
			boolean login_status = false;
			PreparedStatement sql_check_user;
			
			try {
							
				String usr_email = in.readUTF();
				String usr_pwd = in.readUTF();
				
				// check does usr credential is correct	
				sql_check_user = con.prepareStatement("Select password from user where email = ?");
				sql_check_user.setString(1, usr_email);
				ResultSet result = sql_check_user.executeQuery();

				// if result.next() return false then that signifies email does not exist 
				// if true then email exist
				if (result.next()) {    
					 if (result.getString(1).equals(usr_pwd))
						 login_status = true;
				}
				System.out.println(login_status);
				out.writeBoolean(login_status);
				
			}
			catch (IOException | SQLException e){
				e.printStackTrace();
			}
			
		
		} // end login_check function
		
		/**
		 * Register new user into the db
		 */
		private void register_new_user() {
			
			PreparedStatement sql_register_user;
			PreparedStatement sql_check_email;
			boolean duplicate_email = false;
			
			try {
				
				String new_usr_name = in.readUTF();
				String new_usr_email = in.readUTF();
				String new_usr_pwd = in.readUTF();
				int new_usr_gender_int = in.readInt();
				
				String new_usr_gender;
				
				// assign char value to gender
				switch (new_usr_gender_int) {
					case 0: new_usr_gender = "f";
							break;
					case 1: new_usr_gender = "m";
							break;
					case 2: new_usr_gender = "o";
							break;
					default: new_usr_gender = "o";
							break;
				}
				
				// check email for possible duplicate
				sql_check_email  = con.prepareStatement("select email from user where email = ?");
				sql_check_email.setString(1, new_usr_email);
				ResultSet result = sql_check_email.executeQuery();
				
				// result.next returns true iff duplicate email exists, false otherwise
				// !result.next shows that no duplicate email
				if (!result.next()) {  
					duplicate_email = true;
				}
					
				// Update the db with the new user's info only if there is no duplicate email
				if (!duplicate_email) {
					sql_register_user = con.prepareStatement("insert into dischatdatabase.user (username, password, sex, email) values (? , ? , ? , ?);");
					sql_register_user.setString(1, new_usr_name);
					sql_register_user.setString(2, new_usr_pwd);
					sql_register_user.setString(3, new_usr_gender);
					sql_register_user.setString(4, new_usr_email);
					
					sql_register_user.executeQuery();
				}
				
				out.writeBoolean(duplicate_email);
				
			} catch (IOException e ) {
				e.printStackTrace();
				System.err.println("IO errors in register_new_user");
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("sql insert error in register new_user");
			}

		}
		
		@Override
		public void run() {
			
			try {
				
				int login_or_register = in.readInt(); // 0 - login, 1 - register
				
				if (login_or_register == 0) { 
					login_check();
					System.out.println("Login!");
				}
				else {
					register_new_user();
					System.out.println("Register");
				}
				
			} 
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error throws in thread run");
			}
			finally {
				try 
				{
					in.close();
					out.close();
					con.close();
				} 
				catch (IOException | SQLException e) {
					e.printStackTrace();
					System.err.println("close error in login_check");
				}
			}
			
		} // end run
		
	} // end thread class
	
}



