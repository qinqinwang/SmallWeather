package com.weather.view;

import com.smallweather.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

	private final String APP_ID = "wxc1166aff17ba799b";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onReq(BaseReq arg0) { }
 
    @Override
    public void onResp(BaseResp resp) {
       
    	int result = 0;
    	 
        switch (resp.errCode)
        {
        case BaseResp.ErrCode.ERR_OK:
            result = R.string.errcode_success;
            break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
            result = R.string.errcode_cancel;
            break;
        case BaseResp.ErrCode.ERR_AUTH_DENIED:
            result = R.string.errcode_deny;
            break;
        default:
            result = R.string.errcode_unknown;
            break;
        }
 
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }
}