package org.jack.common.logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jack.common.util.HttpUtils;
import org.jack.common.util.IOUtils;
import org.junit.Test;

public class LoggerCollectTest {
	@Test
	public void testBms() {
		String fileName="bms-api-info.log_2019-04-01-09.log";
		String destFileName="1"+"-"+fileName;
		String url="http://10.100.3.71:10000/jiekuan_api1/"+fileName;
		File dir=new File("D:/data/online/bms");
		collect(url,new File(dir,destFileName));
	}
	private void collect(String url,File file){
		try {
			HttpResponse httpResponse=HttpUtils.request(url);
			HttpEntity httpEntity=httpResponse.getEntity();
			BufferedInputStream in=new BufferedInputStream(httpEntity.getContent());
			BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(file));
			IOUtils.copy(in, out);
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
