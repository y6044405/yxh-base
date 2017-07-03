/*package com.tzg.tool.kit.test.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.Mixin;
import net.sf.cglib.proxy.NoOp;

import com.tzg.rpc.ClientTest;
import com.tzg.rpc.SampleInterfaceA;
import com.tzg.rpc.SampleInterfaceAImpl;
import com.tzg.rpc.SampleInterfaceB;
import com.tzg.rpc.SampleInterfaceBImpl;
import com.tzg.rpc.ClientTest.CallbackFilterImpl;
import com.tzg.rpc.ClientTest.MethodInterceptorImpl;

public class ProxyTest {
    public static void main(String[] args) {
        *//** style1.增强一个已有类 *//*
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ClientTest.class);
        enhancer.setCallback(new MethodInterceptorImpl());
        ClientTest sampleClazz1 = (ClientTest) enhancer.create();
        sampleClazz1.method1();

        *//** style2.使用CallbackFilter *//*
        Callback[] callbacks = new Callback[] { new MethodInterceptorImpl(), NoOp.INSTANCE };
        Enhancer enhancer2 = new Enhancer();
        enhancer2.setSuperclass(ClientTest.class);
        enhancer2.setCallbacks(callbacks);
        enhancer2.setCallbackFilter(new CallbackFilterImpl());
        ClientTest sampleClazz2 = (ClientTest) enhancer2.create();
        sampleClazz2.method1();
        sampleClazz2.method2();

        *//** style3.使用Mixin *//*
        Class[] interfaces = new Class[] { SampleInterfaceA.class, SampleInterfaceB.class };
        Object[] delegates = new Object[] { new SampleInterfaceAImpl(), new SampleInterfaceBImpl() };
        Object obj = Mixin.create(interfaces, delegates);
        SampleInterfaceA sampleInterfaceA = (SampleInterfaceA) obj;
        sampleInterfaceA.methodA();
        SampleInterfaceB sampleInterfaceB = (SampleInterfaceB) obj;
        sampleInterfaceB.methodB();
    }
    

    private static class CallbackFilterImpl implements CallbackFilter {
        *//** 返回的值,为指定Callback[]数组中index的值 *//*
        @Override
        public int accept(Method method) {
            if (method.getName().equals("method2"))
                return 1;
            else
                return 0;
        }

        
    }

    private static class MethodInterceptorImpl implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args,
                MethodProxy proxy) throws Throwable {
            System.out.println(method);
            return proxy.invokeSuper(obj, args);
        }

    }
}
*/