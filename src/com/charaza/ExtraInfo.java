package com.charaza;

import java.util.ArrayList;
import java.util.List;
import com.charaza.resources.CharazaData;
import com.charaza.resources.Profile;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
//import android.widget.Toast;
import android.widget.Toast;

public class ExtraInfo extends SherlockActivity implements View.OnClickListener, OnItemClickListener, ISideNavigationCallback
{
	private SideNavigationView sideNavigationView;
	private ImageButton plus;
	private ImageButton doneButton;
	private CharazaData charazaData;
	private int networkCheckStatus=0;
	private String[][] aliasTypes;
	private String[] extraAliasTypes;
	private ListView aliasTypeListView;
	private ArrayAdapter<String> arrayAdapter;
	private ArrayAdapter<String> extraArrayAdapter;
	private Dialog aliasTypeDialog;
	private Dialog addAliasDialog;
	private ImageButton aliasAddButton;
	private List<Button> addedAliases;
	private TextView aliasTypeTextView;
	private AutoCompleteTextView somethingElseAutoComplete;
	private TextView helpText;
	private EditText aliasEditText;
	private Profile profile;
	private RelativeLayout extraInfoRelativeLayout;
	private ScrollView extraInfoScrollView;
	private Context context;
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extra_info);
        
        //initialise views
        extraInfoScrollView=(ScrollView)this.findViewById(R.id.extraInfoScrollView);
        extraInfoRelativeLayout=(RelativeLayout)this.findViewById(R.id.extraInfoRelativeLayout);
        int minHeight=0;
        Display display=this.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
        {
        	minHeight=display.getHeight()-48-67;
        }
        else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM)
        {
        	minHeight=display.getHeight()-32-45;
        }
        else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
        {
        	minHeight=display.getHeight()-24-34;
        }
		extraInfoRelativeLayout.setMinimumHeight(minHeight);//API level 8-12 require getHeight()
		
		sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_extra_info);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.mulikaSideNavigationTitle);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		sideNavigationView.setMinimumHeight(minHeight+5);
		
		doneButton=(ImageButton)this.findViewById(R.id.doneButton);
		doneButton.setOnClickListener(this);
		plus=(ImageButton)this.findViewById(R.id.plus);
		plus.setOnClickListener(this);
		
		aliasTypeDialog=new Dialog(this);
		aliasTypeDialog.setContentView(R.layout.alias_type_popup);
		
		addAliasDialog=new Dialog(this);
		addAliasDialog.setContentView(R.layout.add_alias_dialog);
		WindowManager.LayoutParams layoutParams=addAliasDialog.getWindow().getAttributes();
		layoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;//since FILL_PARENT was deprecated
		aliasTypeTextView=(TextView)addAliasDialog.findViewById(R.id.aliasTypeTextView);
		aliasEditText=(EditText)addAliasDialog.findViewById(R.id.aliasEditText);
		somethingElseAutoComplete=(AutoCompleteTextView)addAliasDialog.findViewById(R.id.somethingElseAutoComplete);
		
		aliasAddButton=(ImageButton)addAliasDialog.findViewById(R.id.aliasAddButton);
		aliasAddButton.setOnClickListener(this);
		helpText=(TextView)this.findViewById(R.id.helpText);
		
		//initialise utils
		profile=new Profile();
		Bundle bundle=this.getIntent().getExtras();
		networkCheckStatus=bundle.getInt("networkCheckStatus");
		profile=bundle.getParcelable(profile.PARCELABLE_KEY);
		profile.setContext(this);
		context=this;
		
		charazaData=new CharazaData(this);
		addedAliases=new ArrayList<Button>();
		
		if(profile.getNumberOfAliases()>0)
		{
			for(int index=0;index<profile.getNumberOfAliases();index++)
			{
				RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(plus.getLayoutParams().width,plus.getLayoutParams().height);
				//Toast.makeText(this, String.valueOf(plus.getHeight()), Toast.LENGTH_LONG).show();
				final Button newButton=new Button(this);
				newButton.setText(profile.getAliasTypeAt(index));
				helpText.setVisibility(TextView.GONE);
				if(index==0)//then this is the first button
				{
					//lp.addRule(RelativeLayout.BELOW, R.id.extraInfoTitleSeperator);
					lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
					lp.topMargin=18;
					int id=325;
					newButton.setId(id);
				}
				else
				{
					lp.addRule(RelativeLayout.BELOW,addedAliases.get(addedAliases.size()-1).getId());
					lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
					lp.topMargin=12;
					int id=325+index;
					newButton.setId(id);
				}
				newButton.setOnFocusChangeListener(new View.OnFocusChangeListener()
				{
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) 
					{
						if(hasFocus)
						{
							newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
						}
						else
						{
							newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
						}
					}
				});
				newButton.setOnTouchListener(new View.OnTouchListener()
				{
					
					@Override
					public boolean onTouch(View v, MotionEvent event) 
					{
						if(event.getAction()==MotionEvent.ACTION_DOWN)
						{
							newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
						}
						else if(event.getAction()==MotionEvent.ACTION_UP)
						{
							newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
							addedAliasButtonClicked(newButton);
						}
						else
						{
							newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
						}
						return true;
					}
				});
				newButton.setOnClickListener(new OnClickListener() 
				{
					
					@Override
					public void onClick(View v) 
					{	
						addedAliasButtonClicked(newButton);
					}
				});
				newButton.setTextColor(getResources().getColor(R.color.normalTextColor));
				newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
				newButton.setLayoutParams(lp);
				if(index==profile.getNumberOfAliases()-1)//last button
				{
					RelativeLayout.LayoutParams lp2=new RelativeLayout.LayoutParams(plus.getLayoutParams().width,plus.getLayoutParams().height);
					lp2.addRule(RelativeLayout.BELOW,newButton.getId());
					lp2.topMargin=12;
					lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
					plus.setLayoutParams(lp2);
				}
				addedAliases.add(newButton);
				extraInfoRelativeLayout.addView(newButton);
			}
		}
		aliasTypes=null;
		
		//fetch network data
        //Thread thread=new Thread(new Initializer());
      	//thread.run();
		new GetAliasTypesThread().execute(0);
		
		//check network connection
      	if(!charazaData.checkNetworkConnection() && networkCheckStatus==0)
        {
        	charazaData.networkError();
        	networkCheckStatus=1;
        }
		
    }
    
    private void addedAliasButtonClicked(Button newButton)
    {
    	aliasTypeTextView.setVisibility(TextView.GONE);
		aliasEditText.setText(profile.getAliasAt(newButton.getId()-325));
		somethingElseAutoComplete.setVisibility(AutoCompleteTextView.VISIBLE);
		somethingElseAutoComplete.setAdapter(arrayAdapter);//arrayAdapter is initialised in Initializer running in another thread
		somethingElseAutoComplete.setText(profile.getAliasTypeAt(newButton.getId()-325));
		addAliasDialog.show();
    }
    
    /*@Override
    protected void onDestroy ()
    {
    	charazaData.closeDatabase();
    	profile.closeDatabase();
    	super.onDestroy();
    }
    
    @Override
	protected void onPause() 
    {
    	charazaData.closeDatabase();
    	profile.closeDatabase();
		super.onPause();
	}*/

    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	extraInfoScrollView.fullScroll(0);
    	InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE); 
    	if(this.getCurrentFocus()!=null)
    	{
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	}
    	switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Type of Discription");
		if(aliasTypes!=null)
		{
			int count=0;
			while(count<aliasTypes.length)
			{
				menu.add(0, v.getId(), Integer.parseInt(aliasTypes[count][0]), aliasTypes[count][1]);
				count++;
			}
		}
	}

	@Override
	public void onClick(View v) 
	{
		if(v==doneButton)
		{
			//profile.setAddedAliases(addedAliases);
			Intent intent=new Intent(ExtraInfo.this, Mulika.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			Bundle bundle=new Bundle();
			bundle.putParcelable(profile.PARCELABLE_KEY, profile);
			intent.putExtras(bundle);
			startActivity(intent);
		}
		else if (v==plus)
		{
			showListPopup();
		}
		else if(v==aliasAddButton)
		{
			RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(plus.getWidth(), plus.getHeight());
			final Button newButton=new Button(this);
			if(aliasTypeTextView.getVisibility()==TextView.VISIBLE)
			{
				newButton.setText(aliasTypeTextView.getText());
			}
			else
			{
				newButton.setText(somethingElseAutoComplete.getText());
			}
			if(addedAliases.isEmpty())
			{
				helpText.setVisibility(TextView.GONE);
				//layoutParams.addRule(RelativeLayout.BELOW, R.id.extraInfoTitleSeperator);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				layoutParams.topMargin=18;
				int id=325;
				newButton.setId(id);
			}
			else
			{
				layoutParams.addRule(RelativeLayout.BELOW, addedAliases.get(addedAliases.size()-1).getId());
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				layoutParams.topMargin=12;
				int id=325+addedAliases.size();
				newButton.setId(id);
			}
			newButton.setOnFocusChangeListener(new View.OnFocusChangeListener()
			{
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) 
				{
					if(hasFocus)
					{
						newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
					}
					else
					{
						newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
					}
				}
			});
			newButton.setOnTouchListener(new View.OnTouchListener()
			{
				
				@Override
				public boolean onTouch(View v, MotionEvent event) 
				{
					if(event.getAction()==MotionEvent.ACTION_DOWN)
					{
						newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
					}
					else if(event.getAction()==MotionEvent.ACTION_UP)
					{
						newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
						addedAliasButtonClicked(newButton);
					}
					else
					{
						newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
					}
					return true;
				}
			});
			newButton.setOnClickListener(new OnClickListener() 
			{
				
				@Override
				public void onClick(View v) 
				{
					addedAliasButtonClicked(newButton);
				}
			});
			newButton.setTextColor(getResources().getColor(R.color.normalTextColor));
			//newButton.setTextSize(, size)//TODO:specify textsize in dp
			newButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
			newButton.setLayoutParams(layoutParams);
			RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(plus.getWidth(),plus.getHeight());
			layoutParams2.addRule(RelativeLayout.BELOW,newButton.getId());
			layoutParams2.topMargin=12;
			layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
			plus.setLayoutParams(layoutParams2);
			addedAliases.add(newButton);
			//profile.setAddedAliases(addedAliases);
			profile.addAlias(newButton.getText().toString(), aliasEditText.getText().toString());
			extraInfoRelativeLayout.addView(newButton);
			addAliasDialog.dismiss();
		}
	}
	
	private void showListPopup()
	{
		aliasTypeDialog.setTitle("Things you might know");
		aliasTypeListView=(ListView)aliasTypeDialog.findViewById(R.id.aliasTypeListView);
		aliasTypeListView.setAdapter(extraArrayAdapter);
		aliasTypeListView.setOnItemClickListener(this);
		aliasTypeDialog.show();
	}
	
	public void setArrayAdapter(String[] aliasTypesText)
	{
		arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,aliasTypesText);
		aliasTypesText[aliasTypes.length]="Something else";
		extraAliasTypes=aliasTypesText;
		extraArrayAdapter=new ArrayAdapter<String>(this,R.layout.custom_list_item,R.id.text1,aliasTypesText);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
	{
		if(arg0==aliasTypeListView)
		{
			aliasTypeDialog.dismiss();
			if(extraAliasTypes[position].equals("Something else"))
			{
				aliasTypeTextView.setVisibility(TextView.GONE);
				aliasEditText.setText("");
				somethingElseAutoComplete.setVisibility(AutoCompleteTextView.VISIBLE);
				somethingElseAutoComplete.setAdapter(arrayAdapter);
				addAliasDialog.show();
			}
			else
			{
				aliasTypeTextView=(TextView)addAliasDialog.findViewById(R.id.aliasTypeTextView);
				aliasTypeTextView.setVisibility(TextView.VISIBLE);
				aliasEditText=(EditText)addAliasDialog.findViewById(R.id.aliasEditText);
				aliasEditText.setText("");
				somethingElseAutoComplete=(AutoCompleteTextView)addAliasDialog.findViewById(R.id.somethingElseAutoComplete);
				somethingElseAutoComplete.setVisibility(AutoCompleteTextView.GONE);
				aliasTypeTextView.setText(extraAliasTypes[position]);
				addAliasDialog.show();
			}
		}
		
	}
	private class GetAliasTypesThread extends AsyncTask<Integer, Integer, String[]>
	{

		@Override
		protected String[] doInBackground(Integer... params)
		{
			aliasTypes=charazaData.getAliasTypes();
			String[] aliasTypesText=new String[aliasTypes.length+1];
			for(int i=0;i<aliasTypes.length;i++)
			{
				aliasTypesText[i]=aliasTypes[i][1];
			}
			return aliasTypesText;
		}

		@Override
		protected void onPostExecute(String[] result) 
		{
			setArrayAdapter(result);
			if(aliasTypeDialog.isShowing())
			{
				aliasTypeDialog.dismiss();
				Toast.makeText(context, "Try pressing + again", Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
		
	}
	@Override
	public void onSideNavigationItemClick(int itemId)
	{
		if(itemId==R.id.mulikaSideNavigation)
		{
			//TODO: do something
		}
		else if(itemId==R.id.ranksSideNavigation)
		{
			Intent intent=new Intent(ExtraInfo.this, Ranks.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			charazaData.closeDatabase();
			startActivity(intent);
		}
	}
	
	/*private class Initializer implements Runnable
	{

		@Override
		public void run() 
		{
			aliasTypes=charazaData.getAliasTypes();
			String[] aliasTypesText=new String[aliasTypes.length+1];
			for(int i=0;i<aliasTypes.length;i++)
			{
				aliasTypesText[i]=aliasTypes[i][1];
			}
			
			setArrayAdapter(aliasTypesText);
		}
		
	}*/
}
