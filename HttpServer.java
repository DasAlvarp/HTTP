import java.io.*;
import java.net.*;

public class HttpServer
{
	public static void main(String[] args)
	{
		boolean running = true;
		int port = Integer.parseInt(args[0]);
		try{
			//set up reciever, get reciever set up.
			final ServerSocket server = new ServerSocket(port);

			while(running){
				Socket client = server.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String messageRecieved;
				//recieve message
				if(in.ready())
				{
					messageRecieved = in.readLine();
					System.out.println("msg" + messageRecieved);				
					//send response in a thread. Keep on looping.
					//-status line
					//-entity headers
					//the actual body (use java FileInputStream)
				}

			}
		}catch(Exception e){
			System.out.println("there was an error");
		}
	}
}