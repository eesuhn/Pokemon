package pokemon.util

import scala.collection.mutable.Map

import pokemon.MainApp
import scalafx.scene.image.Image
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.text.Font
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object ResourceUtil {

  private val _soundPlayers = Map[String, List[MediaPlayer]]()

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

  /**
    * Load a resource sound from "sfx" folder
    *
    * - Dispose first if the target sound player exists
    * - `loop = true` requires manual disposal
    *
    * @param target
    * @param loop
    */
  def playSound(target: String, loop: Boolean = false): Unit = {
    val resource = MainApp.getClass.getResource(s"sfx/$target")
    if (resource == null) throw new Exception(s"Resource: Cannot load sound: $target")

    disposeSound(target)

    val media: Media = new Media(resource.toURI.toString)
    if (media == null) throw new Exception(s"Media: Cannot load sound: $target")

    val newPlayer = new MediaPlayer(media)
    _soundPlayers(target) = newPlayer :: _soundPlayers.getOrElse(target, List.empty)

    if (loop) newPlayer.setCycleCount(MediaPlayer.Indefinite)
    newPlayer.seek(newPlayer.getStartTime)
    newPlayer.play()
  }

  /**
    * Dispose target sound player if exists
    *
    * @param target
    */
  def disposeSound(target: String): Unit = {
    _soundPlayers.get(target).foreach(_.foreach(_.dispose()))
    _soundPlayers.remove(target)
  }

  def disposeAllSounds(): Unit = {
    _soundPlayers.values.foreach(_.foreach(_.dispose()))
    _soundPlayers.clear()
  }

  /**
    * @deprecated Cause memory exhaustion
    *
    * @param target
    */
  def stopSound(target: String): Unit = {
    _soundPlayers.get(target).foreach(_.foreach(_.stop()))
  }

  /**
    * @deprecated Cause memory exhaustion
    */
  def stopAllSounds(): Unit = {
    _soundPlayers.values.foreach(_.foreach(_.stop()))
  }
}
