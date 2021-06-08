package com.zev.noads;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.TextView;

public class HideAdsService extends AccessibilityService {
  private static String TAG = HideAdsService.class.getSimpleName();

  private boolean mRunningService = false;
  private static final int DURATION = 300;

  private static final String RES_ID_NAME
    = "com.cmbchina.ccd.pluto.cmbActivity:id/img_cancel" +
    "com.tencent.qqmusic:id/er0" +
    "com.xiaomi.shop:id/skip";

  private static final String BLACK_LIST_PKG_NAME =
    "com.cmbchina.ccd.pluto.cmbActivity" +
    "com.xiaomi.shop" +
    "com.tencent.qqmusic";

  private String mPkgName = "";
  private long mLastTime = 0;

  public HideAdsService() {
    Log.d(TAG, "HideAdsService: ");
  }

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate: ");
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Log.d(TAG, "onUnbind: ");
    mRunningService = false;
    return super.onUnbind(intent);
  }

  @Override
  protected void onServiceConnected() {
    Log.d(TAG, "onServiceConnected: ");
    mRunningService = true;
    super.onServiceConnected();
  }

  @Override
  public void onInterrupt() {
    Log.d(TAG, "onInterrupt: ");
    mRunningService = false;
  }

  @Override
  public void onAccessibilityEvent(AccessibilityEvent event) {
    // running duration
    if((System.currentTimeMillis() - mLastTime) < DURATION){
      // Log.w(TAG, "onAccessibilityEvent: DURATION");
      return;
    }

    AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
    if(rootNode == null){
      Log.w(TAG, "onAccessibilityEvent: NODE NULL");
      return;
    }
    //
    mPkgName = String.valueOf(rootNode.getPackageName());
    if(TextUtils.isEmpty(mPkgName)){
      Log.w(TAG, "onAccessibilityEvent: PKG NULL");
      return;
    }

    // white list
//    if(WHITE_LIST.contains(pkgName)){
//      return;
//    }

    AccessibilityNodeInfo node = findSkipAdsNode(rootNode);
    if(node != null){
      Log.i(TAG, "package: " + mPkgName);
      Log.i(TAG, "onAccessibilityEvent: ACTION_CLICK ads");
      node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    mLastTime = System.currentTimeMillis();
  }

  // skip button && text view which contains skip words.
  private AccessibilityNodeInfo findSkipAdsNode(AccessibilityNodeInfo info){
    int count = info.getChildCount();
    if(count > 0){
      for(int i = 0; i < count; i++){
        AccessibilityNodeInfo subNode = info.getChild(i);
        if(subNode == null){
          continue;
        }
        // Recursive call
        AccessibilityNodeInfo currentNode = findSkipAdsNode(subNode);
        if(currentNode != null){
          return currentNode;
        }
      }
    } else {
      // using ImageView as skip button
      if (BLACK_LIST_PKG_NAME.contains(info.getPackageName())) {
        if (info.getViewIdResourceName() != null) {
          if (RES_ID_NAME.contains(info.getViewIdResourceName())) {
            if (info.isEnabled()) {
              return info;
            }
          }
        }
      }

      // Button and TextView
      if(Button.class.getName().equals(info.getClassName())
        || TextView.class.getName().equals(info.getClassName())){
        // target node
        if (TextUtils.isEmpty(info.getText())) {
          return null;
        }

        // if not short skip text msg, is not target node.
        if (info.getText().length() > 10) {
          return null;
        }

        if(!info.getText().toString()
                  .contains(getResources().getString(R.string.skip))){
          return null;
        }

        if (info.isEnabled()) {
          return info;
        } else {
          AccessibilityNodeInfo parent = info.getParent();
          if (parent.isEnabled()) {
            return parent;
          }
        }
      }
    }

    return null;
  }
}
