package org.blissos.ethernetmanager;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.blissos.ethernetservice.IBlissEthernet;
import org.blissos.ethernetservice.IBlissEthernetServiceListener;

public class BlissEthernetManager {
    private static final String TAG = "BlissEthernetManager";
    private static final String SERVICE_NAME = "blissethernet";

    public static final int IP_ASSIGNMENT_UNASSIGNED = -1;

    public static final int IP_ASSIGNMENT_DHCP = 0;
    public static final int IP_ASSIGNMENT_STATIC = 1;
    private IBlissEthernet sService;
    private static BlissEthernetManager sInstance;
    private Context mContext;

    BlissEthernetManager(Context context) {
        Context appContext = context.getApplicationContext();
        mContext = appContext == null ? context : appContext;
        getService();

        if (sService == null) {
            //throw new RuntimeException("Unable to get Bliss Ethernet Service. The service" +
            //        " either crashed, was not started, or the interface has been called too early" +
            //        " in SystemServer init");
        }
    }

    public static synchronized BlissEthernetManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BlissEthernetManager(context);
        }

        return sInstance;
    }

    /** @hide **/
    public IBlissEthernet getService() {
        if (sService != null) {
            return sService;
        }
        //IBinder b = ServiceManager.getService(DariaContextConstants.DARIA_WALLET);
        //sService = IBlissEthernet.Stub.asInterface(b);

        Intent intent = new Intent("BlissEthernetService");
        intent.setPackage("org.blissos.ethernetservice");

        mContext.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder b) {
                sService = IBlissEthernet.Stub.asInterface(b);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);

        if (sService == null) {
            Log.e(TAG, "null BlissEthernet service, SAD!");
            return null;
        }

        return sService;
    }

    /**
     * @return true if service is valid
     */
    private boolean checkService() {
        if (sService == null) {
            Log.w(TAG, "not connected to BlissEthernetService");
            return false;
        }
        return true;
    }

    public String[] getAvailableInterfaces() {
        try {
            if (checkService())
                return sService.getAvailableInterfaces();
        } catch (RemoteException e) {
            Log.e(TAG, "getAvailableInterfaces failed", e);
        }

        return new String[0];
    }

    public boolean isAvailable(String iface) {
        try {
            if (checkService())
                return sService.isAvailable(iface);
        } catch (RemoteException e) {
            Log.e(TAG, "isAvailable failed", e);
        }

        return false;
    }

    public void setListener(IBlissEthernetServiceListener listener) {
        try {
            if (checkService())
                sService.setListener(listener);
        } catch (RemoteException e) {
            Log.e(TAG, "setListener failed", e);
        }
    }

    public void setInterfaceUp(String iface) {
        try {
            if (checkService())
                sService.setInterfaceUp(iface);
        } catch (RemoteException e) {
            Log.e(TAG, "setInterfaceUp failed", e);
        }
    }

    public void setInterfaceDown(String iface) {
        try {
            if (checkService())
                sService.setInterfaceDown(iface);
        } catch (RemoteException e) {
            Log.e(TAG, "setInterfaceDown failed", e);
        }
    }

    public String getEthernetMacAddress(String ifname) {
        try {
            if (checkService())
                return sService.getEthernetMacAddress(ifname);
        } catch (RemoteException e) {
            Log.e(TAG, "getEthernetMacAddress failed", e);
        }

        return null;
    }

    public int getIpAssignment(String iface) {
        try {
            if (checkService())
                return sService.getIpAssignment(iface);
        } catch (RemoteException e) {
            Log.e(TAG, "getIpAssignment failed", e);
        }

        return -1;
    }

    public void setIpAssignment(String iface, int assignment) {
        try {
            if (checkService())
                sService.setIpAssignment(iface, assignment);
        } catch (RemoteException e) {
            Log.e(TAG, "setIpAssignment failed", e);
        }
    }

    public String getIpAddress(String iface) {
        try {
            if (checkService())
                return sService.getIpAddress(iface);
        } catch (RemoteException e) {
            Log.e(TAG, "getIpAddress failed", e);
        }

        return null;
    }

    public void setIpAddress(String iface, String ipAddress) {
        try {
            if (checkService())
                sService.setIpAddress(iface, ipAddress);
        } catch (RemoteException e) {
            Log.e(TAG, "setIpAddress failed", e);
        }
    }

    public String getGateway(String iface) {
        try {
            if (checkService())
                return sService.getGateway(iface);
        } catch (RemoteException e) {
            Log.e(TAG, "getGateway failed", e);
        }

        return null;
    }

    public void setGateway(String iface, String gateway) {
        try {
            if (checkService())
                sService.setGateway(iface, gateway);
        } catch (RemoteException e) {
            Log.e(TAG, "setGateway failed", e);
        }
    }

    public String[] getDnses(String iface) {
        try {
            if (checkService())
                return sService.getDnses(iface);
        } catch (RemoteException e) {
            Log.e(TAG, "getDnses failed", e);
        }

        return new String[0];
    }

    public void setDnses(String iface, String[] dnses) {
        try {
            if (checkService())
                sService.setDnses(iface, dnses);
        } catch (RemoteException e) {
            Log.e(TAG, "setDnses failed", e);
        }
    }
}
