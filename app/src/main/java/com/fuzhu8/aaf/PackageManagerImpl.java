package com.fuzhu8.aaf;

import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * package manager impl
 * Created by zhkl0228 on 2017/12/6.
 */
class PackageManagerImpl extends AbstractPackageManager implements PackageManager {
    PackageManagerImpl(IPackageManager pm, Method getPackageInfo, Method queryIntentActivities) {
        super(pm, getPackageInfo, queryIntentActivities);
    }

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws android.content.pm.PackageManager.NameNotFoundException {
        try {
            Object[] values = new Object[getPackageInfo.getParameterTypes().length];
            values[0] = packageName;
            values[1] = flags;
            if (values.length > 2) {
                values[2] = 0;
            }
            PackageInfo packageInfo = (PackageInfo) getPackageInfo.invoke(pm, values);
            if (packageInfo == null) {
                throw new android.content.pm.PackageManager.NameNotFoundException("find package failed: " + packageName);
            }
            return packageInfo;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        try {
            Object[] values = new Object[queryIntentActivities.getParameterTypes().length];
            values[0] = intent;
            values[1] = null;
            values[2] = flags;
            if (values.length > 3) {
                values[3] = 0;
            }
            List<?> resolveInfos = (List<?>) queryIntentActivities.invoke(pm, values);
            List<ResolveInfo> list = new ArrayList<>(resolveInfos.size());
            for (Object obj : resolveInfos) {
                list.add((ResolveInfo) obj);
            }
            return list;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
