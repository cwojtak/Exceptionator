/*
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package jdk.jfr.internal.instrument;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import jdk.jfr.events.SocketReadEvent;

/**
 * See {@link JITracer} for an explanation of this code.
 */
@JIInstrumentationTarget("java.net.Socket$SocketInputStream")
final class SocketInputStreamInstrumentor {

    private SocketInputStreamInstrumentor() {
    }

    @SuppressWarnings("deprecation")
    @JIInstrumentationMethod
    public int read(byte b[], int off, int length) throws IOException {
        SocketReadEvent event = SocketReadEvent.EVENT.get();
        if (!event.isEnabled()) {
            return read(b, off, length);
        }
        int bytesRead = 0;
        try {
            event.begin();
            bytesRead = read(b, off, length);
        } finally {
            event.end();
            if (event.shouldCommit()) {
                InetAddress remote = parent.getInetAddress();
                event.host = remote.getHostName();
                event.address = remote.getHostAddress();
                event.port = parent.getPort();
                if (bytesRead < 0) {
                    event.endOfStream = true;
                } else {
                    event.bytesRead = bytesRead;
                }
                event.timeout = parent.getSoTimeout();

                event.commit();
                event.reset();
            }
        }
        return bytesRead;
    }

    // private field in java.net.Socket$SocketInputStream
    private Socket parent;
}
