package com.abook23.utils.task;

/**
 * Created by abook23 on 2015/9/7.
 */
public interface PauseOnScrollHandler {
    boolean supportPause();

    boolean supportResume();

    boolean supportCancel();

    void pause();

    void resume();

    void cancel();

    boolean isPaused();

    boolean isCancelled();
}
