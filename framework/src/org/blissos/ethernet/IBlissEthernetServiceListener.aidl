package org.blissos.ethernet;

interface IBlissEthernetServiceListener {
    void onAvailabilityChanged(String iface, boolean isAvailable);

}
