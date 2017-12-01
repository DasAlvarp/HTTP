import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HttpServer
{
	public static void main(String[] args)
	{
		boolean running = true;
		int port = Integer.parseInt(args[0]);
		try
		{
			//set up reciever, get reciever set up.
			final ServerSocket server = new ServerSocket(port);

			while(running)
			{
				Socket client = server.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String message;
				ArrayList messageRecieved = new ArrayList<String>();

				//recieve message
				if(in.ready())
				{
					while(in.ready())
					{
						message = in.readLine();
						messageRecieved.add(message);
						System.out.println(message);
					}

					//send response. I wish I had some kind of collapsable anonymous function-y thing, but here we go.
					new Thread(new Runnable()
					{
						public void run()
						{
							ArrayList results = checkMessage(messageRecieved);
							try{
								File toLoad = new File("public_html" + results.get(3));
								FileInputStream fstream = new FileInputStream(toLoad);
								
								//status
								String toSend = "HTTP/1.1 " + results.get(0) + " " + results.get(1) + "\r\n";
								
								//headers
								toSend += "Server: alvaroServer/0.0.01\r\n";
								toSend += "Content-Length: " + fstream.getChannel().size() + "\r\n";
								toSend += "Content-Type: " + results.get(2) + "\r\n\r\n";
								//body
								byte[] readMe = new byte[(int)fstream.getChannel().size()];
								fstream.read(readMe);
								toSend += new String(readMe);

								byte[] bytesToSend = toSend.getBytes();
								//sending!
							
								client.getOutputStream().write(bytesToSend);
								System.out.println(toSend);
								System.out.println("yeah, I'm done now." + fstream.getChannel().size());
							}catch(Exception e){
								System.out.println("lol there was an error glhf.");
								e.printStackTrace();
							}
						}
				    }).start();
					//-status line
					//-entity headers
					//the actual body (use java FileInputStream)
				}

			}
		}catch(Exception e){
			System.out.println("there was an error");
		}
	}

	static ArrayList<String> checkMessage(ArrayList<String> input)
	{
		ArrayList<String> toSends = new ArrayList<String>();

		//validating first line and first parameter sent.
		

		String[] line1 = input.get(0).split(" ");

		String extension = (line1[1].split("\\."))[1];//get file type.
		String contentType;
		switch(extension)
		{
			case "html":
			case "htm":
				contentType = "text/html";
				break;
			case "gif":
				contentType = "image/gif";
				break;
			case "jpg":
			case "jpeg":
				contentType = "image/jpeg";
				break;
			case "pdf":
				contentType = "application/pdf";
				break;
			default:
				contentType = "error";
				break;
		}

		//Statuses to implement: 404/200 differentiation.
		if(!line1[0].equals("GET") || !line1[2].equals("HTTP/1.1") || contentType.equals("error"))
		{

			switch(line1[0])
			{
				case "OPTIONS":
				case "POST":
				case "PUT":
				case "DELETE":
				case "TRACE":
				case "CONNECT":
					toSends.add("501");
					toSends.add("Not Implemented");
					break;
				default:
					toSends.add("400");
					toSends.add("Bad Request");
					break;
			}
		}
		else
		{
			String[] path = line1[1].split("/");
			toSends.add("200");//check for valid path, etc later. For now, we make code.
			toSends.add("OK");
		}
		toSends.add(contentType);
		toSends.add(line1[1]);

		return toSends;
	}
}