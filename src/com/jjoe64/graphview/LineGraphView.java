/**
 * This file is part of GraphView.
 *
 * GraphView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GraphView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GraphView.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 *
 * Copyright Jonas Gehring
 */

package com.jjoe64.graphview;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

/**
 * Line Graph View. This draws a line chart.
 */
public class LineGraphView extends GraphView {
	private final Paint paintBackground;
	private boolean drawBackground;

	public LineGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paintBackground = new Paint();
		paintBackground.setColor(Color.rgb(20, 40, 60));
		paintBackground.setStrokeWidth(4);
	}

	public LineGraphView(Context context, String title) {
		super(context, title);

		paintBackground = new Paint();
		paintBackground.setColor(Color.rgb(20, 40, 60));
		paintBackground.setStrokeWidth(4);
	}

	@Override
	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight, float border, double minX, double minY, double diffX, double diffY, float horstart, GraphViewSeriesStyle style, boolean setPadding) {

		// draw background
		double lastEndY = 0;
		double lastEndX = 0;
		if (drawBackground) {
			float startY = graphheight + border;
			for (int i = 0; i < values.length; i++) {
				double valY = values[i].getY() - minY;
				double ratY = valY / diffY;
				double y = graphheight * ratY;

				double valX = values[i].getX() - minX;
				double ratX = valX / diffX;
				double x = graphwidth * ratX;

				float endX = (float) x + (horstart + 1);
				float endY = (float) (border - y) + graphheight +2;

				if (i > 0) {
					// fill space between last and current point
					double numSpace = ((endX - lastEndX) / 3f) +1;
					for (int xi=0; xi<numSpace; xi++) {
						float spaceX = (float) (lastEndX + ((endX-lastEndX)*xi/(numSpace-1)));
						float spaceY = (float) (lastEndY + ((endY-lastEndY)*xi/(numSpace-1)));

						// start => bottom edge
						float startX = spaceX;

						// do not draw over the left edge
						if (startX-horstart > 1) {
							canvas.drawLine(startX, startY, spaceX, spaceY, paintBackground);
						}
					}
				}

				lastEndY = endY;
				lastEndX = endX;
			}
		}

		// draw data
		paint.setStrokeWidth(style.thickness);
		paint.setColor(style.color);

		lastEndY = 0;
		lastEndX = 0;
		for (int i = 0; i < values.length; i++) {
			double valY = values[i].getY() - minY;
			double ratY = valY / diffY;
			double y = graphheight * ratY;

			double valX = values[i].getX() - minX;
			double ratX = valX / diffX;
			double x = graphwidth * ratX;

			float endX = (float) x + (horstart + 1);
			float endY = (float) (border - y) + graphheight;
			if (!setPadding || (values[i].getX() > GraphViewSeries.firstX && values[i].getX() < GraphViewSeries.lastX)) {
//				LGtempPopupXYs.add(new PointF((float)values[i].getX(), (float)values[i].getY()));
				if (lastEndX == 0 && lastEndY == 0) {
					lastEndX = x;
					lastEndY = y;
				}
				float startX = (float) lastEndX + (horstart + 1);
				float startY = (float) (border - lastEndY) + graphheight;
				
				canvas.drawLine(startX, startY, endX, endY, paint);
				if (LGdrawCircles) {
					canvas.drawCircle((float)(x), (float)(graphheight - y + border), 5, paint);
					values[i].setPopupX((float)(x + verLabelTextWidth + 20));
					values[i].setPopupY((float)(graphheight - y + border));
					LGPoints.add(values[i]);
				}
				lastEndY = y;
				lastEndX = x;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return touchPoints(event, true);
	}

	public int getBackgroundColor() {
		return paintBackground.getColor();
	}

	public boolean getDrawBackground() {
		return drawBackground;
	}

	public boolean getDrawCircles() {
		return LGdrawCircles;
	}

	@Override
	public void setBackgroundColor(int color) {
		paintBackground.setColor(color);
	}

	/**
	 * @param drawBackground true for a light blue background under the graph line
	 */
	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}

	public void setDrawCircles(boolean drawCircles) {
		this.LGdrawCircles = drawCircles;
	}

	public void setPopupBuilder(AlertDialog builder, TextView tv) {
		this.LG_builder = builder;
		this.LG_tv = tv;
	}
}