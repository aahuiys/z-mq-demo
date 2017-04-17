package async;

import javax.swing.SwingUtilities;

public abstract class AsyncWorker {

	private Object value;

	private boolean finished = false;

	private static class ThreadVar {

		private Thread thread;

		private ThreadVar(Thread thread) {
			this.thread = thread;
		}

		private synchronized Thread get() {
			return thread;
		}

		private synchronized void clear() {
			thread = null;
		}
	}

	private ThreadVar threadVar;

	private synchronized Object getValue() {
		return value;
	}

	private synchronized void setValue(Object value) {
		this.value = value;
	}

	public abstract Object construct();

	public void finished() {
		finished = true;
	}

	public boolean isFinished() {
		return finished;
	}

	public void interrupt() {
		Thread thread = threadVar.get();
		if (thread != null) {
			thread.interrupt();
		}
		threadVar.clear();
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		Thread thread = threadVar.get();
		if (thread != null) {
			thread.stop();
		}
		threadVar.clear();
	}

	public Object get() {
		while (true) {
			Thread thread = threadVar.get();
			if (thread == null) {
				return getValue();
			}
			try {
				System.out.println("Join and wait thread [" + thread.getName() + "]");
				thread.join();
				System.out.println("Wait thread [" + thread.getName() + "] complete");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return null;
			}
		}
	}

	public AsyncWorker(String workThreadName) {
		final Runnable doFinished = new Runnable() {

			@Override
			public void run() {
				finished();
			}
		};
		Runnable doConstruct = new Runnable() {

			@Override
			public void run() {
				try {
					setValue(construct());
				} finally {
					threadVar.clear();
				}
				SwingUtilities.invokeLater(doFinished);
			}
		};
		Thread thread = new Thread(doConstruct, workThreadName);
		threadVar = new ThreadVar(thread);
	}

	public void start() {
		finished = false;
		Thread thread = threadVar.get();
		if (thread != null) {
			thread.start();
		}
	}
}
