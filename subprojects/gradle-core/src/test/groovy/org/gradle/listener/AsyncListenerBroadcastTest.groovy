/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.listener

import org.gradle.util.JUnit4GroovyMockery
import org.gradle.util.MultithreadedTestCase
import org.jmock.Sequence
import org.jmock.integration.junit4.JMock
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMock.class)
public class AsyncListenerBroadcastTest extends MultithreadedTestCase {
    private final JUnit4GroovyMockery context = new JUnit4GroovyMockery()
    private final TestListener listener1 = context.mock(TestListener.class, "listener1")
    private final TestListener listener2 = context.mock(TestListener.class, "listener2")
    private final AsyncListenerBroadcast broadcast = new AsyncListenerBroadcast<TestListener>(TestListener.class, executor)

    @Test
    public void deliversEventsToListenerInOrderEventsGenerated() {
        broadcast.add(listener1)

        context.checking {
            Sequence sequence = context.sequence("seq")
            20.times {
                one(listener1).event("$it")
                inSequence(sequence)
            }
        }

        20.times {
            broadcast.source.event("$it")
        }

        broadcast.close()
    }

    @Test
    public void deliversEventsToListenersInOrderListenersAdded() {
        broadcast.add(listener1)
        broadcast.add(listener2)

        context.checking {
            Sequence sequence = context.sequence("seq")
            20.times {
                one(listener1).event("$it")
                inSequence(sequence)
                one(listener2).event("$it")
                inSequence(sequence)
            }
        }

        20.times {
            broadcast.source.event("$it")
        }

        broadcast.close()
    }

    @Test
    public void blockingListenerDoesNotBlockEventGeneration() {
        broadcast.add(listener1)

        context.checking {
            one(listener1).event("1")
            will {
                moveTo(1)
                blockUntil(2)
            }
            one(listener1).event("2")
            one(listener1).event("3")
        }
        
        thread {
            broadcast.source.event("1")
            blockUntil(1)
            broadcast.source.event("2")
            broadcast.source.event("3")
            moveTo(2)
        }.waitFor()

        broadcast.close()
    }

    @Test
    public void closeBlocksUntilEventsDelivered() {
        broadcast.add(listener1)

        context.checking {
            one(listener1).event("1")
            will {
                blockUntil(1)
                moveTo(2)
            }
        }

        broadcast.source.event("1")

        thread {
            moveTo(1)
            broadcast.close()
            shouldBeAt(2)
        }

        waitForAll()
    }
}

public interface TestListener {
    void event(String param)
}
