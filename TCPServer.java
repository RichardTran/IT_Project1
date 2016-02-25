import java.io.*;
import java.net.*;
public class TCPServer {
	public static void main(String args[]) throws Exception {

		ServerSocket svc = new ServerSocket(12345, 5); // listen on port 12345
//		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));

		while(true){
			System.out.println("Awaiting request");
			Socket conn = svc.accept(); // wait for a connection
			System.out.println("Received request");
			// get the input/output streams for the socket
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());

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
			if(result.equalsIgnoreCase("id "+"rpt38")){
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