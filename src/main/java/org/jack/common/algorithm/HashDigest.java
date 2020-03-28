package org.jack.common.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;

public class HashDigest {
	public static String getFileSHA1(File file) throws IOException {
		try {
			return hash(file,"SHA-1",40);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getFileMD5(File file) throws IOException {
		try {
			return hash(file,"MD5",32);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) throws IOException {
		final List<String> files=new ArrayList<String>();
		IOUtils.processText(new File("D:\\tmp\\dependecties.txt"), new Task<String>() {
			
			@Override
			public void toDo(String t) {
				files.add(t);
				
			}
		});
		for(String file:files){
			String sha1=getFileSHA1(new File(file));
			try{
				String sha1dest=IOUtils.readText(new File(file+".sha1"));
				if(!sha1.equals(sha1dest.trim())){
					log(file);
					log("sha1:"+sha1);
					log("sha1dest:"+sha1dest);
				}else{
//					log(file);
//					log("sha1:"+sha1);
				}
			}catch(Exception e){
				log(e);
			}
		}
	}
	public static void log(Object msg){
		System.out.println(msg);
	}
	private static String hash(File file,String algorithm,int ln) throws NoSuchAlgorithmException, IOException{
		byte[] bytes=digest(file, algorithm);
		String sha1 = new BigInteger(1, bytes).toString(16);
		return leftPad(sha1, "0", ln);
	}
	
	private static String leftPad(String text,String pad,int ln){
		int diff = ln - text.length();
		if(diff<=0||ln<=0){
			return text;
		}
		int c=diff/pad.length();
		StringBuilder pre=new StringBuilder();
		if (c > 0) {
			for (int i = 0; i < c; i++) {
				pre.append(pad);
			}
		}
		int mod=diff%pad.length();
		if(mod>0){
			pre.append(pad.substring(0,mod));
		}
		return pre+text;
	}
	private static byte[] digest(File file,String algorithm) throws NoSuchAlgorithmException, IOException {
		FileInputStream in = new FileInputStream(file);
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		byte[] buffer = new byte[1024 * 1024 * 10];
		int len = 0;
		while ((len = in.read(buffer)) > 0) {
			digest.update(buffer, 0, len);
		}
		in.close();
		return digest.digest();
	}
	public static String toHexString(byte b){
		return digits[b>>>4&0xF]+""+digits[b&0xF];
	}
	final static char[] digits = {
        '0' , '1' , '2' , '3' , '4' , '5' ,
        '6' , '7' , '8' , '9' , 'a' , 'b' ,
        'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
        'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
        'o' , 'p' , 'q' , 'r' , 's' , 't' ,
        'u' , 'v' , 'w' , 'x' , 'y' , 'z'
    };
}
