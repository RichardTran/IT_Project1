//Group Members: Amanda Goonetilleke, Richard Tran, Cheryl Chi


import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;


public class server implements Runnable { 
	
		Socket conn; //per-thread copy of the client socket
		public static Hashtable<String, ArrayList<Message>> groupsAndMessages = new Hashtable<String, ArrayList<Message>>();
		public static int portNum;

		server(Socket sock) {
			this.conn = sock;
		}

		public static void main(String args[]) throws Exception {	

			ServerSocket svc = null;
			String host = "localhost";
			String groupname = null;
			portNum = 12345;
			int argc = args.length;
			
			if (argc == 2 && args[0].equals("-p")){
					try{
					portNum = Integer.parseInt(args[1]); 
					}catch(Exception e){
						System.out.println("error: invalid port number");
			        	System.exit(1);
					}
			    	if (!(portNum >= 1024 && portNum <= 65535)){
			        	System.out.println("error: invalid port number");
			        	System.exit(1);
			        }
			}else if (argc != 0){
				System.out.println("error: invalid server command");
				System.exit(1);
			}
			
			try{
				svc = new ServerSocket(portNum, 5);
			}
			catch (Exception e){
				System.out.println("error connecting to server");
			}
			while (true) {
				Socket conn = svc.accept();
				new Thread(new server(conn)).start();
			}
		} 

		public void run() {
			try {
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());
				String line;
				 //Used to check if string is all ASCII
				CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

				
				String msg = "";
				line = fromClient.readLine();
				while (line != null) { 
					//System.out.println("received line \"" + line + "\"");
					String[] request = line.split("\\s+");
					String groupName = request[1];
					String requestType = request[0];
					String result = "";

					if (requestType.equals("post")){
						if (asciiEncoder.canEncode(groupName) && !(groupName.matches(".*\\s+.*"))){
							//groupname is in a valid format
							result = "Ok" + '\n'; 
							toClient.writeBytes(result);
							
							//if groupname doesn't exist we add it
							if (!groupsAndMessages.containsKey(groupName)){
								ArrayList<Message> messages = new ArrayList<Message>();
								groupsAndMessages.put(groupName, messages);
							}
							
							//checks and prints id
							line = fromClient.readLine();
							request = line.split("\\s+");
							//System.out.println("got line \"" + line + "\""); // show what we got
							
							if (request.length!=2){
								result = "error: invalid user name\n";
								toClient.writeBytes(result);
								conn.close();
								continue;
							}
							
							String id = request[1];
							if (request[0].equals("id") && asciiEncoder.canEncode(id) && !(id.matches(".*\\s+.*"))){
								//id is in a valid format
								result = "Ok" + '\n'; 
								toClient.writeBytes(result); 
								
								while((line = fromClient.readLine()) != null){
									msg+=line + '\n';
									toClient.writeBytes("Received: "+line+'\n');
								}
								//System.out.println(msg);
								// (String message, String timestamp, String id, String ip)
								Date date = new Date();
								SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
								String formattedDate = sdf.format(date);
								Message message = new Message(msg, formattedDate, id, conn.getInetAddress().toString(), portNum);
								ArrayList<Message> messages = groupsAndMessages.get(groupName);
								messages.add(message);
								
								conn.close();
							}else{
								result = "error: invalid user name\n";
								toClient.writeBytes(result);
								conn.close();
								continue;
							}
						}else{
							result = "error: invalid group name\n";
							toClient.writeBytes(result);
							conn.close();
							continue;
						}
					}else if (requestType.equals("get")){
						if (asciiEncoder.canEncode(groupName) && !(groupName.matches(".*\\s+.*")) && groupsAndMessages.containsKey(groupName)){
							//groupname is valid
							result = "Ok\n";
							toClient.writeBytes(result);
							
							ArrayList<Message> groupMessages = groupsAndMessages.get(groupName);
							toClient.writeBytes("messages: "+ groupMessages.size() + '\n');
							
							//print message header
							System.out.println();
							for (int i = 0; i< groupMessages.size(); i++){
								Message message = groupMessages.get(i);
								String header ="From " + message.getId() + " "+ message.getIp() + ":"+message.getPortNum()+ " "+message.getTimeStamp()+'\n';
								toClient.writeBytes(header + '\n');
								toClient.writeBytes(message.getMessage()+ '\n');
							}

							conn.close();
							return;
						}else{
							result = "error: invalid group name\n";
							toClient.writeBytes(result);
							conn.close();
							continue;
						}
					}
				}
				//System.out.println("closing the connection\n");
				//conn.close(); 
			}catch (IOException e) {
				//System.out.println(e);
			}



	}
}
