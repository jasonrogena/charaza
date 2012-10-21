package com.charaza;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.R;
import com.devspark.sidenavigation.SideNavigationView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
	
	private float previousY;
	private int networkCheckStatus=0;
	private int incidentId;
	private int commentButtonStatus=1;//0 is for hidden
	private int buttonAnimationTime;
	private int commentEditTextAnimationTime;
	private int postCommentButtonAnimationTime;
	private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incident_activity);
        
        mainLayout=(RelativeLayout)this.findViewById(R.id.incidentActivityMainLayout);
        relativeLayout=(RelativeLayout)this.findViewById(R.id.incidentActivityRelativeLayout);
        scrollView=(ScrollView)this.findViewById(R.id.incidentActivityScrollView);
        scrollView.setOnTouchListener(this);
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
		charazaButton=(Button)this.findViewById(R.id.incidentActivityCharazaButton);
		charazaButton.setOnTouchListener(this);
		charazaButton.setOnClickListener(this);
		scrollView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,minHeight+5-charazaButton.getLayoutParams().height-commentButton.getLayoutParams().height));
		incidentActivityName=(TextView)this.findViewById(R.id.incidentActivityName);
		incidentActivityIncident=(TextView)this.findViewById(R.id.incidentActivityIncident);
		commentEditText=(EditText)this.findViewById(R.id.incidentActivityCommentEditText);
		postCommentButton=(Button)this.findViewById(R.id.incidentActivityPostCommentButton);
		postCommentButton.setOnClickListener(this);
		postCommentButton.setOnTouchListener(this);
		
		previousY=0;
		buttonAnimationTime=200;
		commentEditTextAnimationTime=400;
		postCommentButtonAnimationTime=200;
		Bundle bundle=this.getIntent().getExtras();
		networkCheckStatus=bundle.getInt("networkCheckStatus");
		incidentActivityName.setText(bundle.getString("profileText"));
		incidentActivityIncident.setText(bundle.getString("incidentText"));
		incidentId=bundle.getInt("incidentId");
		context=this;
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
		showCommentEditText(buttonAnimationTime);
		commentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
	}
	
	private void postCommentButtonClicked()
	{
		//hideCommentEditText();
		hideKeyboard();
		hidePostCommentButton(0);
		postCommentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
	}
	
	private void charazaButtonClicked()
	{
		charazaButton.setBackgroundColor(getResources().getColor(R.color.normalButtonBackgroundColor));
		charazaButton.setTextColor(getResources().getColor(R.color.normalButtonTextColor));
	}
	
	private void showCommentEditText(int startOffset)
	{
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
				showPostCommentButton(0);
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
				// TODO Auto-generated method stub
				
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
				postCommentButton.setVisibility(Button.GONE);
				hideCommentEditText(0);
				//hideKeyboard();
				//showCommentButton();
			}
		});
		postCommentButton.clearAnimation();
		postCommentButton.startAnimation(hidePostCommentButtonAnimation);
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
					
				}
			});
			commentButton.clearAnimation();
			commentButton.startAnimation(showButtonAnimation);
			commentButtonStatus=1;
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
		if(view==commentButton)
		{
			
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
				commentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
				commentButtonClicked();
			}
			else if(event.getAction()==MotionEvent.ACTION_CANCEL)
			{
				commentButton.setBackgroundColor(getResources().getColor(R.color.aliasButtonBackground));
			}
		}
		else if(view==postCommentButton)
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
		}
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
}
