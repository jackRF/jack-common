package com.jd.drools.test;

import java.util.Collection;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRuleEngine {
    protected Logger logger=LoggerFactory.getLogger(getClass());
    private InternalKnowledgeBase knowledgeBase;
    static{
        System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
    }
    public AbstractRuleEngine(){
        knowledgeBase=KnowledgeBaseFactory.newKnowledgeBase();
    }
    protected abstract Collection<KiePackage> loadKiePackages();
    protected void init(){
        logger.info("初始化规则引擎…");
        knowledgeBase.addPackages(loadKiePackages());
        logger.info("初始化规则引擎结束.");
    }
    public void refresh(){
        logger.info("刷新规则文件…");
        Collection<KiePackage>  kiePackages=knowledgeBase.getKiePackages();
        for(KiePackage kiePackage:kiePackages){
            knowledgeBase.removeKiePackage(kiePackage.getName());
        }
        this.init();
        logger.info("刷新规则文件结束.");
    }
    public void execute(Object...facts){
        logger.info("执行规则…");
        long startMillis = System.currentTimeMillis();
        KieSession session=knowledgeBase.newKieSession();
        for(Object fact:facts){
            session.insert(fact);
        }
        session.fireAllRules();
        session.dispose();
        logger.info(String.format("执行规则结束,耗时:%sms", System.currentTimeMillis()-startMillis));
    }
}