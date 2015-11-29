/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.CommonUtils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by sanyinchen on 15/11/26.
 */
public class LogUtils {
    private static int[] disPlay;

    public static int diptopx(Context context, float dipValue) {
        if (context == null) {
            return 0;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int[] getDevDispplay(Context context) {
        if (disPlay == null) {
            disPlay = new int[2];
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            disPlay[0] = dm.widthPixels;
            disPlay[1] = dm.heightPixels;
        }
        return disPlay;
    }
}
