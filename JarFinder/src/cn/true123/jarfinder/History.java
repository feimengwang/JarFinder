package cn.true123.jarfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class History {
	public static String[] getHistory() {
		File file = new File("history.txt");
		if (!file.exists())
			return null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			String[] history = new String[2];
			
			while ((line = br.readLine()) != null) {
				if(line.startsWith("search:")){
					history[1]=line;
				}else if(line.startsWith("dir:")){
					history[0]=line;
				}
			}
			return history;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	

	public static void save(String text) {
		FileOutputStream fos =null;
		try {
			String[] history = getHistory();
			File file = new File("history.txt");
			if (!file.exists())
				file.createNewFile();
			fos = new FileOutputStream(file);
			
			if(history==null)history=new String[2];
			if(text.startsWith("search:")){
				history[1]=text;
			}else if(text.startsWith("dir:")){
				history[0]=text;
			}
			if(history[0]==null)history[0]="";
			if(history[1]==null)history[1]="";
			fos.write(history[0].getBytes());
			fos.write("\n".getBytes());
			fos.write(history[1].getBytes());
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}
	
}
