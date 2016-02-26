import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;


public class TCPServer implements Runnable { 
		Socket conn; //per-thread copy of the client socket
		Hashtable<String, ArrayList<Message>> groupsAndMessages = new Hashtable<String, ArrayList<Message>>();


		TCPServer(Socket sock) {
			this.conn = sock;
		}

		public static void main(String args[]) throws Exception {
			String host = "localhost";
			String groupname = null;
			int portNum = 12345;

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
				String line;
				CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder(); //To check if string is all ASCII

				
				line = fromClient.readLine();
				while (line!= null) { // while there's data from the client
					System.out.println("got line \"" + line + "\""); // show what we got
					String result = line;
					
					String[] request = line.split("\\s+");
					String groupName = request[1];
					String requestType = request[0];
					System.out.println("server reading groupname as "+groupName);
					System.out.println("server reading request type as "+requestType);

					if (requestType.equals("post")){
						if (asciiEncoder.canEncode(groupName) && !(groupName.matches(".*\\s+.*"))){
							if (!groupsAndMessages.containsKey(groupName)){
								ArrayList<Message> messages = new ArrayList<Message>();
								groupsAndMessages.put(groupName, messages);
							}
							System.out.println(line);
							line = fromClient.readLine();
							System.out.println(line);
							System.out.println("got line \"" + line + "\""); // show what we got
							result = line;
							String id = request[1];
							System.out.println(id);
							if (request[0].equals("id") && asciiEncoder.canEncode(id) && !(id.matches(".*\\s+.*"))){
								String msg = "";
								while((line = fromClient.readLine()) != null){
									msg+=line + '\n';
									toClient.writeBytes("Received: "+line+'\n');
								}
								// (String message, String timestamp, String id, String ip)
								Date date = new Date();
								SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
								String formattedDate = sdf.format(date);
								Message message = new Message(msg, formattedDate, id, conn.getRemoteSocketAddress().toString());
								ArrayList<Message> messages = groupsAndMessages.get(groupName);
								messages.add(message);
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
					}else if (requestType.equals("get")){
						if (asciiEncoder.canEncode(groupName) && !(groupName.matches(".*\\s+.*")) && groupsAndMessages.containsKey(groupName)){
							ArrayList<Message> groupMessages = groupsAndMessages.get(groupName);
							System.out.println(groupMessages.size() + "messages");
							//print message header
							System.out.println();
							for (int i = 0; i< groupMessages.size(); i++){

								//From paul /192.168.60.132:42786 Fri Feb 05 16:15:38 EST 2016
								//print message
								Message message = groupMessages.get(i);
								System.out.println("From " + message.getId() + " /" + message.getIp() + " "+message.getTimeStamp());
								System.out.println();
								System.out.println(message);
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
}
