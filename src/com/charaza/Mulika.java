package com.charaza;

import com.charaza.resources.CharazaData;
import com.charaza.resources.MulikaDataCarrier;
import com.charaza.resources.Profile;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Mulika extends SherlockActivity implements View.OnClickListener, OnItemSelectedListener, ISideNavigationCallback, View.OnFocusChangeListener, View.OnTouchListener
{
	//private static final int SWIPE_MIN_DISTANCE=100;//initially 120
	//private static final int SWIPE_MAX_OFF_PATH=250;
	//private static final int SWIPE_THRESHOLD_VELOCITY=10;//initially 200
	
	private SideNavigationView sideNavigationView;
	//private GestureDetector gestureDetector;
	//protected View.OnTouchListener gestureListener;
	private RelativeLayout mulikaLayout;
	private ImageButton extraInfoButton;
	private ImageButton mulikaButton;
	private ScrollView mulikaScrollView;
	private TextView detailsLabel;
	private TextView splashScreenName;
	private TextView splashScreenSlogan;
	protected CharazaData charazaData;
	protected Profile profile;
	private String[] names;
	private AutoCompleteTextView nameTextBox;
	private  Spinner post;
	private String[] extraPosts;
	private String[] posts;
	private AutoCompleteTextView somethingElse;
	private EditText details;
	private Context context;
	private Dialog splashScreen;
	private Bitmap extraInfoButtonUnclickedImage;
	private Bitmap extraInfoButtonClickedImage;
	private Bitmap mulikaButtonUnclickedImage;
	private Bitmap mulikaButtonClickedImage;
	private MulikaDataCarrier mulikaDataCarrier;
	private String selectedPost;
	private boolean splashScreenFlag;
	private int networkCheckStatus=0;//flag showing all other activities that the user has already been notified that there is no connection to internet
	private Dialog welcomeDialog;
	private Button welcomeDoneButton;
	private Dialog mulikaInstructionsDialog;
	private Button mulikaInstructionsButton;
	private boolean isFirstTimeMulika;
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mulika);
        
        //fetch data from previous activity
        
        
        //gesture detection
        /*gestureDetector=new GestureDetector(new MyGestureDetector());
        gestureListener=new View.OnTouchListener() 
        {
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				Log.d("Gestures","touch detected");
				return gestureDetector.onTouchEvent(event);
			}
		};*/
        //gestureDetector=new GestureDetector(new SwipeGestuer)
        
        
        //initialise views
        isFirstTimeMulika=true;
		mulikaScrollView=(ScrollView)this.findViewById(R.id.mulikaScrollView);
		//mulikaScrollView.setOnTouchListener(gestureListener);
		
		mulikaLayout=(RelativeLayout)this.findViewById(R.id.mulikaLayout);
		//mulikaLayout.setOnTouchListener(gestureListener);
		int minHeight=0;
        Display display=this.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
        {
        	minHeight=display.getHeight()-146;
        }
        else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
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
        else
        {
        	minHeight=display.getHeight()-146;
        }
		//mulikaLayout.setMinimumHeight(minHeight);//API level 8-12 require getHeight()
		
		sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_mulika);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.mulikaSideNavigationTitle);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//sideNavigationView.setMinimumHeight(minHeight+5);
		
		extraInfoButton=(ImageButton)this.findViewById(R.id.extraInfoButton);
		extraInfoButton.setOnClickListener(this);//click events handled by ontouch
		extraInfoButton.setOnFocusChangeListener(this);
		extraInfoButton.setOnTouchListener(this);
		nameTextBox=(AutoCompleteTextView)findViewById(R.id.nameTextBox);
		post=(Spinner)findViewById(R.id.post);
        //post.setOnItemSelectedListener(this);
        somethingElse=(AutoCompleteTextView)findViewById(R.id.somethingElse);
        details=(EditText)findViewById(R.id.details);
        mulikaButton=(ImageButton)this.findViewById(R.id.mulikaButton);
        mulikaButton.setOnClickListener(this);
        context=this;
        splashScreen=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        splashScreen.setContentView(R.layout.splash_screen);
        splashScreenName=(TextView)splashScreen.findViewById(R.id.splashScreenText);
        splashScreenSlogan=(TextView)splashScreen.findViewById(R.id.splashScreenSlogan);
        WindowManager.LayoutParams layoutParams=splashScreen.getWindow().getAttributes();
		layoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;//since FILL_PARENT was deprecated
		layoutParams.height=WindowManager.LayoutParams.MATCH_PARENT;
		detailsLabel=(TextView)this.findViewById(R.id.detailsLabel);
		splashScreen.show();
		
		welcomeDialog=new Dialog(this);
		welcomeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		welcomeDialog.setContentView(R.layout.welcome);
		WindowManager.LayoutParams welcomeLayoutParams=welcomeDialog.getWindow().getAttributes();
		welcomeLayoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
		welcomeDoneButton=(Button)welcomeDialog.findViewById(R.id.welcomeDoneButton);
		welcomeDoneButton.setOnTouchListener(this);
		
		mulikaInstructionsDialog=new Dialog(this);
		mulikaInstructionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mulikaInstructionsDialog.setContentView(R.layout.mulika_instructions);
		WindowManager.LayoutParams mulikaInstructionsLayoutParams=mulikaInstructionsDialog.getWindow().getAttributes();
		mulikaInstructionsLayoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
		mulikaInstructionsButton=(Button)mulikaInstructionsDialog.findViewById(R.id.mulikaInstructionsButton);
		mulikaInstructionsButton.setOnTouchListener(this);
		
		//initialise resources
		splashScreenFlag=false;
		selectedPost=null;
		mulikaDataCarrier=new MulikaDataCarrier();
		charazaData=new CharazaData(this);
		profile=new Profile(this);//no need to call set context in this situation since the database is initialised in this situation
		Bundle bundle=this.getIntent().getExtras();
		if(bundle!=null)
		{
			if(bundle.getParcelable(profile.PARCELABLE_KEY)!=null)
			{
				profile=bundle.getParcelable(profile.PARCELABLE_KEY);
				profile.setContext(this);
			}
			if(bundle.getParcelable(mulikaDataCarrier.PARCELABLE_KEY)!=null)
			{
				mulikaDataCarrier=bundle.getParcelable(mulikaDataCarrier.PARCELABLE_KEY);
				restoreMulikaData();
			}
			networkCheckStatus=bundle.getInt("networkCheckStatus");
			splashScreen.dismiss();
			splashScreenFlag=true;
		}
		
		names=null;
		extraPosts=null;
		posts=null;
		Thread thread=new Thread(new Runnable()
		{
			
			@Override
			public void run() 
			{
				extraInfoButtonClickedImage=BitmapFactory.decodeResource(getResources(), R.drawable.plus_clicked);
				extraInfoButtonUnclickedImage=BitmapFactory.decodeResource(getResources(), R.drawable.plus);
				mulikaButtonClickedImage=BitmapFactory.decodeResource(getResources(), R.drawable.mulika_selected);
				mulikaButtonUnclickedImage=BitmapFactory.decodeResource(getResources(), R.drawable.mulika);
			}
		});
		thread.run();
		
		//check network connection
		charazaData=new CharazaData(this);
        if(!charazaData.checkNetworkConnection() && networkCheckStatus==0)
        {
        	charazaData.networkError();
        	networkCheckStatus=1;
        }
		
      //fetch network data
      new GetIsFirstTimeMulikaTread().execute(0);
      new GetProfilesThread().execute(0);
      new GetPostsThread().execute(0);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	/*mulikaScrollView.post(new Runnable() 
		{

	        @Override
	        public void run() 
	        {
	        	mulikaScrollView.fullScroll(ScrollView.FOCUS_UP);
	        }
	    });*/
    	InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE); 
    	if(this.getCurrentFocus()!=null)
    	{
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	}
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
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
        	this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onDestroy ()
    {
    	Log.d("mulika database", "close database about to be called by onDestroy()");
    	charazaData.closeDatabase();
    	profile.closeDatabase();
    	super.onDestroy();
    }

	public void setNameSuggestions(String[] names)
    {
    	ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, R.layout.simple_dropdown_hint, names);
    	this.nameTextBox.setAdapter(arrayAdapter);
    	this.names=names;
    }
    
    public void setPostSuggestions(String[] posts)
    {
    	Log.d("setpostsuggestions called","called");
    	String[] extraPosts=new String[posts.length+2];
    	for(int i=0;i<extraPosts.length;i++)
    	{
    		if(i==0)
    		{
    			extraPosts[i]="I don't know";
    		}
    		else if(i>0 && i<extraPosts.length-1 && posts.length>0)
    		{
    			extraPosts[i]=posts[i-1];
    		}
    		else if(i==extraPosts.length-1)
    		{
    			extraPosts[i]="Something else";
    		}
    	}
    	ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, R.layout.simple_dropdown_hint, extraPosts);//android.R.layout.simple_dropdown_item_1line
    	arrayAdapter.setDropDownViewResource(R.layout.simple_dropdown_hint);//android.R.layout.simple_spinner_dropdown_item
    	this.post.setAdapter(arrayAdapter);
    	post.setOnItemSelectedListener(this);
    	ArrayAdapter<String> arrayAdapter2=new ArrayAdapter<String>(this, R.layout.simple_dropdown_hint, posts);
    	this.somethingElse.setAdapter(arrayAdapter2);
    	this.posts=posts;
    	this.extraPosts=extraPosts;
    	if(mulikaDataCarrier.getPostPostion(extraPosts)!=-1)
    	{
    		post.setSelection(mulikaDataCarrier.getPostPostion(this.extraPosts));
    		Log.d("post suggestion", "post should be restored ");
    	}
    }
    
    private void extraInfoButtonClicked()
    {
    	Intent intent=new Intent(Mulika.this, ExtraInfo.class);
		intent.putExtra("networkCheckStatus", networkCheckStatus);
		Bundle bundle=new Bundle();
		bundle.putParcelable(profile.PARCELABLE_KEY, profile);
		saveMulikaData();
		bundle.putParcelable(mulikaDataCarrier.PARCELABLE_KEY, mulikaDataCarrier);
		intent.putExtras(bundle);
		startActivity(intent);
    }
    
    private void saveMulikaData()
    {
		boolean status=true;
		if(somethingElse.getVisibility()==AutoCompleteTextView.VISIBLE)
		{
			status=false;
		}
		mulikaDataCarrier=new MulikaDataCarrier(extraPosts[post.getSelectedItemPosition()], somethingElse.getText().toString(), details.getText().toString(),status);
		
		profile.setName(nameTextBox.getText().toString());
		/*if(somethingElse.getVisibility()==AutoCompleteTextView.VISIBLE)
		{
			profile.setPost(somethingElse.getText().toString());
		}
		else
		{
			if(extraPosts[post.getSelectedItemPosition()]=="I don't know")
			{
				profile.setPost("");
			}
			else		
			{
				profile.setPost(extraPosts[post.getSelectedItemPosition()]);
			}
		}*/
    }
    
    private void restoreMulikaData()
    {
    	nameTextBox.setText(profile.getName());
    	
    	//post restored in setPostSuggestions()
    	
    	/*if(mulikaDataCarrier.getProfileStatus()==true)
    	{ 
    		somethingElse.setVisibility(AutoCompleteTextView.GONE);
    		post.setVisibility(Spinner.VISIBLE);
    	}
    	else
    	{
    		somethingElse.setVisibility(AutoCompleteTextView.VISIBLE);
    		post.setVisibility(Spinner.GONE);
    		RelativeLayout.LayoutParams detailsLableLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    		detailsLableLayoutParams.addRule(RelativeLayout.BELOW,somethingElse.getId());
    		detailsLableLayoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.sideMargin), getResources().getDimensionPixelSize(R.dimen.fromUnrelatedViewVMargin), 0, 0);
    		detailsLableLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    		detailsLabel.setLayoutParams(detailsLableLayoutParams);
    	}*/
    	somethingElse.setText(mulikaDataCarrier.getSomethingElse());
    	details.setText(mulikaDataCarrier.getIncidentDetails());
    }
    
    @Override
	public void onClick(View v)
    {
		if(v==extraInfoButton)
		{
			extraInfoButtonClicked();
		}
		else if(v==mulikaButton)
		{
			//Toast.makeText(this, nameTextBox.getText(), Toast.LENGTH_LONG).show();
			if(profile.getNumberOfAliases()==0 && (nameTextBox.getText().toString().split(" ").length==0 || nameTextBox.getText().toString().equals("")))
			{
				Toast.makeText(this, "You have to enter either the name of the person or something else that can help identify the person by pressing the + button", Toast.LENGTH_LONG).show();
			}
			else
			{
				if(details.getText().toString().equals("") || details.getText().toString().split(" ").length==0)
				{
					Toast.makeText(this, "What did the person do?", Toast.LENGTH_LONG).show();
				}
				else
				{
					profile.setName(nameTextBox.getText().toString());
					if(somethingElse.getVisibility()==AutoCompleteTextView.VISIBLE)
					{
						profile.setPost(somethingElse.getText().toString());
					}
					else
					{
						if(extraPosts[post.getSelectedItemPosition()]=="I don't know")
						{
							profile.setPost("");
						}
						else		
						{
							profile.setPost(extraPosts[post.getSelectedItemPosition()]);
						}
					}
					Log.d("incident post", profile.getName());
					Log.d("incident post", profile.getPost());
					/*Thread thread=new Thread(new Runnable()
					{
						
						@Override
						public void run()
						{
							if(charazaData.postIncidet(profile, details.getText().toString()))
							{
								Toast.makeText(context, "Incident successfully submitted", Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(context, "Something went wrong while trying to submit the incident", Toast.LENGTH_LONG).show();
							}
						}
					});
					thread.run();
					Intent intent=new Intent(Mulika.this, Ranks.class);
					startActivity(intent);*/
					mulikaButton.setImageBitmap(mulikaButtonClickedImage);
					mulikaButton.setClickable(false);
					new PostIncidentThread().execute(0);
				}
			}
		}
		
	}
    
    private class PostIncidentThread extends AsyncTask<Integer, Integer, Boolean>
    {

		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			return charazaData.postIncidet(profile, details.getText().toString());
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			Log.d("Progress", String.valueOf(values[0]*2));
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			if(result)
			{
				Toast.makeText(context, "Incident successfully submitted", Toast.LENGTH_LONG).show();
				Intent intent=new Intent(Mulika.this, Latest.class);
				intent.putExtra("networkCheckStatus", networkCheckStatus);
				//charazaData.closeDatabase();
				mulikaButton.setImageBitmap(mulikaButtonUnclickedImage);
				mulikaButton.setClickable(true);
				startActivity(intent);
			}
			else
			{
				mulikaButton.setImageBitmap(mulikaButtonUnclickedImage);
				mulikaButton.setClickable(true);
				Toast.makeText(context, "Something went wrong! Try resending", Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
		
    	
    }
    
    /*private class MyGestureDetector extends SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			Log.d("Gestures", "MyGestureDetector called");
			try
			{
				Log.d("Gestures","calculating gesture");
				if(Math.abs(e1.getY() - e2.getY())>SWIPE_MAX_OFF_PATH)
				{
					return false;
				}
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					Log.d("Gestures", "left swipe detected");
					Intent intent=new Intent(Mulika.this, Ranks.class);
					intent.putExtra("networkCheckStatus", networkCheckStatus);
					startActivity(intent);
					
				}
				else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					Log.d("Gestures", "right swipe detected");
					//TODO: do right swipe shit
				}
			}
			catch (Exception e)
			{
				Log.d("Gestures", "somethine went wrong with the gestures//////////////\n"+e.getMessage());
			}
			return false;
		}
	}*/
    
    private class GetIsFirstTimeMulikaTread extends AsyncTask<Integer, Integer, Integer>
    {

		@Override
		protected Integer doInBackground(Integer... params)
		{
			if(charazaData.isFirstTimeMulika())
			{
				return 1;//true
			}
			else
			{
				return 0;//false
			}
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			if(result==1)
			{
				isFirstTimeMulika=true;
			}
			else
			{
				isFirstTimeMulika=false;
			}
			super.onPostExecute(result);
		}
    	
    }
    
    private class GetProfilesThread extends AsyncTask<Integer, Integer, String[]>
    {

		@Override
		protected String[] doInBackground(Integer... params) 
		{
			
			String[][] profiles=charazaData.getProfiles();
			if(profiles!=null)
			{
				int count=0;
		        String[] profileNames=new String[profiles.length];
		        while(count<profiles.length)
		        {
		        	profileNames[count]=profiles[count][1];//we want the names only
		        	count++;
		        }
		        return profileNames;
			}
			else
			{
				return null;
			}
		}

		@Override
		protected void onPostExecute(String[] result)
		{
			if(result!=null)
			{
				setNameSuggestions(result);
			}
			else
			{
				Log.e("getProfiles()", "getProfiles() retured null probably becuase the database is already closed");
			}
			if(splashScreenFlag==false)
			{
				splashScreenFlag=true;
				//splashScreen.dismiss();
				//nameTextBox.requestFocus();
				hideSplashScreenName();
			}
			super.onPostExecute(result);
		}
    }

    private class GetPostsThread extends AsyncTask<Integer, Integer, String[]>
    {

		@Override
		protected String[] doInBackground(Integer... params)
		{
			String[][] posts=charazaData.getPosts();
			if(posts!=null)
			{
				int count=0;
		        String[] postNames=new String[posts.length];
		        while(count<posts.length)
		        {
		        	postNames[count]=posts[count][1];
		        	count++;
		        }
		        return postNames;
			}
			else
			{
				return null;
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			Log.d("progress", String.valueOf(values));
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String[] result)
		{
			if(result!=null)
			{
				setPostSuggestions(result);
			}
			else
			{
				Log.e("getPosts()", "getPosts() returned null probably because the database is already closed");
			}
			if(splashScreenFlag==false)
			{
				splashScreenFlag=true;
				//splashScreen.dismiss();
				//nameTextBox.requestFocus();
				hideSplashScreenName();
			}
			super.onPostExecute(result);
		}
    	
    }
    
    /*private class Initializer implements Runnable// fetch all the things from the database and server without affecting UI performance. keep it clean
    {

		@Override
		public void run() 
		{
			//GET PROFILE NAMES
	        String[][] profiles=charazaData.getProfiles();
	        int count=0;
	        String[] profileNames=new String[profiles.length];
	        while(count<profiles.length)
	        {
	        	profileNames[count]=profiles[count][1];//we want the names only
	        	count++;
	        }
	        setNameSuggestions(profileNames);
	        
	        //GET POST TYPES
	        String[][] posts=charazaData.getPosts();
	        count=0;
	        String[] postNames=new String[posts.length];
	        while(count<posts.length)
	        {
	        	postNames[count]=posts[count][1];
	        	count++;
	        }
	        setPostSuggestions(postNames);
	        splashScreen.dismiss();
		}
    }*/

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
	{
		Log.d("onitemselectedListener", "itemselectedlistener called");
		Log.d("position of selected item", String.valueOf(position));
		Log.d("size of extraposts", String.valueOf(extraPosts.length));
		if(extraPosts!=null && position<extraPosts.length)
		{
			//TODO:deferenciate different views
			String item=extraPosts[position];
			if(item.contains("Something else"))
			{
				post.setVisibility(Spinner.INVISIBLE);
				somethingElse.setVisibility(AutoCompleteTextView.VISIBLE);
				somethingElse.requestFocus();
				nameTextBox.setNextFocusDownId(R.id.somethingElse);
				details.setNextFocusUpId(R.id.somethingElse);
			}
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
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
			Intent intent=new Intent(Mulika.this, Ranks.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			//charazaData.closeDatabase();
			mulikaButton.setImageBitmap(mulikaButtonUnclickedImage);
			mulikaButton.setClickable(true);
			startActivity(intent);
		}
		else if(itemId==R.id.latestSideNavigation)
		{
			Intent intent=new Intent(Mulika.this, Latest.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			//charazaData.closeDatabase();
			mulikaButton.setImageBitmap(mulikaButtonUnclickedImage);
			mulikaButton.setClickable(true);
			startActivity(intent);
		}
		else if(itemId==R.id.aboutSideNavigation)
		{
			Intent intent=new Intent(Mulika.this, About.class);
			intent.putExtra("networkCheckStatus", networkCheckStatus);
			//charazaData.closeDatabase();
			mulikaButton.setImageBitmap(mulikaButtonUnclickedImage);
			mulikaButton.setClickable(true);
			startActivity(intent);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		if(v==extraInfoButton)
		{
			if(hasFocus)
			{
				extraInfoButton.setBackgroundColor(getResources().getColor(R.color.extraInfoButtonFocusedColor));
			}
			else
			{
				extraInfoButton.setBackgroundColor(getResources().getColor(R.color.extraInfoOutOfFocusColor));
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		if(v==extraInfoButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				extraInfoButton.setImageBitmap(extraInfoButtonClickedImage);
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				extraInfoButton.setImageBitmap(extraInfoButtonUnclickedImage);
				
				extraInfoButtonClicked();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				extraInfoButton.setImageBitmap(extraInfoButtonUnclickedImage);
			}
		}
		else if(v==welcomeDoneButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				welcomeDoneButton.setBackgroundColor(getResources().getColor(R.color.normalButtonFocusedBackgroundColor));
				welcomeDoneButton.setTextColor(getResources().getColor(R.color.normalButtonFocusedTextColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				welcomeDoneButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				welcomeDoneButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
				welcomeDialog.dismiss();
				mulikaInstructionsDialog.show();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				welcomeDoneButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				welcomeDoneButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
			}
		}
		else if(v==mulikaInstructionsButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				mulikaInstructionsButton.setBackgroundColor(getResources().getColor(R.color.normalButtonFocusedBackgroundColor));
				mulikaInstructionsButton.setTextColor(getResources().getColor(R.color.normalButtonFocusedTextColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				mulikaInstructionsButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				mulikaInstructionsButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
				mulikaInstructionsDialog.dismiss();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				mulikaInstructionsButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				mulikaInstructionsButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
			}
		}
		return true;
	}
	
	private void hideSplashScreenName()
	{
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		Animation hideNameAnimation=new TranslateAnimation(0, -displaymetrics.widthPixels, 0, 0);
		hideNameAnimation.setDuration(500);
		hideNameAnimation.setAnimationListener(new Animation.AnimationListener()
		{
			
			@Override
			public void onAnimationStart(Animation animation)
			{
				showSplashScreenSlogan();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) 
			{
				splashScreenName.setVisibility(TextView.GONE);
				/*if(splashScreen.isShowing())
				{
					splashScreen.dismiss();
					nameTextBox.requestFocus();
				}*/
				
			}
		});
		splashScreenName.clearAnimation();
		splashScreenName.startAnimation(hideNameAnimation);
	}
	
	private void showSplashScreenSlogan()
	{
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		Animation showSplashScreenSloganAnimation=new TranslateAnimation(displaymetrics.widthPixels, 0, 0, 0);
		showSplashScreenSloganAnimation.setDuration(500);
		
		showSplashScreenSloganAnimation.setAnimationListener(new Animation.AnimationListener() 
		{
			
			@Override
			public void onAnimationStart(Animation animation)
			{
				splashScreenSlogan.setVisibility(TextView.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				phantomAnimation();
				
			}
		});
		splashScreenSlogan.clearAnimation();
		splashScreenSlogan.startAnimation(showSplashScreenSloganAnimation);
	}
	
	private void phantomAnimation()
	{
		Animation phantomAnimation=new ScaleAnimation(1, 1, 1, 1);
		phantomAnimation.setDuration(1000);
		phantomAnimation.setAnimationListener(new Animation.AnimationListener()
		{
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				splashScreenName.setVisibility(TextView.GONE);
				splashScreenSlogan.setVisibility(TextView.GONE);
				if(splashScreen.isShowing())
				{
					splashScreen.dismiss();
					nameTextBox.requestFocus();
				}
				if(isFirstTimeMulika)
				{
					welcomeDialog.show();
				}
			}
		});
		splashScreenSlogan.clearAnimation();
		splashScreenSlogan.startAnimation(phantomAnimation);
	}
    
}
