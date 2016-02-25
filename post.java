import java.io.*;
import java.net.*;
public class post {
	public static void main(String args []) throws Exception {

		int argc = args.length;
		String host = "localhost";
		String groupname = null;
		int port = 12345;
		if(argc == 1){
			// just has groupname
			groupname = args[0];
		}
		else if(argc == 3){
			if(args[0].equals("-h")){
				host = args[1];
			}
			else if(args[0].equals("-p") && args[1].length()>0){
				try{
					port = Integer.parseInt(args[1]);
				}catch(Exception e){
					System.err.println("Port isn't valid");
					System.exit(1);
				}
			}
			else{
				System.err.println("Proper cmd: post [-h hostname] [-p port] groupname. " + 
						"In addition, groupname should have no white spaces");
				System.exit(1);
			}
			groupname = args[2];
		}
		else if(argc == 5) {
				if(args[0].equals("-h") && args[2].equals("-p")){
					try{
						port = Integer.parseInt(args[3]);
						host = args[1];
						groupname = args[4];
					}catch(Exception e){
						System.err.println("Port isn't valid");
						System.exit(1);
					}
				}
				else if(args[0].equals("-p") && args[2].equals("-h")){
					try{
						port = Integer.parseInt(args[1]);
						host = args[3];
						groupname = args[4];
					}catch(Exception e){
						System.err.println("Port isn't valid");
						System.exit(1);
					}
				}
				else{
					System.err.println("Proper cmd: post [-h hostname] [-p port] groupname. " + 
						"In addition, groupname should have no white spaces");
					System.exit(1);
				}
		}
		else{
			System.err.println("Proper cmd: post [-h hostname] [-p port] groupname. " + 
						"In addition, groupname should have no white spaces");
			System.exit(1);
		}


		String line; // user input
		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
	 	Socket sock = new Socket(host, port); // connect to localhost port 12345

		DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		toServer.writeBytes(groupname + '\n'); // send the line to the server
		

		//line = userdata.readLine(); // read a line from the user

		String result = fromServer.readLine(); // read a one-line result
		if(!result.equals("ok")){
			System.err.println(result);
			System.exit(1);
		}
		toServer.writeBytes(System.getProperty("user.name" + '\n'));
		result = fromServer.readLine();
		if(!result.equals("ok")){
			System.err.println(result);
			System.exit(1);
		}


		System.out.println(result); // print it
		sock.close(); // and we’re done

	 }
}