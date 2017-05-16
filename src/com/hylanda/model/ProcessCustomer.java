package com.hylanda.model;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ProcessCustomer<E> implements Runnable
{
	private LinkedBlockingQueue<E> queueWrite = new LinkedBlockingQueue<E>(1000);
	public Map<String, String> propMap = null;
	ProcessMain pm = null;

	public ProcessCustomer(LinkedBlockingQueue<E> queueWrite, Map<String, String> propMap, ProcessMain pm)
	{
		this.queueWrite = queueWrite;
		this.propMap = propMap;
		this.pm = pm;
	}

	@Override
	public void run()
	{
		int nCount = 0;
		while (true)
		{
			try
			{
				E e = queueWrite.take();
				if (e == null || IsEmpty(e))
				{
					break;
				}
				else
				{
					execute(e);
					nCount++;
					System.out.println("consume:" + nCount);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	protected abstract boolean Init();

	protected abstract boolean UnInit();

	protected abstract boolean IsEmpty(E t);

	protected abstract boolean execute(E t);

}
