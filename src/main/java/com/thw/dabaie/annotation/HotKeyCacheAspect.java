package com.thw.dabaie.annotation;

import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class HotKeyCacheAspect {


    // 创建一个 SpEL 表达式解析器，SpEL 是 Spring 的内置表达式语言，能在运行时动态计算字符串里的变量。
    private final ExpressionParser parser = new SpelExpressionParser();
    // 有了这个 nameDiscoverer，就能拿到方法签名里的参数名字，然后把它们放进 SpEL 的上下文。
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(hotKeyCache)")
    public Object around(ProceedingJoinPoint joinPoint, HotKeyCache hotKeyCache) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 解析 SpEL，生成 key
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        String key = parser.parseExpression(hotKeyCache.key()).getValue(context, String.class);

        // 1. 判断是否为热 key
        if (JdHotKeyStore.isHotKey(key)) {
            Object cached = JdHotKeyStore.get(key);
            if (cached != null) {
                return cached;
            }
        }

        // 2. 调用原方法
        Object result = joinPoint.proceed();

        // 3. 缓存结果
        JdHotKeyStore.smartSet(key, result);

        return result;
    }
}
