package pokemon.util

import scala.collection.mutable.Map

import pokemon.MainApp
import scalafx.scene.image.Image
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.text.Font
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object ResourceUtil {

  private val soundPlayers = Map[String, MediaPlayer]()

  /**
    * Load a resource layout from "view" folder
    *
    * @param path
    *
    * @throws Exception if resource is not found
    * @return
    */
  def resourceLayout(path: String): FXMLLoader = {
    val resource = MainApp.getClass.getResourceAsStream(s"view/${path}.fxml")
    if (resource == null) throw new Exception(s"Cannot load resource: $path")

    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    loader
  }

  /**
    * Load a resource image from any folder
    *
    * @param path
    *
    * @throws Exception if resource is not found
    * @return
    */
  def resouceImage(path: String): Image = {
    val resource = MainApp.getClass.getResourceAsStream(path)
    if (resource == null) throw new Exception(s"Cannot load resource: $path")

    new Image(resource)
  }

  /**
    * Load specifically TTF, from "font" folder
    *
    * @param path
    *
    * @throws Exception if resource is not found
    */
  def loadFont(path: String): Unit = {
    val fontResource = MainApp.getClass.getResourceAsStream(s"font/${path}")
    if (fontResource == null) throw new Exception(s"Cannot load font: $path")

    Font.loadFont(fontResource, 1)
  }

  def playSound(target: String, loop: Boolean = false): Unit = {
    val resource = MainApp.getClass.getResource(s"sfx/$target")
    if (resource == null) throw new Exception(s"Resource: Cannot load sound: $target")

    val media = new Media(resource.toURI.toString)
    if (media == null) throw new Exception(s"Media: Cannot load sound: $target")

    val player = soundPlayers.getOrElseUpdate(target, new MediaPlayer(media))
    if (player == null) throw new Exception(s"Player: Cannot load sound: $target")

    if (loop) player.setCycleCount(MediaPlayer.Indefinite)
    else player.setCycleCount(1)

    player.seek(player.getStartTime)
    player.play()
  }

  def stopSound(target: String): Unit = {
    soundPlayers.get(target).foreach(_.stop())
  }

  def stopAllSounds(): Unit = {
    soundPlayers.values.foreach(_.stop())
  }

  def disposeSound(target: String): Unit = {
    soundPlayers.get(target).foreach { player =>
      player.stop()
      player.dispose()
    }
    soundPlayers.remove(target)
  }

  def disposeAllSounds(): Unit = {
    soundPlayers.values.foreach { player =>
      player.stop()
      player.dispose()
    }
    soundPlayers.clear()
  }
}
