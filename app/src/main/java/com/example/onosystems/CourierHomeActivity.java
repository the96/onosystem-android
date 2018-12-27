package com.example.onosystems;

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

            if (id == R.id.mapView) {
                showMapActivity(); //マップ画面へ
            } else if (id == R.id.toggle1 || id == R.id.toggle2 || id == R.id.toggle3) {
                toggleVisibleFromReceivable();
            } else if (id == R.id.receivableSelect) {
                receivableSelect();
            } else if (id == R.id.sortTime) {
                sortTime();
            } else if  (id == R.id.sortDistance) {
                sortDistance();
            }
            return super.onOptionsItemSelected(item);
        }

        public void sortTime() {
        }

        public void sortDistance() {
        }

        public void showMapActivity() {
        }
}
