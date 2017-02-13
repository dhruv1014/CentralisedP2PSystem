package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;


public class P2PServer {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			ServerSocket socket = new ServerSocket(5005);
			System.out.println("Server is up and running!!!");
			while(true){
				Socket serverSocket = socket.accept();
				//thread creation
				Thread t = new Thread(new PeerConnect(serverSocket));
				t.start();
			}		
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}

/*
 * Class PeerConnect has all the functionality
 * It runs on the thread 
 */
class PeerConnect implements Runnable{
	
	public static HashMap<String,ArrayList<String>> fileMap = new HashMap<String, ArrayList<String>>();
	public static HashSet <String> exitCheck = new HashSet<String>();
	public Socket clientSocket;
	
	// constructor to initialize socket
	public PeerConnect(Socket socket)throws IOException{
		this.clientSocket = socket;
	}
	boolean check = true;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(check){
			try{
				String peerId, fileName, option, searchResult, error, checkId;
				String location = "";
				ArrayList<String> fileLocation = new ArrayList<String> ();
				
				
				Scanner sc = new Scanner(System.in);
				Scanner serverIn = new Scanner(clientSocket.getInputStream());
				PrintStream serverOut = new PrintStream(clientSocket.getOutputStream());
				peerId = serverIn.nextLine();
				
				exitCheck.add(peerId);
				System.out.println(exitCheck);
				//Automatically registering files as the peer is connected
				//File peerDirectory = new File("E:/Java/Workspace/PA1_CentralizedP2PSystem/sharedfolder/"+peerId);
				File peerDirectory = new File("D:/Project/HW1/SharedFolder/"+peerId);
				File[] sharedFiles = peerDirectory.listFiles();
				
				for(int i = 0; i < sharedFiles.length; i++){
						registry(peerId, sharedFiles[i].getName());
				}
				
				//getting the option selected at client side
				option = serverIn.nextLine();
				
				switch(option){
			
				case "1": // case to register a file.
					
					//registering the remaining files that are not already registered.
					for(int i = 0; i < sharedFiles.length; i++){
							registry(peerId, sharedFiles[i].getName());
					}
					serverOut.println(option);
					
					/* ***!!!PERFORMANCE EVALUATION!!! *** */ 

					/*long startTime = System.currentTimeMillis();
					 long endTime;
					 File dir= new File("D:/Project/HW1/SharedFolder/1100");
					File[] sharedfiles = dir.listFiles();

					System.out.println("# of files registered: " + sharedfiles.length);

					System.out.println("Registering the File(s) ...");

					for(int i=0;i<sharedfiles.length;i++)
						registry(peerId,sharedfiles[i].getName());

					System.out.println("File(s) Registered!!!");

					endTime = System.currentTimeMillis();

					long totalTime = endTime - startTime;
					System.out.println("Total time to register "+ sharedfiles.length +" files : "+(totalTime)+"msec");

					System.out.println("List of all the files on different Peers");
					for(Map.Entry<String, ArrayList<String>> entry : fileMap.entrySet())
					{
						System.out.println("File Name is: "+entry.getKey()+" PeerID is: "+entry.getValue());
					}

					*/
					break;
					
				case "2": // case to search a file.
					
					fileName = serverIn.nextLine();
					fileLocation = lookup(fileName);
					
					try{
						for(int i = 0; i< fileLocation.size();i++){
							location += fileLocation.get(i)+" ";
						}
						searchResult = "The file is present with these peers: "+location;
						System.out.println(location);
						serverOut.println(searchResult);
						
					}
					catch(Exception e){
						error = "File not registered";
						serverOut.println(error);
					}
					
					/* ***!!!PERFORMANCE EVALUATION!!! ***/
					File dir1= new File("D:/Project/HW1/SharedFolder/1100");
					File[] sharedfiles1 = dir1.listFiles();

					System.out.println("# of files registered: " + sharedfiles1.length);

					//System.out.println("Registering the File(s) ...");

					long startTime1 =System.currentTimeMillis();
					long endTime1;

					for(int i=0;i<sharedfiles1.length;i++)
					{
						fileLocation = lookup(sharedfiles1[i].getName());
						System.out.println("File present with "+fileLocation);
					}

					String searchDetails="";
					Iterator<String> iterator = (fileLocation).listIterator();
					while(iterator.hasNext())
					{
						searchDetails+=iterator.next()+" ";
					}
					String sendDetails = "The file is present with these Peers: "+searchDetails;
					serverOut.println(sendDetails);
					System.out.println("Search Complete found at: "+searchDetails);

					endTime1 = System.currentTimeMillis();

					long totalTime1 = endTime1 - startTime1;
					System.out.println("Total time to search files: "+(totalTime1)+"msec");

					
					break;
					
				
				case "3": // case to download a file
					
					serverOut.println(option);
					break;
				
				case "4": // exit case
					
					checkId = serverIn.nextLine();
					/*exitCheck.remove(checkId);
					if((exitCheck.isEmpty())){
						System.out.println("Exiting Server!!!");
						clientSocket.close();
						System.exit(0);
						serverOut.println(option);
						
					}
					else{
						System.out.println("Peer "+ peerId+" disconected");
						serverOut.println(option);
					}*/
					exitCheck.remove(checkId);
					clientSocket.close();
					System.out.println("Client Dissconnected");
					check = false;	//exit from the while(true) loop
					//System.exit(0);
					
					break;
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}	
		}
	}
	
	/*
	 * Register function - To register file present with each peer  
	 */
	public void registry(String peerId, String fileName) throws IOException{
		
		ArrayList<String> peerList = new ArrayList<String>();
		ArrayList<String> checkList = new ArrayList<String>();
		
		peerList.add(peerId);
		checkList = fileMap.get(fileName);
		
		if(checkList == null || checkList.isEmpty()){
			fileMap.put(fileName, peerList);
		}
		else{
			for(int i = 0; i <checkList.size();i++){
				if(checkList.get(i).equals(peerId)){
					checkList.remove(i);
				}
			}
			checkList.add(peerId);
			fileMap.put(fileName, checkList);
		}
	}
	
	/*
	 * Lookup file - Search file return the peer with which it is present 
	 */
	public ArrayList<String> lookup(String fileName)throws IOException{
		
		ArrayList<String> peerList = new ArrayList<String>();
		peerList = fileMap.get(fileName);
		return peerList;
		
	}
	
}