package com.mycompany.a4;

import com.codename1.charts.models.Point;
import com.codename1.ui.Graphics;

public interface ISelectable {
	//From notes (7b - Interactive II)
	public void setSelected(boolean b);
	public boolean isSelected();
	public boolean contains(float[] fPtr);
}
