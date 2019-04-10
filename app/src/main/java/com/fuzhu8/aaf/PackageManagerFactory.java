package com.fuzhu8.aaf;

import android.annotation.SuppressLint;
import android.content.pm.IPackageManager;
import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * package manager factory
 * Created by zhkl0228 on 2017/12/6.
 */

class PackageManagerFactory {

    @SuppressLint("PrivateApi")
    static PackageManager createPackageManager() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> IPackageManager$Stub = Class.forName("android.content.pm.IPackageManager$Stub");
        Method asInterface = IPackageManager$Stub.getDeclaredMethod("asInterface", IBinder.class);
        IPackageManager pm = (IPackageManager) asInterface.invoke(null, getService("package"));

        Method getPackageInfo = null;
        Method queryIntentActivities = null;
        for (Method method : IPackageManager.class.getDeclaredMethods()) {
            if ("getPackageInfo".equals(method.getName())) {
                getPackageInfo = method;
            } else if ("queryIntentActivities".equals(method.getName())) {
                queryIntentActivities = method;
            }
            if (getPackageInfo != null && queryIntentActivities != null) {
                break;
            }
        }
        if (getPackageInfo == null) {
            throw new IllegalStateException("find method getPackageInfo from class android.content.pm.IPackageManager failed.");
        }
        if (queryIntentActivities == null) {
            throw new IllegalStateException("find method queryIntentActivities from class android.content.pm.IPackageManager failed.");
        }

        return new PackageManagerImpl(pm, getPackageInfo, queryIntentActivities);
    }

    @SuppressLint("PrivateApi")
    static IBinder getService(String name) {
        try {
            Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
            Method getServer = ServiceManager.getDeclaredMethod("getService", String.class);
            IBinder binder = (IBinder) getServer.invoke(null, name);
            return binder;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
