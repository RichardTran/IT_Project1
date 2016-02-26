import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Hashtable;


public class TCPServer {
	public static void main(String args[]) throws Exception {
		Socket conn; //per-thread copy of the client socket
		private Hashtable<String, ArrayList<String>> groupsAndMessages = new Hashtable<String, ArrayList<String>>();


		TCPServer(Socket sock) {
			this.conn = sock;
		}

		public static void main(String args[]) throws Exception {
			String host = "localhost";
			String groupname = null;
			int portNum;

			if (args[0].equals("server")){
				if (args.length == 1){
					portNum = 12345;
				}else if (args[1].equals("-p") && args.length == 3){
					    try { 
					    	portNum = Integer.parseInt(args[2]); 
					    } catch(NumberFormatException e) { 
					        System.out.println("error: invalid port number");
					    } catch(NullPointerException e) {
					        System.out.println("error: invalid port number");
					    }
				}else{
					System.out.println("error: invalid server command");
				}
				ServerSocket svc = new ServerSocket(portNum, 5);
				while (true) {
					System.out.println("Awaiting request");
					Socket conn = svc.accept(); // wait for a connection
					System.out.println("Received request");
					new Thread(new TCPServer(conn)).start();

				}
			}else{
				System.out.println("error: invalid server command");
			}
		} 

		public void run() {
			try {
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());
				
				while ((line = fromClient.readLine()) != null) { // while there's data from the client
					System.out.println("got line \"" + line + "\""); // show what we got
					String result = line;

					String[] request = line.split("\\s+");
					String groupName = request[1];

					if (request[0].equals("post")){
						if (asciiEncoder.canEncode(groupName) && !(groupName.matches(".*\\s+.*"))){
							if (!groupsAndMessages.containsKey(groupName)){
								ArrayList<String> messages = new ArrayList<String>();
								groupsAndMessages.put(groupName, messages);
							}
							line = fromClient.readLine();
							System.out.println("got line \"" + line + "\""); // show what we got
							result = line;
							String id = request[1];
							if (request[0].equals("id") && asciiEncoder.canEncode(id) && !(id.matches(".*\\s+.*"))){
								String msg = "";
								while((line = fromClient.readLine()) != null){
									msg+=line + '\n';
									toClient.writeBytes("Received: "+line+'\n');
								}
								messages = groupsAndMessages.get(groupName);
								messages = messages.add(msg);
								System.out.print(msg);
								conn.close();
							}else{
								result = "error: invalid user name";
								toClient.writeBytes(result);
								conn.close();
								continue;
							}
						}else{
							result = "error: invalid group name";
							toClient.writeBytes(result);
							conn.close();
							continue;
						}
					}else if (request[0].equals("get")){
						if (asciiEncoder.canEncode(groupName) && !(groupName.matches(".*\\s+.*")) && groupsAndMessages.containsKey(groupName)){
							ArrayList<String> groupMessages = groupsAndMessages.get(groupName);
							System.out.println(groupMessages.size() + "messages");
							//print message header
							System.out.println();
							for (int i = 0; i< groupMessages.size(); i++){
								//print message
								System.out.println();
							}
						}else{
							result = "error: invalid group name";
							toClient.writeBytes(result);
							conn.close();
							continue;
						}
					}
				}
				System.out.println("closing the connection\n");
				conn.close(); 
			}catch (IOException e) {
				System.out.println(e);
			}
		}




		while(true){

			String line; // read the data from the client
			line = fromClient.readLine();
			System.out.println("got line \"" + line + "\""); // show what we got
			String result = line; // do the work
			if(result.equalsIgnoreCase("post "+"group2")){
				result = "Ok" + '\n';
			}
			else{
				result = "error" + '\n';
			}
			toClient.writeBytes(result); // send the result
			if(result.equalsIgnoreCase("error" + '\n')){
				conn.close();
				continue;
			}
			line = fromClient.readLine();
			System.out.println("got line \"" + line + "\""); // show what we got
			result = line;
			if(result.equalsIgnoreCase("id "+"School")){
				result = "Ok" + '\n';
			}
			else{
				result = "error" + '\n';
			}
			toClient.writeBytes(result);
			if(result.equalsIgnoreCase("error" + '\n')){
				conn.close();
				continue;
			}
			String msg = "";
			while((line = fromClient.readLine()) != null){
				msg+=line + '\n';
				toClient.writeBytes("Received: "+line+'\n');
			}
			// implement message storing

			System.out.print(msg);
			conn.close(); // close connection
		}
		//svc.close(); // stop listening
	}
}