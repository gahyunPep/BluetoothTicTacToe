package com.chickeneater.tictactoe.game;

import com.chickeneater.tictactoe.core.data.OnMessageReceivedListener;
import com.chickeneater.tictactoe.core.data.TickTackBluetoothService;

import androidx.annotation.NonNull;

import static com.chickeneater.tictactoe.game.GameBoard.EMPTY;

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
        mBluetoothService.disconnect();
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
        int winner = mBoard.checkForAWinner(x, y);

        mOnGameEventListener.onMoveMade();
        if (winner != EMPTY) {
            mOnGameEventListener.onPlayerWon(winner);
        } else if (mBoard.isDraw()) {
            mOnGameEventListener.onDraw();
        }
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
