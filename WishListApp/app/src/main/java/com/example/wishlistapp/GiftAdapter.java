package com.example.wishlistapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
    private List<Gift> gifts;
    private Context context;
    private OnSelectionChangedListener listener;

    public GiftAdapter(Context context, List<Gift> gifts, OnSelectionChangedListener listener) {
        this.context = context;
        this.gifts = gifts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gift, parent, false);
        return new GiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GiftViewHolder holder, int position) {
        Gift gift = gifts.get(position);

        // Картинка по URL
        Glide.with(context).load(gift.getImageUrl()).into(holder.ivGiftImage);

        holder.tvGiftName.setText(gift.getName());
        holder.tvGiftPrice.setText(gift.getPrice() + " грн");

        holder.cbSelect.setChecked(gift.isSelected());

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            gift.setSelected(isChecked);
            listener.onSelectionChanged();
        });
    }

    @Override
    public int getItemCount() {
        return gifts.size();
    }

    public static class GiftViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGiftImage;
        TextView tvGiftName;
        TextView tvGiftPrice;
        CheckBox cbSelect;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGiftImage = itemView.findViewById(R.id.ivGiftImage);
            tvGiftName = itemView.findViewById(R.id.tvGiftName);
            tvGiftPrice = itemView.findViewById(R.id.tvGiftPrice);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }
}