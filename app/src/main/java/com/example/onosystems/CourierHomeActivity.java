package com.example.onosystems;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CourierHomeActivity extends HomeActivity {

        //toolbarのアイテム表示
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.tool_options_courier, menu);
            return super.onCreateOptionsMenu(menu);
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            switch(id) {
                case R.id.mapView:
                    showMapActivity();
                    break;
                case R.id.toggle1:
                case R.id.toggle2:
                case R.id.toggle3:
                    toggleVisibleFromReceivable();
                    break;
                case R.id.receivableSelect:
                    receivableSelect();
                    break;
                case R.id.sortTime:
                    sortTime();
                    break;
                case R.id.sortDistance:
                    sortDistance();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        public void sortTime() {
        }

        public void sortDistance() {
        }

        public void showMapActivity() {
            // Intent intent = new Intent(getApplication(), CourierMapActivity.class);  // 遷移先指定
            // startActivity(intent);// CourierMapActivityに遷移
        }
}