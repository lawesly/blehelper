package com.ppcrong.blescanner.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ppcrong.blescanner.R;
import com.ppcrong.blescanner.R2;
import com.ppcrong.blescanner.adapter.AvailableListAdapter;
import com.ppcrong.blescanner.adapter.BondedListAdapter;
import com.ppcrong.blescanner.bluetooth.BleScanner;
import com.ppcrong.blescanner.utils.Constant;
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

        initView();
        initScanner();
    }

    @Override
    protected void onDestroy() {
        KLog.d();
        scanDevice(false, true);
        super.onDestroy();
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
    }

    private void initScanner() {
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
    // endregion [Callback]
}
