package com.ppcrong.blescanner.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ppcrong.blescanner.R;
import com.ppcrong.blescanner.R2;
import com.ppcrong.blescanner.utils.Constant;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableListAdapter extends RecyclerView.Adapter<AvailableListAdapter.AvailableDeviceViewHolder> {

    private Context mContext;
    private CopyOnWriteArrayList<HashMap<String, Object>> mAvailableList;

    public AvailableListAdapter(Context context, CopyOnWriteArrayList<HashMap<String, Object>> availableList) {
        this.mContext = context;
        this.mAvailableList = availableList;
    }

    @Override
    public AvailableDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View availableDeviceItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_device, parent, false);
        return new AvailableDeviceViewHolder(availableDeviceItem);
    }

    @Override
    public void onBindViewHolder(AvailableDeviceViewHolder holder, int position) {
        KLog.d("position: " + position);

        String name = mAvailableList.get(position).get(Constant.DEVICE_NAME).toString();
        String address = mAvailableList.get(position).get(Constant.DEVICE_ADDRESS).toString();
        boolean isConnecting = mAvailableList.get(position).get(Constant.DEVICE_CONNECTING) == null ? false : (boolean) mAvailableList.get(position).get(Constant.DEVICE_CONNECTING);

        holder.mTvName.setText(name);
        holder.mTvAddress.setText(address);

        if (isConnecting)
            holder.mPbConnecting.setVisibility(View.VISIBLE);
        else
            holder.mPbConnecting.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mAvailableList.size();
    }

    public BluetoothDevice getDevice(int position) {

        return (BluetoothDevice) mAvailableList.get(position).get(Constant.DEVICE_BLE);
    }

    public class AvailableDeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.tv_available_device_name)
        TextView mTvName;
        @BindView(R2.id.tv_available_device_address)
        TextView mTvAddress;
        @BindView(R2.id.pb_connecting)
        ProgressBar mPbConnecting;

        public AvailableDeviceViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            mAvailableList.get(position).put(Constant.DEVICE_CONNECTING, true);
            notifyDataSetChanged();

            Intent connectIntent = new Intent(Constant.ACTION_REQUEST_CONNECT);
            connectIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, getDevice(position));
            mContext.sendBroadcast(connectIntent);
        }
    }
}
