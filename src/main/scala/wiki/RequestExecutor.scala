package wiki

import org.elasticsearch.action.{ActionListener, ActionRequestBuilder, ActionResponse}
import scala.concurrent.{Future, Promise}

class RequestExecutor[T <: ActionResponse] extends ActionListener[T] {

  private val promise = Promise[T]()

  override def onResponse(response: T) {
    promise.success(response)
  }

  override def onFailure(e: Exception) {
    promise.failure(e)
  }

  def execute[RB <: ActionRequestBuilder[_, T, _]](request: RB): Future[T] = {
    request.execute(this)
    promise.future
  }

}

object RequestExecutor {
  def apply[T <: ActionResponse](): RequestExecutor[T] = {
    new RequestExecutor[T]
  }
}

