package pku.sei.echo.launcherlogger;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);

        if (lpparam.packageName.equals("com.android.launcher3")) {
            String[] mName = {"onClickAppShortcut", "onClickFolderIcon", "startAppShortcutOrInfoActivity"};
            for (String method : mName)
                XposedHelpers.findAndHookMethod("com.android.launcher3.Launcher", lpparam.classLoader, method, View.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        mLog(param, "Launch_S1," + ((TextView) param.args[0]).getText());
                        super.beforeHookedMethod(param);
                    }
                });
        }
        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "performCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        mLog(param, "Launch_S4,");
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mLog(param, "Launch_S5,");
                        super.afterHookedMethod(param);
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "performResume",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        mLog(param, "Launch_S6,");
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mLog(param, "Launch_S7,");
                        super.afterHookedMethod(param);
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                "android.view.ViewRootImpl",
                lpparam.classLoader,
                "performTraversals",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // mLog(param, "Launch_S8," + Log.getStackTraceString(new Exception()));
                        mLog(param, "Launch_S8,");
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mLog(param, "Launch_S9,");
                        super.afterHookedMethod(param);
                    }
                }
        );
        if (lpparam.packageName.equals("android")) {
            Class PRecord = XposedHelpers.findClass("com.android.server.am.ProcessRecord", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(
                    "com.android.server.am.ActivityManagerService",
                    lpparam.classLoader,
                    "startProcessLocked",
                    PRecord,
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    String[].class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            mLog(param, "Launch_S2," + param.args[2]);
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(
                    "com.android.server.am.ActivityRecord",
                    lpparam.classLoader,
                    "reportLaunchTimeLocked",
                    long.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            mLog(param, "Launch_S10,");
                            super.afterHookedMethod(param);
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(
                    "android.app.Activity",
                    lpparam.classLoader,
                    "onCreate",
                    Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            mLog(param, "Launch_S4,");
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            mLog(param, "Launch_S5,");
                            super.afterHookedMethod(param);
                        }
                    }
            );

            // in frameworks/base/core/java/android/app/ActivityThread.java
            // attach(boolean system)
            /*XposedHelpers.findAndHookMethod(
                    "android.app.ActivityThread",
                    lpparam.classLoader,
                    "attach",
                    boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            mLog(param, "Launch_S3," + param.args[0]);
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    }
            );*/

            // in framework/base/core/java/com/android/internal/os/Zygote.java
            // public static int forkAndSpecialize(int uid, int gid, int[] gids, int debugFlags,
            //      int[][] rlimits, int mountExternal, String seInfo, String niceName, int[] fdsToClose,
            //      String instructionSet, String appDataDir)
            /*XposedHelpers.findAndHookMethod(
                    "com.android.internal.os.Zygote",
                    lpparam.classLoader,
                    "forkAndSpecialize",
                    int.class, int.class, int[].class, int.class, int[][].class, int.class, String.class, String.class, int[].class, String.class, String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            mLog(param, "Launch_S3," + param.args[7]);
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    }
            );*/
        }
    }
    private void mLog(XC_MethodHook.MethodHookParam param, String str) {
        XposedBridge.log(SystemClock.elapsedRealtime() + "," + param.method.getName() + "," + str);
    }
}
