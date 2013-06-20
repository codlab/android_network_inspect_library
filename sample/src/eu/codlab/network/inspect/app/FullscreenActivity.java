package eu.codlab.network.inspect.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.vending.util.IabHelper;
import com.android.vending.util.IabHelper.OnConsumeFinishedListener;
import com.android.vending.util.IabResult;
import com.android.vending.util.Inventory;
import com.android.vending.util.Purchase;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends SherlockFragmentActivity
implements TabListener,
IabHelper.QueryInventoryFinishedListener,
IabHelper.OnIabPurchaseFinishedListener, OnConsumeFinishedListener{
	private Random _random;
	private final static String STATE_SELECTED_NAVIGATION_ITEM = "23459098";
	private IabHelper mHelper;

	public void createDonationDialog(boolean don1_purchased, boolean don2_purchased){
		if(don1_purchased == true && don2_purchased == true){
			return;
		}
		AlertDialog alertDiaLog = new AlertDialog.Builder(this).create();
		alertDiaLog.setTitle(R.string.dialog_donation_title);
		alertDiaLog.setMessage(getString(R.string.dialog_donation_message));
		alertDiaLog.setButton(getString(R.string.dialog_donation_no_thx), new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		if(don1_purchased == false){
			alertDiaLog.setButton2(getString(R.string.dialog_donation_mini), new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mHelper.launchPurchaseFlow(FullscreenActivity.this, "don1", 01,   
							FullscreenActivity.this, _random.nextInt(1353676232)+"");
					arg0.dismiss();
				}
			});
		}
		if(don2_purchased == false){
			alertDiaLog.setButton3(getString(R.string.dialog_donation_max), new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mHelper.launchPurchaseFlow(FullscreenActivity.this, "don2", 02,   
							FullscreenActivity.this, _random.nextInt(1353676232)+"");
					arg0.dismiss();
				}
			});
		}
		alertDiaLog.show();
	}
	public void onPlaystoreOK(){
		try{
			List additionalSkuList = new ArrayList();
			additionalSkuList.add("don1");
			additionalSkuList.add("don2");
			mHelper.queryInventoryAsync(true, additionalSkuList,
					this);
		}catch(Exception e){

		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		_random = new Random();
		super.onCreate(savedInstanceState);

		String base64EncodedPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAipI+tPn5s2TYDFI0WfX27EBxQFIMcozRG4BsdVprWEILJt7JO2reOEE4YJzRQWgusUUx8HFbkAaKbw4DWfdAtaW3/n3rzXFiita5wz/FOqtDKzDyDGkWGaQwrWy0oj9ihpxSK7DlYKcFFpcNM68rRSuxhkhA7LpZNA1WfOqjYCe3sBdHdt5dgTs4sktEdvPs49AYX8URxAWeCTp+TCyZBzcQnmjNlZhE2lw4ooOxxm+pKx2IfFE8j810WGgBOufnmli0/6F7Q+fdQwcco1tknHXFQRRVEqoHoEuCJP2zQoc6fX55qZMBOsVi8QHFm2PwLACJGO76vezFG+orzysOVwIDAQAB";

		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(final IabResult result) {
				runOnUiThread(new Runnable(){
					public void run(){
						if (!result.isSuccess()) {
							Toast.makeText(FullscreenActivity.this, "Problem setting up inapp "+result, Toast.LENGTH_LONG);
							// Oh noes, there was a problem.
						}else{
							onPlaystoreOK();
						}
					}
				});
				// Hooray, IAB is fully set up!  
			}
		});
		this.setContentView(R.layout.main);

		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		bar.setDisplayHomeAsUpEnabled(false);


		bar.addTab(bar.newTab().setText(R.string.title_section_service)
				.setTabListener(this));
		bar.addTab(bar.newTab().setText(R.string.title_section_graph)
				.setTabListener(this));
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		//if (isSmartphone() && savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
		getSupportActionBar().setSelectedNavigationItem(
				savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		//}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.main_donation:
        	this.createDonationDialog(false, false);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.

		Fragment fragment = null;
		if(tab.getPosition() == 0){
			fragment = new StartFragment();

		}else{
			fragment = new GraphFragment();
		}
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
	}
	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
		if (result.isFailure()) {
			//Log.d("ERROR",result.getMessage());
			return;
		}

		String don1 =
				inventory.getSkuDetails("don1").getPrice();
		String don2 =
				inventory.getSkuDetails("don2").getPrice();
		//Log.d("DON",don1+" "+don2);

		if(inventory.hasPurchase("don1")){
			mHelper.consumeAsync(inventory.getPurchase("don1"), 
					this);
		}
		if(inventory.hasPurchase("don2")){
			mHelper.consumeAsync(inventory.getPurchase("don2"), 
					this);
		}

		if(_random.nextInt(100) < 20)
			createDonationDialog(inventory.hasPurchase("don1"),inventory.hasPurchase("don2"));

		// update the UI 
	}
	@Override
	public void onIabPurchaseFinished(final IabResult result, final Purchase info) {
		runOnUiThread(new Runnable(){
			public void run(){
				if (result.isFailure()) {
					return;
				}else if (info.getSku().equals("don1")) {
					Toast.makeText(FullscreenActivity.this, R.string.purchased_don1, Toast.LENGTH_LONG);
				}else if (info.getSku().equals("don2")) {
					Toast.makeText(FullscreenActivity.this, R.string.purchased_don2, Toast.LENGTH_LONG);
				}
			}
		});
	}
	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
	}
}
