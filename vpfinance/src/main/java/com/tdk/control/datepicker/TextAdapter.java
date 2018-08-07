package com.tdk.control.datepicker;

import java.util.List;

public class TextAdapter implements WheelAdapter
{
	public static final int DEFAULT_LENGTH = -1;
	private int length;
	private List<String> list;

	public TextAdapter(List<String> list)
	{
		this(list, -1);
	}

	public TextAdapter(List<String> list, int length)
	{
		this.list = list;
		this.length = length;
	}

	public String getItem(int index)
	{
		if ((index >= 0) && (index < this.list.size()))
			return (String)this.list.get(index);
		return null;
	}

	public int getItemsCount()
	{
		return this.list.size();
	}

	public int getMaximumLength()
	{
		return this.length;
	}
}