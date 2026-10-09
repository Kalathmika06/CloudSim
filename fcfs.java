
package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;
import java.util.*;

public class fcfs {

    public static void main(String[] args) {

        CloudSim.init(1, Calendar.getInstance(), false);

        createDatacenter("Datacenter_0");
        DatacenterBroker broker = createBroker();
        int brokerId = broker.getId();

        // Create 4 VMs
        List<Vm> vmList = new ArrayList<>();
        int vmCount = 4;

        for (int i = 0; i < vmCount; i++) {
            vmList.add(new Vm(i, brokerId, 1000, 1, 1024,
                    1000, 10000, "Xen",
                    new CloudletSchedulerTimeShared()));
        }
        broker.submitVmList(vmList);

        // Create 8 Cloudlets and assign in order
        List<Cloudlet> cloudletList = new ArrayList<>();
        int cloudletCount = 8;

        for (int i = 0; i < cloudletCount; i++) {
            Cloudlet cl = new Cloudlet(i, 40000, 1, 300, 300,
                    new UtilizationModelFull(),
                    new UtilizationModelFull(),
                    new UtilizationModelFull());

            cl.setUserId(brokerId);

            // FCFS-style sequential VM assignment
            int vmId = i / (cloudletCount / vmCount);
            cl.setVmId(vmId);

            System.out.println("Cloudlet " + i +
                    " assigned to VM " + vmId);

            cloudletList.add(cl);
        }

        broker.submitCloudletList(cloudletList);

        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        // Display results
        for (Cloudlet cl : broker.getCloudletReceivedList()) {
            System.out.println("Cloudlet " + cl.getCloudletId()
                    + " Status: " + cl.getStatus()
                    + " VM: " + cl.getVmId());
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hosts = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            List<Pe> pes = new ArrayList<>();
            pes.add(new Pe(0, new PeProvisionerSimple(1000)));

            hosts.add(new Host(i,
                    new RamProvisionerSimple(2048),
                    new BwProvisionerSimple(10000),
                    1000000, pes,
                    new VmSchedulerTimeShared(pes)));
        }

        DatacenterCharacteristics ch =
                new DatacenterCharacteristics("x86", "Linux", "Xen",
                        hosts, 10.0, 3.0, 0.05, 0.001, 0.0);

        try {
            return new Datacenter(name, ch,
                    new VmAllocationPolicySimple(hosts),
                    new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DatacenterBroker createBroker() {
        try {
            return new DatacenterBroker("Broker");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}