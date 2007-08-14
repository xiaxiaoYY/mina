/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.mina.filter.codec.support;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.common.BaseIoSession;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoFilter.NextFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * A {@link ProtocolDecoderOutput} based on queue.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev$, $Date$
 *
 */
public class SimpleProtocolDecoderOutput implements ProtocolDecoderOutput {
    private final NextFilter nextFilter;

    private final IoSession session;

    private final Queue<Object> messageQueue = new LinkedList<Object>();

    public SimpleProtocolDecoderOutput(IoSession session, NextFilter nextFilter) {
        this.nextFilter = nextFilter;
        this.session = session;
    }

    public void write(Object message) {
        if (message == null) {
            throw new NullPointerException("message");
        }

        messageQueue.offer(message);
        if (session instanceof BaseIoSession) {
            ((BaseIoSession) session).increaseReadMessages();
        }
    }

    public void flush() {
        while (!messageQueue.isEmpty()) {
            nextFilter.messageReceived(session, messageQueue.poll());
        }
    }
}
