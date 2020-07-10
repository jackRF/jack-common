package org.jack.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = { "com.sitco.point.repository" })
@EnableTransactionManagement
public class DataSourceConfig {
  // @Bean
  // @ConfigurationProperties(prefix = "spring.datasource")
  // public DataSource dataSource() {
  //   return DataSourceBuilder.create().build();
  // }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("com.sitco.point.entity");
    // Map<String, Object> jpaProperties = new HashMap<>();
    // jpaProperties.put("hibernate.enable_lazy_load_no_trans", true);
    // jpaProperties.put("hibernate.jdbc.time_zone", "Asia/Shanghai");
    // factory.setJpaPropertyMap(jpaProperties);
    factory.setDataSource(dataSource());
    return factory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory);
    return txManager;
  }

  @Bean
  public DataSource dataSource() {
    JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
    bean.setJndiName("java:/comp/env/datasources/crm");
    bean.setProxyInterface(DataSource.class);
    bean.setLookupOnStartup(false);
    try {
      bean.afterPropertiesSet();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (NamingException e) {
      e.printStackTrace();
    }
    return (DataSource) bean.getObject();
  }
}