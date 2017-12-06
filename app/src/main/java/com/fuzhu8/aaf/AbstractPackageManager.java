package com.fuzhu8.aaf;

import android.content.pm.IPackageManager;

import java.lang.reflect.Method;

/**
 * abstract package manager
 * Created by zhkl0228 on 2017/12/6.
 */
abstract class AbstractPackageManager implements PackageManager {

    final IPackageManager pm;
    final Method getPackageInfo;
    final Method queryIntentActivities;

    AbstractPackageManager(IPackageManager pm, Method getPackageInfo, Method queryIntentActivities) {
        this.pm = pm;
        this.getPackageInfo = getPackageInfo;
        this.queryIntentActivities = queryIntentActivities;
    }
}
