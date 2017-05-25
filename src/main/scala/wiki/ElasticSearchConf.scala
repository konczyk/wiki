package wiki

import java.net.InetAddress

import com.typesafe.config.ConfigFactory
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient

class ElasticSearchConf {

  private val conf = ConfigFactory.load().getConfig("elasticsearch")
  private val host = conf.getString("host")
  private val port = conf.getInt("port")
  private val address = new InetSocketTransportAddress(InetAddress.getByName(host), port)

  val index = conf.getString("index")
  lazy val client: TransportClient =
    new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(address)
}
