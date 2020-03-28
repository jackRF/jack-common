package com.jd.drools.test;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.marshalling.impl.ProtobufMessages.KnowledgeBase;

/**
 * RuleBaseFacatory 单实例RuleBase生成工具
 * 
 * @author quzishen
 */

public class RuleBaseFacatory {

    private static InternalKnowledgeBase knowledgeBase;

    public static InternalKnowledgeBase getRuleBase() {
        return knowledgeBase= KnowledgeBaseFactory.newKnowledgeBase();
    }
}