package com.fuzhu8.aaf;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
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

            PackageManager packageManager = PackageManagerFactory.createPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_ACTIVITIES);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(packageName);
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, android.content.pm.PackageManager.GET_ACTIVITIES);
            Set<String> main = new HashSet<>(1);
            for (ResolveInfo resolveInfo : resolveInfos) {
                main.add(resolveInfo.activityInfo.name);
            }

            Set<String> activities = new HashSet<>();
            for (ActivityInfo info : packageInfo.activities) {
                if (!main.contains(info.name)) {
                    activities.add(info.name);
                }
            }

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

            JSONObject object = new JSONObject();
            object.put("code", 0);
            object.put("main", new JSONArray(main));
            object.put("activities", new JSONArray(activities));

            try {
                if (addAssetPath != null && getResourceText != null && ensureStringBlocks != null &&
                        (int) addAssetPath.invoke(assetManager, packageInfo.applicationInfo.publicSourceDir) != 0) {
                    ensureStringBlocks.invoke(assetManager);
                    CharSequence label = (CharSequence) getResourceText.invoke(assetManager, packageInfo.applicationInfo.labelRes);
                    if (label != null) {
                        object.put("label", label.toString());
                    }
                }
            } catch(Throwable ignored) {}

            System.out.println(object.toString());
        } catch(RuntimeException e) {
            throw e;
        } catch (Exception e) {
            // e.printStackTrace();
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
