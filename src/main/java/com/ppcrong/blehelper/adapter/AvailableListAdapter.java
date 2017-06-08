package com.ppcrong.blehelper.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ppcrong.blehelper.R;
import com.ppcrong.blehelper.R2;
import com.ppcrong.blehelper.model.BleDeviceItemData;
import com.ppcrong.blehelper.utils.Constant;
import com.socks.library.KLog;

import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableListAdapter extends RecyclerView.Adapter<AvailableListAdapter.AvailableDeviceViewHolder> {

    private Context mContext;
    private CopyOnWriteArrayList<BleDeviceItemData> mAvailableList;

    public AvailableListAdapter(Context context, CopyOnWriteArrayList<BleDeviceItemData> availableList) {
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

        // Get item data
        BleDeviceItemData item = mAvailableList.get(position);

        // Show/Hide connecting progress
        if (item.getIsConnecting())
            holder.mPbConnecting.setVisibility(View.VISIBLE);
        else
            holder.mPbConnecting.setVisibility(View.GONE);

        // Get device to set name and address
        BluetoothDevice device = item.getDevice();
        holder.mTvName.setText((device.getName() == null) ? mContext.getString(R.string.unknown) : device.getName());
        holder.mTvAddress.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return mAvailableList.size();
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

            // Get item data
            int position = getAdapterPosition();
            BleDeviceItemData item = mAvailableList.get(position);

            // Show connecting progress
            item.setIsConnecting(true);
            notifyDataSetChanged();

            // sendBroadcast with device data
            Intent connectIntent = new Intent(Constant.ACTION_REQUEST_CONNECT);
            connectIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, item.getDevice());
            mContext.sendBroadcast(connectIntent);
        }
    }
}
