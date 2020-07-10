package org.jack.common.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class JobConfig {

    private static final Logger logger=LoggerFactory.getLogger(JobConfig.class);

    
    @Scheduled(cron="${job.schedules.memberLevelUpdateAndPointGrant.cron}")
    public void executeMemberLevelUpdateAndPointGrant(){
        logger.info("executeMemberLevelUpdateAndPointGrant start");
        logger.info("executeMemberLevelUpdateAndPointGrant finish");
   }
   @Scheduled(cron="${job.schedules.memberPointExpire.cron}")
    public void executeMemberPointExpire(){
        logger.info("executeMemberPointExpire start");
        logger.info("executeMemberPointExpire finish");
   }
   @Scheduled(cron="${job.schedules.customerTask.cron}")
    public void executeCustomerTask(){
        logger.info("executeCustomerTask start");
        logger.info("executeCustomerTask finish");
   }
}