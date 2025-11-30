package com.example.minesweeper5x5;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int SIZE = 5;
    private static final int MINES = 6;

    private Button[][] buttons = new Button[SIZE][SIZE];
    private int[][] field = new int[SIZE][SIZE];
    private boolean[][] revealed = new boolean[SIZE][SIZE];
    private boolean[][] flagged = new boolean[SIZE][SIZE];
    private boolean gameOver = false;

    // Чат сапёра
    private TextView tvChat;
    private StringBuilder chatLog = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gridField = findViewById(R.id.gridField);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        tvChat = findViewById(R.id.tvChat);

        // Размер клетки в dp
        int cellSizeDp = 56;
        final int cellSizePx = (int) (cellSizeDp * getResources().getDisplayMetrics().density);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Button btn = new Button(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.width = cellSizePx;
                params.height = cellSizePx;
                btn.setLayoutParams(params);

                btn.setPadding(8, 8, 8, 8);
                btn.setBackgroundColor(Color.rgb(180, 180, 180));
                btn.setTextSize(18);
                btn.setText(""); // пока пусто

                final int row = i;
                final int col = j;

                // Клик — открываем клетку
                btn.setOnClickListener(v -> {
                    if (gameOver || revealed[row][col] || flagged[row][col]) return;
                    openCell(row, col);
                });

                // Долгий клик — ставим флажок
                btn.setOnLongClickListener(v -> {
                    if (gameOver || revealed[row][col]) return false;
                    flagged[row][col] = !flagged[row][col];
                    btn.setText(flagged[row][col] ? "F" : "");
                    addChat("Клетка (" + row + "," + col + "): " +
                            (flagged[row][col] ? "поставлен флаг" : "флаг убран"));
                    return true;
                });

                buttons[i][j] = btn;
                gridField.addView(btn);
            }
        }

        btnNewGame.setOnClickListener(v -> newGame());
        newGame(); // первая игра
    }

    // --- Чат сапёра ---
    private void addChat(String msg) {
        chatLog.append(msg).append("\n");
        tvChat.setText(chatLog.toString());
    }

    // --- Логика игры ---
    private void newGame() {
        gameOver = false;
        revealed = new boolean[SIZE][SIZE];
        flagged = new boolean[SIZE][SIZE];
        generateField();
        updateDisplay();

        chatLog.setLength(0);
        addChat("Новая игра! Поле " + SIZE + "×" + SIZE + ", мин: " + MINES);
    }

    private void generateField() {
        // Обнуляем
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                field[i][j] = 0;

        // Расставляем мины
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < MINES) {
            int r = rand.nextInt(SIZE);
            int c = rand.nextInt(SIZE);
            if (field[r][c] != -1) {
                field[r][c] = -1;
                minesPlaced++;
                // Увеличиваем счётчики вокруг
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr, nc = c + dc;
                        if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && field[nr][nc] != -1) {
                            field[nr][nc]++;
                        }
                    }
                }
            }
        }
    }

    private void openCell(int row, int col) {
        if (revealed[row][col] || flagged[row][col]) return;

        revealed[row][col] = true;

        if (field[row][col] == -1) {
            buttons[row][col].setText("💣");
            buttons[row][col].setBackgroundColor(Color.RED);
            gameOver = true;
            addChat("Ой! На (" + row + "," + col + ") была мина. Поражение.");
            Toast.makeText(this, "БАМ! Ты проиграл!", Toast.LENGTH_LONG).show();
            revealAllMines();
        } else {
            int num = field[row][col];
            if (num > 0) {
                buttons[row][col].setText(String.valueOf(num));
                buttons[row][col].setBackgroundColor(Color.LTGRAY);
                addChat("Клетка (" + row + "," + col + "): рядом мин — " + num);
            } else {
                buttons[row][col].setBackgroundColor(Color.LTGRAY);
                addChat("Клетка (" + row + "," + col + "): пусто, открываю соседей");
                // Автооткрытие пустых зон
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = row + dr, nc = col + dc;
                        if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && !revealed[nr][nc]) {
                            openCell(nr, nc);
                        }
                    }
                }
            }
        }
        checkWin();
    }

    private void revealAllMines() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j] == -1) {
                    buttons[i][j].setText("💣");
                    buttons[i][j].setBackgroundColor(Color.RED);
                }
            }
        }
    }

    private void checkWin() {
        boolean won = true;
        for (int i = 0; i < SIZE && won; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j] != -1 && !revealed[i][j]) {
                    won = false;
                    break;
                }
            }
        }
        if (won) {
            gameOver = true;
            addChat("Победа! Все безопасные клетки открыты.");
            Toast.makeText(this, "Победа! Все мины найдены!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackgroundColor(Color.rgb(180, 180, 180));
            }
        }
    }
}
