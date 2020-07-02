/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.client.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zowe.apiml.client.model.UploadFileResponse;

@RestController
public class MultipartController {
    @RequestMapping(
        value = "api/v1/multipart",
        method = RequestMethod.POST,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResponse uploadFileWithPost(@RequestParam("file") MultipartFile file) {
        return new UploadFileResponse(file.getOriginalFilename(), file.getContentType(), file.getSize());
    }

    @RequestMapping(
        value = "api/v1/multipart",
        method = RequestMethod.PUT,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResponse uploadFileWithPut(@RequestParam("file") MultipartFile file) {
        return new UploadFileResponse(file.getOriginalFilename(), file.getContentType(), file.getSize());
    }
}
