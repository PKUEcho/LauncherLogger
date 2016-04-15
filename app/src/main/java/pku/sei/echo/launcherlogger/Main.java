package pku.sei.echo.launcherlogger;

import android.os.SystemClock;
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
            String[] mName = {"onClickAppShortcut", "onClickFolderIcon", "onClickAllAppsButton", "startAppShortcutOrInfoActivity"};
            for (String method : mName)
                findAndHookMethod("com.android.launcher3.Launcher", lpparam.classLoader, method, View.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        mLog(param, "Launch_S1," + ((TextView) param.args[0]).getText());
                        super.beforeHookedMethod(param);
                    }
                });
        }

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
        }
    }
    private void mLog(XC_MethodHook.MethodHookParam param, String str) {
        XposedBridge.log(SystemClock.elapsedRealtime() + "," + param.method.getName() + "," + str);
    }
}
