/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper;

/**
 * This interface specifies the public interface an event handler class must
 * implement. A ZooKeeper client will get various events from the ZooKeeper
 * server it connects to. An application using such a client handles these
 * events by registering a callback object with the client. The callback object
 * is expected to be an instance of a class that implements Watcher interface.
 * 
 */
public interface Watcher {

    /**
     * This interface defines the possible states an Event may represent
     */
    public interface Event {
    	//通知状态
        /**
         * Enumeration of states the ZooKeeper may be at the event
         */
        public enum KeeperState {
            /** Unused, this state is never generated by the server */
            @Deprecated
            Unknown (-1),

            /** The client is in the disconnected state - it is not connected
             * to any server in the ensemble. */
            Disconnected (0),

            /** Unused, this state is never generated by the server */
            @Deprecated
            NoSyncConnected (1),

            /** The client is in the connected state - it is connected
             * to a server in the ensemble (one of the servers specified
             * in the host connection parameter during ZooKeeper client
             * creation). */
            SyncConnected (3),

            /**
             * Auth failed state（并不是简单指当前客户端会话没有权限，而是指授权失败）
             */
            AuthFailed (4),

            /**
             * The client is connected to a read-only server, that is the
             * server which is not currently connected to the majority.
             * The only operations allowed after receiving this state is
             * read operations.
             * This state is generated for read-only clients only since
             * read/write clients aren't allowed to connect to r/o servers.
             */
            ConnectedReadOnly (5),

            /**
              * SaslAuthenticated: used to notify clients that they are SASL-authenticated,
              * so that they can perform Zookeeper actions with their SASL-authorized permissions.
              */
            SaslAuthenticated(6),

            /** The serving cluster has expired this session. The ZooKeeper
             * client connection (the session) is no longer valid. You must
             * create a new client connection (instantiate a new ZooKeeper
             * instance) if you with to access the ensemble. */
            Expired (-112);

            private final int intValue;     // Integer representation of value
                                            // for sending over wire

            KeeperState(int intValue) {
                this.intValue = intValue;
            }

            public int getIntValue() {
                return intValue;
            }

            public static KeeperState fromInt(int intValue) {
                switch(intValue) {
                    case   -1: return KeeperState.Unknown;
                    case    0: return KeeperState.Disconnected;
                    case    1: return KeeperState.NoSyncConnected;
                    case    3: return KeeperState.SyncConnected;
                    case    4: return KeeperState.AuthFailed;
                    case    5: return KeeperState.ConnectedReadOnly;
                    case    6: return KeeperState.SaslAuthenticated;
                    case -112: return KeeperState.Expired;

                    default:
                        throw new RuntimeException("Invalid integer value for conversion to KeeperState");
                }
            }
        }
        
        //事件类型
        /**
         * Enumeration of types of events that may occur on the ZooKeeper
         */
        public enum EventType {
            None (-1),
            NodeCreated (1),
            NodeDeleted (2),
            /**
             * 此处的变更包括节点的数据内容和数据的版本号dataVersion.
                                      即使用相同数据内容来更新，还是会触发这个事件通知；因为一旦客户端调用数据更新的接口，
                                      且更新成功，就会更新dataVersion值
             */
            NodeDataChanged (3),
            /**
             * 子节点列表变化特指子节点个数或者组成情况的变化（新增，或删除子节点）；
             * 而子节点内容的变化是不会触发这个事件的
             */
            NodeChildrenChanged (4);

            private final int intValue;     // Integer representation of value
                                            // for sending over wire

            EventType(int intValue) {
                this.intValue = intValue;
            }

            public int getIntValue() {
                return intValue;
            }

            public static EventType fromInt(int intValue) {
                switch(intValue) {
                    case -1: return EventType.None;
                    case  1: return EventType.NodeCreated;
                    case  2: return EventType.NodeDeleted;
                    case  3: return EventType.NodeDataChanged;
                    case  4: return EventType.NodeChildrenChanged;

                    default:
                        throw new RuntimeException("Invalid integer value for conversion to EventType");
                }
            }           
        }
    }
    //III:ZK服务器会给客户端发送一个Watch事件通知，客户端就会对相应的process()方法进行回调，从而实现对事件的处理
    abstract public void process(WatchedEvent event);
}
