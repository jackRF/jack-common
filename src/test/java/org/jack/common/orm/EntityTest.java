package org.jack.common.orm;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.jack.common.BaseTest;
import org.jack.common.db.Table;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

public class EntityTest extends BaseTest {
	
	private Configuration  configuration;
	public EntityTest() {
		Configuration configuration=new Configuration(new Version("2.3.29"));
		configuration.setClassLoaderForTemplateLoading(EntityTest.class.getClassLoader(), "META-INF/templates/");
		this.configuration=configuration;
	}
	private void generateClass(Map<String,Object> dataModel) {
		try {
			Template template=configuration.getTemplate("classTematate.ftl");
			StringWriter writer = new StringWriter(); 
			template.process(dataModel, writer);
			log(writer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void tableGenerateEntity() {
//		DBTest.getTable();
		log(yaml().getProperty("database.dev_crm.db.type"));
	}
	private void tableGenerateEntity(Table table) {
		generateClass(table.convertToClassModel());
	}
	public Properties yaml() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("META-INF/database-config.yml"));
        return yaml.getObject();
    }
}
