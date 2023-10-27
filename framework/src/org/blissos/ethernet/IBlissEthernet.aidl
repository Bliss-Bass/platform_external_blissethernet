package org.blissos.ethernet;

import org.blissos.ethernet.IBlissEthernetServiceListener;

interface IBlissEthernet {
    String[] getAvailableInterfaces();
    boolean isAvailable(String iface);
    void setListener(in IBlissEthernetServiceListener listener);

    void setInterfaceUp(String iface);
    void setInterfaceDown(String iface);

    String getEthernetMacAddress(String ifname);
    int getIpAssignment(String iface);
    void setIpAssignment(String iface, int assignment);
    String getIpAddress(String iface);
    void setIpAddress(String iface, String ipAddress);
    String getGateway(String iface);
    void setGateway(String iface, String gateway);
    String[] getDnses(String iface);
    void setDnses(String iface, in String[] dnses);

}
