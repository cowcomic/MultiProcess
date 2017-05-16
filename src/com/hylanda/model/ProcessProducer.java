package com.hylanda.model;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ProcessProducer<T,E> implements Runnable
{
	private LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>(1000);
	protected LinkedBlockingQueue<E> queueWrite = new LinkedBlockingQueue<E>(1000);
	public Map<String, String> propMap = null;
	ProcessMain<T, E> pm = null;

	public ProcessProducer(LinkedBlockingQueue<T> queue, LinkedBlockingQueue<E> queueWrite, Map<String, String> propMap, ProcessMain<T, E> pm)
	{
		this.queue = queue;
		this.queueWrite = queueWrite;
		this.propMap = propMap;
		this.pm = pm;
	}

	@Override
	public void run()
	{
		int nCount = 0;
		Thread currentThread = Thread.currentThread();
		Long id = currentThread.getId();
		while (true)
		{
			try
			{
				T t = queue.take();
				if (t == null || IsEmpty(t))
				{
					break;
				}
				else
				{
					execute(t);
					System.out.println(id.toString() + " produce:" + nCount);
					nCount++;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	protected void AddCustomer(E e) throws InterruptedException{
		queueWrite.put(e);
	}
	
	protected abstract boolean Init();
	
	protected abstract boolean UnInit();
	
	protected abstract boolean IsEmpty(T t);

	protected abstract boolean execute(T t);
}
