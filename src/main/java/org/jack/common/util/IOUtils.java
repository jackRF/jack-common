package org.jack.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 流工具类
 * 
 * @author YM10177
 *
 */
public class IOUtils {
	private static final int BUFFER_SIZE = 1024 * 8;

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
