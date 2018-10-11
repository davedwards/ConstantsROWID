package com.sandlot.dedwards.stackviews;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ConstantsBrowser extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null){
            getSupportFragmentManager().beginTransaction().
                    add(android.R.id.content, new ConstantsFragment()).commit();
        }
    }
}
