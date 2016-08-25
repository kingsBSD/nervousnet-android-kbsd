/*******************************************************************************
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.hub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.ui.SplashActivity;

/**
 * This class allows the app to display the Terms of Use Dialog. Conditions for
 * showing the Terms of Use Dialog: - New Install - New Version Update - User
 * not accepted the Terms.
 */
public class TermsOfUse {

    private static String LOG_TAG = TermsOfUse.class.getSimpleName();

    private Activity mActivity;
    private String TERMS_PREFIX = "TERMS_";

    public TermsOfUse(Activity context) {
        this.mActivity = context;
    }

    public void showTerms(boolean showApproveButtons) {

        final String versionKey;
        PackageInfo pkgInfo = null;
        try {

            pkgInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);

        } catch (NameNotFoundException e) {
            Log.e(LOG_TAG,
                    "TermsOfUse : Version Code could not be retrieved for checking if Terms of Use has been shown.");
            e.printStackTrace();
        }
        versionKey = TERMS_PREFIX + pkgInfo.versionCode;
        boolean termsShownFlag = PreferenceManager.getDefaultSharedPreferences(mActivity).getBoolean(versionKey, false);

        if (!termsShownFlag && pkgInfo != null || !showApproveButtons) {
            // Show the Eula
            String title = mActivity.getString(R.string.app_name) + " v" + pkgInfo.versionName;

            // Includes the updates as well so users know what changed.
            String message = "\n\n" + mActivity.getString(R.string.terms_of_use);

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle(title)
                    .setMessage(Html.fromHtml(message));
                    if(showApproveButtons){
                        builder.setPositiveButton(R.string.button_accept_label, new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mActivity)
                                        .edit();
                                editor.putBoolean(versionKey, true);
                                editor.commit();
                                dialogInterface.dismiss();
                                // Intent intent = new Intent(mActivity,
                                // SplashActivity.class);
                                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                //
                                // mActivity.startActivity(intent);
                                ((SplashActivity) mActivity).startThread();

                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Close the activity as they have declined the EULA
                                ((SplashActivity) mActivity).finish();

                            }

                        });
                        builder.setCancelable(false);
                    } else {
                        builder.setPositiveButton("OK", new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                    }


            AlertDialog alert = builder.create();
            alert.show();

            alert.getWindow().getAttributes();

            TextView textView = (TextView) alert.findViewById(android.R.id.message);
            textView.setTextSize(12);
        } else {
            // Intent intent = new Intent(mActivity, MainActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //
            // mActivity.startActivity(intent);
            // mActivity.finish();

            ((SplashActivity) mActivity).startThread();

        }

    }

}
