package com.hylanda.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ProcessMain<T, E>
{
	protected LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>(1000);
	protected LinkedBlockingQueue<E> queueWrite = new LinkedBlockingQueue<E>(1000);

	private Thread[] threads;
	private Thread[] threadWs;
	private ProcessProducer<T, E>[] producers;
	private ProcessCustomer<E>[] customers;

	protected int iDealThreadCnt = 1;
	protected int iDealWThreadCnt = 1;
	protected Map<String, String> propMap = new HashMap<String, String>();

	protected boolean Init()
	{
		OpenThread();
		return true;
	}

	private void OpenThread()
	{
		// 开启消费线程
		threads = new Thread[iDealThreadCnt];
		producers = new ProcessProducer[iDealThreadCnt];
		for (int i = 0; i < iDealThreadCnt; i++)
		{
			ProcessProducer<T, E> newProducer = newProducer(queue, queueWrite, propMap);
			newProducer.Init();
			Thread thread = new Thread(newProducer);// .start();
			thread.start();
			producers[i] = newProducer;
			threads[i] = thread;
		}
		threadWs = new Thread[iDealWThreadCnt];
		customers = new ProcessCustomer[iDealWThreadCnt];
		for (int i = 0; i < iDealWThreadCnt; i++)
		{
			ProcessCustomer<E> newCustomer = newCustomer(queueWrite, propMap);
			if (newCustomer != null)
			{
				newCustomer.Init();
				Thread threadW = new Thread(newCustomer);
				threadW.start();
				customers[i] = newCustomer;
				threadWs[i] = threadW;
			}
		}
	}

	protected void Uninit()
	{
		// 停止线程
		for (int i = 0; i < iDealThreadCnt * 2; i++)
		{
			try
			{
				PutProducerEmptyData();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		while (true)
		{
			if (IsAllClose(threads))
			{
				break;
			}
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		for (ProcessProducer<T, E> p : producers)
		{
			p.UnInit();
		}
		for (int i = 0; i < iDealWThreadCnt * 2; i++)
		{
			try
			{
				PutCustomerEmptyData();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		while (true)
		{
			if (IsAllClose(threadWs))
			{
				break;
			}
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		for (ProcessCustomer<E> c : customers)
		{
			if (c != null)
			{
				c.UnInit();
			}
		}
	}

	private static boolean IsAllClose(Thread[] threads)
	{
		for (Thread thread : threads)
		{
			if (thread != null && thread.isAlive())
			{
				return false;
			}
		}
		return true;
	}

	protected abstract void execute();

	protected abstract void PutProducerEmptyData() throws InterruptedException;

	protected abstract void PutCustomerEmptyData() throws InterruptedException;

	protected abstract ProcessProducer<T, E> newProducer(LinkedBlockingQueue<T> queue,
			LinkedBlockingQueue<E> queueWrite, Map<String, String> propMap);

	protected abstract ProcessCustomer<E> newCustomer(LinkedBlockingQueue<E> queueWrite, Map<String, String> propMap);
}
