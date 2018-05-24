package log.analysis.storm

import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date, Properties}

import monix.execution.Scheduler.{global => scheduler}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.scalacheck._

object Producer extends App {
  val topic = "kafka_docker_topic"
  val groupId = "kafka_docker_group"
  val broker = "kafka:9092"

  val props = new Properties()
  props.put("bootstrap.servers", broker)
  props.put("group.id", groupId)
  props.put("client.id", "ScalaProducerExample")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  while(true){
    val g1to5: Gen[List[Int]] = Gen.containerOf[List,Int](Gen.choose(1, 5))

    val runtime = new Date().getTime()
    val calendar = Calendar.getInstance()
    calendar.setTimeInMillis(runtime)

    val runtimeHMS =
      s"Hour : ${calendar.get(Calendar.HOUR)}" +
        s"Minute :${calendar.get(Calendar.MINUTE)}" +
        s"Seconds :${calendar.get(Calendar.SECOND)}"

    val msg = "Message sent at: " + g1to5.toString
    val data = new ProducerRecord[String, String](topic, msg)
    //sync
    producer.send(data)
    println(msg)
  }
  scheduler.scheduleWithFixedDelay(
    0, 100, TimeUnit.MILLISECONDS,
    new Runnable {
      def run(): Unit = {

        val runtime = new Date().getTime()
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(runtime)


        val g1to5: Gen[List[Int]] = Gen.containerOf[List,Int](Gen.choose(1, 5))



        val runtimeHMS =
          s"Hour : ${calendar.get(Calendar.HOUR)}" +
          s"Minute :${calendar.get(Calendar.MINUTE)}" +
          s"Seconds :${calendar.get(Calendar.SECOND)}"

        val msg = "Message sent at: " + g1to5.toString
        val data = new ProducerRecord[String, String](topic, msg)
        //sync
        producer.send(data)
        println(msg)
        //async
        //producer.send(data, (m,e) => {})
      }
    })

  Thread.sleep(10000000)
  System.out.println("Stopping producer")
  producer.close()
}
