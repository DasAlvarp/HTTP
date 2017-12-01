import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
								File toLoad = new File("public_html" + results.get(1));
								FileInputStream fstream = new FileInputStream(toLoad);
								
								//status
								String toSend = "HTTP/1.1 " + results.get(0) + " " + results.get(1) + "\n";
								
								//headers
								toSend += "Server: alvaroServer/0.0.01\n";
								toSend += "Content-Length: " + toLoad.length() + "\n";
								toSend += "Content-Type: " + results.get(3) + "\n";
								//body
								byte[] readMe = new byte[(int)toLoad.length()];
								fstream.read(readMe);
								toSend += readMe.toString();

								//sending!
								PrintWriter out = new PrintWriter(client.getOutputStream(), true);
								out.println(toSend);
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
		if(line1[0] != "GET" || line1[2] != "HTTP/1.1" || contentType == "error")
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
					toSends.add("OK");
					break;
			}
		}
		else
		{
			String[] path = line1[1].split("/");
			toSends.add("200");//check for valid path, etc later. For now, we make code.
			toSends.add("OK");
		}



		return toSends;
	}
}