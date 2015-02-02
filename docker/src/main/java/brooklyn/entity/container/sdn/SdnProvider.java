/*
 * Copyright 2014 by Cloudsoft Corporation Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brooklyn.entity.container.sdn;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.jclouds.net.domain.IpPermission;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.Group;
import brooklyn.entity.basic.BasicStartable;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.container.DockerAttributes;
import brooklyn.entity.group.DynamicCluster;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.AttributeSensorAndConfigKey;
import brooklyn.event.basic.Sensors;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.net.Cidr;

import com.google.common.reflect.TypeToken;

/**
 * An SDN provider implementation.
 */
public interface SdnProvider extends BasicStartable {

    ConfigKey<Cidr> CIDR = ConfigKeys.newConfigKey(Cidr.class, "sdn.agent.cidr", "CIDR for address allocation");

    ConfigKey<Collection<String>> EXTRA_NETWORKS = ConfigKeys.newConfigKey(
            new TypeToken<Collection<String>>() { }, "sdn.extra.networks", "Collection of extra networks to create for an entity", Collections.<String>emptyList());

    ConfigKey<Cidr> CONTAINER_NETWORK_CIDR = ConfigKeys.newConfigKey(Cidr.class, "sdn.network.cidr", "CIDR for network allocation to containers");
    ConfigKey<Integer> CONTAINER_NETWORK_SIZE = ConfigKeys.newIntegerConfigKey("sdn.network.size", "Size of network CIDR allocation for containers");

    AttributeSensor<Integer> ALLOCATED_NETWORKS = Sensors.newIntegerSensor("sdn.network.allocated", "Number of allocated networks");
    AttributeSensor<Map<String, Cidr>> NETWORKS = Sensors.newSensor(
            new TypeToken<Map<String, Cidr>>() { }, "sdn.networks", "Map of network subnets that have been created");
    AttributeSensor<Map<String, Integer>> NETWORK_ALLOCATIONS = Sensors.newSensor(
            new TypeToken<Map<String, Integer>>() { }, "sdn.networks.allocated", "Map of allocated address count on network subnets");

    AttributeSensor<Map<String, InetAddress>> CONTAINER_ADDRESSES = Sensors.newSensor(
            new TypeToken<Map<String, InetAddress>>() { }, "sdn.container.addresses", "Map of container ID to IP addresses on network");

    AttributeSensor<Group> SDN_AGENTS = Sensors.newSensor(Group.class, "sdn.agents", "Group of SDN agent services");

    AttributeSensor<Integer> ALLOCATED_IPS = Sensors.newIntegerSensor("sdn.agent.ips", "Number of allocated IPs for agents");
    AttributeSensor<Map<String, InetAddress>> ALLOCATED_ADDRESSES = Sensors.newSensor(
            new TypeToken<Map<String, InetAddress>>() { }, "sdn.agent.addresses", "Allocated IP addresses for agents");

    @SetFromFlag("agentSpec")
    AttributeSensorAndConfigKey<EntitySpec<?>,EntitySpec<?>> SDN_AGENT_SPEC = ConfigKeys.newSensorAndConfigKey(
            new TypeToken<EntitySpec<?>>() { }, "sdn.agent.spec", "SDN agent specification");

    @SetFromFlag("dockerInfrastructure")
    AttributeSensorAndConfigKey<Entity, Entity> DOCKER_INFRASTRUCTURE = DockerAttributes.DOCKER_INFRASTRUCTURE;

    Collection<IpPermission> getIpPermissions();

    DynamicCluster getDockerHostCluster();

    Group getAgents();

    InetAddress getNextContainerAddress(String networkId);

    InetAddress getNextAddress();

    Cidr getSubnet(String subnetId, String subnetName);

    void addHost(Entity host);

    void removeHost(Entity host);
}