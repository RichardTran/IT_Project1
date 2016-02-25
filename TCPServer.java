import java.io.*;
import java.net.*;
public class TCPServer {
	public static void main(String args[]) throws Exception {

		ServerSocket svc = new ServerSocket(12345, 5); // listen on port 12345
		while(true){
			System.out.println("Awaiting request");
			Socket conn = svc.accept(); // wait for a connection
			System.out.println("Received request");
			// get the input/output streams for the socket
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());

			String line; // read the data from the client
			line = fromClient.readLine();
				//System.out.println("got line \"" + line + "\"" + '\n'); // show what we got
				String result = line + '\n'; // do the work
				if(result.equalsIgnoreCase("group" + '\n')){
					result = "Ok";
				}
				else{
					result = "error";
				}
				toClient.writeBytes(result); // send the result
				line = fromClient.readLine();
				System.out.println(line);
				result = line;
				if(result.equalsIgnoreCase("group" + '\n')){
					result = "Ok";
				}
				else{
					result = "error";
				}



			conn.close(); // close connection
		}
		//svc.close(); // stop listening
	}
}