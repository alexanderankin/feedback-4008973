import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * This class illustrates the difference, but does not issue any warnings
 * because there is no "non blocking context" (calls to frameworks) detected.
 */
@SuppressWarnings("unused")
public abstract class ExampleWithoutContext {
    private final Scheduler schedulerFromLibraryMethod;
    private final Scheduler schedulerFromConstructor;

    @SuppressWarnings("unused")
    public ExampleWithoutContext() {
        schedulerFromLibraryMethod = Schedulers.boundedElastic();
        schedulerFromConstructor = newBoundedElastic(5, 10, "schedulerFromConstructor");
    }

    protected abstract Mono<?> getMonoSomeHow();

    @SuppressWarnings("unused")
    public Mono<Void> schedulePublisherWithNoWarning() {
        return getMonoSomeHow().subscribeOn(schedulerFromLibraryMethod).then();
    }

    @SuppressWarnings("unused")
    public Mono<Void> schedulePublisherFromConstructor() {
        return getMonoSomeHow().subscribeOn(schedulerFromConstructor).then();
    }

    public static Scheduler newBoundedElastic(int threadCap, int queuedTaskCap, String name) {
        return Schedulers.newBoundedElastic(threadCap, queuedTaskCap, name);
    }
}
