package com.fuzhu8.aaf;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.os.Build;

import java.lang.reflect.Constructor;
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
    public String loadLabel(ApplicationInfo applicationInfo) {
        try {
            Constructor<AssetManager> constructor = AssetManager.class.getConstructor();
            constructor.setAccessible(true);
            AssetManager assetManager = constructor.newInstance();
            Method addAssetPath = null;
            Method getResourceText = null;
            Method ensureStringBlocks = null;
            for (Method method : AssetManager.class.getDeclaredMethods()) {
                if ("addAssetPath".equals(method.getName())) {
                    addAssetPath = method;
                    method.setAccessible(true);
                } else if ("getResourceText".equals(method.getName())) {
                    getResourceText = method;
                    method.setAccessible(true);
                } else if ("ensureStringBlocks".equals(method.getName())) {
                    ensureStringBlocks = method;
                    method.setAccessible(true);
                }
                if (addAssetPath != null && getResourceText != null && ensureStringBlocks != null) {
                    break;
                }
            }

            if (addAssetPath != null && getResourceText != null && ensureStringBlocks != null &&
                    (int) addAssetPath.invoke(assetManager, applicationInfo.publicSourceDir) != 0) {
                ensureStringBlocks.invoke(assetManager);
                CharSequence label = (CharSequence) getResourceText.invoke(assetManager, applicationInfo.labelRes);
                if (label != null) {
                    return label.toString();
                }
            }
            return null;
        } catch (Throwable ignored) {
            return null;
        }
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
            List<?> resolveInfos = null;
            if (Build.VERSION.SDK_INT >= 24) {
                Object parceledListSlice = queryIntentActivities.invoke(pm, values);
                Class clazz = parceledListSlice.getClass();
                Method getList = clazz.getMethod("getList");
                resolveInfos = (List<?>) getList.invoke(parceledListSlice);
            } else {
                resolveInfos = (List<?>) queryIntentActivities.invoke(pm, values);
            }
            List<ResolveInfo> list = new ArrayList<>(resolveInfos.size());
            for (Object obj : resolveInfos) {
                list.add((ResolveInfo) obj);
            }
            return list;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
