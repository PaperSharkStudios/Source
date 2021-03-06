package jaggrab.dispatch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A class which manages the pool of request workers.
 * @author Graham
 */
public final class RequestWorkerPool {
	
	/**
	 * The number of threads per request type.
	 */
	private static final int THREADS_PER_REQUEST_TYPE = Runtime.getRuntime().availableProcessors();
	
	/**
	 * The number of request types.
	 */
	private static final int REQUEST_TYPES = 3;
	
	/**
	 * The executor service.
	 */
	private final ExecutorService service;
	
	/**
	 * A list of request workers.
	 */
	private final List<RequestWorker<?>> workers = new ArrayList<RequestWorker<?>>();
	
	/**
	 * The request worker pool.
	 */
	public RequestWorkerPool() {
		int totalThreads = REQUEST_TYPES * THREADS_PER_REQUEST_TYPE;
		service = Executors.newFixedThreadPool(totalThreads);
	}

	/**
	 * Starts the threads in the pool.
	 * @throws Exception if the file system cannot be created.
	 */
	public void start() throws Exception {
		File base = new File("./cache/");
		for (int i = 0; i < THREADS_PER_REQUEST_TYPE; i++) {
			workers.add(new JagGrabRequestWorker());
			workers.add(new OnDemandRequestWorker());
			workers.add(new HttpRequestWorker());
		}
		
		for (RequestWorker<?> worker : workers) {
			service.submit(worker);
		} 
	}
	
	/**
	 * Stops the threads in the pool.
	 */
	public void stop() {
		for (RequestWorker<?> worker : workers) {
			worker.stop();
		}
		
		service.shutdownNow();
	}

}
