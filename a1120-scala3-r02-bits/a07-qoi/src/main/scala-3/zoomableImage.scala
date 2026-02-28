/*
* 
* This file is part of the CS-A1120 Programming 2 course materials at
* Aalto University in Spring 2026, and is for your personal use on that
* course only.
* Distribution of any parts of this file in any form, including posting or
* sharing on public or shared forums or repositories is *prohibited* and
* constitutes a violation of the code of conduct of the course.
* The programming exercises of CS-A1120 are individual and confidential
* assignments---this means that as a student taking the course you are
* allowed to individually and confidentially work with the material,
* to discuss and review the material with course staff, and submit the
* material for grading on course infrastructure.
* All other use - including, having other persons or programs
* (e.g. AI/LLM tools) working on or solving the exercises for you is
* forbidden, and constitutes a violation of the code of conduct of this
* course.
* 
*/


package qoi

// GUI related
import scala.swing._
import scala.swing.event.{MouseWheelMoved, MouseClicked,
                          ValueChanged, KeyPressed, KeyReleased, Key}
import javax.swing.ImageIcon
import javax.swing.border.{EmptyBorder, MatteBorder}
import java.awt.{Font, Image}
import java.awt.image.BufferedImage

// scala Futures, used in image resizing
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * A helper class that acts as a swing component which allows showing large images and
  * zooming on them
  *
  * @param img The starting image, can be changed later
  * @param cfastZoom Whether the image resizing should happen fast (or smooth)
  */
class ZoomableImage(img: BufferedImage, cfastZoom: Boolean) extends BoxPanel(Orientation.Vertical):

  private var baseImage = img
  // Some flags and constants
  private var ctrlDown = false
  private var currentImgWidth = baseImage.getWidth()
  private var currentImgHeight = baseImage.getHeight()
  private var _fastZoom = cfastZoom
  private val scrollSens = 12
  private val refZoomHeight = 512

  // Handle the zoom amount
  private val zoomSlider = new Slider:
    min = 1
    max = 20              // Adjust this to zoom further in
    majorTickSpacing = 1
    snapToTicks = true
    paintTicks = true
    value = 1
    listenTo(this)
    reactions += {
      case ValueChanged(src) =>
        if src == this && !adjusting && !ctrlDown then // Try zooming
          initiateZoomToScale(value * refZoomHeight)
    }
  
  // Sets the viewed image to be the base image 1:1
  private val origScaleButton = Button("original")({
    imageLabel.icon = ImageIcon(baseImage)
    currentImgWidth = baseImage.getWidth()
    currentImgHeight = baseImage.getHeight()
  })
  origScaleButton.preferredSize = new Dimension(60,25)
  origScaleButton.margin = new Insets(2, 2, 2, 2)

  // Fits the image such that it's entirely visible
  private val fitToViewButton = Button("fit to view")({
    this.fitToView()
  })
  fitToViewButton.preferredSize = new Dimension(75,25)
  fitToViewButton.margin = new Insets(2, 2, 2, 2)

  // Actual image Label
  private val imageLabel = new Label
  
  private val scrollPane = new ScrollPane:
    contents = imageLabel
    verticalScrollBar.unitIncrement = scrollSens
    horizontalScrollBar.unitIncrement = scrollSens
    focusable = true
    tooltip = s"size: ${baseImage.getWidth()}x${baseImage.getHeight()}"
    listenTo(mouse.clicks, mouse.wheel, keys)
    reactions += {
      case MouseClicked(src, _, _, _, _) =>
        if src == this then
          //println("Mouse clicked in the scrollpane, requesting focus")
          requestFocus()
      case MouseWheelMoved(src, _, _, rot) =>
        if ctrlDown then
          //println("Rotation: " + rot)
          val newZoom = zoomSlider.value - rot
          zoomSlider.value = newZoom
      case KeyPressed(_, key, _, _)  =>
        if key == Key.Control then
          //println("Ctrl pressed")
          ctrlDown = true
          verticalScrollBar.unitIncrement = 0   // Disable scrolling when Ctrl is pressed for zoom
      case KeyReleased(_, key, _, _) =>
        if key == Key.Control then
          ctrlDown = false
          //println("Ctrl released")
          verticalScrollBar.unitIncrement = scrollSens
          zoomSlider.publish(new ValueChanged(zoomSlider))  // Initiate zoom when control is released
      
    }
  end scrollPane

  // Set the contents
  this.contents ++= Seq(
    new BoxPanel(Orientation.Horizontal):   // Zoom controls
      contents ++= Seq(
        new TextField:
          background = new Color(238, 238, 238)
          border = new EmptyBorder(0,0,0,0)
          editable = false
          font = new Font("Courier", Font.PLAIN, 14)
          maximumSize = new Dimension(40, 25)
          text = "zoom: "
        ,
        zoomSlider,
        origScaleButton,
        fitToViewButton
      )
      border = new MatteBorder(0, 0, 2, 0, new Color(160, 160, 160))
    ,
    scrollPane      // The actual image in zoomable pane
  )
  //this.preferredSize = new Dimension(400, 600)

  // Set the image to default size
  this.initiateZoomToScale(this.refZoomHeight, true, true)

  // Methods

  /**
    * Asks the image to scale to the defined resolution, either on vertical or horizontal
    * axis. The rescaling will be done asynchronously using Futures, and image will be updated
    * once ready. By default scales using the vertical axis, and only computes rescaled image
    * if it would differ from the current resolution.
    *
    * @param scale The resolution (along the defined axis) to which the image should scale
    * @param forceZoom Whether the computation should be done regardless of no change in resolution
    * @param useVertAxis true: uses vertical axis; false: uses horizontal axis
    */
  def initiateZoomToScale(scale: Int, forceZoom: Boolean = false, useVertAxis: Boolean = true): Unit =
    val dimChanges = (useVertAxis && (scale != currentImgHeight)) || (!useVertAxis && (scale != currentImgWidth))
    // Only do work when necessary
    if dimChanges || forceZoom then
      //println(s"zooming to scale $scale (${if useVertAxis then "vertical" else "horizontal"})")
      val scalingF = Future {
        if _fastZoom then
          // Noticeably faster than using Image.SCALE_FAST
          val (newW, newH) =
            if useVertAxis then
              val factor = scale.toDouble / baseImage.getHeight()
              ((factor * baseImage.getWidth()).round.toInt, scale)
            else
              val factor = scale.toDouble / baseImage.getWidth()
              (scale, (factor * baseImage.getHeight()).round.toInt)
          val resisedImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB)
          val g = resisedImg.createGraphics()
          g.drawImage(baseImage, 0, 0, newW, newH, null)
          g.dispose()
          resisedImg
        else if useVertAxis then                            // Vertical smooth
          baseImage.getScaledInstance(-1, scale, Image.SCALE_SMOOTH)
        else                                                // Horizontal smooth
          baseImage.getScaledInstance(scale, -1, Image.SCALE_SMOOTH)
      }
      scalingF.foreach({image =>
        try
          // The scaling should already be ready, therefore ImageObserver shouldn't be called ever
          this.currentImgWidth = image.getWidth(null)
          this.currentImgHeight = image.getHeight(null)
        catch
          case e: NullPointerException => println("NullPtrException while getting scaled img dim")
        //println(s"New image w ${this.currentImgWidth}, h ${this.currentImgHeight}")
        this.imageLabel.icon = ImageIcon(image)
      })
  end initiateZoomToScale

  /**
    * Rescales the image such that it just fits into the current view
    */
  def fitToView(forceRefresh: Boolean = false): Unit =
    val margin = 3
    val viewH = scrollPane.size.getHeight() - margin
    val viewW = scrollPane.size.getWidth() - margin
    val scaleFactor = baseImage.getHeight() / viewH
    val potentialW = baseImage.getWidth() / scaleFactor
    if potentialW <= viewW then
      // Scaling by height works
      initiateZoomToScale(viewH.toInt, forceRefresh)
    else
      // Should scale by width
      initiateZoomToScale(viewW.toInt, forceRefresh, false)
  end fitToView

  /**
    * Get the base image
    */
  def image = this.baseImage

  /**
    * Set the base image
    * @param newImg the new base image
    */
  def image_=(newImg: BufferedImage): Unit =
    this.baseImage = newImg
    scrollPane.tooltip = s"size: ${baseImage.getWidth()}x${baseImage.getHeight()}"
    this.initiateZoomToScale(this.refZoomHeight, true, true)
  
  /**
    * Get the current zoom style
    */
  def fastZoom = this._fastZoom

  /**
    * Set the zoom style (either fast or smooth)
    */
  def fastZoom_=(newVal: Boolean): Unit =
    this._fastZoom = newVal

end ZoomableImage