package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){}


    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint){
        MethodSignature signature=(MethodSignature) joinPoint.getSignature();
        AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);
        OperationType type=autoFill.value();
        Object[]args=joinPoint.getArgs();
        Object entity=args[0];
        if(args==null||args.length==0){
            return;
        }
        Long userId= BaseContext.getCurrentId();
        LocalDateTime time=LocalDateTime.now();
        if(type==OperationType.INSERT){
            try {
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setCreateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);

                setUpdateTime.invoke(entity,time);
                setUpdateUser.invoke(entity,userId);
                setCreateTime.invoke(entity,time);
                setCreateUser.invoke(entity,userId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(type==OperationType.UPDATE){
            try {
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                setUpdateTime.invoke(entity,time);
                setUpdateUser.invoke(entity,userId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
