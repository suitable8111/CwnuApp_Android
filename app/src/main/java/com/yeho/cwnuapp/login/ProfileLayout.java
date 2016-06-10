package com.yeho.cwnuapp.login;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.yeho.cwnuapp.R;

/**
 * Created by KimDaeho on 16. 1. 6..
 */
public class ProfileLayout extends FrameLayout {
    private MeResponseCallback meResponseCallback;

    private String schoolnum;
    private String realname;
    private String nickname;
    private TextView profileDescriptionRealName;
    TextView profileDescriptionSchoolNum;

    public ProfileLayout(Context context) {
        super(context);
        initView();
    }

    public ProfileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ProfileLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 사용자정보 요청 결과에 따른 callback을 설정한다.
     * @param callback 사용자정보 요청 결과에 따른 callback
     */
    public void setMeResponseCallback(final MeResponseCallback callback){
        this.meResponseCallback = callback;
    }
    /**
     * param으로 온 UserProfile에 대해 view를 update한다.
     * @param userProfile 화면에 반영할 사용자 정보
     */
    public void setUserProfile(final UserProfile userProfile) {
        //setProfileURL(userProfile.getProfileImagePath());
        setRealname(userProfile.getProperty("name"));
        setSchoolNum(userProfile.getProperty("schoolnum"));
        setNickname(userProfile.getProperty("nick"));
    }

    /**
     * 프로필 이미지에 대해 view를 update한다.
     * @param profileImageURL 화면에 반영할 프로필 이미지
     */

    /**
     * 별명 view를 update한다.
     * @param schoolnum 화면에 반영할 별명
     */
    public void setSchoolNum(final String schoolnum) {
        this.schoolnum = schoolnum;
        updateLayout();
    }
    public void setRealname(final String realname) {
        this.realname = realname;
    }
    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }



    private void updateLayout() {


        if (profileDescriptionRealName != null) {
            if (realname != null && realname.length() > 0) {
                profileDescriptionRealName.setText(realname);
            }

        }
        if (profileDescriptionSchoolNum != null) {
            if (schoolnum != null && schoolnum.length() > 0) {
                profileDescriptionSchoolNum.setText(schoolnum);
            }
        }
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.layout_common_kakao_profile, this);
        profileDescriptionRealName = (TextView) view.findViewById(R.id.profile_description_realname);
        profileDescriptionSchoolNum = (TextView) view.findViewById(R.id.profile_description_schoolnum);

    }

    /**
     * 사용자 정보를 요청한다.
     */
    public void requestMe() {
        UserManagement.requestMe(meResponseCallback);
    }
}
