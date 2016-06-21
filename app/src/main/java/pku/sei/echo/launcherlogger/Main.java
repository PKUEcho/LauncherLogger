package pku.sei.echo.launcherlogger;


import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.*;

import org.apache.commons.lang3.StringUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class Main implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);
        hookPackageManager(lpparam);
        hookIntentAction(lpparam);
        hookActivityLifecycle(lpparam);
        hookBroadcast(lpparam);
    }

    public void hookPackageManager(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals("android"))
            return;
        XposedHelpers.findAndHookMethod(
                "com.android.server.pm.PackageManagerService",
                lpparam.classLoader,
                "getPackageInfo",
                String.class, int.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String targetPackage = (String) param.args[0];
                        int flags = (int) param.args[1];
                        int userId = (int) param.args[2];
                        formatPrt(lpparam, "getPackageInfo",
                                "{'targetPackage': '" + targetPackage + "', "
                                        + "'flags': " + flags + ", "
                                        + "'userId': " + userId + "}");
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
        XposedHelpers.findAndHookMethod(
                "com.android.server.pm.PackageManagerService",
                lpparam.classLoader,
                "getInstalledPackages",
                int.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        formatPrt(lpparam, "getInstalledPackages", "");
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
    }

    public void hookIntentAction(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> cls = XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(cls, "startActivity", Intent.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.args[0];
                formatPrt(lpparam, "startActivity", "'" + intent.toUri(0) + "'");
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod(cls, "startService", Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.args[0];
                formatPrt(lpparam, "startService", "'" + intent.toUri(0) + "'");
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod(cls, "bindService", Intent.class, ServiceConnection.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.args[0];
                formatPrt(lpparam, "bindService", "'" + intent.toUri(0) + "'");
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    public void hookActivityLifecycle(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "onCreate",
                Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        formatPrt(lpparam, "onCreate", "'");
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
        String[] hook_methods = {"onStart", "onRestart", "onResume", "onPause", "onStop", "onDestroy"};
        for (final String method: hook_methods) {
            XposedHelpers.findAndHookMethod(
                    "android.app.Activity",
                    lpparam.classLoader,
                    method,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            formatPrt(lpparam, method, "'");
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    });
        }
    }

    public void hookBroadcast(final XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> cls = XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader);
        XposedBridge.hookAllMethods(cls, "sendBroadcast", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.args[0];
                formatPrt(lpparam, "sendBroadcast", "{'IntentUri': '" + intent.toUri(0) + "'}");
//                formatPrt(lpparam, "broadcastIntent",
//                        "{'IntentUri': '" + intent.toUri(0) + "', "
//                                + "'requestCode': " + param.args[4] + ", "
//                                + "'resultData': '" + param.args[5] + "', "
//                                + "'requiredPerm': " + param.args[7] + "'}");
                        super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });

        XposedBridge.hookAllMethods(cls, "registerReceiver", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                BroadcastReceiver receiver = (BroadcastReceiver) param.args[0];
                IntentFilter filter = (IntentFilter) param.args[1];
                String actions = "";
                for (int i = 0; i < filter.countActions(); ++ i)
                    actions += (filter.getAction(i) + ",");
                String categories = "";
                for (int i = 0; i < filter.countCategories(); ++ i)
                    categories += (filter.getCategory(i) + ",");
                formatPrt(lpparam, "registerReceiver",
                        "{'action': '" + actions + "', "
                                + "'categories': " + categories + "'}");
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    private String intent2desc(Intent intent) {
        return "{'package': '" + intent.getComponent().getPackageName() + "', "
                + "'class': '" + intent.getComponent().getClassName() + "', "
                + "'action': '" + intent.getAction() + "', "
                + "'type': '" + intent.getType() + "', "
                + "'categories': '" + StringUtils.join(intent.getCategories().toArray()) + "', "
                + "'Uri': '" + intent.toUri(0) + "'";
    }

    private String getProcessName(Context ctx) {
        String currentProcName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses())
        {
            if (processInfo.pid == pid)
            {
                currentProcName = processInfo.processName;
                break;
            }
        }
        return currentProcName;
    }

    private void formatPrt(final XC_LoadPackage.LoadPackageParam lpparam, String action, String desc) {
        Context context = AndroidAppHelper.currentApplication();
        XposedBridge.log("{'package': '" + context.getPackageName() + "', "
                + "'process': '" + getProcessName(context) + "', "
                + "'method': '" + action + "', "
                + "'desc': " + desc);
    }
}
