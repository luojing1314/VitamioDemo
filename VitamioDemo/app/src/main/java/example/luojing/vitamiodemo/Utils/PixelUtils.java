package example.luojing.vitamiodemo.Utils;

import android.content.Context;

public class PixelUtils {
	public static int dip2px(Context context,int dip){
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dip*density+0.5f);
	}
	public static int px2dip(Context context,int px){
		float density = context.getResources().getDisplayMetrics().density;
		return (int)(px/density+0.5f);
	}
}
