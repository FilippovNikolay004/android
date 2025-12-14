package com.example.telegramchatlist;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvChats = findViewById(R.id.lvChats);
        List<Map<String, Object>> chatList = new ArrayList<>();

        Map<String, Object> chat1 = new HashMap<>();
        chat1.put("avatar", R.drawable.ic_launcher_background);
        chat1.put("name", "Пётр Иванов");
        chat1.put("last_message", "Хорошо, договорились на 11:20");
        chat1.put("time", "12:15");
        chat1.put("unread_count", 3);
        chat1.put("is_sent", true);
        chat1.put("is_read", false);
        chatList.add(chat1);

        Map<String, Object> chat2 = new HashMap<>();
        chat2.put("avatar", R.drawable.ic_launcher_background);
        chat2.put("name", "Группа 'Работа'");
        chat2.put("last_message", "Собрание в 10:00");
        chat2.put("time", "Вчера");
        chat2.put("unread_count", 0);
        chat2.put("is_sent", false);
        chat2.put("is_read", true);
        chatList.add(chat2);

        Map<String, Object> chat3 = new HashMap<>();
        chat3.put("avatar", 0);
        chat3.put("name", "Мария Сидорова");
        chat3.put("last_message", "Привет! Как дела?");
        chat3.put("time", "01.12");
        chat3.put("unread_count", 1);
        chat3.put("is_sent", true);
        chat3.put("is_read", true);
        chatList.add(chat3);

        String[] from = {
            "avatar",
            "name",
            "last_message",
            "time",
            "unread_count",
            "is_sent",
            "is_read"
        };

        int[] to = {
                0,
                R.id.tvName,
                R.id.tvLastMessage,
                R.id.tvTime,
                R.id.tvUnreadCount,
                0,
                0
        };

        SimpleAdapter adapter = new SimpleAdapter(this, chatList, R.layout.item_chat, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                Map<String, Object> item = (Map<String, Object>) getItem(position);

                TextView tvUnread = view.findViewById(R.id.tvUnreadCount);
                int unread = (int) item.get("unread_count");
                if (unread > 0) {
                    tvUnread.setText(String.valueOf(unread));
                    tvUnread.setVisibility(View.VISIBLE);
                } else {
                    tvUnread.setVisibility(View.GONE);
                }

                // Статус галочек
                ImageView ivStatus = view.findViewById(R.id.ivStatus);
                boolean isSent = (boolean) item.get("is_sent");
                boolean isRead = (boolean) item.get("is_read");

                if (isSent) {
                    ivStatus.setVisibility(View.VISIBLE);
                    if (isRead) {
                        ivStatus.setImageResource(R.drawable.ic_double_check);
                    } else {
                        ivStatus.setImageResource(R.drawable.ic_single_check);
                    }
                } else {
                    ivStatus.setVisibility(View.GONE);
                }

                // Аватарка
                ImageView ivAvatarImage = view.findViewById(R.id.ivAvatarImage);
                TextView tvAvatarInitial = view.findViewById(R.id.tvAvatarInitial);
                int avatarRes = (int) item.get("avatar");
                String name = (String) item.get("name");

                if (avatarRes != 0) {
                    ivAvatarImage.setImageResource(avatarRes);
                    ivAvatarImage.setVisibility(View.VISIBLE);
                    tvAvatarInitial.setVisibility(View.GONE);
                } else {
                    ivAvatarImage.setVisibility(View.GONE);
                    tvAvatarInitial.setVisibility(View.VISIBLE);
                    if (name != null && !name.isEmpty()) {
                        String initial = String.valueOf(name.charAt(0)).toUpperCase();
                        tvAvatarInitial.setText(initial);
                    } else {
                        tvAvatarInitial.setText("?");
                    }
                }

                return view;
            }
        };

        lvChats.setAdapter(adapter);
    }
}