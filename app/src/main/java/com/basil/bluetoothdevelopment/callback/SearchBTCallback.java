package com.basil.bluetoothdevelopment.callback;

/**
 * Created by Basil on 2017/2/20.
 */

public interface SearchBTCallback {

    void onSearchSuccess(String deviceInfo);
    void onSearchFailed();
    void onSearchCompleted();
}
