package br.org.cn.ressuscitou;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ActivityImageView extends Activity {

	ImageView mImageView;
	PhotoViewAttacher mAttacher;
	@Override
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);

		Intent intent = getIntent();
		String imagem = intent.getStringExtra("imagem");
		
		 // Any implementation of ImageView can be used!
	    mImageView = findViewById(R.id.imageView);

	    // Set the Drawable displayed
	    if (imagem.equals("acordes")){
	    Drawable bitmap = getResources().getDrawable(R.drawable.acordes);
	    mImageView.setImageDrawable(bitmap);
	    }
	    if (imagem.equals("arpejos")){
	    Drawable bitmap = getResources().getDrawable(R.drawable.arpejos);
	    mImageView.setImageDrawable(bitmap);
	    }

	    // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
	    mAttacher = new PhotoViewAttacher(mImageView);
		
	}
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

}