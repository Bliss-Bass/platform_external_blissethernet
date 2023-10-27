package org.blissos.ethernetmanager;

import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.blissos.ethernet.BlissEthernetManager;
import org.blissos.ethernet.IBlissEthernetServiceListener;

import java.util.Arrays;
import java.util.List;

public class EthernetFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private BlissEthernetManager mBlissEthernetManager;
    private String mSelectedInterface;

    private Preference mRefreshPreference;
    private ListPreference mInterfaceListPreference;
    private Preference mInterfaceUpPreference;
    private Preference mInterfaceDownPreference;
    private ListPreference mIpAssignmentListPreference;
    private EditTextPreference mIpAddressPreference;
    private EditTextPreference mGatewayAddressPreference;
    private EditTextPreference mDnsAddressesPreference;

    public static final String KEY_REFRESH = "refresh";
    public static final String KEY_INTERFACE_LIST = "interfaceList";
    public static final String KEY_INTERFACE_UP = "interfaceUp";
    public static final String KEY_INTERFACE_DOWN = "interfaceDown";
    public static final String KEY_IP_ASSIGNMENT_LIST = "ipAssignmentList";
    public static final String KEY_IP_ADDRESS = "ipAddress";
    public static final String KEY_GATEWAY_ADDRESS = "gatewayAddress";
    public static final String KEY_DNS_ADDRESSES = "dnsAddresses";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.menu, rootKey);

        mRefreshPreference = findPreference(KEY_REFRESH);
        mInterfaceListPreference = findPreference(KEY_INTERFACE_LIST);
        mInterfaceUpPreference = findPreference(KEY_INTERFACE_UP);
        mInterfaceDownPreference = findPreference(KEY_INTERFACE_DOWN);
        mIpAssignmentListPreference = findPreference(KEY_IP_ASSIGNMENT_LIST);
        mIpAddressPreference = findPreference(KEY_IP_ADDRESS);
        mGatewayAddressPreference = findPreference(KEY_GATEWAY_ADDRESS);
        mDnsAddressesPreference = findPreference(KEY_DNS_ADDRESSES);

        mRefreshPreference.setOnPreferenceClickListener(this);
        mInterfaceListPreference.setOnPreferenceChangeListener(this);
        mInterfaceUpPreference.setOnPreferenceClickListener(this);
        mInterfaceDownPreference.setOnPreferenceClickListener(this);
        mIpAssignmentListPreference.setOnPreferenceChangeListener(this);
        mIpAddressPreference.setOnPreferenceChangeListener(this);
        mGatewayAddressPreference.setOnPreferenceChangeListener(this);
        mDnsAddressesPreference.setOnPreferenceChangeListener(this);

        mBlissEthernetManager = BlissEthernetManager.getInstance(getContext());
        refresh();
    }

    private void refresh() {
        mBlissEthernetManager.setListener(new IBlissEthernetServiceListener.Stub() {
            @Override
            public void onAvailabilityChanged(String iface, boolean isAvailable) throws RemoteException {
                List<CharSequence> currentEntries = Arrays.asList(mInterfaceListPreference.getEntryValues());

                if (currentEntries.contains(iface)) {
                    if (!isAvailable) {
                        currentEntries.remove(iface);
                        if (mSelectedInterface.equals(iface)) {
                            if (currentEntries.size() > 0) {
                                selectInterface((String) currentEntries.get(0));
                            } else
                                disableAll();
                        }
                    }
                } else {
                    if (isAvailable)
                        currentEntries.add(iface);
                }
                mInterfaceListPreference.setEntries(currentEntries.toArray(new CharSequence[0]));
                mInterfaceListPreference.setEntryValues(currentEntries.toArray(new CharSequence[0]));
            }
        });

        String[] ifaces = mBlissEthernetManager.getAvailableInterfaces();
        if (ifaces.length > 0) {
            mInterfaceListPreference.setEntries(ifaces);
            mInterfaceListPreference.setEntryValues(ifaces);
            selectInterface(ifaces[0]);
        } else {
            disableAll();
        }
    }

    private void disableAll() {
        mSelectedInterface = null;

        mInterfaceUpPreference.setEnabled(false);
        mInterfaceDownPreference.setEnabled(false);
        mIpAssignmentListPreference.setEnabled(false);
        mIpAddressPreference.setEnabled(false);
        mGatewayAddressPreference.setEnabled(false);
        mDnsAddressesPreference.setEnabled(false);
    }

    private void selectInterface(String iface) {
        mSelectedInterface = iface;

        mInterfaceUpPreference.setEnabled(true);
        mInterfaceDownPreference.setEnabled(true);
        mIpAssignmentListPreference.setEnabled(true);
        mIpAddressPreference.setEnabled(true);
        mGatewayAddressPreference.setEnabled(true);
        mDnsAddressesPreference.setEnabled(true);

        mInterfaceListPreference.setSummary(mBlissEthernetManager.getEthernetMacAddress(iface));
        restartValues();
    }

    private void restartValues() {
        String[] ipAssignmentTypes = getResources().getStringArray(R.array.ipAssignmentTypes);
        int ipAssignment = mBlissEthernetManager.getIpAssignment(mSelectedInterface);
        mIpAssignmentListPreference.setSummary(ipAssignmentTypes[ipAssignment + 1]);
        mIpAssignmentListPreference.setValue(String.valueOf(mBlissEthernetManager.getIpAssignment(mSelectedInterface)));
        mIpAddressPreference.setSummary(mBlissEthernetManager.getIpAddress(mSelectedInterface));
        mIpAddressPreference.setText(mBlissEthernetManager.getIpAddress(mSelectedInterface));
        mGatewayAddressPreference.setSummary(mBlissEthernetManager.getGateway(mSelectedInterface));
        mGatewayAddressPreference.setText(mBlissEthernetManager.getGateway(mSelectedInterface));
        mDnsAddressesPreference.setSummary(String.join(",", mBlissEthernetManager.getDnses(mSelectedInterface)));
        mDnsAddressesPreference.setText(String.join(",", mBlissEthernetManager.getDnses(mSelectedInterface)));

        if (ipAssignment != BlissEthernetManager.IP_ASSIGNMENT_STATIC) {
            mIpAddressPreference.setEnabled(false);
            mGatewayAddressPreference.setEnabled(false);
            mDnsAddressesPreference.setEnabled(false);
        } else {
            mIpAddressPreference.setEnabled(true);
            mGatewayAddressPreference.setEnabled(true);
            mDnsAddressesPreference.setEnabled(true);
        }
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        if (preference.getKey().equals(KEY_INTERFACE_LIST)) {
            if (mBlissEthernetManager.isAvailable((String) newValue))
                selectInterface((String) newValue);
        } else if (preference.getKey().equals(KEY_IP_ASSIGNMENT_LIST)) {
            mBlissEthernetManager.setIpAssignment(mSelectedInterface, Integer.parseInt((String) newValue));
        } else if (preference.getKey().equals(KEY_IP_ADDRESS)) {
            mBlissEthernetManager.setIpAddress(mSelectedInterface, (String) newValue);
        } else if (preference.getKey().equals(KEY_GATEWAY_ADDRESS)) {
            mBlissEthernetManager.setGateway(mSelectedInterface, (String) newValue);
        } else if (preference.getKey().equals(KEY_DNS_ADDRESSES)) {
            String dnses = (String) newValue;
            mBlissEthernetManager.setDnses(mSelectedInterface, dnses.split(","));
        }

        restartValues();
        return true;
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        if (preference.getKey().equals(KEY_INTERFACE_UP)) {
            mBlissEthernetManager.setInterfaceUp(mSelectedInterface);
        } else if (preference.getKey().equals(KEY_INTERFACE_DOWN)) {
            mBlissEthernetManager.setInterfaceDown(mSelectedInterface);
        } else if (preference.getKey().equals(KEY_REFRESH)) {
            refresh();
        }
        return true;
    }
}
