package com.ppcrong.blescanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppcrong.blescanner.R;
import com.ppcrong.blescanner.R2;
import com.ppcrong.blescanner.utils.Constant;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BondedListAdapter extends RecyclerView.Adapter<BondedListAdapter.BondedDeviceViewHolder> {

    private Context mContext;
    private CopyOnWriteArrayList<HashMap<String, Object>> mBondedList;

    public BondedListAdapter(Context context, CopyOnWriteArrayList<HashMap<String, Object>> bondedList) {
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

        String name = mBondedList.get(position).get(Constant.DEVICE_NAME).toString();
        String addr = mBondedList.get(position).get(Constant.DEVICE_ADDRESS).toString();
        int type = mBondedList.get(position).get(Constant.DEVICE_TYPE) == null ? -1 : (int) mBondedList.get(position).get(Constant.DEVICE_TYPE);
        int percentage = mBondedList.get(position).get(Constant.DEVICE_BATTERY) == null ? -1 : (int) mBondedList.get(position).get(Constant.DEVICE_BATTERY);

        holder.mTvName.setText(name);
        holder.mTvAddress.setText(addr);

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

            int position = getAdapterPosition();

            Intent disconnectIntent = new Intent(Constant.ACTION_REQUEST_DISCONNECT);
            disconnectIntent.putExtra(Constant.DEVICE_ADDRESS, mBondedList.get(position).get(Constant.DEVICE_ADDRESS).toString());
            mContext.sendBroadcast(disconnectIntent);
        }
    }

}
