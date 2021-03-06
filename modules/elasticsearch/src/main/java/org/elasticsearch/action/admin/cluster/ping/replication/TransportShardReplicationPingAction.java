/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.admin.cluster.ping.replication;

import org.elasticsearch.action.support.replication.TransportShardReplicationOperationAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.action.shard.ShardStateAction;
import org.elasticsearch.cluster.routing.ShardIterator;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

/**
 * @author kimchy (shay.banon)
 */
public class TransportShardReplicationPingAction extends TransportShardReplicationOperationAction<ShardReplicationPingRequest, ShardReplicationPingRequest, ShardReplicationPingResponse> {

    @Inject public TransportShardReplicationPingAction(Settings settings, TransportService transportService,
                                                       ClusterService clusterService, IndicesService indicesService, ThreadPool threadPool,
                                                       ShardStateAction shardStateAction) {
        super(settings, transportService, clusterService, indicesService, threadPool, shardStateAction);
    }

    @Override protected String executor() {
        return ThreadPool.Names.CACHED;
    }

    @Override protected boolean checkWriteConsistency() {
        return true;
    }

    @Override protected ShardReplicationPingRequest newRequestInstance() {
        return new ShardReplicationPingRequest();
    }

    @Override protected ShardReplicationPingRequest newReplicaRequestInstance() {
        return new ShardReplicationPingRequest();
    }

    @Override protected ShardReplicationPingResponse newResponseInstance() {
        return new ShardReplicationPingResponse();
    }

    @Override protected String transportAction() {
        return "ping/replication/shard";
    }

    @Override protected PrimaryResponse<ShardReplicationPingResponse, ShardReplicationPingRequest> shardOperationOnPrimary(ClusterState clusterState, PrimaryOperationRequest shardRequest) {
        return new PrimaryResponse<ShardReplicationPingResponse, ShardReplicationPingRequest>(shardRequest.request, new ShardReplicationPingResponse(), null);
    }

    @Override protected void shardOperationOnReplica(ReplicaOperationRequest shardRequest) {
    }

    @Override protected ShardIterator shards(ClusterState clusterState, ShardReplicationPingRequest request) {
        return clusterService.state().routingTable().index(request.index()).shard(request.shardId()).shardsIt();
    }
}