package com.ai.servicebusiness.config.db;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReadWriteDataSourceAspect {

    @Pointcut("@annotation(com.ai.servicebusiness.config.db.ReadOnly)")
    public void readOnlyMethods() {}

    @Pointcut("within(com.ai.servicebusiness.handler..*)")
    public void applicationPackagePointcut() {}

    @Before("applicationPackagePointcut() && readOnlyMethods()")
    public void setReadDataSource() {
        RoutingContext.markRead();
    }


    @After("applicationPackagePointcut()")
    public void clearDataSource() {
        RoutingContext.clear();
    }


}


 /*   @Before("applicationPackagePointcut() && !readOnlyMethods()")
    public void setWriteDataSource() {
        RoutingContext.markWrite();
    }*/