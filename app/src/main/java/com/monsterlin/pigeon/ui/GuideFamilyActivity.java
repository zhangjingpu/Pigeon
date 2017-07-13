package com.monsterlin.pigeon.ui;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.monsterlin.pigeon.MainActivity;
import com.monsterlin.pigeon.R;
import com.monsterlin.pigeon.base.BaseActivity;
import com.monsterlin.pigeon.bean.Family;
import com.monsterlin.pigeon.bean.User;
import com.monsterlin.pigeon.common.AppManager;
import com.monsterlin.pigeon.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * @author : monsterLin
 * @version : 1.0
 * @email : monster941025@gmail.com
 * @github : https://github.com/monsterLin
 * @time : 2017/7/11
 * @desc : 家庭引导
 */
public class GuideFamilyActivity extends BaseActivity {

    private Toolbar mToolBar;
    private Button mBtnCopy;
    private TextInputLayout mFamilyIdWrapper;
    private EditText mEdtSearch;
    private TextView mTvCreate;
    private MaterialDialog mMDialog;
    private View viewCreate;
    private User currentUser;

    @Override
    public int getLayoutId() {
        return R.layout.activity_family_guide;
    }

    @Override
    public void initViews() {
        mToolBar = findView(R.id.common_toolbar);
        initToolBar(mToolBar, "飞鸽", false);
        mBtnCopy = findView(R.id.familyGuide_btn_copy);
        mFamilyIdWrapper = findView(R.id.familyGuide_familyIdWrapper);
        mFamilyIdWrapper.setHint("创建者飞鸽号");
        mEdtSearch = findView(R.id.familyGuide_edt_searchFamily);
        mTvCreate = findView(R.id.familyGuide_tv_create);
    }

    @Override
    public void initListener() {
        setOnClick(mTvCreate);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.familyGuide_tv_create:
                currentUser = BmobUser.getCurrentUser(User.class);
                BmobQuery<Family> queryFamily = new BmobQuery<>();
                queryFamily.addWhereEqualTo("familyCreator", currentUser);
                queryFamily.findObjects(new FindListener<Family>() {
                    @Override
                    public void done(List<Family> list, BmobException e) {
                        if (null != list) {
                            ToastUtils.showToast(GuideFamilyActivity.this, "你已经创建过家庭");
                        } else {
                            showCreateFamilyDialog();
                        }
                    }
                });
                break;
        }
    }


    /**
     * 展示创建家庭弹出框并且完成创建家庭的功能
     */
    private void showCreateFamilyDialog() {
        viewCreate = LayoutInflater.from(this).inflate(R.layout.view_simple_create_family, null);
        final EditText mEdtFamily = (EditText) viewCreate.findViewById(R.id.family_edt_create);
        mMDialog = new MaterialDialog(this)
                .setTitle("创建家庭")
                .setView(viewCreate)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String familyName = mEdtFamily.getText().toString();
                        if (!TextUtils.isEmpty(familyName)) {
                            Family family = new Family();
                            family.setFamilyName(familyName);
                            family.setFamilyCreator(currentUser);
                            family.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        ToastUtils.showToast(GuideFamilyActivity.this, "创建成功，快去邀请你的家庭成员吧");
                                        nextActivity(MainActivity.class);
                                        AppManager.getAppManager().finishActivity();
                                    } else {
                                        ToastUtils.showToast(GuideFamilyActivity.this, "创建家庭失败：" + e.getMessage());
                                    }
                                }
                            });
                        }

                        mMDialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMDialog.dismiss();
                    }
                });
        mMDialog.show();
    }
}