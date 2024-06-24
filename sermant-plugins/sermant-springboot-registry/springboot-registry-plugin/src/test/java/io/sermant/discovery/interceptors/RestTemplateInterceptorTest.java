/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sermant.discovery.interceptors;

import io.sermant.core.plugin.agent.entity.ExecuteContext;
import io.sermant.core.plugin.service.PluginServiceManager;
import io.sermant.core.utils.ReflectUtils;
import io.sermant.discovery.config.PlugEffectWhiteBlackConstants;
import io.sermant.discovery.entity.PlugEffectStrategyCache;
import io.sermant.discovery.service.InvokerService;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * RestTemplate call test
 *
 * @author chengyouling
 * @since 2022-10-10
 */
public class RestTemplateInterceptorTest extends BaseTest {

    private RestTemplateInterceptor interceptor;

    private final Object[] arguments;

    @Mock
    private InvokerService invokerService;

    private final String realmName = "www.domain.com";

    private final String realmNames = "www.domain.com,www.domain2.com";

    private final static String url = "http://www.domain.com/zookeeper-provider-demo/sayHello?name=123";

    private final static String convertUrl = "http://127.0.0.1:8010/sayHello?name=123";

    /**
     * Constructor
     */
    public RestTemplateInterceptorTest() {
        arguments = new Object[2];
    }

    @Override
    public void setUp() {
        super.setUp();
        interceptor = new RestTemplateInterceptor();
        MockitoAnnotations.openMocks(this);
        pluginServiceManagerMockedStatic.when(() -> PluginServiceManager.getPluginService(InvokerService.class))
            .thenReturn(invokerService);
    }

    private URI createURI(String url) {
        return URI.create(url);
    }

    private void initStrategy(String strategy, String serviceName) {
        Optional<Object> dynamicConfig = ReflectUtils.getFieldValue(PlugEffectStrategyCache.INSTANCE, "caches");
        Assert.assertTrue(dynamicConfig.isPresent() && dynamicConfig.get() instanceof Map);
        ((Map) dynamicConfig.get()).put(PlugEffectWhiteBlackConstants.DYNAMIC_CONFIG_STRATEGY, strategy);
        ((Map) dynamicConfig.get()).put(PlugEffectWhiteBlackConstants.DYNAMIC_CONFIG_VALUE, serviceName);

    }

    @Test
    public void testRestTemplateInterceptor() throws Exception {
        ExecuteContext context = ExecuteContext.forMemberMethod(new Object(), null, arguments, null, null);
        URI uri = createURI(url);
        arguments[0] = uri;
        arguments[1] = HttpMethod.GET;

        //Contains domain names, sets multiple domain names, and does not set blacklist or whitelist
        discoveryPluginConfig.setRealmName(realmNames);
        interceptor.doBefore(context);
        URI uriNew = (URI) context.getArguments()[0];
        Assert.assertEquals(url, uriNew.toString());

        discoveryPluginConfig.setRealmName(realmName);
        //Contains domain names, and sets all through policies
        initStrategy(PlugEffectWhiteBlackConstants.STRATEGY_ALL, "zookeeper-provider-demo");
        Mockito.when(invokerService.invoke(null, null, "zookeeper-provider-demo"))
            .thenReturn(Optional.ofNullable(new Object()));
        interceptor.doBefore(context);
        uriNew = (URI) context.getArguments()[0];
        Assert.assertEquals(url, uriNew.toString());
    }

    @Test
    public void rebuildUriTest() {
        Optional<Method> method = ReflectUtils.findMethod(RestTemplateInterceptor.class, "rebuildUri",
            new Class[]{String.class, URI.class});
        URI uri = createURI(url);
        if (method.isPresent()) {
            Optional<Object> uriNew = ReflectUtils
                .invokeMethod(interceptor, method.get(), new Object[]{convertUrl, uri});
            Assert.assertEquals(convertUrl, uriNew.get().toString());
        }
    }

    @Test
    public void buildErrorResponseTest() throws IOException {
        Optional<Method> method = ReflectUtils.findMethod(RestTemplateInterceptor.class, "buildErrorResponse",
            new Class[]{Exception.class});
        Exception ex = new Exception();
        if (method.isPresent()) {
            Optional<Object> exception = ReflectUtils
                .invokeMethod(interceptor, method.get(), new Object[]{ex});
            Assert.assertEquals(ex, exception.get());
        }
    }
}