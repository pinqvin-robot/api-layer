/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.filters.manager.provider;

import com.netflix.zuul.ZuulFilter;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
public class ZuulFilterProvider implements FilterProvider {


    private ApimlGatewayFilter apimlGatewayFilter;

    @Override
    public void set(ApimlGatewayFilter filter) {
        apimlGatewayFilter = filter;
    }

    @Override
    public ZuulFilter get() {
        return apimlGatewayFilter.getFilter();
    }

/*
    @EventListener
    public void onRefreshScopeRefreshed(final RefreshScopeRefreshedEvent event) {
        logger.info("Received Refresh event. Refreshing all beans...");
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Class<?> beanClass = applicationContext.getBean(beanName).getClass();
            if(beanClass.getName().contains("SpringCGLIB")) {
                logger.info("Proxied bean: bean name: " + beanName + " - Bean class: " + applicationContext.getBean(beanName).getClass());
            } else {
                logger.info("Regular Bean: Bean name: " + beanName + " - Bean class: " + applicationContext.getBean(beanName).getClass());
            }
            applicationContext.getBean(beanName).getClass(); // to cause refresh eagerly
        }
    }

    @RefreshScope
    @Bean
    MyBean aBean() {
        return new MyBean();
    }
*/
}
