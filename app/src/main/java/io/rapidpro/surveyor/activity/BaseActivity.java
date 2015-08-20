package io.rapidpro.surveyor.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import io.rapidpro.surveyor.R;
import io.rapidpro.surveyor.Surveyor;
import io.rapidpro.surveyor.SurveyorIntent;
import io.rapidpro.surveyor.data.Flow;
import io.rapidpro.surveyor.data.Org;
import io.rapidpro.surveyor.net.RapidProService;
import io.realm.Realm;

public class BaseActivity extends AppCompatActivity {

    // our logging tag
    private static String TAG = "Surveyor";

    private Org m_org;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    protected void onResume() {
        super.onResume();
        Surveyor.LOG.d(getClass().getSimpleName() + ".onResume()");
    }

    protected void onPause() {
        super.onPause();
        Surveyor.LOG.d(getClass().getSimpleName() + ".onPause()");
    }

    public SharedPreferences getPreferences() {
        return Surveyor.getPreferences(this);
    }

    public void saveString(int key, String value) {
        SharedPreferences.Editor editor = Surveyor.getPreferences(this).edit();
        editor.putString(getString(key), value);
        editor.apply();
    }

    public void saveInt(int key, int value) {
        SharedPreferences.Editor editor = Surveyor.getPreferences(this).edit();
        editor.putInt(getString(key), value);
        editor.apply();
    }

    public String getPreferenceString(int key, String def) {
        return getPreferences().getString(getString(key), def);
    }

    public int getPreferenceInt(int key, int def) {
        return getPreferences().getInt(getString(key), def);
    }

    public Surveyor getSurveyor() {
        return (Surveyor)getApplication();
    }

    public Realm getRealm() {
        return getSurveyor().getRealm();
    }

    public RapidProService getRapidProService() {
        return getSurveyor().getRapidProService();
    }

    public Org getOrg() {
        if (m_org == null) {
            int orgId = getIntent().getIntExtra(SurveyorIntent.EXTRA_ORG_ID, 0);
            m_org = getRealm().where(Org.class).equalTo("id", orgId).findFirst();
        }
        return m_org;
    }

    public Flow getFlow(String id) {
        return getRealm().where(Flow.class).equalTo("id", id).findFirst();
    }

    public Intent getIntent(Activity from, Class to) {
        Intent intent = new Intent(from, to);
        Org org = getOrg();
        if (org !=null) {
            intent.putExtra(SurveyorIntent.EXTRA_ORG_ID, org.getId());
        }
        return intent;
    }
}
