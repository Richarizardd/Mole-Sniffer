package org.centum.android.molescan;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.centum.android.molescan.fragments.ImageFragment;
import org.centum.android.molescan.fragments.IntroFragment;
import org.centum.android.molescan.network.ParseInterface;
import org.centum.android.molescan.utils.Fonts;


public class MainActivity extends Activity {

    private static final String CURRENT_FRAGMENT_TAG = "currFragment";
    private static final int INTRO_FRAGMENT_ID = 0;
    private static final int IMAGE_FRAGMENT_ID = 1;

    private Fragment fragments[] = new Fragment[]{new IntroFragment(), new ImageFragment()};    // array to manage fragments
    private int currentFragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fonts.loadFonts(this);
        ParseInterface.init(this);

        setContentView(R.layout.activity_main);

        getActionBar().setHomeButtonEnabled(true);
        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_FRAGMENT_TAG)) {
            switchToFragment(savedInstanceState.getInt(CURRENT_FRAGMENT_TAG, 0));
        } else {
            switchToFragment(INTRO_FRAGMENT_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (currentFragment != -1)
            bundle.putInt(CURRENT_FRAGMENT_TAG, currentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (currentFragment > 0)
                    switchToFragment(currentFragment - 1);
                return true;
           /* case R.id.action_check_for_comments:
                checkForComments();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == INTRO_FRAGMENT_ID) {
            super.onBackPressed();
        } else {
            switchToFragment(currentFragment - 1);
        }
    }

    private void switchToFragment(int i) {
        if (i != currentFragment)
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, 0, 0)
                    .replace(R.id.fragment_container, fragments[i])
                    .commit();
        getActionBar().setDisplayHomeAsUpEnabled(i != INTRO_FRAGMENT_ID);
        currentFragment = i;
    }

    public void switchToImageFragment() {
        switchToFragment(IMAGE_FRAGMENT_ID);
    }

}// end MainActivity

