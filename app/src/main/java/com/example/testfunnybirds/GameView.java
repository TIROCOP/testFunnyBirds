package com.example.testfunnybirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private int viewWidth;
    private int speed = 200;
    private final int timerInterval = 30;
    private Sprite playerBird;
    private Sprite enemyBird;
    private int points = 0;
    private int kTapLose = 0;
    private boolean youLose = false;
    private int viewHeight;

    public GameView(Context context) {
        super(context);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth() / 5;
        int h = b.getHeight() / 3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, speed, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth() / 5;
        h = b.getHeight() / 3;
        firstFrame = new Rect(4 * w, 0, 5 * w, h);
        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {
                if (i == 0 && j == 4) {
                    continue;
                }
                if (i == 2 && j == 0) {
                    continue;
                }
                enemyBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }
        Timer t = new Timer();
        t.start();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!youLose) {
            System.out.println(0);
            canvas.drawARGB(250, 127, 199, 255); // заливаем цветом
            playerBird.draw(canvas);
            enemyBird.draw(canvas);
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setTextSize(55.0f);
            p.setColor(Color.WHITE);
            canvas.drawText(points + "", viewWidth - 100, 70, p);
        } else {
            super.onDraw(canvas);
            System.out.println(1);
            canvas.drawARGB(250, 0, 0, 0); // заливаем цветом
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setTextSize(55.0f);
            p.setColor(Color.WHITE);
            canvas.drawText("You Lose", viewWidth - 600, viewHeight - 500, p);
            canvas.drawText("DoubleClick to restart",
                    viewWidth - 600, viewHeight - 400, p);
        }
    }

    private void teleportEnemy() {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }

    protected void update() {
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval);
        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        } else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        if (enemyBird.getX() < -enemyBird.getFrameWidth()) {
            teleportEnemy();
            points += 10;
        }
        if (enemyBird.intersect(playerBird)) {
            teleportEnemy();
            points -= 40;
        }
        if (points < -40) {
            youLose = true;
        }
        invalidate();
    }

    class Timer extends CountDownTimer {
        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            // Движение вверх
            if (event.getY() < playerBird.getBoundingBoxRect().top) {
                if (playerBird.getVy() != -speed) {
                    points--;
                }
                playerBird.setVy(-speed);
            } else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                if (playerBird.getVy() != speed) {
                    points--;
                }
                playerBird.setVy(speed);
            }
            if (youLose) {
                ++kTapLose;
                if (kTapLose > 1) {
                    points = 0;
                    System.out.println(12);
                    speed = 150;
                    youLose = false;
                    enemyBird.setX(2000);
                    enemyBird.setY(250);
                    playerBird.setY(0);
                    playerBird.setVy(speed);
                    kTapLose = 0;
                }

            }
        }
        return true;
    }
}