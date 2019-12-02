package com.techespo.tsafe;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {

    public static ProgressDialog showProgress(Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("loading");
        pd.show();
        return pd;
    }
}
