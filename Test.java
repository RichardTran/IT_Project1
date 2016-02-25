import java.io.*;

public class Test{
	public static void main(String args[]) throws Exception{
		String line;
		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
		line = userdata.readLine();
		System.out.println("got: \"" + line + "\"");
	}
}
