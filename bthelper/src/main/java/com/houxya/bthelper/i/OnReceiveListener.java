package com.houxya.bthelper.i;

import android.os.Bundle;

/**
 * Created by Basil on 2017/2/18.
 */

public interface OnReceiveListener extends IErrorListener, OnConnectionLostListener {
    void onNewLine(Bundle dataBundle);
}
