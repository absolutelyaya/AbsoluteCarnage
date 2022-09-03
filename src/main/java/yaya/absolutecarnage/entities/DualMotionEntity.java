package yaya.absolutecarnage.entities;

public interface DualMotionEntity
{
	boolean isGroundNavigating();
	
	void switchNavigator(boolean onLand);
	
	void switchNavigator();
}
