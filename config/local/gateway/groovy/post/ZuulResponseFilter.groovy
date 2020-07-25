/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package org.zowe.apiml.gateway_extensions.filters.groovy;

import com.netflix.config.DynamicBooleanProperty
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import com.netflix.util.Pair

//2.x import com.netflix.zuul.context.SessionContext
//2.x import com.netflix.zuul.filters.http.HttpOutboundSyncFilter
//2.x import com.netflix.zuul.message.Headers
//2.x import com.netflix.zuul.message.http.HttpResponseMessage
//2.x import com.netflix.zuul.niws.RequestAttempts
//2.x import com.netflix.zuul.passport.CurrentPassport
//2.x import com.netflix.zuul.stats.status.StatusCategory
//2.x import com.netflix.zuul.stats.status.StatusCategoryUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletResponse

class ZuulResponseFilter extends ZuulFilter {
    private static final Logger logger = LoggerFactory.getLogger(ZuulResponseFilter.class)

    private static final DynamicBooleanProperty SEND_RESPONSE_HEADERS =
            new DynamicBooleanProperty("zuul.responseFilter.send.headers", true)

    @Override
    String filterType() {
        return "post"
    }

    @Override
    int filterOrder() {
        return 998
    }

    @Override
    boolean shouldFilter() {
        return true
    }

    @Override
    Object run() {
        RequestContext context = RequestContext.getCurrentContext()
        if (context.getResponseBody() == null && context.getResponseDataStream() == null) {
            return
        };

        HttpServletResponse servletResponse = context.getResponse()
        OutputStream outStream = servletResponse.getOutputStream()

        try {
            if (context.responseBody != null) {
                String body = RequestContext.getCurrentContext().responseBody
                writeResponse(new ByteArrayInputStream(body.getBytes(Charset.forName("UTF-8"))), outStream)
                return
            }

        } finally {
            try {
                //is?.close()
                outStream.flush()
                outStream.close()
            } catch (IOException e) {
                logger.error("", e)
            }
        }

        List<String> rd = (List<String>) RequestContext.getCurrentContext().get("requestDebug")
        rd?.each {
            logger.debug("REQUEST_DEBUG::${it}")
        }

        rd = (List<String>) RequestContext.getCurrentContext().get("routingDebug")
        rd?.each {
            logger.debug("ZUUL_DEBUG::${it}")
        }

        /*if (context.getResponse().getStatus() >= 400 && context.getError() != null) {
            Throwable error = context.getError()
            headers.set(X_ZUUL_ERROR_CAUSE,
                    error instanceof ZuulException ? ((ZuulException) error).getErrorCause() : "UNKNOWN_CAUSE")
        }*/

        if (servletResponse.status >= 400) {
            logger.debug("HTTP response error ") //, context.getResponse().getStatus(), context.requestURI)
        }

        /*if (logger.isDebugEnabled()) {
            logger.debug("Filter execution summary :: {}", context.getFilterExecutionSummary())
        }*/
    }

    private void addResponseHeaders() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse servletResponse = context.getResponse();
        List<Pair<String, String>> zuulResponseHeaders = context.getZuulResponseHeaders();
        String debugHeader = ""

        List<String> rd

        rd = (List<String>) RequestContext.getCurrentContext().get("routingDebug");
        rd?.each {
            debugHeader += "[[[${it}]]]";
        }

        if (INCLUDE_DEBUG_HEADER.get()) servletResponse.addHeader("X-Zuul-Debug-Header", debugHeader)

        if (Debug.debugRequest()) {
            zuulResponseHeaders?.each { Pair<String, String> it ->
                servletResponse.addHeader(it.first(), it.second())
                Debug.addRequestDebug("OUTBOUND: <  " + it.first() + ":" + it.second())
            }
        } else {
            zuulResponseHeaders?.each { Pair<String, String> it ->
                servletResponse.addHeader(it.first(), it.second())
            }
        }

        RequestContext ctx = RequestContext.getCurrentContext()
        Integer contentLength = ctx.getOriginContentLength()

        // only inserts Content-Length if origin provides it and origin response is not gzipped
        if (SET_CONTENT_LENGTH.get()) {
            if (contentLength != null && !ctx.getResponseGZipped())
                servletResponse.setContentLength(contentLength)
        }
    }

    def writeResponse(InputStream zin, OutputStream out) {
        byte[] bytes = new byte[INITIAL_STREAM_BUFFER_SIZE.get()];
        int bytesRead = -1;
        while ((bytesRead = zin.read(bytes)) != -1) {

            try {
                out.write(bytes, 0, bytesRead);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace()
            }

            // doubles buffer size if previous read filled it
            if (bytesRead == bytes.length) {
                bytes = new byte[bytes.length * 2]
            }
        }
    }
}

