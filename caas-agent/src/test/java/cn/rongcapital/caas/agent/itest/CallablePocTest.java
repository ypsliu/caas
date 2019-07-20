/**
 * 
 */
package cn.rongcapital.caas.agent.itest;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.NotFoundException;

/**
 * @author shangchunming@rongcapital.cn
 *
 */
public class CallablePocTest {

	private final ExecutorService pool = Executors.newCachedThreadPool();

	private volatile AtomicInteger index = new AtomicInteger(1);

	private <T> T call(final Callable<T> request) {
		T result = null;
		final Future<T> f = pool.submit(request);
		try {
			result = f.get();
		} catch (NotFoundException e) {
			System.out.println("not found exception");
		} catch (ExecutionException e) {
			System.out.println("execution exception");
			if (e.getCause() instanceof NotFoundException) {
				System.out.println("not found exception from execution exception");
			}
		} catch (Exception e) {
			System.out.println("other exception");
		}
		return result;
	}

	public String test() {
		return this.call(new Callable<String>() {

			@Override
			public String call() throws Exception {
				int i = index.incrementAndGet();
				if (i % 2 == 0) {
					return "" + i;
				}
				throw new NotFoundException();
			}

		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CallablePocTest cpt = new CallablePocTest();
		System.out.println(cpt.test());
		System.out.println("====================");
		System.out.println(cpt.test());
		System.out.println("====================");
		System.out.println(cpt.test());
	}

}
