package com.jd.drools.test;

import java.util.Collection;

import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class Point2RuleEngine extends AbstractRuleEngine {

    public Point2RuleEngine(){
        this.init();
    }
    @Override
    protected Collection<KiePackage> loadKiePackages() {
        KnowledgeBuilder  kBuilder=KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource  resource=ResourceFactory.newClassPathResource("addpoint.drl");
        kBuilder.add(resource, ResourceType.DRL);
        resource=ResourceFactory.newClassPathResource("subpoint.drl");
        kBuilder.add(resource, ResourceType.DRL);
        resource=ResourceFactory.newClassPathResource("orderpoint.drl");
        kBuilder.add(resource, ResourceType.DRL);
        resource=ResourceFactory.newClassPathResource("customer.drl");
        kBuilder.add(resource, ResourceType.DRL);
        if(kBuilder.hasErrors()){
            logger.error("规则文件解析错误："+kBuilder.getErrors().toString());
        }
        return kBuilder.getKnowledgePackages();
    }
    
}