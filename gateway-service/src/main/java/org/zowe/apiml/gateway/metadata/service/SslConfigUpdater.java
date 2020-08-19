/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.metadata.service;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SslConfigUpdater extends RefreshEventListener {

    @Autowired
    private ServletWebServerApplicationContext context;

    @Override
    public void refresh() {
        for (Connector connector : ((TomcatWebServer) this.context.getWebServer()).getTomcat().getService().findConnectors()) {

            if (connector.getProtocolHandler() instanceof Http11NioProtocol && connector.getPort() == 10010) {
                Http11NioProtocol protocol = ((Http11NioProtocol) connector.getProtocolHandler());
                protocol.reloadSslHostConfigs();
            }
        }
    }
}
