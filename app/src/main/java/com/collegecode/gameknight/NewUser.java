package com.collegecode.gameknight;

import android.os.Bundle;
import android.view.View;
/**
 * Created by saurabh on 14-11-06.
 */
public class NewUser extends BaseActivity{

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        showBack(false);
    }
}
