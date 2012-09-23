package com.charaza;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import android.content.Intent;
import android.os.Bundle;

public class About extends SherlockActivity implements ISideNavigationCallback
{
	private SideNavigationView sideNavigationView;
	private int networkCheckStatus=0;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_about);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.aboutSideNavigationTitle);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle bundle=this.getIntent().getExtras();
		if(bundle!=null)
		{
			networkCheckStatus=bundle.getInt("networkCheckStatus");
		}
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) 
    	{
			case android.R.id.home:
				sideNavigationView.toggleMenu();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
    	//nameTextBox.requestFocus();
		return true;
	}
    
	@Override
	public void onSideNavigationItemClick(int itemId)
	{	
		if(itemId==R.id.mulikaSideNavigation)
		{
			Intent intent=new Intent(About.this, Mulika.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
		else if(itemId==R.id.ranksSideNavigation)
		{
			Intent intent=new Intent(About.this, Ranks.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
		else if(itemId==R.id.latestSideNavigation)
		{
			Intent intent=new Intent(About.this, Latest.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			startActivity(intent);
		}
	}

}
