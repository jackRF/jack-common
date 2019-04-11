package org.jack.common.orm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public class MapperTest {
	protected final Log logger = LogFactory.getLog(getClass());
	private ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);
	private DocumentLoader documentLoader = new DefaultDocumentLoader();
	private EntityResolver entityResolver=new DelegatingEntityResolver(getClass().getClassLoader());
	@Test
	public void testMapper() {
		Map<String,MapperInfo> context=new HashMap<String,MapperInfo>();
		File projectDir=new File("D:\\Git\\merge\\bms-trade\\bms-biz");
		File resourcesDir=new File(projectDir,"src/main/resources");
		File mapperDir=new File(resourcesDir,"mybatis/mapper");
		File file=new File(mapperDir,"audit/last/FinalAuditMapper.sqlMap.xml");
		processMapper(file, context);

	}
	private void processMapper(File file,Map<String,MapperInfo> context){
		Document doc=loadDocument(file);
		Element element=doc.getDocumentElement();
		logger.info(element.getNodeName());
		NodeList nodeList=element.getChildNodes();
		int ln=nodeList.getLength();
		for(int i=0;i<ln;i++){
			Node node=nodeList.item(i);
			if(node instanceof Element){
				Element el=(Element)node;
				String sql=exportSql(el, context);
				logger.info(sql);
			}
			
		}
	}
	private String exportSql(Element element,Map<String,MapperInfo> context){
		NodeList nodeList=element.getChildNodes();
		int ln=nodeList.getLength();
		StringBuilder sql=new StringBuilder();
		for(int i=0;i<ln;i++){
			Node node=nodeList.item(i);
			if(node.getNodeType()==3){
				sql.append(node.getNodeValue());
			}else if(node.getNodeType()==1){
				Element cel=(Element)node;
				if(node.getNodeName()=="include"){
					String refid=cel.getAttribute("refid");
					sql.append("<include "+refid+">");
				}
			}
		}
		return sql.toString();
	}
	private Document loadDocument(File file) {
		InputSource inputSource;
		try {
			inputSource = new InputSource(new FileInputStream(file));
			inputSource.setEncoding("utf-8");
			return documentLoader.loadDocument(inputSource, entityResolver, errorHandler,  XmlValidationModeDetector.VALIDATION_AUTO, false);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
