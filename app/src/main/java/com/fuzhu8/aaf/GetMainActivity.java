package com.fuzhu8.aaf;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

            JSONObject object = new JSONObject();
            object.put("code", 0);
            object.put("main", new JSONArray(main));
            object.put("activities", new JSONArray(activities));

            String label = packageManager.loadLabel(packageInfo.applicationInfo);
            if (label != null) {
                object.put("label", label);
            }

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
