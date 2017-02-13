package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class P2PClient {

	@SuppressWarnings("deprecation")
	public static void main(String[] args){
		// TODO Auto-generated method stub
		String fileDirectory = "D:/Project/HW1/SharedFolder/";
		try{
			String option, check, fileSearch, fileDownload, clientId;
			Socket clientSocket = new Socket("localhost", 5005);
			int peerId = Integer.parseInt(args[0]);
			Scanner sc = new Scanner(System.in);
			Scanner clientIn = new Scanner(clientSocket.getInputStream());
			
			// thread for file download amongst peers
			Thread t1 = new Thread(new PeerFileDownload(fileDirectory,peerId));
			t1.start();
			
			do{
				System.out.println("****MENU****");
				System.out.println("1. Register Files");
				System.out.println("2. Search for a File");
				System.out.println("3. Obtain a File");
				System.out.println("4. Exit");
				
				option = sc.nextLine();
				
				PrintStream clientOut = new PrintStream(clientSocket.getOutputStream());
				clientOut.println(Integer.toString(peerId));
				
				switch(option){
				
				case "1": // case to register a file
					
					clientOut.println(option);
					System.out.println("Registering file. Please Wait!!!");
					check = clientIn.nextLine();
					System.out.println("Files Registered!!!");
					break;
					
				case "2": // case to search a file
					
					clientOut.println(option);
					System.out.println("Enter filename: ");
					fileSearch = sc.nextLine();
					clientOut.println(fileSearch);
					check = clientIn.nextLine();
					System.out.println(check);
					break;
					
				case "3": // case to download a file
					
					clientOut.println(option);
					System.out.println("Enter the name of the file to be downloaded:");
					fileDownload = sc.nextLine();
					System.out.println("Enter the peer id from where you want to download the file: ");
					clientId = sc.nextLine();
					retrieve(fileDownload,clientId,peerId);
					check = clientIn.nextLine();
					break;
					
				case "4": // exit case
					
					clientOut.println(option);
					clientOut.println(Integer.toString(peerId));
					//check = clientIn.nextLine();				
					/*sc.close();
					clientIn.close();*/
					clientSocket.close();
					t1.stop();
					clientIn.close();
					System.exit(0);
					break;
				}
			}while(!(option.equals("4")));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Retrieve function -  invoked by a peer to download a file from another peer
	 */
	public static void retrieve(String fileName, String clientId, int inpPort){
		
		String downPath = "E:/Java/Workspace/PA1_CentralizedP2PSystem/sharedfolder/"+inpPort+"/";
		//Check if the folder exists
		//Create if it doesn't exist
		File directory = new File(downPath);
		if(!directory.exists()){
			System.out.println("Creating a new folder named: "+inpPort);
			directory.mkdir();
			System.out.println("The file will be found at: "+downPath);
		}

		//Make a connection with server to get file from
		int portNumber = Integer.parseInt(clientId);
		
		try {
			Socket peerClient = new Socket("localhost",portNumber);
			System.out.println("Downloading File Please wait ...");

			//Input & Output for socket Communication
			Scanner peerIn = new Scanner(peerClient.getInputStream());
			PrintStream peerOut = new PrintStream(peerClient.getOutputStream());
	
			peerOut.println(fileName);
			peerOut.println(clientId);	
	
			long buffSize = peerIn.nextLong();
			int newBuffSize = (int) buffSize;
	
			byte[] b = new byte[newBuffSize];
			String filePath = downPath + fileName;
			//Write the file requested by the peer
			FileOutputStream writeFileStream = new FileOutputStream(filePath);

			writeFileStream.write(b);
			writeFileStream.close();

			System.out.println("Downloaded Successfully");
			System.out.println("Display file " + fileName);

			peerClient.close();

		} 
		catch (FileNotFoundException ex){
			System.out.println("FileNotFoundException : " + ex);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
/*
 * Exceptions that are left
 * while downloading check if the peer given is correct or not
 * and if the peer is up and running
 * */
 