package com.dixon.phoneshare.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.dixon.phoneshare.R;
import com.dixon.tools.CustomDialog;
import com.dixon.tools.ScreenUtil;

public class DialogUtil {

    public static void showTipDialog(Context context, String desc, View.OnClickListener onSureListener) {
        if (!canShow(context)) {
            return;
        }
        CustomDialog dialog = new CustomDialog.Builder(context)
                .view(R.layout.dialog_tip)
                .style(R.style.dialog)
                .isCancelOnTouchOutSide(true)
                .windowAnimStyle(R.style.dialogAnim)
                .widthPx(ScreenUtil.dpToPxInt(context, 280))
//                .heightPx(ScreenUtils.dpToPxInt(context, 196))
                .addViewOnClick(R.id.tvOk, onSureListener)
                .build();
        ((TextView) dialog.getView().findViewById(R.id.tvTip)).setText(desc);
        show(dialog);
    }

    private static void show(Dialog dialog) {
        if (dialog != null) {
            dialog.show();
        }
    }


    private static boolean canShow(Context context) {
        if (context instanceof Activity && Looper.myLooper() == Looper.getMainLooper()) {
            if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                return true;
            }
        }
        return false;
    }
}
