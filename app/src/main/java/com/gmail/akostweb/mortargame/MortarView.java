package com.gmail.akostweb.mortargame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MortarView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "MortarView";

    private MortarThread mortarThread;
    private Activity activity;
    private boolean dialogIsDisplayed = false;

    public static final int TARGET_PIECES = 5;
    public static final int MISS_PENALTY = 2;
    public static final int HIT_REWARD = 3;

    private boolean gameOver;
    private double timeLeft;
    private int shotsFired;
    private double totalElapsedTime;

    private Line blocker;
    private int blockerDistance;
    private int blockerBeginning;
    private int blockerEnd;
    private int initialBlockerVelocity;
    private float blockerVelocity;

    private Line target;
    private int targetDistance;
    private int targetBeginning;
    private double pieceLength;
    private int targetEnd;
    private int initialTargetVelocity;
    private float targetVelocity;

    private int lineWidth;
    private boolean[] hitStates;
    private int targetPieceHit;

    private Point mortarBall;
    private int mortarVelocityX;
    private int mortarVelocityY;
    private boolean mortarBallOnScreen;
    private int mortarBallRadius;
    private int mortarSpeed;
    private int mortarBaseRadius;
    private int mortarLength;
    private Point barrelEnd;
    private int screenWidth;
    private int screenHeight;

    public static final int TARGET_SOUND_ID = 0;
    public static final int CANNON_SOUND_ID = 1;
    public static final int BLOCKER_SOUND_ID = 2;
    public SoundPool soundPool;
    public SparseIntArray soundMap;

    private Paint textPaint;
    private Paint mortarBallPaint;
    private Paint mortarPaint;
    private Paint blockerPaint;
    private Paint targetPaint;
    private Paint backGroundPaint;



    public MortarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity) context;

        getHolder().addCallback(this);

        blocker = new Line();
        target = new Line();
        mortarBall = new Point();

        hitStates = new boolean[TARGET_PIECES];

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundMap = new SparseIntArray(3);
        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));
        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));

        textPaint = new Paint();
        mortarPaint = new Paint();
        mortarBallPaint = new Paint();
        blockerPaint = new Paint();
        targetPaint = new Paint();
        backGroundPaint = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        mortarBaseRadius = h / 22;

        mortarLength = w / 8;
        mortarBallRadius = w / 36;
        mortarSpeed = w ;

        lineWidth = w / 24;
        blockerDistance = w * 5 / 8;
        blockerBeginning = h / 8;
        blockerEnd = 3 * h / 8;
        initialBlockerVelocity = h / 2;
        blocker.start = new Point(blockerDistance, blockerBeginning);
        blocker.end = new Point(blockerDistance, blockerEnd);

        targetDistance = w * 7 / 8;
        targetBeginning = h / 9;

        targetEnd = h * 7 / 8;
        pieceLength = (targetEnd - targetBeginning) / TARGET_PIECES;
        initialTargetVelocity = h / 4;
        target.start = new Point(targetDistance, targetBeginning);
        target.end = new Point(targetDistance, targetEnd);

        barrelEnd = new Point(mortarLength, h / 2);

        textPaint.setTextSize(w / 20);
        textPaint.setAntiAlias(true);
        mortarPaint.setStrokeWidth(lineWidth * 1.5f);
        mortarPaint.setColor(Color.LTGRAY);
        blockerPaint.setStrokeWidth(lineWidth);
        blockerPaint.setColor(Color.RED);
        mortarBallPaint.setColor(Color.DKGRAY);
        targetPaint.setStrokeWidth(lineWidth);
        backGroundPaint.setColor(Color.WHITE);
        mortarBallPaint.setColor(Color.DKGRAY);

        newGame();
    }

    public void newGame() {
        for (int i = 0; i < TARGET_PIECES; i++)
            hitStates[i] = false;

        targetPieceHit = 0;
        blockerVelocity = initialBlockerVelocity;
        targetVelocity = initialTargetVelocity;
        timeLeft = 50;
        mortarBallOnScreen = false;
        shotsFired = 0;
        totalElapsedTime = 0.0;

        blocker.start.set(blockerDistance, blockerBeginning);
        blocker.end.set(blockerDistance, blockerEnd);
        target.start.set(targetDistance, targetBeginning);
        target.end.set(targetDistance, targetEnd);

        if (gameOver) {
            gameOver = false;
            mortarThread = new MortarThread(getHolder());
            mortarThread.start();
        }
    }

    private void updatePositions(double elapsedTimeMS){
        double interval = elapsedTimeMS / 1500.0;
        if (mortarBallOnScreen){
            mortarBall.x += interval * mortarVelocityX;
            mortarBall.y += interval * mortarVelocityY;
            if (mortarBall.x + mortarBallRadius > blockerDistance &&
                    mortarBall.x - mortarBallRadius < blockerDistance &&
                    mortarBall.y + mortarBallRadius > blocker.start.y &&
                    mortarBall.y - mortarBallRadius < blocker.end.y){
                mortarVelocityX *= -1;
                timeLeft -=MISS_PENALTY;
                soundPool.play(soundMap.get(TARGET_SOUND_ID), 1, 1, 1, 0, 1f);
                soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
            } else if (mortarBall.x + mortarBallRadius > screenWidth ||
                    mortarBall.x - mortarBallRadius < 0){
                mortarBallOnScreen = false;
            } else if (mortarBall.y + mortarBallRadius > screenHeight &&
                    mortarBall.y - mortarBallRadius < 0){
             mortarBallOnScreen = false;
            } else if (mortarBall.x + mortarBallRadius > targetDistance &&
                    mortarBall.x - mortarBallRadius < targetDistance &&
                    mortarBall.y + mortarBallRadius > target.start.y &&
                    mortarBall.y - mortarBallRadius < target.end.y){
                int section = (int) ((mortarBall.y - target.start.y) / pieceLength);

                if ((section>= 0 &&section < TARGET_PIECES) && !hitStates[section]){
                    hitStates[section] = true;
                    mortarBallOnScreen = false;
                    timeLeft += HIT_REWARD;
                    soundPool.play(soundMap.get(TARGET_SOUND_ID), 1, 1, 1, 0, 1f);

                    if (++targetPieceHit == TARGET_PIECES){
                        mortarThread.setRunning(false);
                        showGameOverDialog(R.string.win);
                        gameOver = true;
                    }
                }
            }


        }
        double blockerUpdate = interval * blockerVelocity;
        blocker.start.y += blockerUpdate;
        blocker.end.y += blockerUpdate;

        double targetUpdate = interval * targetVelocity;
        target.start.y +=targetUpdate;
        target.end.y += targetUpdate;

        if (blocker.start.y < 0 || blocker.end.y > screenHeight)
            blockerVelocity *= - 1;
        if (target.start.y < 0 || target.end.y > screenHeight)
            targetVelocity *= -1;
        timeLeft -= interval;

        if (timeLeft <= 0.0){
            timeLeft = 0.0;
            gameOver = true;
            mortarThread.setRunning(false);
            showGameOverDialog(R.string.lose);
        }
    }

    public void fireMortarBall(MotionEvent event){
        if (mortarBallOnScreen){
            return;
        }
        double angle = alignMortar(event);
        mortarBall.x = mortarBallRadius;
        mortarBall.y = screenHeight / 2;

        mortarVelocityX = (int) (mortarSpeed * Math.sin(angle));

        mortarVelocityY = ( int ) ( -mortarSpeed * Math.cos(angle));
        mortarBallOnScreen = true;
        ++shotsFired;

        soundPool.play(soundMap.get(CANNON_SOUND_ID), 1, 1, 1, 0, 1f);
    }

    public double alignMortar(MotionEvent event){
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());

        double centerMinusY = ( screenHeight / 2 - touchPoint.y);
        double angle = 0;

        if (centerMinusY !=0)
            angle = Math.atan((double) touchPoint.x / centerMinusY);

        if (touchPoint.y > screenHeight / 2)
            angle += Math.PI;

        barrelEnd.x = (int) (mortarLength * Math.sin(angle));
        barrelEnd.y = (int) (-mortarLength * Math.cos(angle) + screenHeight / 2);
        return angle;
    }

    public void drawGameElements(Canvas canvas){
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backGroundPaint);

        canvas.drawText(getResources().getString(R.string.time_remaining_format, timeLeft), 30, 50, textPaint);

        if (mortarBallOnScreen)
            canvas.drawCircle(mortarBall.x, mortarBall.y, mortarBallRadius, mortarBallPaint);

        canvas.drawLine(0, screenHeight / 2, barrelEnd.x, barrelEnd.y, mortarPaint);

        canvas.drawCircle(0, screenHeight / 1.9f, mortarBaseRadius, mortarPaint);
        canvas.drawCircle(0, screenHeight / 2.1f, mortarBaseRadius, mortarPaint);

        canvas.drawLine(blocker.start.x, blocker.start.y, blocker.end.x, blocker.end.y, blockerPaint);

        Point currentPoint = new Point();

        currentPoint.x = target.start.x;
        currentPoint.y = target.start.y;

        for (int i = 0; i < TARGET_PIECES; i++) {
            if (!hitStates[i]){
                if (i % 2 != 0 ){
                    targetPaint.setColor(Color.BLUE);
                    canvas.drawLine(currentPoint.x, currentPoint.y, target.end.x, (int) (currentPoint.y + pieceLength), targetPaint);
                } else {
                    targetPaint.setColor(Color.YELLOW);
                    canvas.drawLine(currentPoint.x, currentPoint.y, target.end.x, (int) (currentPoint.y + pieceLength), targetPaint);
                }

            }
            currentPoint.y +=pieceLength;
        }
    }

    private  void showGameOverDialog(final int messageID){
        @SuppressLint("ValidFragment")
        final DialogFragment gameResult = new DialogFragment(){
            @Override
            public Dialog onCreateDialog (Bundle bundle){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(messageID));

                builder.setMessage(getResources().getString(R.string.result_format, shotsFired, totalElapsedTime));

                builder.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogIsDisplayed = false;
                        newGame();
                    }
                });
                return builder.create();
            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogIsDisplayed = true;
                gameResult.setCancelable(false);
                gameResult.show(activity.getFragmentManager(), "results");
            }
        });
    }

    public void stopGame(){
        if(mortarThread != null)
            mortarThread.setRunning(false);
    }

    public void releaseResources(){
        soundPool.release();
        soundPool = null;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!dialogIsDisplayed){
            mortarThread = new MortarThread(holder);
            mortarThread.setRunning(true);
            mortarThread.start();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mortarThread.setRunning(false);
        while(retry){
            try {
                mortarThread.join();
                retry = false;
            } catch (InterruptedException e){
                Log.e(TAG, "Thread interrupted", e);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        int action = e.getAction();

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
            fireMortarBall(e);
        }
        return true;
    }



    private class MortarThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private boolean threadIsRunning = true;

        public MortarThread(SurfaceHolder holder){
            surfaceHolder = holder;
            setName("MortarThread");
        }
        public void setRunning(boolean running){
            threadIsRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas = null;
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning){
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder){
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        totalElapsedTime += elapsedTimeMS / 1000.0;
                        updatePositions(elapsedTimeMS);
                        drawGameElements(canvas);
                        previousFrameTime = currentTime;
                    }
                }
                finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
















}
