package client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class PeerFileDownload implements Runnable
{
	String fileDownloadPath;
	int portNumber;
	Socket peerSocket;
	Scanner in;
	PrintStream out;

	public PeerFileDownload(String filePath,int userInpPort){
		this.portNumber = userInpPort;
		this.fileDownloadPath = filePath;
	}

	public void run(){
		try{
			ServerSocket downloadSocket = new ServerSocket(portNumber);
			System.out.println("starting client socket now");
			
			while(true){
				//accept the connection from the socket
				peerSocket = downloadSocket.accept();
				System.out.println("Client connected for File sharing ...");

				out = new PrintStream(peerSocket.getOutputStream());
				in = new Scanner(peerSocket.getInputStream());

				//get the fileName from ClientAskingForFile
				String fileName = in.nextLine();
				System.out.println("Requested file is: "+fileName);
				String peerForFile = in.nextLine();
				
				
				// !!!***COMMENT THE BELOW LINE***!!! -- as this downloads from a specific folder(named "PORT_NO")
				File checkFile = new File(fileDownloadPath + peerForFile +"/" + fileName);
				
				// !!!***UNCOMMENT THE BELOW LINE***!!!	-- this will download from the path you specify in Client.java line (18) 
				//File checkFile = new File(fileDownloadPath + fileName);
				
				FileInputStream fin = new FileInputStream(checkFile);
				BufferedInputStream buffReader = new BufferedInputStream(fin);
				
				//check if the file exists, for it to be downloaded
				if (!checkFile.exists()){
					System.out.println("File doesnot Exists");
					buffReader.close();
					return;
				}

				//get the file size, as the buffer needs to be allocated an initial size
				int size = (int) checkFile.length();	//convert from long to int
				byte[] buffContent = new byte[size];
				
				//send file size
				out.println(size);
				
				//allocate a buffer to store contents of file
				int startRead = 0;	//how much is read in total
				int numOfRead = 0;	//how much is read in each read() call

				//read into buffContent, from StartRead until end of file
				while (startRead < buffContent.length && (numOfRead = buffReader.read(buffContent, startRead, buffContent.length - startRead)) >= 0) 
				{
					startRead = startRead + numOfRead;
				}
				//Validate all the bytes have been read
				if (startRead < buffContent.length){
					System.out.println("File Read Incompletely" + checkFile.getName());
				}
				out.println(buffContent);
				buffReader.close();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
