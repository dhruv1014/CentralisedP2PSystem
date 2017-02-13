package server;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CreaterRandomFiles 
{

	public static void main(String args[]) throws IOException
	{
		
				
		for(int i=50000;i<51000;i++)
		{
			File file = new File("D:/Project/HW1/SharedFolder/1400/"+i);
			RandomAccessFile rndmAccFile = new RandomAccessFile(file, "rw");
			rndmAccFile.setLength(1000);
			rndmAccFile.close();
		}
		
		for(int i=70000;i<71000;i++)
		{
			File file = new File("D:/Project/HW1/SharedFolder/1300/"+i);
			RandomAccessFile rndmAccFile = new RandomAccessFile(file, "rw");
			rndmAccFile.setLength(1000);
			rndmAccFile.close();
		}
	}
}