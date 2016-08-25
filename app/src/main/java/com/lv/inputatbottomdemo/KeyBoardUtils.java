package com.lv.inputatbottomdemo;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class KeyBoardUtils {

    public static void attach(final Activity activity, final KeyBoardListener keyBoardListener) {
        final ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        final int statusBarHeight = outRect.top;
        contentView.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public int fullDisplayHeight;
                    boolean isKeyboardVisible;

                    @Override
                    public void onGlobalLayout() {

                        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
                        View userRootView = contentView.getChildAt(0);
                        //计算userRootView的高度
                        Rect r = new Rect();
                        userRootView.getWindowVisibleDisplayFrame(r);
                        int displayHeight = r.bottom - r.top;

                        if (fullDisplayHeight == 0) {
                            fullDisplayHeight = displayHeight;
                            return;
                        }
                        int keyboardHeight = Math.abs(displayHeight - fullDisplayHeight);
                        if (keyboardHeight == 0) {
                            if (isKeyboardVisible) {
                                isKeyboardVisible = false;
                                if (keyBoardListener != null)
                                    keyBoardListener.keyboardShowingChanged(isKeyboardVisible);
                            }
                            return;
                        }


                        //当前变化由，非全屏到全屏导致，此时应该更新fullDisplayHeight
                        if (keyboardHeight == statusBarHeight) {
                            fullDisplayHeight = displayHeight;
                            return;
                        }

                        if (keyBoardListener != null)
                            keyBoardListener.keyboardHeight(keyboardHeight);

                        if (!isKeyboardVisible) {
                            isKeyboardVisible = true;
                            if (keyBoardListener != null)
                                keyBoardListener.keyboardShowingChanged(isKeyboardVisible);

                        }


                    }
                });
    }

    public interface KeyBoardListener {
        void keyboardHeight(int keyboardHeight);

        void keyboardShowingChanged(boolean visible);

    }

    public static void editAtBottom(final Activity activity) {
        final boolean[] keyboardVisible = {false};
        attach(activity, new KeyBoardListener() {
            @Override
            public void keyboardHeight(int keyboardHeight) {

            }

            @Override
            public void keyboardShowingChanged(boolean visible) {
                keyboardVisible[0] = visible;
            }
        });
        final ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                //r.top 是状态栏高度
                int screenHeight = contentView.getRootView().getHeight();
                int softHeight = screenHeight - r.bottom;
                contentView.scrollTo(0, keyboardVisible[0] ? softHeight : 0);
            }
        });
    }


}
