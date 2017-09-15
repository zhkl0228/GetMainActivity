package com.fuzhu8.aaf;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GetMainActivity
 * Created by zhkl0228 on 2017/9/16.
 */

public class GetMainActivity {

    public static void main(String[] args) {
        try {
            if(args.length < 1) {
                System.err.println(json(1, "Error: usage " + GetMainActivity.class.getCanonicalName() + " packageName"));
                return;
            }
            final String packageName = args[0];

            Class<?> IPackageManager$Stub = Class.forName("android.content.pm.IPackageManager$Stub");
            Method asInterface = IPackageManager$Stub.getDeclaredMethod("asInterface", IBinder.class);
            IPackageManager pm = (IPackageManager) asInterface.invoke(null, ServiceManager.getService("package"));
            Method getPackageInfo = null;
            Method queryIntentActivities = null;
            for (Method method : IPackageManager.class.getDeclaredMethods()) {
                if ("getPackageInfo".equals(method.getName())) {
                    getPackageInfo = method;
                } else if ("queryIntentActivities".equals(method.getName())) {
                    queryIntentActivities = method;
                } else if (getPackageInfo != null && queryIntentActivities != null) {
                    break;
                }
            }
            if (getPackageInfo == null) {
                throw new IllegalStateException("find method getPackageInfo from class android.content.pm.IPackageManager failed.");
            }
            if (queryIntentActivities == null) {
                throw new IllegalStateException("find method queryIntentActivities from class android.content.pm.IPackageManager failed.");
            }

            Object[] values = new Object[getPackageInfo.getParameterTypes().length];
            values[0] = packageName;
            values[1] = PackageManager.GET_ACTIVITIES;
            if (values.length > 2) {
                values[2] = 0;
            }
            PackageInfo packageInfo = (PackageInfo) getPackageInfo.invoke(pm, values);
            if (packageInfo == null) {
                throw new IllegalStateException("find packageInfo failed for: " + packageName);
            }

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(packageName);
            values = new Object[queryIntentActivities.getParameterTypes().length];
            values[0] = intent;
            values[1] = null;
            values[2] = PackageManager.GET_ACTIVITIES;
            if (values.length > 3) {
                values[3] = 0;
            }
            List<?> resolveInfos = (List<?>) queryIntentActivities.invoke(pm, values);
            Set<String> main = new HashSet<>(1);
            for (Object obj : resolveInfos) {
                ResolveInfo resolveInfo = (ResolveInfo) obj;
                main.add(resolveInfo.activityInfo.name);
            }

            Set<String> activities = new HashSet<>();
            for (ActivityInfo info : packageInfo.activities) {
                if (!main.contains(info.name)) {
                    activities.add(info.name);
                }
            }

            JSONObject object = new JSONObject();
            object.put("code", 0);
            object.put("main", new JSONArray(main));
            object.put("activities", new JSONArray(activities));

            System.out.println(object.toString());
        } catch(RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(json(3, "Error: " + e.getMessage()));
        }
    }

    private static String json(int code, String msg) {
        try {
            JSONObject object = new JSONObject();
            object.put("code", code);
            object.put("msg", msg);
            return object.toString();
        } catch(JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
