package com.example.onosystems;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CustomerHomeActivity extends HomeActivity {

        //toolbarのアイテム表示
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.tool_options_customer, menu);
            return super.onCreateOptionsMenu(menu);
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            switch(id) {
                case R.id.toggle1:
                case R.id.toggle2:
                case R.id.toggle3:
                    toggleVisibleFromReceivable();
                    break;
                case R.id.receivableSelect:
                    receivableSelect();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }
}