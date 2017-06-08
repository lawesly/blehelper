package com.ppcrong.blehelper.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppcrong.blehelper.R;
import com.ppcrong.blehelper.R2;
import com.ppcrong.blehelper.model.BleDeviceItemData;
import com.ppcrong.blehelper.utils.Constant;
import com.socks.library.KLog;

import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BondedListAdapter extends RecyclerView.Adapter<BondedListAdapter.BondedDeviceViewHolder> {

    private Context mContext;
    private CopyOnWriteArrayList<BleDeviceItemData> mBondedList;

    public BondedListAdapter(Context context, CopyOnWriteArrayList<BleDeviceItemData> bondedList) {
        this.mContext = context;
        this.mBondedList = bondedList;
    }

    @Override
    public BondedDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View availableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bonded_device, parent, false);

        return new BondedDeviceViewHolder(availableView);
    }

    @Override
    public void onBindViewHolder(BondedDeviceViewHolder holder, int position) {
        KLog.d("position: " + position);

        // Get item data
        BleDeviceItemData item = mBondedList.get(position);

        // Get device to set name and address
        BluetoothDevice device = item.getDevice();
        holder.mTvName.setText(device.getName());
        holder.mTvAddress.setText(device.getAddress());

        int percentage = item.getBatteryPercentage();
        if (percentage == 100) holder.mImgBattery.setImageResource(R.drawable.battery_full);
        if (percentage < 100 && percentage >= 90)
            holder.mImgBattery.setImageResource(R.drawable.battery_90);
        if (percentage < 90 && percentage >= 80)
            holder.mImgBattery.setImageResource(R.drawable.battery_90);
        if (percentage < 80 && percentage >= 70)
            holder.mImgBattery.setImageResource(R.drawable.battery_70);
        if (percentage < 70 && percentage >= 60)
            holder.mImgBattery.setImageResource(R.drawable.battery_60);
        if (percentage < 60 && percentage >= 50)
            holder.mImgBattery.setImageResource(R.drawable.battery_50);
        if (percentage < 50 && percentage >= 40)
            holder.mImgBattery.setImageResource(R.drawable.battery_40);
        if (percentage < 40 && percentage >= 30)
            holder.mImgBattery.setImageResource(R.drawable.battery_30);
        if (percentage < 30 && percentage >= 20)
            holder.mImgBattery.setImageResource(R.drawable.battery_20);
        if (percentage < 20 && percentage >= 10)
            holder.mImgBattery.setImageResource(R.drawable.battery_10);
        if (percentage < 10 && percentage >= 0)
            holder.mImgBattery.setImageResource(R.drawable.battery_outline);
    }

    @Override
    public int getItemCount() {

        return mBondedList.size();
    }

    public class BondedDeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.tv_bonded_device_name)
        TextView mTvName;
        @BindView(R2.id.tv_bonded_device_address)
        TextView mTvAddress;
        @BindView(R2.id.tv_bonded_device_type)
        TextView mTvType;
        @BindView(R2.id.img_battery)
        ImageView mImgBattery;

        public BondedDeviceViewHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            // Get item data
            int position = getAdapterPosition();
            BleDeviceItemData item = mBondedList.get(position);

            // sendBroadcast with device data
            Intent disconnectIntent = new Intent(Constant.ACTION_REQUEST_DISCONNECT);
            disconnectIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, item.getDevice());
            mContext.sendBroadcast(disconnectIntent);
        }
    }

}
