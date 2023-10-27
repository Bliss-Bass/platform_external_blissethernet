package org.blissos.ethernetservice;

interface IBlissEthernetServiceListener {
    void onAvailabilityChanged(String iface, boolean isAvailable);

}
