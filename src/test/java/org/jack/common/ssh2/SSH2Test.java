package org.jack.common.ssh2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jack.common.BaseTest;
import org.junit.Test;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileHandle;

public class SSH2Test extends BaseTest {
	@Test
	public void test1() {
		Connection conn=RemoteCommandUtils.login("172.16.235.9", "root", "zd,123");
		String stdout=null;
		stdout=RemoteCommandUtils.execute(conn, "cd /home/bms/bms_biz&&pwd");
		log(stdout);
//		stdout=RemoteCommandUtils.execute(session, "cd /home/bms/bms_biz");
//		log(stdout);
//		stdout=RemoteCommandUtils.execute(conn, "ls");
//		log(stdout);
//		SCPClient client = new SCPClient(conn);
//		try {
//			client.put("D:\\tmp\\sfsd.txt", 0, "/home/bms/bms_biz", null);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		File file=new File("D:\\tmp\\sfsd.txt");
//		RemoteCommandUtils.uploadFile(file, "/home/bms/bms_biz/sfsd.txt", conn);
		conn.close();
	}
}
