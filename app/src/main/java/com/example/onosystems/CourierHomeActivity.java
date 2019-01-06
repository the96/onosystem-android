package com.example.onosystems;

import android.content.Intent;
import android.view.MenuItem;

public class CourierHomeActivity extends HomeActivity {

    @Override
    public void setUserOptions() {
        toolBarLayout = R.menu.tool_options_courier;
        detailActivity = CourierDeliveryDetail.class;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.mapView:
                showMapActivity();
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
        //BubbleSort
        for (int i = 0; i < deliveryInfo.size()-1; i++) {
            for (int j = 1; j < deliveryInfo.size(); j++) {
                if (deliveryInfo.get(i).getTime() > deliveryInfo.get(j).getTime()) {
                    Delivery tmp = deliveryInfo.get(i);
                    deliveryInfo.set(i, deliveryInfo.get(j));
                    deliveryInfo.set(j, tmp);
                }
            }
        }

        reloadDeliveries();
    }

    public void sortDistance() {

    }

    public void showMapActivity() {
        //Intent intent = new Intent(getApplication(), CourierMapActivity.class);  // 遷移先指定
        //intent.putExtra("itemInfo", list.toString());
        // startActivity(intent);// CourierMapActivityに遷移
    }

}