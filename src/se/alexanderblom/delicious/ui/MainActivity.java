package se.alexanderblom.delicious.ui;

import se.alexanderblom.delicious.Constants;
import se.alexanderblom.delicious.DeliciousAccount;
import se.alexanderblom.delicious.R;
import se.alexanderblom.delicious.fragments.ClipboardFragment;
import se.alexanderblom.delicious.fragments.PostListFragment;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(new ClipboardFragment(), ClipboardFragment.TAG)
					.add(R.id.content, new PostListFragment())
					.commit();
		}
		
		ViewGroup container = (ViewGroup) findViewById(R.id.container);
		LayoutTransition transition = new LayoutTransition();

		transition.setStartDelay(LayoutTransition.APPEARING, 0);
		transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
		transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
		transition.setStartDelay(LayoutTransition.DISAPPEARING, 0);
		
		ObjectAnimator animator = ObjectAnimator.ofFloat(null, View.ALPHA, 1f, 0f);
		transition.setAnimator(LayoutTransition.DISAPPEARING, animator);
		
		container.setLayoutTransition(transition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_add:
				startActivity(new Intent(this, AddBookmarkActivity.class));
				break;
			case R.id.menu_logout:
				logout();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		
		return true;
	}
	
	@Override
	protected void accountChanged(DeliciousAccount account) {
		// Just replace our old fragment, this works when an error is shown too
		getFragmentManager().beginTransaction()
				.replace(R.id.content, new PostListFragment())
				.commit();
	}

	private void logout() {
		Log.d(TAG, "Removing account");
		
		AccountManager accountManager = AccountManager.get(this);
		Account accounts[] = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
		Account account = accounts[0];
		
		// Callback to wait for the account to actually be removed
		AccountManagerCallback<Boolean> callback = new AccountManagerCallback<Boolean>() {
			@Override
			public void run(AccountManagerFuture<Boolean> future) {
				try {
					if (future.getResult()) {
						checkAccount();
					} else {
						// Could not remove account, should not happen
						Log.e(TAG, "Could not remove account");
					}
				} catch (Exception e) {
					Log.e(TAG, "Error fetching remove account result", e);
				}
			}
		};
		
		accountManager.removeAccount(account, callback, null);
	}
}
