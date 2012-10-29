package com.charaza;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.charaza.resources.CharazaData;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class IncidentActivity extends SherlockActivity implements View.OnClickListener, ISideNavigationCallback, View.OnTouchListener
{
	private SideNavigationView sideNavigationView;
	private ScrollView scrollView;
	private RelativeLayout relativeLayout;
	private RelativeLayout mainLayout;
	private Button commentButton;
	private Button charazaButton;
	private TextView incidentActivityName;
	private TextView incidentActivityIncident;
	private EditText commentEditText;
	private Button postCommentButton;
	private ProgressBar progressBar;
	private TextView noCommentText;
	
	private CharazaData charazaData;
	private float previousY;
	private int networkCheckStatus=0;
	private int incidentId;
	private int commentButtonStatus=1;//0 is for hidden
	private int buttonAnimationTime;
	private int commentEditTextAnimationTime;
	private int postCommentButtonAnimationTime;
	private Context context;
	private List<TextView> commentTimeList;
	private List<TextView> commentTextList;
	private int commentTextShowAnimationTime;
	private GetCommentsThread commentsThread;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incident_activity);
        
        //initialize views
        mainLayout=(RelativeLayout)this.findViewById(R.id.incidentActivityMainLayout);
        relativeLayout=(RelativeLayout)this.findViewById(R.id.incidentActivityRelativeLayout);
        scrollView=(ScrollView)this.findViewById(R.id.incidentActivityScrollView);
        //scrollView.setOnTouchListener(this);
        int minHeight=0;
        Display display=this.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
        {
        	minHeight=display.getHeight()-150;
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
        	minHeight=display.getHeight()-150;
        }
        //scrollView.setMinimumHeight(minHeight);
        relativeLayout.setMinimumHeight(minHeight);
        
        sideNavigationView=(SideNavigationView)this.findViewById(R.id.side_navigation_view_incident_activity);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		this.setTitle(R.string.profileActivitySubTitle);//TODO: change this to something else if it doesnt work
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		sideNavigationView.setMinimumHeight(minHeight+5);
		
		commentButton=(Button)this.findViewById(R.id.incidentActivityCommentButton);
		//incidentActivityCommentButton.setOnTouchListener(this);
		commentButton.setOnClickListener(this);
		commentButton.setOnTouchListener(this);
		commentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
		charazaButton=(Button)this.findViewById(R.id.incidentActivityCharazaButton);
		charazaButton.setOnTouchListener(this);
		charazaButton.setOnClickListener(this);
		scrollView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,minHeight+5-charazaButton.getLayoutParams().height-commentButton.getLayoutParams().height));
		incidentActivityName=(TextView)this.findViewById(R.id.incidentActivityName);
		incidentActivityIncident=(TextView)this.findViewById(R.id.incidentActivityIncident);
		commentEditText=(EditText)this.findViewById(R.id.incidentActivityCommentEditText);
		postCommentButton=(Button)this.findViewById(R.id.incidentActivityPostCommentButton);
		postCommentButton.setOnClickListener(this);
		postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
		//postCommentButton.setOnTouchListener(this);
		progressBar=(ProgressBar)this.findViewById(R.id.incidentActivityProgressBar);
		noCommentText=(TextView)this.findViewById(R.id.incidentactivityNoCommentText);
		
		//initialize resources
		charazaData=new CharazaData(this);
		previousY=0;
		buttonAnimationTime=200;
		commentEditTextAnimationTime=400;
		postCommentButtonAnimationTime=200;
		commentTextShowAnimationTime=180;
		Bundle bundle=this.getIntent().getExtras();
		networkCheckStatus=bundle.getInt("networkCheckStatus");
		incidentActivityName.setText(bundle.getString("profileText"));
		incidentActivityIncident.setText(bundle.getString("incidentText"));
		incidentId=bundle.getInt("incidentId");
		commentTimeList=new ArrayList<TextView>();
		commentTextList=new ArrayList<TextView>();
		context=this;
		
		//check network connection
        if(!charazaData.checkNetworkConnection() && networkCheckStatus==0)
        {
        	charazaData.networkError();
        	networkCheckStatus=1;
        }
        
        //fetch comments
        commentsThread=new GetCommentsThread();
        commentsThread.execute("all");
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE); 
    	if(this.getCurrentFocus()!=null)
    	{
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	}
    	switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			//sideNavigationView.toggleMenu();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	
	
	private void commentButtonClicked()
	{
		cancleGettingComments(commentsThread);
		noCommentText.setVisibility(TextView.GONE);
		showCommentEditText(buttonAnimationTime);
		commentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
	}
	
	private void cancleGettingComments(GetCommentsThread getCommentsThread)
	{
		getCommentsThread.cancel(true);
		progressBar.setVisibility(ProgressBar.GONE);
	}
	
	private void postCommentButtonClicked()
	{
		postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
		new PostCommentThread().execute(String.valueOf(incidentId),commentEditText.getText().toString());
		hideKeyboard();
		//resetScrollViewLayoutParams();
		//postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
	}
	
	private void charazaButtonClicked()
	{
		charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
		charazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
	}
	
	private void showCommentEditText(int startOffset)
	{
		if(commentTextList.size()>0)
		{
			RelativeLayout.LayoutParams commentEditTextLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.commetEditTextHeight));
			/*android:layout_marginTop="@dimen/fromRelatedViewVMargin"
			        android:layout_marginLeft="@dimen/sideMargin"
			        android:layout_marginRight="@dimen/sideMargin"
			        android:layout_marginBottom="0dp"*/
			commentEditTextLayoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.sideMargin), getResources().getDimensionPixelSize(R.dimen.fromRelatedViewVMargin), getResources().getDimensionPixelSize(R.dimen.sideMargin), 0);
			commentEditTextLayoutParams.addRule(RelativeLayout.BELOW,commentTextList.get(commentTextList.size()-1).getId());
			commentEditText.setLayoutParams(commentEditTextLayoutParams);
		}
		
		Animation showCommentEditTextAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
		showCommentEditTextAnimation.setDuration(commentEditTextAnimationTime);
		showCommentEditTextAnimation.setStartOffset(startOffset);
		showCommentEditTextAnimation.setAnimationListener(new Animation.AnimationListener() 
		{
			
			@Override
			public void onAnimationStart(Animation animation)
			{
				commentEditText.setVisibility(EditText.VISIBLE);
				hideCommentButton();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				commentEditText.requestFocus();
				showKeyboard(commentEditText);
				showPostCommentButton(0);
				//resetScrollViewLayoutParams();
				//scrollToBottom();
			}
		});
		commentEditText.clearAnimation();
		commentEditText.startAnimation(showCommentEditTextAnimation);
	}
	
	private void showPostCommentButton(int startOffset)
	{
		Animation showPostCommentButtonAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
		showPostCommentButtonAnimation.setDuration(postCommentButtonAnimationTime);
		showPostCommentButtonAnimation.setStartOffset(startOffset+50);
		showPostCommentButtonAnimation.setAnimationListener(new Animation.AnimationListener()
		{
			
			@Override
			public void onAnimationStart(Animation animation)
			{
				postCommentButton.setVisibility(Button.VISIBLE);	
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				scrollView.scrollTo(0, commentEditText.getTop()+commentEditText.getHeight());
				relativeLayout.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.commetEditTextHeight));
			}
		});
		postCommentButton.clearAnimation();
		postCommentButton.startAnimation(showPostCommentButtonAnimation);
	}
	
	
	private void hideCommentEditText(int offset)
	{
		//hidePostCommentButton(commentEditTextAnimationTime);
		Animation hideCommentEditTextAnimation=new ScaleAnimation((float)1, (float)1, (float)1, (float)0, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
		hideCommentEditTextAnimation.setDuration(commentEditTextAnimationTime);
		hideCommentEditTextAnimation.setStartOffset(offset+100);
		hideCommentEditTextAnimation.setAnimationListener(new Animation.AnimationListener() 
		{
			
			@Override
			public void onAnimationStart(Animation animation) 
			{
				//movePostCommentButton(commentEditTextAnimationTime);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				commentEditText.setVisibility(EditText.GONE);
				showCommentButton();
				mainLayout.invalidate();
			}
		});
		commentEditText.clearAnimation();
		commentEditText.startAnimation(hideCommentEditTextAnimation);
	}
	
	/*private void movePostCommentButton(int duration)
	{
		Animation movePostCommentButtonAnimation=new TranslateAnimation((float)1, (float)1, (float)0, (float)(commentEditText.getHeight()*0.75));
		movePostCommentButtonAnimation.setDuration(duration);
		movePostCommentButtonAnimation.setAnimationListener(new Animation.AnimationListener()
		{
			
			@Override
			public void onAnimationStart(Animation animation) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				hidePostCommentButton(100);
			}
		});
		postCommentButton.clearAnimation();
		postCommentButton.startAnimation(movePostCommentButtonAnimation);
	}*/
	
	private void hidePostCommentButton(int startOffset)
	{
		Animation hidePostCommentButtonAnimation=new ScaleAnimation((float)1, (float)1, (float)1, (float)0, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
		hidePostCommentButtonAnimation.setDuration(postCommentButtonAnimationTime);
		hidePostCommentButtonAnimation.setStartOffset(startOffset+50);
		hidePostCommentButtonAnimation.setAnimationListener(new Animation.AnimationListener()
		{
			
			@Override
			public void onAnimationStart(Animation animation)
			{
					
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				relativeLayout.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.fromRelatedViewVMargin));
				postCommentButton.setVisibility(Button.GONE);
				hideCommentEditText(0);
				//hideKeyboard();
				//showCommentButton();
			}
		});
		postCommentButton.clearAnimation();
		postCommentButton.startAnimation(hidePostCommentButtonAnimation);
	}
	
	private void scrollToBottom()
	{
		scrollView.post(new Runnable() 
		{

	        @Override
	        public void run() 
	        {
	        	scrollView.fullScroll(ScrollView.FOCUS_DOWN);
	        }
	    });
	}
	
	private void showKeyboard(EditText editText)
	{
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}
	
	private void hideKeyboard()
	{
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    	if(this.getCurrentFocus()!=null)
    	{
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	}
	}
	
	private void showCommentButton()
	{
		if(commentButtonStatus==0 && postCommentButton.getVisibility()==Button.GONE)
		{
			Animation showButtonAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)1, Animation.RELATIVE_TO_SELF, (float)1);
			showButtonAnimation.setDuration(buttonAnimationTime);
			showButtonAnimation.setAnimationListener(new Animation.AnimationListener() 
			{
				
				@Override
				public void onAnimationStart(Animation animation) 
				{
					commentButton.setVisibility(Button.VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					resetScrollViewLayoutParams();
					mainLayout.invalidate();
				}
			});
			commentButton.clearAnimation();
			commentButton.startAnimation(showButtonAnimation);
			commentButtonStatus=1;
		}
	}
	
	private void resetScrollViewLayoutParams()
	{
		if(commentButton.getVisibility()==Button.GONE)
		{
			RelativeLayout.LayoutParams scrollViewLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mainLayout.getHeight()-charazaButton.getHeight());
			scrollView.setLayoutParams(scrollViewLayoutParams);
		}
		else
		{
			RelativeLayout.LayoutParams scrollViewLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mainLayout.getHeight()-charazaButton.getHeight()-commentButton.getHeight());
			scrollView.setLayoutParams(scrollViewLayoutParams);
		}
	}
	
	private void hideCommentButton()
	{
		if(commentButtonStatus==1)
		{
			Animation hideButtonAnimation=new ScaleAnimation((float)1, (float)1, (float)1, (float)0, Animation.RELATIVE_TO_SELF, (float)1, Animation.RELATIVE_TO_SELF, (float)1);
			hideButtonAnimation.setDuration(buttonAnimationTime);
			hideButtonAnimation.setAnimationListener(new Animation.AnimationListener() 
			{
				
				@Override
				public void onAnimationStart(Animation animation) 
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					commentButton.setVisibility(Button.GONE);
					resetScrollViewLayoutParams();
				}
			});
			commentButton.clearAnimation();
			commentButton.startAnimation(hideButtonAnimation);
			commentButtonStatus=0;
		}
	}

	@Override
	public void onSideNavigationItemClick(int itemId)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) 
	{
		if(view==postCommentButton)
		{
			postCommentButtonClicked();
		}
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		if(view==commentButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				commentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				commentButtonClicked();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				commentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
			}
		}
		/*else if(view==postCommentButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonFocusedColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
				postCommentButtonClicked();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
			}
		}*/
		else if(view==charazaButton)
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				hideCommentButton();
				charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonFocusedBackgroundColor));
				charazaButton.setTextColor(getResources().getColor(R.color.normalButtonFocusedTextColor));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{
				showCommentButton();
				charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				charazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
				charazaButtonClicked();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				showCommentButton();
				charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
				charazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
			}
		}
		return true;
	}
	
	public void addComments(String[][] comments)
    {
    	int count=0;
    	while(count<comments.length)
    	{
    		TextView commentTime=new TextView(this);
    		commentTime.setId(23542+commentTimeList.size());
    		commentTime.setText(comments[count][2]);
    		RelativeLayout.LayoutParams commentTimeLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
    		
    		if(commentTextList.size()==0)//first comment in the list
    		{
    			commentTimeLayoutParams.addRule(RelativeLayout.BELOW,incidentActivityIncident.getId());
    		}
    		
    		else
    		{
    			commentTimeLayoutParams.addRule(RelativeLayout.BELOW,commentTextList.get(commentTextList.size()-1).getId());//below the last comment text
    		}
    		Display display=this.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int commentTimeLayoutParamsTopMargin=0;
            int commentTimeSideMargin=0;
            int commentTimeTextSize=0;
            int commentTextLayoutParamsTopMargin=0;
            int commentTextSideMargin=0;
            int commentTextSize=0;
            if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
            {
            	if(count==0)
            	{
            		commentTimeLayoutParamsTopMargin = 35; 
            	}
            	else
            	{
            		commentTimeLayoutParamsTopMargin = 29; 
            	}
            	commentTimeSideMargin=35;
            	commentTimeTextSize=13;
            	commentTextLayoutParamsTopMargin=7;
            	commentTextSideMargin=35;
            	commentTextSize=15;
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
            {
            	if(count==0)
            	{
            		commentTimeLayoutParamsTopMargin = 27; 
            	}
            	else
            	{
            		commentTimeLayoutParamsTopMargin = 21; 
            	}
            	commentTimeSideMargin=27;
            	commentTimeTextSize=13;
            	commentTextLayoutParamsTopMargin=6;
            	commentTextSideMargin=27;
            	commentTextSize=15;
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM)
            {
            	if(count==0)
            	{
            		commentTimeLayoutParamsTopMargin = 21; 
            	}
            	else
            	{
            		commentTimeLayoutParamsTopMargin = 15; 
            	}
            	commentTimeSideMargin=21;
            	commentTimeTextSize=12;
            	commentTextLayoutParamsTopMargin=4;
            	commentTextSideMargin=21;
            	commentTextSize=14;
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
            {
            	if(count==0)
            	{
            		commentTimeLayoutParamsTopMargin = 18; 
            	}
            	else
            	{
            		commentTimeLayoutParamsTopMargin = 11; 
            	}
            	commentTimeSideMargin=18;
            	commentTimeTextSize=12;
            	commentTextLayoutParamsTopMargin=3;
            	commentTextSideMargin=18;
            	commentTextSize=14;
            }
            else
            {
            	if(count==0)
            	{
            		commentTimeLayoutParamsTopMargin = 35; 
            	}
            	else
            	{
            		commentTimeLayoutParamsTopMargin = 29; 
            	}
            	commentTimeSideMargin=35;
            	commentTimeTextSize=13;
            	commentTextLayoutParamsTopMargin=7;
            	commentTextSideMargin=35;
            	commentTextSize=15;
            }
    		commentTimeLayoutParams.topMargin=commentTimeLayoutParamsTopMargin;
    		commentTimeLayoutParams.leftMargin=commentTimeSideMargin;
    		commentTimeLayoutParams.rightMargin=commentTimeSideMargin/2;
    		commentTime.setTextColor(getResources().getColor(R.color.incidentTimeTextColor));
    		commentTime.setTextSize(commentTimeTextSize);
    		commentTime.setLayoutParams(commentTimeLayoutParams);
    		relativeLayout.addView(commentTime);
    		commentTimeList.add(commentTime);
    		Animation showTimeAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
			showTimeAnimation.setDuration(commentTextShowAnimationTime);
			showTimeAnimation.setStartOffset(count*commentTextShowAnimationTime);
			commentTime.clearAnimation();
			commentTime.startAnimation(showTimeAnimation);
    		
    		final TextView commentText=new TextView(this);
    		commentText.setId(4672+commentTextList.size());
    		commentText.setText(comments[count][1]);
    		RelativeLayout.LayoutParams commentTextLayoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    		commentTextLayoutParams.addRule(RelativeLayout.BELOW,commentTime.getId());
    		commentTextLayoutParams.topMargin=commentTextLayoutParamsTopMargin;
    		commentTextLayoutParams.leftMargin=commentTextSideMargin;
    		commentTextLayoutParams.rightMargin=commentTextSideMargin/2;
    		commentText.setTextColor(getResources().getColor(R.color.normalTextColor));
    		commentText.setTextSize(commentTextSize);
    		commentText.setLayoutParams(commentTextLayoutParams);
    		relativeLayout.addView(commentText);
    		commentTextList.add(commentText);
    		Animation showIncidentAnimation=new ScaleAnimation((float)1, (float)1, (float)0, (float)1, Animation.RELATIVE_TO_SELF, (float)0, Animation.RELATIVE_TO_SELF, (float)0);
    		showIncidentAnimation.setDuration(commentTextShowAnimationTime);
    		showIncidentAnimation.setStartOffset(count*commentTextShowAnimationTime+commentTextShowAnimationTime/2);
			commentText.clearAnimation();
			commentText.startAnimation(showIncidentAnimation);
    		
    		count++;
    	}
    }
	
	private class GetCommentsThread extends AsyncTask<String, Integer, String[][]>
	{

		@Override
		protected void onPreExecute() 
		{
			progressBar.setVisibility(ProgressBar.VISIBLE);
			noCommentText.setVisibility(TextView.GONE);
			super.onPreExecute();
		}

		@Override
		protected String[][] doInBackground(String... arg0)
		{
			return charazaData.getComments(incidentId, arg0[0]);
		}

		@Override
		protected void onPostExecute(String[][] result)
		{
			progressBar.setVisibility(ProgressBar.GONE);
			if(result!=null)
			{
				addComments(result);
			}
			else
			{
				Log.e("getComments()", "getComments() returned null");
				if(commentTextList.size()==0)
				{
					noCommentText.setVisibility(TextView.VISIBLE);
				}
			}
			super.onPostExecute(result);
		}
		
	}
	
	private class PostCommentThread extends AsyncTask<String, Integer, Boolean>
	{

		@Override
		protected void onPreExecute()
		{
			commentEditText.setEnabled(false);
			postCommentButton.setClickable(false);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params)
		{
			//postCommet(incident,comment)
			Log.d("comment incident", params[0]);
			Log.d("comment comment", params[1]);
			if(params[1]!=null && params[1].trim().length()>0)
			{
				return charazaData.postComment(Integer.parseInt(params[0]), params[1]);
			}
			else
			{
				return false;
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			if(result)
			{
				commentEditText.setText("");
				Toast.makeText(context, "Your comment has been added", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(context, "A problem occured while trying to add your comment", Toast.LENGTH_LONG).show();
			}
			postCommentButton.setClickable(true);
			postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
			commentEditText.setEnabled(true);
			hidePostCommentButton(0);
			if(commentTimeList.size()>0)
			{
				new GetCommentsThread().execute(commentTimeList.get(commentTimeList.size()-1).getText().toString());
			}
			else
			{
				new GetCommentsThread().execute("all");
			}
			
			super.onPostExecute(result);
		}
		
	}
}
