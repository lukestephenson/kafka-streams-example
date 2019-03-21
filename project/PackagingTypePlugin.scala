import sbt._

/**
  * See https://github.com/sbt/sbt/issues/3618.
  *
  * This has been added due to intellij failures adding
  * `"org.apache.kafka" %% "kafka-streams-scala" % "2.1.0"`
  */
object PackagingTypePlugin extends AutoPlugin {
  override val buildSettings = {
    sys.props += "packaging.type" -> "jar"
    Nil
  }
}