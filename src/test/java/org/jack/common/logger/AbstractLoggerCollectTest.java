package org.jack.common.logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jack.common.BaseTest;
import org.jack.common.util.HttpUtils;
import org.jack.common.util.IOUtils;

public abstract class AbstractLoggerCollectTest extends BaseTest {
	protected void collect(String url,File file){
		try {
			HttpResponse httpResponse=HttpUtils.request(url);
			HttpEntity httpEntity=httpResponse.getEntity();
			BufferedInputStream in=new BufferedInputStream(httpEntity.getContent());
			BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(file));
			IOUtils.copy(in, out);
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
