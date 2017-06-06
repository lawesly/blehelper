package com.ppcrong.blehelper.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ppcrong.blehelper.R;
import com.ppcrong.blehelper.R2;
import com.ppcrong.blehelper.adapter.AvailableListAdapter;
import com.ppcrong.blehelper.adapter.BondedListAdapter;
import com.ppcrong.blehelper.ble.BleGattCb;
import com.ppcrong.blehelper.ble.BleScanner;
import com.ppcrong.blehelper.utils.Constant;
import com.ppcrong.blehelper.utils.SPUtils;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class BleScannerActivity extends AppCompatActivity {

    // region [Content]
    private Context mContext;
    private Resources mRes;
    // endregion [Content]

    // region [Variable]
    private BleScanner mScanner;
    private BluetoothGatt mGatt;
    private SPUtils mScanSp;
    private String mConnectedName = "";
    private String mConnectedAddress = "";
    private boolean mManualDisconnect = false;
    // endregion [Variable]

    // region [Adapter]
    private AvailableListAdapter mAvailableListAdapter;
    private BondedListAdapter mBondedListAdapter;
    // endregion [Adapter]

    // region [List]
    private CopyOnWriteArrayList<HashMap<String, Object>> mAvailableList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<HashMap<String, Object>> mBondedList = new CopyOnWriteArrayList<>();
    // endregion [List]

    // region [Widget]
    @BindView(R2.id.img_btn_scan)
    AppCompatImageButton mImgBtnScan;
    @BindView(R2.id.tv_scan_state)
    TextView mTvScanState;
    @BindView(R2.id.pb_scanning)
    ProgressBar mPbScanning;
    @BindView(R2.id.cb_auto_connect)
    CheckBox mCbAutoConnect;
    @BindView(R2.id.rv_bonded_list)
    RecyclerView mRvBondedList;
    @BindView(R2.id.tv_bonded_empty)
    TextView mTvBondedEmpty;
    @BindView(R2.id.rv_available_list)
    RecyclerView mRvAvailableList;
    @BindView(R2.id.tv_available_empty)
    TextView mTvAvailableEmpty;
    // endregion [Widget]

    // region [OnClick]
    @OnClick(R2.id.img_btn_scan)
    public void onClickImgBtnScan() {
        boolean bScanning = mTvScanState.getText().toString().equalsIgnoreCase(mRes.getString(R.string.scanning));
        KLog.d("bScanning = " + bScanning);
        if (!bScanning) {
            // Set scan timeout
            mStopScanHandler.postDelayed(mStopScanRunnable, Constant.SCAN_PERIOD);

            // Clear available list
            mAvailableList.clear();
            mAvailableListAdapter.notifyDataSetChanged();
        }
        scanDevice(!bScanning, true);
    }
    // endregion [OnClick]

    // region [Life Cycle]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        mRes = getResources();
        mScanSp = new SPUtils(mContext, Constant.SP_NAME_SCAN_SETTINGS);

        initView();
        initScanner();
    }

    @Override
    protected void onDestroy() {
        KLog.d();
        scanDevice(false, true);

        // Disconnect and Close GATT client
        if (mGatt != null) {

            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
        }

        // Unregister receiver
        unregisterReceiver(mBleConnectReceiver);

        // Save auto connect
        mScanSp.put(Constant.DEVICE_AUTO_CONNECT, mCbAutoConnect.isChecked());

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    // endregion [Life Cycle]

    // region [Private Function]
    private void initView() {
        setContentView(R.layout.activity_ble_scanner);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mRes.getString(R.string.device));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set bonded list
        mBondedListAdapter = new BondedListAdapter(mContext, mBondedList);
        mBondedListAdapter.registerAdapterDataObserver(mBondedObserver);
        mRvBondedList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvBondedList.setAdapter(mBondedListAdapter);

        // Set available list
        mAvailableListAdapter = new AvailableListAdapter(mContext, mAvailableList);
        mAvailableListAdapter.registerAdapterDataObserver(mAvailableObserver);
        mRvAvailableList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvAvailableList.setAdapter(mAvailableListAdapter);

        mCbAutoConnect.setChecked(mScanSp.getBoolean(Constant.DEVICE_AUTO_CONNECT, false));
    }

    private void initScanner() {
        // Receiver to update BLE device connect status
        registerReceiver(mBleConnectReceiver, getIntentFilter());

        // Get BleScanner object
        mScanner = new BleScanner.Builder(this)
                .setScanCb(mScanCallback)
                .setScanSettings(new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setReportDelay(Constant.SCAN_REPORT_DELAY)
                        .setUseHardwareBatchingIfSupported(false).build())
                .build();

        // Start scanning BLE devices
        scanDevice(true, false);

        // Set scan timeout
        mStopScanHandler.postDelayed(mStopScanRunnable, Constant.SCAN_PERIOD);
    }

    private void scanDevice(boolean scan, boolean manualStop) {
        KLog.d("scan = " + scan + ", manualStop = " + manualStop);
        if (scan) {
            // Start scanning and postDelay to rescan
            if (mScanner != null) mScanner.startScan();
            mReScanHandler.postDelayed(mReScanRunnable, Constant.RESCAN_PERIOD);

            // Show scanning text
            mTvScanState.setText(mRes.getString(R.string.scanning));
            // Show progress and switch button to stop
            mPbScanning.setVisibility(View.VISIBLE);
            setScanButtonImage(false);
        } else {
            // Remove callbacks
            mReScanHandler.removeCallbacks(mReScanRunnable);
            if (manualStop) mStopScanHandler.removeCallbacks(mStopScanRunnable);

            // Stop scanning
            if (mScanner != null) mScanner.stopScan();

            // Show proper text
            int id = manualStop ? R.string.stop_scanning : R.string.scan_completed;
            mTvScanState.setText(mRes.getString(id));
            // Hide progress and switch button to scan
            mPbScanning.setVisibility(View.INVISIBLE);
            setScanButtonImage(true);
        }
    }

    private void setScanButtonImage(boolean scan) {
        int id = scan ? R.drawable.ic_refresh : R.drawable.ic_stop;
        mImgBtnScan.setImageResource(id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply different color for scan and stop
            if (scan) {
                mImgBtnScan.setBackgroundTintList(new ColorStateList(
                        new int[][]{new int[]{android.R.attr.state_pressed},
                                new int[]{}
                        },
                        new int[]{ContextCompat.getColor(mContext, R.color.colorAccent), ContextCompat.getColor(mContext, R.color.colorPrimary)}
                ));
            } else {
                mImgBtnScan.setBackgroundTintList(new ColorStateList(
                        new int[][]{new int[]{android.R.attr.state_pressed},
                                new int[]{}
                        },
                        new int[]{ContextCompat.getColor(mContext, R.color.colorPrimary), Color.RED}
                ));
            }
        }
    }

    private void connectGatt(BluetoothDevice device) {

        mScanSp.put(Constant.DEVICE_PAIR_ADDRESS, device.getAddress()); // Save pair device address
        boolean autoConnect = mScanSp.getBoolean(Constant.DEVICE_AUTO_CONNECT, false);
        KLog.i("Connecting to " + device.getName() + " Address: " + device.getAddress() + " AutoConnect: " + autoConnect);

        mGatt = device.connectGatt(mContext, autoConnect, mBleGattCb);
    }

    private IntentFilter getIntentFilter() {

        KLog.d();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_REQUEST_CONNECT);
        filter.addAction(Constant.ACTION_REQUEST_DISCONNECT);

        return filter;
    }
    // endregion [Private Function]

    // region [Task]
    private Handler mStopScanHandler = new android.os.Handler();
    private final Runnable mStopScanRunnable = new Runnable() {

        @Override
        public void run() {

            KLog.d("////// Scan timeout //////");
            // Stop scan
            scanDevice(false, false);
        }
    };

    private Handler mReScanHandler = new android.os.Handler();
    private final Runnable mReScanRunnable = new Runnable() {

        @Override
        public void run() {

            // Stop scan
            scanDevice(false, false);

            // Restart scan
            scanDevice(true, false);
        }
    };
    // endregion [Task]

    // region [BroadcastReceiver]
    private BroadcastReceiver mBleConnectReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {

            final String action = intent.getAction();
            KLog.i(action);

            if (action.equalsIgnoreCase(Constant.ACTION_REQUEST_CONNECT)) {

                if (!mTvScanState.getText().toString().equalsIgnoreCase(mRes.getString(R.string.scan_completed))) {
                    scanDevice(false, true);
                } else {
                    scanDevice(false, false);
                }

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                boolean isExist = false;

                if (mConnectedAddress.equals(device.getAddress())) {
                    isExist = true;
                }

                boolean hasBondedDevice = mBondedList.size() != 0;
                KLog.i("isExist: " + isExist + ", hasBondedDevice: " + hasBondedDevice);

                // Only connect device when no bonded device
                if (!isExist && !hasBondedDevice) {

                    if (mConnectedAddress.equals("")) {

                        mConnectedName = (device.getName() == null) ? mRes.getString(R.string.unknown) : device.getName();
                        mConnectedAddress = device.getAddress();
                        connectGatt(device);
                    }
                }
            } else if (action.equals(Constant.ACTION_REQUEST_DISCONNECT)) {

                final String address = intent.getStringExtra(Constant.DEVICE_ADDRESS);

                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title(R.string.disconnect)
                        .titleColor(ContextCompat.getColor(mContext, R.color.dimgray))
                        .content(R.string.ask_for_disconnect)
                        .positiveText(R.string.confirm)
                        .negativeText(R.string.back)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                if (!address.isEmpty()) {

                                    if (mConnectedAddress.equals(address)) {
                                        mManualDisconnect = true;

                                        // Disconnect GATT client
                                        if (mGatt != null) mGatt.disconnect();
                                    }
                                }
                            }
                        })
                        .build();

                dialog.show();
            }
        }
    };
    // endregion [BroadcastReceiver]

    // region [Listener]
    public RecyclerView.AdapterDataObserver mBondedObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {

            if (mBondedListAdapter.getItemCount() > 0) {

                mRvBondedList.setVisibility(View.VISIBLE);
                mTvBondedEmpty.setVisibility(View.GONE);

            } else {

                mRvBondedList.setVisibility(View.GONE);
                mTvBondedEmpty.setVisibility(View.VISIBLE);
            }
        }
    };

    public RecyclerView.AdapterDataObserver mAvailableObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {

            if (mAvailableListAdapter.getItemCount() > 0) {

                mRvAvailableList.setVisibility(View.VISIBLE);
                mTvAvailableEmpty.setVisibility(View.GONE);

            } else {

                mRvAvailableList.setVisibility(View.GONE);
                mTvAvailableEmpty.setVisibility(View.VISIBLE);
            }
        }
    };
    // endregion [Listener]

    // region [Callback]
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (final ScanResult result : results) {

                BluetoothDevice device = result.getDevice();

                if (device != null) {
                    if (device.getName() == null)
                        KLog.d("local name = " + result.getScanRecord().getDeviceName());
                    else
                        KLog.d("name = " + device.getName());

                    HashMap<String, Object> deviceDataMap = new HashMap();
                    deviceDataMap.put(Constant.DEVICE_NAME, (device.getName() == null) ? mRes.getString(R.string.unknown) : device.getName());
                    deviceDataMap.put(Constant.DEVICE_ADDRESS, device.getAddress());
                    deviceDataMap.put(Constant.DEVICE_BLE, device);

                    boolean isExist = false;
                    for (int i = 0; i < mAvailableList.size(); i++) {
                        Object address = mAvailableList.get(i).get(Constant.DEVICE_ADDRESS);
                        // Check address null to prevent exception
                        if (address != null && address.equals(device.getAddress())) {
                            isExist = true;
                        }
                    }

                    if (!isExist) {
                        mAvailableList.add(deviceDataMap);
                        mAvailableListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private BleGattCb mBleGattCb = new BleGattCb() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            final BluetoothDevice device = gatt.getDevice();
            KLog.d("address : " + device.getAddress());

            if (mConnectState == BluetoothProfile.STATE_DISCONNECTED || mConnectState == 133) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 0; i < mBondedList.size(); i++) {

                            if (mBondedList.get(i).get(Constant.DEVICE_ADDRESS).equals(device.getAddress())) {

                                mBondedList.remove(i);
                                mBondedListAdapter.notifyDataSetChanged();
                            }
                        }

                        for (int i = 0; i < mAvailableList.size(); i++) {

                            if (mAvailableList.get(i).get(Constant.DEVICE_ADDRESS).equals(device.getAddress())) {

                                mAvailableList.get(i).put(Constant.DEVICE_CONNECTING, false);
                                mAvailableListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

                if (mConnectedAddress.equals(device.getAddress())) {
                    mConnectedName = "";
                    mConnectedAddress = "";
                }

                if (!mManualDisconnect) {

                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            ((device.getName() == null) ? mRes.getString(R.string.unknown) : device.getName()) + " " + mRes.getString(R.string.has_been_disconnected),
                            Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(mRes.getString(R.string.dismiss), new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                        }
                    });
                    snackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    snackbar.show();

                } else {
                    // Close GATT client (Only when manual disconnect)
                    if (mGatt != null) {
                        mGatt.close();
                        mGatt = null;
                    }

                    // Re-scan device
//                runOnUiThread(new Runnable() {
//                                  @Override
//                                  public void run() {
//                                      scanDevice(true, true);
//                                  }
//                              });
                }

            } else if (mConnectState == BluetoothProfile.STATE_CONNECTED) {
                // Check name and addr for re-connect case
                if (mConnectedAddress.equals("")) {

                    mConnectedName = (device.getName() == null) ? mRes.getString(R.string.unknown) : device.getName();
                    mConnectedAddress = device.getAddress();
                }

                final HashMap<String, Object> map = new HashMap();
                map.put(Constant.DEVICE_NAME, (device.getName() == null) ? mRes.getString(R.string.unknown) : device.getName());
                map.put(Constant.DEVICE_ADDRESS, device.getAddress());

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mBondedList.add(map);
                        mBondedListAdapter.notifyDataSetChanged();

                        for (int i = 0; i < mAvailableList.size(); i++) {

                            if (mAvailableList.get(i).get(Constant.DEVICE_ADDRESS).equals(device.getAddress())) {

                                mAvailableList.remove(i);
                                mAvailableListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        }
    };
    // endregion [Callback]
}