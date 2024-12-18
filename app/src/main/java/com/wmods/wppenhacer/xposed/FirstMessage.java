package com.wmods.wppenhacer.xposed;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

import androidx.annotation.NonNull;

import com.wmods.wppenhacer.BuildConfig;
import com.wmods.wppenhacer.xposed.utils.DesignUtils;
import com.wmods.wppenhacer.xposed.utils.ResId;

import java.util.LinkedHashSet;
import java.util.HashSet;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class FirstMessage {

    public static HashSet<HomeMenuItem> menuItems = new LinkedHashSet<>();

    public FirstMessage(@NonNull ClassLoader classLoader, @NonNull XSharedPreferences preferences) {
        super(classLoader, preferences);
    }

    @Override
    public void doHook() throws Throwable {
        hookMenu();
        menuItems.add(this::InsertOpenWae); // Menambahkan menu custom
    }

    private void InsertOpenWae(Menu menu, Activity activity) {
        var itemMenu = menu.add(0, 0, 9999, " " + activity.getString(ResId.string.app_name));
        var iconDraw = DesignUtils.getDrawableByName("ic_settings");
        iconDraw.setTint(0xff8696a0);
        itemMenu.setIcon(iconDraw);
        itemMenu.setOnMenuItemClickListener(item -> {
            Intent intent = activity.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } else {
                // Tambahkan fallback jika intent null (opsional)
            }
            return true;
        });
    }

    private void hookMenu() {
        XposedHelpers.findAndHookMethod("com.whatsapp.Conversation", classLoader, "onCreateOptionsMenu", Menu.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                var menu = (Menu) param.args[0];
                var activity = (Activity) param.thisObject;
                for (var menuItem : menuItems) {
                    menuItem.addMenu(menu, activity);
                }
            }
        });
    }

    public interface ConversationMenuItem {
        void addMenu(Menu menu, Activity activity);
    }
}
