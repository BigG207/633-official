package com.rs.cores;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.rs.GameConstants;
import com.rs.net.decoders.WorldPacketsDecoder;
import com.rs.utilities.CatchExceptionRunnable;

public final class CoresManager {

	protected static volatile boolean shutdown;
	public static ExecutorService serverWorkerChannelExecutor;
	public static ExecutorService serverBossChannelExecutor;
	public static ScheduledExecutorService slowExecutor;
	public static int serverWorkersCount;
	public static WorldThread worldThread;

	public static void init() {
		WorldPacketsDecoder.loadPacketSizes();
		worldThread = new WorldThread();
		
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		serverWorkersCount = availableProcessors >= 6 ? availableProcessors - (availableProcessors >= 12 ? 6 : 4) : 2;
		serverWorkerChannelExecutor = Executors.newFixedThreadPool(serverWorkersCount, new DecoderThreadFactory());
		serverBossChannelExecutor = Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		slowExecutor = availableProcessors >= 6
				? Executors.newScheduledThreadPool(availableProcessors >= 12 ? 4 : availableProcessors >= 6 ? 2 : 1, new SlowThreadFactory())
				: Executors.newSingleThreadScheduledExecutor(new SlowThreadFactory());
		worldThread.start();		
		
	}

	public static void shutdown() {
		serverWorkerChannelExecutor.shutdown();
		serverBossChannelExecutor.shutdown();
		slowExecutor.shutdown();
		shutdown = true;
	}

	public static void schedule(Runnable task, int delay) {
		slowExecutor.schedule(new CatchExceptionRunnable(task), delay * GameConstants.WORLD_CYCLE_MS,
				TimeUnit.MILLISECONDS);
	}

	public static void schedule(Runnable task, int startDelay, int delay) {
		slowExecutor.scheduleWithFixedDelay(new CatchExceptionRunnable(task),
				startDelay * GameConstants.WORLD_CYCLE_MS, delay * GameConstants.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}
}