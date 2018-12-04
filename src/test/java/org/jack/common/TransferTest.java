package org.jack.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jack.common.util.HttpUtils;
import org.jack.common.util.IOUtils;
import org.junit.Test;

public class TransferTest extends BaseTest{
	@Test
	public void testDownload() {
		String url="http://10.100.3.71:10000/jiekuan_api1/bms-api-error.log_2018-10-05-02.log";
		File dir=new File("D:/data/online");
		File dest=new File(dir,url.substring(url.lastIndexOf("/")+1));
		download(url, dest);
	}
	private void download(String url,File dest) {
		try {
			HttpResponse httpResponse=HttpUtils.request(url);
			HttpEntity httpEntity=httpResponse.getEntity();
			InputStream inputStream=httpEntity.getContent();
			IOUtils.copy(inputStream, new FileOutputStream(dest));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
