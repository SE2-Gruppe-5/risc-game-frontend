
class SSEClient {
    private var eventHandler: EventHandler? = null
    private var eventSource: EventSource? = null;

    fun init(eventHandler: EventHandler) {
        this.eventHandler = eventHandler

        eventSource = EventSource.Builder(eventHandler, URI.create(Constants.HOST + Constants.SSE_URL))
            .connectTimeout(Duration.ofSeconds(10))
            .backoffResetThreshold(Duration.ofSeconds(10))
            .build()
        eventSource!!.start()
    }

}