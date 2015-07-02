package com.ht.jellybean.ui.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import com.ht.jellybean.R;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by annuo on 2015/6/28.
 */
public class AboutActivity extends SwipeBackActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //actionbar图标可以点击
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onContextItemSelected(item);
    }
}