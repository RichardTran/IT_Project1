// Group Members: Amanda Goonetilleke, Richard Tran, Cheryl Chi

import java.io.*;
import java.net.*;

public class get {
	public static void main(String args []) throws Exception {

		int argc = args.length;
		String host = "localhost";
		String groupname = null;
		int port = 12345;
		if(argc == 1){
			// just has groupname
			groupname = args[0];
		}
		else if(argc == 3) {
			if(args[0].equals("-h")) {
				host = args[1];
			}
			else if(args[0].equals("-p") && args[1].length()>0) {
				try {
					port = Integer.parseInt(args[1]);
				} catch(Exception e) {
					System.err.println("Port isn't valid");
					System.exit(1);
				}
			}
			else {
				System.err.println("Proper cmd: get [-h hostname] [-p port] groupname. " + 
						"In addition, groupname should have no white spaces");
				System.exit(1);
			}
			groupname = args[2];
		}
		else if(argc == 5) {
			if(args[0].equals("-h") && args[2].equals("-p")) {
				try {
					port = Integer.parseInt(args[3]);
					host = args[1];
					groupname = args[4];
				} catch(Exception e) {
					System.err.println("Port isn't valid");
					System.exit(1);
				}
			}
			else if(args[0].equals("-p") && args[2].equals("-h")) {
				try {
					port = Integer.parseInt(args[1]);
					host = args[3];
					groupname = args[4];
				} catch(Exception e) {
					System.err.println("Port isn't valid");
					System.exit(1);
				}
			}
			else {
				System.err.println("Proper cmd: get [-h hostname] [-p port] groupname. " + 
					"In addition, groupname should have no white spaces");
				System.exit(1);
			}
		}
		else {
			System.err.println("Proper cmd: get [-h hostname] [-p port] groupname. " + 
						"In addition, groupname should have no white spaces");
			System.exit(1);
		}

		String line; // user input
		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
	 	Socket sock = null;

		try {
			sock = new Socket(host, port); // connect to localhost port 12345
		} catch(Exception e) {
			System.err.println("Invalid hostname or port.");
			System.exit(1);
		}
		DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));

		// provide the get command

		String result;
		toServer.writeBytes("get " + groupname + '\n'); // send the line to the server
		
		result = fromServer.readLine(); // read a one-line result
		if(!result.equalsIgnoreCase("ok")) {
			System.err.println(result);
			sock.close();
			System.exit(1);
		}
		else {
			System.out.println(result); // prints "Ok"
		}
		
		result = null;
		//gets and prints out number of messages
		result = fromServer.readLine();
		System.out.println(result);
		
		// Receive a message
		while((line = fromServer.readLine()) != null) {
			System.out.println(line);
		}
		sock.close();
	 }
}
