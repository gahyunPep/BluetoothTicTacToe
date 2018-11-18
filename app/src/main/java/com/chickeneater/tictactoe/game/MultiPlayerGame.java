package com.chickeneater.tictactoe.game;

import com.chickeneater.tictactoe.core.data.OnMessageReceivedListener;
import com.chickeneater.tictactoe.core.data.TickTackBluetoothService;

import androidx.annotation.NonNull;

/**
 * Created by romanlee on 11/17/18.
 * To the power of Love
 */
public class MultiPlayerGame extends AbstractGame implements OnMessageReceivedListener {
    private boolean mCanMakeMove;
    private TickTackBluetoothService mBluetoothService = TickTackBluetoothService.getInstance();

    public MultiPlayerGame(@NonNull OnGameEventListener onGameEventListener, boolean isHost) {
        super(onGameEventListener);
        mBluetoothService.addMessageReceivedListener(this);
        mCanMakeMove = isHost;
        mIsCross = isHost;
    }

    public void clean() {
        mBluetoothService.removeMessageReceivedListener(this);
    }

    @Override
    public void onMessageReceived(String message) {
        String[] messageXY = message.split(" ");
        int x = Integer.parseInt(messageXY[0]);
        int y = Integer.parseInt(messageXY[1]);
        if (isCurrentPlayerCross()) {
            mBoard.setNought(x, y);
        } else {
            mBoard.setCross(x, y);
        }
        moveMade(x, y);
    }

    @Override
    public boolean canMakeMove() {
        return mCanMakeMove;
    }

    @Override
    public void moveMade(int x, int y) {
        mCanMakeMove = !mCanMakeMove;
    }

    @Override
    public void makeMove(int x, int y) {
        super.makeMove(x, y);
        mBluetoothService.write(x + " " + y);
    }
}
