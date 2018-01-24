package org.jack.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 流工具类
 * 
 * @author YM10177
 *
 */
public class IOUtils {
	private static final int BUFFER_SIZE = 1024 * 8;
	/**
	 * 从文件读text  UTF-8
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readText(File file) throws IOException{
		return readText(file, "UTF-8");
	}
	/**
	 * 从文件读text
	 * @param file
	 * @param charsetName 编码
	 * @return
	 * @throws IOException
	 */
	public static String readText(File file,String charsetName) throws IOException{
		BufferedInputStream in=null;
		try{
			return readText(new FileInputStream(file), charsetName);
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 从输入流读text charsetName
	 * @param is
	 * @param charsetName
	 * @return
	 * @throws IOException
	 */
	public static String readText(InputStream is) throws IOException {
		return readText(is,"UTF-8");
	}
	/**
	 * 从输入流读text charsetName
	 * @param is
	 * @param charsetName
	 * @return
	 * @throws IOException
	 */
	public static String readText(InputStream is,String charsetName) throws IOException {
		BufferedReader reader=null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is,charsetName));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public void copy(InputStream in, OutputStream out) throws IOException {
		try {
			write(in, out, BUFFER_SIZE);
		}finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static long write(InputStream is, OutputStream os, int bufferSize)
			throws IOException {
		int read;
		long total = 0;
		byte[] buff = new byte[bufferSize];
		while (is.available() > 0) {
			read = is.read(buff, 0, buff.length);
			if (read > 0) {
				os.write(buff, 0, read);
				total += read;
			}
		}
		return total;
	}
}
