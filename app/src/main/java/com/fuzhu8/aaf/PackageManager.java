package com.fuzhu8.aaf;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * android package manager
 * Created by zhkl0228 on 2017/12/6.
 */

public interface PackageManager {

    PackageInfo getPackageInfo(String packageName, int flags) throws android.content.pm.PackageManager.NameNotFoundException;

    List<ResolveInfo> queryIntentActivities(Intent intent, int flags);

}
