package com.charaza;

import com.charaza.resources.CharazaCanvas;

import android.app.Activity;
import android.os.Bundle;

public class CharazaActivity extends Activity
{
	private CharazaCanvas canvas;
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		canvas=new CharazaCanvas(this);
		this.setContentView(canvas);
	}
}
