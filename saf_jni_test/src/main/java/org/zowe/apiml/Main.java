/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml;

public class Main {
    static {
        System.loadLibrary("native");
    }

    public static void main(String[] args) {
        System.out.println(new Main().getUserId("certificate"));
        new Main().sayHello();
    }

    private native void sayHello();

    private native String getUserId(String certificate);
}
