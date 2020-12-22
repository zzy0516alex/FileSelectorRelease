package com.z.fileselectorlib;

import android.os.Environment;

public class BasicParams {
    public static final String BasicPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    private String RootPath;

    public String getRootPath() {
        return RootPath;
    }

    public void setRootPath(String rootPath) {
        RootPath = rootPath;
    }

    public static BasicParams getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static BasicParams getInitInstance() {
        BasicParams params= InstanceHolder.INSTANCE;
        params.setRootPath(BasicPath);
        return params;
    }

    private static final class InstanceHolder {
        private static final BasicParams INSTANCE = new BasicParams();
    }
}
