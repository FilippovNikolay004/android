package com.example.wishlistapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GiftAdapter.OnSelectionChangedListener {
    private RecyclerView rvWishList;
    private TextView tvTotalPrice;
    private List<Gift> gifts;
    private GiftAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvWishList = findViewById(R.id.rvWishList);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        gifts = new ArrayList<>();
        gifts.add(new Gift("https://webobjects2.cdw.com/is/image/CDW/7870980?$product-main$", "4K OLED monitor ASUS ROG", 60000));
        gifts.add(new Gift("https://webobjects2.cdw.com/is/image/CDW/8396227?$product-main$", "Наушники Sony WH-1000XM6", 16000));
        gifts.add(new Gift("https://images.secretlab.co/theme/common/mpro-why-sl-1.jpg", "Secretlab MAGNUS Pro Black", 38000));
        gifts.add(new Gift("https://m.media-amazon.com/images/I/71e9KA6r0OL._AC_UF1000,1000_QL80_.jpg", "Серия книг Ведьмак (полный набор)", 1200));
        gifts.add(new Gift("https://www.lbtechreviews.com/wp-content/uploads/2022/03/ARGFENRISA4BK_O_1.jpg", "Динамики Argon Audio FENRIS A4", 13000));
        gifts.add(new Gift("https://www.logitechg.com/content/dam/gaming/en/products/pro-x60-wireless-keyboard/gallery/pro-x-60-keyboard-black-gallery-1-us.png", "Logitech G PRO X 60 keyboard", 6250));
        gifts.add(new Gift("https://i.rtings.com/assets/products/ZZ9SdscN/logitech-g-pro-wireless/design-large.jpg?format=auto", "Logitech PRO Wireless mouse", 2700));

        rvWishList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GiftAdapter(this, gifts, this);
        rvWishList.setAdapter(adapter);

        updateTotalPrice();
    }

    @Override
    public void onSelectionChanged() {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (Gift gift : gifts) {
            if (gift.isSelected()) {
                total += gift.getPrice();
            }
        }
        tvTotalPrice.setText("Общая стоимость: " + total + " грн");
    }
}