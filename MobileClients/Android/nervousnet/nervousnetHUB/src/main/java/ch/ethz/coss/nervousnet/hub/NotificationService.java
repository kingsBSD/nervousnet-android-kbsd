package ch.ethz.coss.nervousnet.hub;

/**
 * Created by grg on 17/08/16.
 */

        import android.accessibilityservice.AccessibilityService;
        import android.accessibilityservice.AccessibilityServiceInfo;
        import android.content.Intent;
        import android.content.pm.ApplicationInfo;
        import android.content.pm.PackageManager;
        import android.content.pm.PackageManager.NameNotFoundException;
        import android.support.v4.content.LocalBroadcastManager;
        import android.view.accessibility.AccessibilityEvent;
        import java.util.ArrayList;
        import java.util.List;

/**
 * A cheap trick to collect notifications from other apps, pretend to be an accessibility service.
 */
public class NotificationService extends AccessibilityService {

    @Override
    public void onServiceConnected() {

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> netEnabledPackageNames = new ArrayList<String>();

        // Assume we only care about apps that have net access.
        for (ApplicationInfo appInfo : packages) {
            String[] permissions;
            try {
                permissions = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
                if (permissions != null) {
                    for (String permission : permissions) {
                        if (permission.equals("android.permission.INTERNET")) {
                            netEnabledPackageNames.add(appInfo.packageName);
                            break;
                        }
                    }
                }
            } catch (NameNotFoundException e) {
            }

        }

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.packageNames = netEnabledPackageNames.toArray(new String[netEnabledPackageNames.size()]);
        info.feedbackType = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Intent intent = new Intent("nervousnet-notification-sensor-event");
        intent.putExtra("appName", (String) event.getPackageName());
        intent.putExtra("timestamp",System.currentTimeMillis());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void onInterrupt() {
    }

}