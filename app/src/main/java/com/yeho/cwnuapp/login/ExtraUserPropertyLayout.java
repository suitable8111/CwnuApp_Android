package com.yeho.cwnuapp.login;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.yeho.cwnuapp.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KimDaeho on 16. 1. 5..
 */
public class ExtraUserPropertyLayout extends FrameLayout {
    private  static final String NAME_KEY = "nick";
    private  static final String PHONENUM_KEY = "phonenum";
    static String KAKAO_USER_NAME = null;


    private EditText nickname;
    private EditText phoneNumber;

    public ExtraUserPropertyLayout(Context context) {
        super(context);
    }
    public ExtraUserPropertyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExtraUserPropertyLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        final View view = inflate(getContext(), R.layout.layout_usermgmt_extra_user_property, this);

        phoneNumber = (EditText) view.findViewById(R.id.phone);
        nickname = (EditText) view.findViewById(R.id.nickname);

        final Button buttonUpdateProfile = (Button) findViewById(R.id.kakao_user_update_button_on);

    }

    public HashMap<String, String> getProperties(){
        final String nickNameValue = nickname.getText().toString();
        final String phoneNumValue = phoneNumber.getText().toString();



        HashMap<String, String> properties = new HashMap<String, String>();
        if(nickNameValue != null)
            properties.put(NAME_KEY, nickNameValue);
        if(phoneNumValue != null)
            properties.put(PHONENUM_KEY, phoneNumValue);

        return properties;
    }

    void showProperties(final Map<String, String> properties) {
        final String nameValue = properties.get(NAME_KEY);
        if (nameValue != null)
            nickname.setText(nameValue);
        final String phoneNumValue = properties.get(PHONENUM_KEY);
        if (phoneNumValue != null)
            phoneNumber.setText(phoneNumValue);
    }
}
